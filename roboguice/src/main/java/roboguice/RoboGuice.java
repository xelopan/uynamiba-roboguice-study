package roboguice;

import roboguice.config.DefaultRoboModule;
import roboguice.inject.*;

import android.app.Application;
import android.content.Context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.StaticInjectionRequest;

import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * BUG hashmap should also key off of stage and modules list
 */
@SuppressWarnings({"ALL"})
public class RoboGuice {
    public static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    protected static WeakHashMap<Application,Injector> injectors = new WeakHashMap<Application,Injector>();
    protected static WeakHashMap<Application,ResourceListener> resourceListeners = new WeakHashMap<Application, ResourceListener>();
    protected static WeakHashMap<Application,ViewListener> viewListeners = new WeakHashMap<Application, ViewListener>();

    private RoboGuice() {
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getApplicationInjector(Application application) {
        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            return rtrn;

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;
            
            return setApplicationInjector(application, DEFAULT_STAGE);
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     * If specifying your own modules, you must include a DefaultRoboModule for most things to work properly.
     * Do something like the following:
     *
     * RoboGuice.setAppliationInjector( app, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.createNewDefaultRoboModule(app)).with(new MyModule() );
     *
     * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
     * @see roboguice.RoboGuice#setApplicationInjector(android.app.Application, com.google.inject.Stage, com.google.inject.Module...)
     * @see roboguice.RoboGuice#createNewDefaultRoboModule(android.app.Application)
     * @see roboguice.RoboGuice#DEFAULT_STAGE
     *
     */
    public static Injector setApplicationInjector(final Application application, Stage stage, Module... modules) {

        // Do a little rewriting on the modules first to
        // add static resource injection
        for(Element element : Elements.getElements(modules)) {
            element.acceptVisitor(new DefaultElementVisitor<Void>() {
                @Override
                public Void visit(StaticInjectionRequest element) {
                    getResourceListener(application).requestStaticInjection(element.getType());
                    return null;
                }
            });
        }

        synchronized (RoboGuice.class) {
            final Injector rtrn = Guice.createInjector(stage, modules);
            injectors.put(application,rtrn);
            return rtrn;
        }
    }

    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector setApplicationInjector(Application application, Stage stage) {

        synchronized (RoboGuice.class) {
            final int id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
            final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> modules = new ArrayList<Module>();
            final DefaultRoboModule defaultRoboModule = createNewDefaultRoboModule(application);

            modules.add(defaultRoboModule);

            try {
                for (String name : moduleNames) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                    modules.add( clazz.newInstance() );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final Injector rtrn = setApplicationInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);
            return rtrn;
        }

    }


    public static RoboInjector getInjector(Context context) {
        final Application application = (Application)context.getApplicationContext();
        return new ContextScopedRoboInjector(context, getApplicationInjector(application), getViewListener(application));
    }


    
    public static DefaultRoboModule createNewDefaultRoboModule(final Application application) {
        return new DefaultRoboModule(application, new ContextScope(), getViewListener(application), getResourceListener(application));
    }






    protected static ResourceListener getResourceListener( Application application ) {
        ResourceListener resourceListener = resourceListeners.get(application);
        if( resourceListener==null ) {
            synchronized (RoboGuice.class) {
                if( resourceListener==null ) {
                    resourceListener = new ResourceListener(application);
                    resourceListeners.put(application,resourceListener);
                }
            }
        }
        return resourceListener;
    }

    protected static ViewListener getViewListener( final Application application ) {
        ViewListener viewListener = viewListeners.get(application);
        if( viewListener==null ) {
            synchronized (RoboGuice.class) {
                if( viewListener==null ) {
                    viewListener = new ViewListener();
                    viewListeners.put(application,viewListener);
                }
            }
        }
        return viewListener;
    }

    public static void destroyInjector(Context context) {
        getInjector(context).getInstance(ContextScope.class).destroy(context);
    }
}
