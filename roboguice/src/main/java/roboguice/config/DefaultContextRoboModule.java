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
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

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

        requestInjection(extrasListener);


        // Sundry Android Classes
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(EventManager.class).toInstance(eventManager);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);
        bind(ViewListener.class).toInstance(viewListener);
        bind(PreferenceListener.class).toInstance(preferenceListener);
        bind(ExtrasListener.class).toInstance(extrasListener);
        

        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), new ExtrasListenerListener());  // BUG highly inefficient, but Matchers.identicalTo not working
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);
        bindListener(Matchers.any(), preferenceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(context, eventManager, observerThreadingDecorator));



        requestStaticInjection(Ln.class);
        requestStaticInjection(RoboThread.class);
        requestStaticInjection(RoboAsyncTask.class);
    }
}


/**
 * Trick to force us to wait to do injection until the injector is actually available
 * but hopefully still before other classes that depend on InjectExtra annotations are injected
 */
class ExtrasListenerListener implements TypeListener {
    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        if( typeLiteral.getRawType().equals(ExtrasListener.class))
            typeEncounter.register( (InjectionListener<I>) new InjectionListener<ExtrasListener>() {
                @Override
                public void afterInjection(ExtrasListener injectee) {
                    injectee.injectExtras();
                }
            });
    }
}
