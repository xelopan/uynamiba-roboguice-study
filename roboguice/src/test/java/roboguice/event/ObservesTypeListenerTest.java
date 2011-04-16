package roboguice.event;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;

import android.app.Application;

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
    protected Application app;
    protected Injector injector;
    protected List<Method> eventOneMethods;
    protected List<Method> eventTwoMethods;

    @Before
    public void setup() throws NoSuchMethodException {
        app = Robolectric.application;
        injector = RoboGuice.getRootInjector(app);

        eventManager = injector.getInstance(EventManager.class);

        eventOneMethods = ContextObserverTesterImpl.getMethods(EventOne.class);
        eventTwoMethods = ContextObserverTesterImpl.getMethods(EventTwo.class);

    }

    @Test
    public void simulateInjection() {
        final InjectedTestClass testClass = new InjectedTestClass();
        injector.injectMembers(testClass);

        eventManager.fire(new EventOne());

        testClass.getTester().verifyCallCount(eventOneMethods, EventOne.class, 1);
        testClass.getTester().verifyCallCount(eventTwoMethods, EventTwo.class, 0);
    }

    @Test(expected = RuntimeException.class)
    public void invalidObservesMethodSignature(){
        injector.getInstance(MalformedObserves.class);
    }

    static public class InjectedTestClass{
        @Inject
        public ContextObserverTesterImpl tester;

        public ContextObserverTesterImpl getTester() {
            return tester;
        }
    }

    public class MalformedObserves{
        public void malformedObserves(int val, @Observes EventOne event){}
    }
}
