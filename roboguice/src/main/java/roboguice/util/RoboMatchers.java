package roboguice.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

public class RoboMatchers {
    public static Matcher<? super TypeLiteral<?>> subclassesOfContext() {
        return subclassesOf(Context.class);
    }

    public static Matcher<? super TypeLiteral<?>> subclassesOfActivity() {
        return subclassesOf(Activity.class);
    }

    public static Matcher<? super TypeLiteral<?>> subclassesOfService() {
        return subclassesOf(Service.class);
    }

    public static Matcher<? super TypeLiteral<?>> subclassesOf( final Class<?> clazz ) {
        return new AbstractMatcher<TypeLiteral<?>>() {
                    @Override
                    public boolean matches(TypeLiteral<?> typeLiteral) {
                        return clazz.isAssignableFrom(typeLiteral.getRawType());
                    }
                };
    }
}
