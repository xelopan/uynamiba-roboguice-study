package roboguice.config;

import roboguice.inject.AssetManagerProvider;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SystemServiceProvider;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import roboguice.util.RoboThread;
import roboguice.util.Strings;

import android.app.*;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * A Module that provides bindings and configuration to use Guice on Android.
 * Used by {@link roboguice.RoboGuice}.
 *
 * @author Mike Burton
 */
public class DefaultApplicationRoboModule extends AbstractModule implements ApplicationModule {

    protected Application application;

    public DefaultApplicationRoboModule(final Application application) {
        this.application = application;
    }

    /**
     * Configure this module to define Android related bindings.
     */
    @Override
    protected void configure() {
        bind(Application.class).toInstance(application);

        final String androidId = Secure.getString(application.getContentResolver(), Secure.ANDROID_ID);

        if(Strings.notEmpty(androidId))
            bindConstant().annotatedWith(Names.named(Settings.Secure.ANDROID_ID)).to(androidId);

        // Package Info
        try {
            final PackageManager packageManager = application.getPackageManager(); // can be null in robolectric
            if( packageManager!=null )
                bind(PackageInfo.class).toInstance(packageManager.getPackageInfo(application.getPackageName(),0));
            
        } catch( PackageManager.NameNotFoundException e ) {
            throw new RuntimeException(e);
        }

        // System Services
        bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
        bind(LayoutInflater.class).toProvider(new SystemServiceProvider<LayoutInflater>(Context.LAYOUT_INFLATER_SERVICE));
        bind(ActivityManager.class).toProvider(new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        bind(PowerManager.class).toProvider(new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));
        bind(AlarmManager.class).toProvider(new SystemServiceProvider<AlarmManager>(Context.ALARM_SERVICE));
        bind(NotificationManager.class).toProvider(new SystemServiceProvider<NotificationManager>(Context.NOTIFICATION_SERVICE));
        bind(KeyguardManager.class).toProvider(new SystemServiceProvider<KeyguardManager>(Context.KEYGUARD_SERVICE));
        bind(SearchManager.class).toProvider(new SystemServiceProvider<SearchManager>(Context.SEARCH_SERVICE));
        bind(Vibrator.class).toProvider(new SystemServiceProvider<Vibrator>(Context.VIBRATOR_SERVICE));
        bind(ConnectivityManager.class).toProvider(new SystemServiceProvider<ConnectivityManager>(Context.CONNECTIVITY_SERVICE));
        bind(WifiManager.class).toProvider(new SystemServiceProvider<WifiManager>(Context.WIFI_SERVICE));
        bind(InputMethodManager.class).toProvider(new SystemServiceProvider<InputMethodManager>(Context.INPUT_METHOD_SERVICE));
        bind(SensorManager.class).toProvider( new SystemServiceProvider<SensorManager>(Context.SENSOR_SERVICE));
        bind(TelephonyManager.class).toProvider( new SystemServiceProvider<TelephonyManager>(Context.TELEPHONY_SERVICE));
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);


        requestStaticInjection(Ln.class);
        requestStaticInjection(RoboThread.class);
        requestStaticInjection(RoboAsyncTask.class);

    }

}
