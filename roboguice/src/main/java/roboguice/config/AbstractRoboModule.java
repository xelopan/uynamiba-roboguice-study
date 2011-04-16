package roboguice.config;

import com.google.inject.AbstractModule;

/**
 * An extension to guice's AbstractModule that gives the module access to
 * the DefaultApplicationRoboModule.
 *
 * In addition, it overrides {@link #requestStaticInjection(Class[])} to add support
 * for RoboGuice's resource and view injection when injecting static methods.
 */
public abstract class AbstractRoboModule extends AbstractModule {
    protected DefaultContextRoboModule applicationRoboModule;

    protected AbstractRoboModule(DefaultContextRoboModule applicationRoboModule) {
        this.applicationRoboModule = applicationRoboModule;
    }

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(types);
        applicationRoboModule.resourceListener.requestStaticInjection(types);
        applicationRoboModule.viewListener.requestStaticInjection(types); // BUG does it make sense to statically inject views?
    }
    

}
