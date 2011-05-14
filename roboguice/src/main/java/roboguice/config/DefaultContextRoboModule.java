package roboguice.config;

import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.inject.ExtrasListener;
import roboguice.inject.PreferenceListener;
import roboguice.inject.ResourceListener;
import roboguice.inject.ViewListener;
import roboguice.util.RoboMatchers;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
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
    protected ExtrasListener extrasListener;
    protected PreferenceListener preferenceListener;

    public DefaultContextRoboModule(Context context) {
        this.context = context;
        this.viewListener = new ViewListener(context);
        this.resourceListener = new ResourceListener((Application)context.getApplicationContext());
        this.eventManager = new EventManager(context);
        this.extrasListener = new ExtrasListener(context);
        this.preferenceListener = new PreferenceListener(context);
    }

    @Override
    protected void configure() {
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator(getProvider(Handler.class));


        // The context
        bind(Context.class).toInstance(context);

        if( context instanceof Service )
            bind(Service.class).toInstance((Service)context);

        else if( context instanceof Activity )
            bind(Activity.class).toInstance((Activity)context);


        // Sundry RoboGuice Classes
        bind(EventManager.class).toInstance(eventManager);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(ViewListener.class).toInstance(viewListener);
        bind(PreferenceListener.class).toInstance(preferenceListener);
        bind(ExtrasListener.class).toInstance(extrasListener);
        

        // Listeners that match on any
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(context, eventManager, observerThreadingDecorator));
        bindListener(Matchers.any(), extrasListener);


        // Listeners that match on activity
        bindListener(RoboMatchers.subclassesOfActivity(), viewListener );
        bindListener(RoboMatchers.subclassesOfActivity(), preferenceListener);

    }
}


