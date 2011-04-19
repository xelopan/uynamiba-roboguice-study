package roboguice;

import roboguice.config.ApplicationModule;
import roboguice.config.DefaultApplicationRoboModule;
import roboguice.config.DefaultContextRoboModule;

import android.app.Application;
import android.content.Context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.WeakHashMap;

public class RoboGuice {
    protected static WeakHashMap<Context,Injector> injectors = new WeakHashMap<Context,Injector>();
    protected static WeakHashMap<Application,Injector> rootInjectors = new WeakHashMap<Application, Injector>();
    protected static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    private RoboGuice() {
    }



    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getRootInjector(Application application) {
        final Injector rootInjector = rootInjectors.get(application);
        return rootInjector!=null ? rootInjector : createAndBindNewRootInjector(DEFAULT_STAGE, application);
    }

    /**
     * Return the cached Injector instance for this context, or create a new child injector for this context if necessary.
     */
    public static Injector getInjector(Context context) {
        final Injector i = injectors.get(context);
        return i!=null ? i : createAndBindNewContextInjector(context);
    }

    public static Injector createAndBindNewContextInjector( Context context, Module... modules ) {
        Injector rtrn = injectors.get(context);
        if( rtrn!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + context);

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(context);
            if( rtrn!=null )
                return rtrn;

            rtrn = getRootInjector((Application) context.getApplicationContext()).createChildInjector( createContextModuleList(context,modules) );
            injectors.put(context, rtrn);

        }

        return rtrn;
    }

    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getRootInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewRootInjector(Application application, ApplicationModule... modules) {
        return createAndBindNewRootInjector(DEFAULT_STAGE, application, modules);
    }

    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getRootInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewRootInjector(Stage stage, Application application, ApplicationModule... modules) {

        Injector rootInjector = rootInjectors.get(application);
        if( rootInjector!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + application );

        synchronized (RoboGuice.class) {
            rootInjector = rootInjectors.get(application);
            if( rootInjector!=null )
                return rootInjector;


            rootInjector = Guice.createInjector(stage, createApplicationModuleList(application,modules));
            rootInjectors.put(application,rootInjector);
        }

        return rootInjector;
    }


    protected static ArrayList<Module> createContextModuleList( Context context, Module... modules ) {

        // BUG should cache this
        final int id = context.getResources().getIdentifier("roboguice_modules", "array", context.getPackageName());
        final String[] moduleNames = id>0 ? context.getResources().getStringArray(id) : new String[]{};
        final ArrayList<Module> m = new ArrayList<Module>(Arrays.asList(modules));

        try {
            for (String name : moduleNames) {
                final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);

                // Application modules go in the root injector, context modules go in the child injector
                if( !ApplicationModule.class.isAssignableFrom(clazz)) {
                    Constructor<? extends Module> c = null;
                    try {
                         c = clazz.getConstructor(Context.class);
                    } catch( NoSuchMethodException ignored) {
                    }
                    m.add( c!=null ? c.newInstance(context) : clazz.newInstance() );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if( m.size()==0 )
            m.add( new DefaultContextRoboModule(context) );

        return m;
    }

    protected static ArrayList<ApplicationModule> createApplicationModuleList( Application application, ApplicationModule... modules ) {

        // BUG should cache this
        final int id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
        final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
        final ArrayList<ApplicationModule> m = new ArrayList<ApplicationModule>(Arrays.asList(modules));

        try {
            for (String name : moduleNames) {
                final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);

                // Application modules go in the root injector, context modules go in the child injector
                if( ApplicationModule.class.isAssignableFrom(clazz)) {
                    Constructor<? extends Module> c = null;
                    try {
                        c = clazz.getConstructor(Application.class);
                    } catch( NoSuchMethodException ignored) {
                    }

                    m.add( (ApplicationModule) (c!=null ? c.newInstance(application) : clazz.newInstance()) );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if( m.size()==0 )
            m.add( new DefaultApplicationRoboModule(application));

        return m;
    }


    public static class util {
        private util() {}

        /**
         * Clear all of RoboGuice's cached injectors.  Designed to be used when working with multiple
         * testcases that share the same application.
         */
        public static void clearAllInjectors() {
            rootInjectors.clear();
            injectors.clear();
        }

    }


}
