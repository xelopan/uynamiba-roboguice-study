package roboguice.config;

import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.inject.*;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import roboguice.util.RoboThread;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * BUG move activity-related bindings to activity module
 */

public class DefaultContextRoboModule extends AbstractModule {
    protected Context context;
    protected ContextScope contextScope;
    protected ResourceListener resourceListener;
    protected ViewListener viewListener;
    protected EventManager eventManager;

    public DefaultContextRoboModule(Context context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(Context.class).toInstance(context);
        contextScope = new ContextScope();
        viewListener = new ViewListener(context, contextScope);
        resourceListener = new ResourceListener((Application)context.getApplicationContext());
        eventManager = new EventManager(context);

        //final ExtrasListener extrasListener = new ExtrasListener(context);
        final PreferenceListener preferenceListener = new PreferenceListener(context,contextScope);
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator(getProvider(Handler.class));



        // Sundry Android Classes
        bind(ContextScope.class).toInstance(contextScope);
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(EventManager.class).toInstance(eventManager);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);

        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), resourceListener);
        //bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);
        bindListener(Matchers.any(), preferenceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(context, eventManager, observerThreadingDecorator));



        requestStaticInjection(Ln.class);
        requestStaticInjection(RoboThread.class);
        requestStaticInjection(RoboAsyncTask.class);
    }
}
