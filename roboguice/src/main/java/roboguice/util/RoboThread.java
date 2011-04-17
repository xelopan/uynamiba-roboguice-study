package roboguice.util;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * An extension to {@link Thread} which propogates the current
 * Context to the background thread.
 *
 * Current limitations:  any parameters set in the RoboThread are
 * ignored other than Runnable.  This means that priorities, groups,
 * names, etc. won't be honored. Yet.
 */
public class RoboThread extends Thread {
    @Inject static protected Provider<Injector> injectorProvider;

    {
        //injectorProvider.get().injectMembers(this);
    }

    public RoboThread() {
    }

    public RoboThread(Runnable runnable) {
        super(runnable);
    }

}
