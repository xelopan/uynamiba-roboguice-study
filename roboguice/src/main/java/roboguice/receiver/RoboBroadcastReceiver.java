package roboguice.receiver;

import roboguice.RoboGuice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class RoboBroadcastReceiver extends BroadcastReceiver {

    @Override
    public final void onReceive(Context context, Intent intent) {
        RoboGuice.getInjector(context);
    }

}
