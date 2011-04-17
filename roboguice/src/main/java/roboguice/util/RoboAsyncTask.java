package roboguice.util;

import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import java.util.concurrent.Executor;

/**
 * Allows injection to happen for tasks that execute in a background thread.
 * 
 * @param <ResultT>
 */
public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    @Inject static protected Provider<Injector> injectorProvider;

    {
        //injectorProvider.get().injectMembers(this);
    }

    protected RoboAsyncTask() {
    }

    protected RoboAsyncTask(Handler handler) {
        super(handler);
    }

    protected RoboAsyncTask(Handler handler, Executor executor) {
        super(handler, executor);
    }

    protected RoboAsyncTask(Executor executor) {
        super(executor);
    }

}
