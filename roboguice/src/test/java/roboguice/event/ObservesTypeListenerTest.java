package roboguice.event;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.config.DefaultContextRoboModule;

import android.app.Activity;
import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Injector;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
public class ObservesTypeListenerTest {

    protected EventManager eventManager;
    protected Injector injector;
    protected List<Method> eventOneMethods;
    protected List<Method> eventTwoMethods;

    @Before
    public void setup() throws NoSuchMethodException {
        final Activity activity = new DummyActivity();
        injector = RoboGuice.bindNewContextInjector(new DummyActivity(), new MyContextModule(activity));

        eventManager = injector.getInstance(EventManager.class);

        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);

    }

    @Test
    public void simulateInjection() {
        final InjectedTestClass testClass = injector.getInstance(InjectedTestClass.class);

        eventManager.fire(new EventOne());

        testClass.tester.verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.tester.verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(expected = RuntimeException.class)
    public void invalidObservesMethodSignature(){
        injector.getInstance(MalformedObserves.class);
    }

    static public class InjectedTestClass{
        @Inject public ContextObserverTesterImpl tester;
    }

    public class MalformedObserves{
        public void malformedObserves(int val, @Observes EventOne event){}
    }


    public static class DummyActivity extends RoboActivity {

    }



    public static class MyContextModule extends DefaultContextRoboModule {
        public MyContextModule(Context context) {
            super(context);
        }

        @Override
        protected void configure() {
            super.configure();

            // BUG it's necessary when using child injectors to explicitly bind Just-In-Time bindings
            // in order to force them to stay on the child injector instead of the parent.  If they
            // go to the parent, the event listeners dont' get run :(
            bind(ContextObserverTesterImpl.class);
        }
    }
}
