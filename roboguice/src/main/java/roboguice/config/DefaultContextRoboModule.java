package roboguice.config;

import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.inject.ExtrasListener;
import roboguice.inject.PreferenceListener;
import roboguice.inject.ResourceListener;
import roboguice.inject.ViewListener;
import roboguice.util.RoboMatchers;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * BUG move activity-related bindings to activity module
 */

public class DefaultContextRoboModule extends AbstractModule {
    protected Context context;
    protected ResourceListener resourceListener;
    protected ViewListener viewListener;
    protected EventManager eventManager;

    public DefaultContextRoboModule(Context context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(Context.class).toInstance(context);
        viewListener = new ViewListener(context);
        resourceListener = new ResourceListener((Application)context.getApplicationContext());
        eventManager = new EventManager(context);


        final ExtrasListener extrasListener = new ExtrasListener(context);
        final PreferenceListener preferenceListener = new PreferenceListener(context);
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator(getProvider(Handler.class));



        // Sundry Android Classes
        bind(EventManager.class).toInstance(eventManager);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(ViewListener.class).toInstance(viewListener);
        bind(PreferenceListener.class).toInstance(preferenceListener);
        bind(ExtrasListener.class).toInstance(extrasListener);
        

        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(context, eventManager, observerThreadingDecorator));
        bindListener(Matchers.any(), extrasListener);


        bindListener(RoboMatchers.subclassesOfContext(), viewListener );
        bindListener(RoboMatchers.subclassesOfContext(), preferenceListener);

    }
}


