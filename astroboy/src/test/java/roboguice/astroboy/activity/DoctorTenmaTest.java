package roboguice.astroboy.activity;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import roboguice.RoboGuice;
import roboguice.astroboy.AstroboyApplicationModule;
import roboguice.astroboy.AstroboyModule;

import android.content.Intent;

import java.io.Serializable;

@RunWith(RobolectricTestRunner.class)
public class DoctorTenmaTest {


    @BeforeClass
    public static void setupClass() {
        // Because we mess with the root injector
        Robolectric.resetStaticState();
    }

    @After
    public void tearDownClass() {
        // Because we messed with the root injector
        Robolectric.resetStaticState();
    }



    @Before
    public void setup() {
        // BUG don't understand why this is necessary, seems like it should read the config from roboguice.xml
        RoboGuice.createAndBindNewRootInjector(Robolectric.application, new AstroboyApplicationModule(Robolectric.application));
    }

    @Test
    public void doctorTenmaShouldNotCrash() {
        // copied from Tobio
        final Intent intent = new Intent(Robolectric.application, DoctorTenma.class);
        intent.putExtra("nullExtra", (Serializable) null);
        intent.putExtra("nameExtra", "Atom");
        intent.putExtra("ageExtra", 3000L);
        intent.putExtra("timestampExtra", 1000L);
        intent.putExtra("timestampTwiceExtra", 1000);

        final DoctorTenma doctorTenma = new DoctorTenma();
        doctorTenma.setIntent(intent);

        RoboGuice.createAndBindNewContextInjector(doctorTenma,new AstroboyModule(doctorTenma));
        doctorTenma.onCreate(null);
    }

    @Test
    @Ignore
    public void astroPrefActivityShouldNotCrash() {
        final AstroPrefActivity astroPrefActivity = new AstroPrefActivity();
        RoboGuice.createAndBindNewContextInjector(astroPrefActivity,new AstroboyModule(astroPrefActivity));
        astroPrefActivity.onCreate(null);

    }
}
