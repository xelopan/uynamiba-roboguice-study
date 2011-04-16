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

            final ArrayList<Module> m = new ArrayList<Module>();
            m.add( new DefaultContextRoboModule(context));
            m.addAll(Arrays.asList(modules));

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

            final int id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
            final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> modules = new ArrayList<Module>();
            final DefaultApplicationRoboModule applicationRoboModule = new DefaultApplicationRoboModule(application);

            modules.add(applicationRoboModule);

            try {
                for (String name : moduleNames) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                    modules.add( AbstractRoboModule.class.isAssignableFrom(clazz) ? clazz.getConstructor(DefaultApplicationRoboModule.class).newInstance(applicationRoboModule) : clazz.newInstance() );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            rootInjector = createAndBindNewRootInjector(application, stage, modules.toArray(new Module[modules.size()]));

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
