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
package roboguice.activity;

import roboguice.RoboGuice;
import roboguice.activity.event.*;
import roboguice.event.EventManager;
import roboguice.inject.PreferenceListener;
import roboguice.inject.ViewListener;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.inject.Injector;

/**
 * A {@link RoboPreferenceActivity} extends from {@link PreferenceActivity} to provide
 * dynamic injection of collaborators, using Google Guice.<br />
 *
 * Note: there is currently a limitation that prevents you from using @InjectView if you use setContentView.
 * http://code.google.com/p/roboguice/issues/detail?id=70
 * 
 * @see RoboActivity
 * 
 * @author Toly Pochkin
 * @author Rodrigo Damazio
 * @author Mike Burton
 */
public abstract class RoboPreferenceActivity extends PreferenceActivity {
    protected EventManager eventManager;
    protected ViewListener viewListener;
    protected PreferenceListener preferenceListener;

    /** {@inheritDoc } */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Injector injector = RoboGuice.getInjector(this);
        eventManager = injector.getInstance(EventManager.class);
        viewListener = injector.getInstance(ViewListener.class);
        preferenceListener = injector.getInstance(PreferenceListener.class);
        injector.injectMembers(this);
        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent(savedInstanceState));
    }

    @Override
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        super.setPreferenceScreen(preferenceScreen);
        preferenceListener.injectPreferenceViews();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        viewListener.injectViews();
        eventManager.fire(new OnContentViewAvailableEvent());
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        viewListener.injectViews();
        eventManager.fire(new OnContentViewAvailableEvent());
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        viewListener.injectViews();
        eventManager.fire(new OnContentViewAvailableEvent());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        eventManager.fire(new OnRestartEvent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventManager.fire(new OnStartEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventManager.fire(new OnResumeEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventManager.fire(new OnPauseEvent());
    }

    @Override
    protected void onNewIntent( Intent intent ) {
        super.onNewIntent(intent);
        eventManager.fire(new OnNewIntentEvent());
    }

    @Override
    protected void onStop() {
        try {
            eventManager.fire(new OnStopEvent());
        } finally {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            eventManager.fire(new OnDestroyEvent());
        } finally {
            RoboGuice.getInjector(this).closeScope(this);
            super.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        eventManager.fire(new OnContentChangedEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
    }
}
