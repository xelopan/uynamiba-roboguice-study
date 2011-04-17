/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.inject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.inject.*;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.google.inject.util.Types;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 
 * @author Mike Burton
 * @author Pierre-Yves Ricau (py.ricau+roboguice@gmail.com)
 */
public class ExtrasListener implements TypeListener {
    @Inject protected Injector injector;

    protected Context context;

    public ExtrasListener(Context context ) {
        this.context = context;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {

        for( Class<?> c = typeLiteral.getRawType(); c!=Object.class; c=c.getSuperclass() )
            for (Field field : c.getDeclaredFields())
                if (field.isAnnotationPresent(InjectExtra.class))
                    typeEncounter.register(new ExtrasMembersInjector<I>(field, field.getAnnotation(InjectExtra.class)));


    }




    protected class ExtrasMembersInjector<T> implements MembersInjector<T> {
        protected Field field;
        protected InjectExtra annotation;

        public ExtrasMembersInjector(Field field, InjectExtra annotation) {
            this.field = field;
            this.annotation = annotation;
        }

        public void injectMembers(T instance) {

            if (!(context instanceof Activity))
                return;


            final Activity activity = (Activity) context;
            final String id = annotation.value();
            final Intent intent = activity.getIntent();

            if(intent==null)
                return;

            final Bundle extras = intent.getExtras();

            if (extras == null || !extras.containsKey(id)) {
                // If no extra found and the extra injection is optional, no
                // injection happens.
                if (annotation.optional()) {
                    return;
                } else {
                    throw new IllegalStateException(String.format("Can't find the mandatory extra identified by key [%s] on field %s.%s", id, field
                            .getDeclaringClass(), field.getName()));
                }
            }

            final Object value = convert(field, extras.get(id));

            /*
             * Please notice : null checking is done AFTER conversion. Having
             *
             * @Nullable on a field means "the injected value might be null", ie
             * "the converted value might be null". Which also means that if you
             * don't use @Nullable and a converter returns null, an exception will
             * be thrown (which I find to be the most logic behavior).
             */
            if (value == null && Nullable.notNullable(field) ) {
                throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field
                        .getName()));
            }

            field.setAccessible(true);
            try {

                field.set(instance, value);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);

            } catch (IllegalArgumentException f) {
                throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                        field.getType(), field.getName()));
            }
        }

        @SuppressWarnings("unchecked")
        protected Object convert(Field field, Object value) {

            // Don't try to convert null or primitives
            if (value == null || field.getType().isPrimitive())
                return value;


            // Building parameterized converter type
            // Please notice that the extra type and the field type must EXACTLY
            // match the declared converter parameter types.
            final Key<?> key = Key.get(Types.newParameterizedType(ExtraConverter.class, value.getClass(), field.getType()));

            // Getting bindings map to check if a binding exists
            // We DO NOT currently check for injector's parent bindings. Should we ?
            //final Injector injector = injectorProvider.get();
            final Map<Key<?>, Binding<?>> bindings = injector.getBindings();
            
            if (bindings.containsKey(key)) {
                final ExtraConverter converter = (ExtraConverter) injector.getInstance(key);
                value = converter.convert(value);
            }

            return value;

        }
    }

}

