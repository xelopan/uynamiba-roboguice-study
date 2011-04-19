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
package roboguice.astroboy;

import roboguice.astroboy.bean.*;
import roboguice.astroboy.service.TalkingThing;
import roboguice.astroboy.service.TalkingThingMockImpl;
import roboguice.config.DefaultContextRoboModule;
import roboguice.inject.ExtraConverter;
import roboguice.inject.SharedPreferencesName;

import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import java.util.Date;

public class AstroboyModule extends DefaultContextRoboModule {

    public AstroboyModule(Context context) {
        super(context);
    }

    @Override
    protected void configure() {
        super.configure();
        
        /*
         * Here is the place to write the configuration specific to your application, i.e. your own custom bindings.
         */
        bind(TalkingThing.class).to(TalkingThingMockImpl.class);
        bind(Person.class).toProvider(PersonFromNameExtraProvider.class);

        // BUG need a better way to set default preferences context
        bindConstant().annotatedWith(SharedPreferencesName.class).to("roboguice.astroboy");
    }
}