package roboguice.astroboy.activity;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
public class DoctorTenmaTest {

    protected DoctorTenma doctorTenma;

    @Before
    public void setup() {
        doctorTenma = new DoctorTenma();
        doctorTenma.onCreate(null);
    }

    @Test
    public void inProgress() {
        
    }
}
