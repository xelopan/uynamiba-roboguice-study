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
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class DoctorTenmaTest {


    protected DoctorTenma doctorTenma;

    @Before
    public void setup() {
        // BUG don't understand why this is necessary, seems like it should read the config from roboguice.xml
        RoboGuice.util.clearAllInjectors();
        RoboGuice.createAndBindNewRootInjector(Robolectric.application, new AstroboyApplicationModule(Robolectric.application));

        // copied from Tobio
        final Intent intent = new Intent(Robolectric.application, DoctorTenma.class);
        intent.putExtra("nullExtra", (Serializable) null);
        intent.putExtra("nameExtra", "Atom");
        intent.putExtra("ageExtra", 3000L);
        intent.putExtra("timestampExtra", 1000L);
        intent.putExtra("timestampTwiceExtra", 1000);

        doctorTenma = new DoctorTenma();
        doctorTenma.setIntent(intent);

        RoboGuice.createAndBindNewContextInjector(doctorTenma,new AstroboyModule(doctorTenma));
        doctorTenma.onCreate(null);
    }

    @Test
    public void doctorTenmaShouldNotCrash() {
        // just run setup
    }

    @Test
    public void doctorTenmaVariousAssertions() {
        assertEquals(doctorTenma.prefs.getString("dummyPref", "la la la"), "la la la");
        assertEquals(doctorTenma.myDateExtra, new Date(0));

        assertNull(doctorTenma.nullInjectedMember);
        assertEquals("Atom", doctorTenma.nameExtra);
        assertEquals("Atom", doctorTenma.personFromExtra.getName());
        assertEquals(3000L, doctorTenma.personFromExtra.getAge().getTime());
        assertEquals("Atom", doctorTenma.personFromConvertedExtra.getName());
        assertEquals(1000L, doctorTenma.dateFromTimestampExtra.getTime());
        assertEquals(2000L, doctorTenma.dateFromTimestampTwiceExtra.getTime());

    }

    @Test
    @Ignore
    public void astroPrefActivityShouldNotCrash() {
        final AstroPrefActivity astroPrefActivity = new AstroPrefActivity();
        RoboGuice.createAndBindNewContextInjector(astroPrefActivity,new AstroboyModule(astroPrefActivity));
        astroPrefActivity.onCreate(null);

    }
}
