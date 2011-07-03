package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;

import com.google.inject.Inject;

import javax.inject.Provider;


/**
 * An extension to {@link Thread} which propogates the current
 * Context to the background thread.
 *
 * Current limitations:  any parameters set in the RoboThread are
 * ignored other than Runnable.  This means that priorities, groups,
 * names, etc. won't be honored. Yet.
 */
public class RoboThread extends Thread {
    @Inject static protected Provider<Context> contextProvider;
    @Inject static protected Provider<ContextScope> scopeProvider;

    public RoboThread() {
    }

    public RoboThread(Runnable runnable) {
        super(runnable);
    }

    @Override
    public void start() {

        // BUG any parameters set in the RoboThread are ignored other than Runnable.
        // This means that priorities, groups, names, etc. won't be honored. Yet.
        new Thread() {
            public void run() {
                scopeProvider.get().enter(contextProvider.get());
                RoboThread.this.run();
            }
        }.start();

    }
}
