package roboguice;

import roboguice.config.AbstractRoboModule;
import roboguice.config.DefaultApplicationRoboModule;
import roboguice.config.DefaultContextRoboModule;

import android.app.Application;
import android.content.Context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.WeakHashMap;

public class RoboGuice {
    protected static WeakHashMap<Context,Injector> injectors = new WeakHashMap<Context,Injector>();
    protected static Injector rootInjector;
    protected static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    private RoboGuice() {
    }



    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getRootInjector(Application application) {
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

            final int id = context.getResources().getIdentifier("roboguice_modules", "array", context.getPackageName());
            final String[] moduleNames = id>0 ? context.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> m = new ArrayList<Module>();
            final DefaultContextRoboModule contextRoboModule = new DefaultContextRoboModule(context);
            m.add( contextRoboModule );
            m.addAll(Arrays.asList(modules));


            try {
                for (String name : moduleNames) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                    m.add( AbstractRoboModule.class.isAssignableFrom(clazz) ? clazz.getConstructor(DefaultContextRoboModule.class).newInstance(contextRoboModule) : clazz.newInstance() );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            rtrn = getRootInjector((Application) context.getApplicationContext()).createChildInjector(m);
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
    public static Injector createAndBindNewRootInjector(Stage stage, Application application) {

        if( rootInjector!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + application);

        synchronized (RoboGuice.class) {
            if( rootInjector!=null )
                return rootInjector;

            rootInjector = createAndBindNewRootInjector(application, stage, new DefaultApplicationRoboModule(application));

        }

        return rootInjector;
    }


    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getRootInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewRootInjector(Application application, Module... modules) {
        return createAndBindNewRootInjector(application, DEFAULT_STAGE, modules);
    }

    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getRootInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewRootInjector(Application application, Stage stage, Module... modules) {

        if( rootInjector!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + application);

        synchronized (RoboGuice.class) {
            if( rootInjector!=null )
                return rootInjector;

            rootInjector = Guice.createInjector(stage, modules);

        }

        return rootInjector;
    }


}
