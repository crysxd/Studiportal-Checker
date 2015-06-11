package de.hfu.studiportal.network;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import de.hfu.funfpunktnull.R;
import de.hfu.studiportal.view.LoginActivity;
import de.hfu.studiportal.view.MainActivity;

public class RefreshTaskStarter extends BroadcastReceiver {

	private static final String CHECK_FOR_UPDATES = "de.hfu.studiportal.CHECK_FOR_UPDATES";

	@Override
	public void onReceive(Context context, Intent intent) {
        //If the Broadcast CHECK_FOR_UPDATE arrives, let's check
		if (intent.getAction().equals(CHECK_FOR_UPDATES)) {
            //Only check if wifi is enabled or we are allowed to check over cellular
			if(getWifiManager(context).isWifiEnabled() || 
					getSharedPreferences(context).getBoolean(context.getResources().getString(R.string.preference_use_mobile), true)) {
				new RefreshTask(context).execute();

			} else {
                //Wifi is off or we are not allowed to use cellular. Set the overdue flag to signalised the upate is delayed
                getSharedPreferences(context).edit().putBoolean(context.getString(R.string.preference_refresh_is_overdue), true).commit();

            }
		} 

        //init after reboot
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) { 
			startRefreshTask(context);

		}

        //If the Network state changed and Wifi is now on and the last update is delayed -> update and reset overdue flag
        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) && getWifiManager(context).isWifiEnabled() &&
        getSharedPreferences(context).getBoolean(context.getResources().getString(R.string.preference_refresh_is_overdue), false)) {
            getSharedPreferences(context).edit().putBoolean(context.getString(R.string.preference_refresh_is_overdue), false).commit();
            new RefreshTask(context).execute();

        }
	}
	
	public static PendingIntent createPendingIntent(Context context) {
		Intent i = new Intent();
		i.setAction(CHECK_FOR_UPDATES);		
		PendingIntent update = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		return update;
	}
	
	public static void cancelRefreshTask(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent toCancel = createPendingIntent(context);
		toCancel.cancel();
		alarmManager.cancel(toCancel);
		
	}

	public static void startRefreshTask(Context context) {
		//Check if user and password is available, if not start Login
		SharedPreferences sp = getSharedPreferences(context);
		String user = sp.getString(context.getResources().getString(R.string.preference_user), "");
		String password = sp.getString(context.getResources().getString(R.string.preference_password), "");
		if((user.length() == 0 || password.length() == 0) && context instanceof Activity) {
			Intent i = new Intent(context, LoginActivity.class);
			i.putExtra(context.getResources().getString(R.string.extra_start_on_success), new Intent(context, MainActivity.class));
			context.startActivity(i);

			//Quit the old Activity to prevent going back
			((Activity) context).finish();

			return;

		}
		
		//If the polling time less than the minimum, reset it to the minimum minutes
        String minValue = context.getResources().getStringArray(R.array.array_refresh_time_key)[0];
		if(TimeUnit.MINUTES.convert(getPauseTime(context), TimeUnit.MILLISECONDS) < Integer.parseInt(minValue)) {
			sp.edit().putString(context.getString(R.string.preference_refresh_rate), minValue).apply();
		}

		//Everything ok, start service
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), getPauseTime(context), createPendingIntent(context));

	}

	private static long getPauseTime(Context con) {
		int minutes = Integer.valueOf(getSharedPreferences(con).getString(con.getResources().getString(R.string.preference_refresh_rate), "60"));

		return TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
	}

	private WifiManager getWifiManager(Context con) {
		return (WifiManager) con.getSystemService(Context.WIFI_SERVICE);

	}
	
	private static SharedPreferences getSharedPreferences(Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con);
	}

}
