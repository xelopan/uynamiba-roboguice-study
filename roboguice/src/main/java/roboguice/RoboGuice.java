package roboguice;

import roboguice.config.AbstractRoboModule;
import roboguice.config.RoboModule;

import android.app.Application;
import android.content.Context;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import java.util.ArrayList;
import java.util.WeakHashMap;

public class RoboGuice {
    protected static WeakHashMap<Context,Injector> injectors = new WeakHashMap<Context,Injector>();
    protected static Stage DEFAULT_STAGE = Stage.PRODUCTION;

    private RoboGuice() {
    }



    /**
     * Return the cached Injector instance for this application, or create a new one if necessary.
     */
    public static Injector getApplicationInjector(Application application) {
        final Injector i = injectors.get(application);
        return i!=null ? i : createAndBindNewApplicationInjector(DEFAULT_STAGE, application);
    }

    public static Injector getContextInjector( Context context ) {
        final Injector i = injectors.get(context);
        return i!=null ? i : createAndBindNewContextInjector(context);
    }

    public static Injector createAndBindNewContextInjector( Context context ) {
        Injector rtrn = injectors.get(context);
        if( rtrn!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + context);

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(context);
            if( rtrn!=null )
                return rtrn;

            rtrn = getApplicationInjector( (Application) context.getApplicationContext() ).createChildInjector();
            injectors.put(context, rtrn);

        }

        return rtrn;
    }


    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getApplicationInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewApplicationInjector(Stage stage, Application application) {

        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + application);

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;

            final int id = application.getResources().getIdentifier("roboguice_modules", "array", application.getPackageName());
            final String[] moduleNames = id>0 ? application.getResources().getStringArray(id) : new String[]{};
            final ArrayList<Module> modules = new ArrayList<Module>();
            final RoboModule roboModule = new RoboModule(application);

            modules.add(roboModule);

            try {
                for (String name : moduleNames) {
                    final Class<? extends Module> clazz = Class.forName(name).asSubclass(Module.class);
                    modules.add( AbstractRoboModule.class.isAssignableFrom(clazz) ? clazz.getConstructor(RoboModule.class).newInstance(roboModule) : clazz.newInstance() );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            rtrn = createAndBindNewApplicationInjector(application, stage, modules.toArray(new Module[modules.size()]));
            injectors.put(application,rtrn);

        }

        return rtrn;
    }


    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getApplicationInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewApplicationInjector(Application application, Module... modules) {
        return createAndBindNewApplicationInjector(application, DEFAULT_STAGE, modules);
    }

    /**
     * Creates a new injector and associates it with the specified application.  It is an error to
     * create multiple injectors for a single application.
     *
     * Generally, you should prefer #getApplicationInjector unless you specifically need this funcionality
     *
     * @throws UnsupportedOperationException if an injector was already associated with this application
     */
    public static Injector createAndBindNewApplicationInjector(Application application, Stage stage, Module... modules) {

        Injector rtrn = injectors.get(application);
        if( rtrn!=null )
            throw new UnsupportedOperationException("An injector was already associated with " + application);

        synchronized (RoboGuice.class) {
            rtrn = injectors.get(application);
            if( rtrn!=null )
                return rtrn;

            rtrn = Guice.createInjector(stage, modules);
            injectors.put(application,rtrn);

        }

        return rtrn;
    }


}
