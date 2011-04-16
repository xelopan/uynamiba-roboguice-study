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

import java.util.ArrayList;

public class ContextScope {

    protected ArrayList<ViewMembersInjector<?>> viewsForInjection = new ArrayList<ViewMembersInjector<?>>();
    protected ArrayList<PreferenceMembersInjector<?>> preferencesForInjection = new ArrayList<PreferenceMembersInjector<?>>();

    public void registerViewForInjection(ViewMembersInjector<?> injector) {
        viewsForInjection.add(injector);
    }

    public void registerPreferenceForInjection(PreferenceMembersInjector<?> injector) {
        preferencesForInjection.add(injector);
    }

    public void injectViews() {
        for (int i = viewsForInjection.size() - 1; i >= 0; --i)
            viewsForInjection.remove(i).reallyInjectMembers();
    }

    public void injectPreferenceViews() {
        for (int i = preferencesForInjection.size() - 1; i >= 0; --i)
            preferencesForInjection.remove(i).reallyInjectMembers();
    }


}
