package roboguice.activity;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.config.DefaultApplicationRoboModule;
import roboguice.config.DefaultContextRoboModule;
import roboguice.inject.*;

import android.R;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ActivityInjectionTest {

    protected DummyActivity activity;
    protected DummyPreferenceActivity prefsActivity;

    @Before
    public void setup() {
        RoboGuice.util.clearAllInjectors();
        RoboGuice.createAndBindNewRootInjector(Robolectric.application, new MyApplicationModule(Robolectric.application));
        activity = new DummyActivity();
        activity.setIntent(new Intent(Robolectric.application, DummyActivity.class).putExtra("foobar", "goober").putExtra("json", "{ 'x':'y'}"));
        RoboGuice.createAndBindNewContextInjector(activity, new MyContextModule(activity));
        activity.onCreate(null);

        prefsActivity = new DummyPreferenceActivity();
        prefsActivity.onCreate(null);
    }

    @Test
    public void shouldInjectUsingDefaultConstructor() {
        assertThat(activity.emptyString,is(""));
    }

    @Test
    public void shouldInjectView() {
        assertThat(activity.text1,is(activity.findViewById(R.id.text1)));
    }

    @Test
    public void shouldInjectStringResource() {
        assertThat(activity.cancel,is("Cancel"));
    }

    @Test
    public void shouldInjectStaticStringResource() {
        assertThat(activity.staticCancel,is("Cancel"));
    }

    @Test
    public void shouldInjectExtras() {
        assertThat(activity.foobar,is("goober"));
    }

    @Test
    public void shouldInjectExtrasIntoPojosToo() {
        assertThat(activity.someDumbObject.foobar,is("goober"));
    }

    @Test
    public void shouldInjectJsonExtras() {
        assertThat(activity.json.get("x").getAsString(), is("y"));
    }

    // BUG This doesn't work yet because createNewPreferenceScreen doesn't properly model whatever's goign on
    @Test
    @Ignore
    public void shouldInjectPreference() {
        assertThat(prefsActivity.pref, is(prefsActivity.findPreference("xxx")));
    }


    public static class DummyActivity extends RoboActivity {
        @InjectResource(R.string.cancel) protected static String staticCancel;

        @Inject protected String emptyString;
        @InjectView(R.id.text1) protected TextView text1;
        @InjectResource(R.string.cancel) protected String cancel;
        @InjectExtra("foobar") protected String foobar;
        @InjectExtra("json") protected JsonObject json;
        @Inject protected SomeDumbObject someDumbObject;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final TextView root = new TextView(this);
            root.setId(R.id.text1);                       
            setContentView(root);
        }
    }

    public static class DummyPreferenceActivity extends RoboPreferenceActivity {
        @Nullable @InjectPreference("xxx") protected Preference pref;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final PreferenceScreen screen = createNewPreferenceScreen();

            final Preference p = new CheckBoxPreference(this);
            p.setKey("xxx");

            screen.addPreference(p);

            setPreferenceScreen(screen);
        }

        protected PreferenceScreen createNewPreferenceScreen() {

            try {
                final Constructor<PreferenceScreen> c = PreferenceScreen.class.getDeclaredConstructor();
                c.setAccessible(true);
                final PreferenceScreen screen = c.newInstance();

                /*
                final Method m = PreferenceScreen.class.getMethod("onAttachedToHierarchy");
                m.setAccessible(true);
                m.invoke(this);
                */

                return screen;
                
            } catch( Exception e ) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class SomeDumbObject {
        @InjectExtra("foobar") protected String foobar;
    }

    public static class MyApplicationModule extends DefaultApplicationRoboModule {

        public MyApplicationModule(Application application) {
            super(application);
        }

        @Override
        protected void configure() {
            super.configure();
            
            bind(new TypeLiteral<ExtraConverter<String,JsonObject>>(){}).toInstance(new ExtraConverter<String, JsonObject>() {
                @Override
                public JsonObject convert(String s) {
                    return new JsonParser().parse(s).getAsJsonObject();
                }
            });
        }
    }

    public static class MyContextModule extends DefaultContextRoboModule {

        public MyContextModule(Context context) {
            super(context);
        }

        @Override
        protected void configure() {
            super.configure();
            
            bind(SomeDumbObject.class);
        }
    }
}
