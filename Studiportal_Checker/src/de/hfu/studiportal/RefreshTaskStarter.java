package de.hfu.studiportal;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import de.hfu.funfpunktnull.R;

public class RefreshTaskStarter extends BroadcastReceiver {

	private static final String CHECK_FOR_UPDATES = "de.hfu.studiportal.CHECK_FOR_UPDATES";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(CHECK_FOR_UPDATES)) {
			if(getWifiManager(context).isWifiEnabled() || 
					getSharedPreferences(context).getBoolean(context.getResources().getString(R.string.preference_use_mobile), false)) {
				new RefreshTask(context).execute();

			}
		} 
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) { 
			startRefreshTask(context);

		}
	}
	
	static PendingIntent createPendingIntent(Context context) {
		Intent i = new Intent();
		i.setAction(CHECK_FOR_UPDATES);		
		PendingIntent update = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		return update;
	}
	
	static void cancelRefreshTask(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent toCancel = createPendingIntent(context);
		toCancel.cancel();
		alarmManager.cancel(toCancel);
		
	}

	static void startRefreshTask(Context context) {
		//Check if user and password is available, if not start Login
		SharedPreferences sp = getSharedPreferences(context);
		String user = sp.getString(context.getResources().getString(R.string.preference_user), "");
		String password = sp.getString(context.getResources().getString(R.string.preference_password), "");
		if(user.length() == 0 || password.length() == 0) {
			Intent i = new Intent(context, LoginActivity.class);
			i.putExtra(context.getResources().getString(R.string.extra_start_on_success), new Intent(context, MainActivity.class));
			context.startActivity(i);

			//Quit the old Activity to prevent going back
			if(context instanceof Activity)
				((Activity) context).finish();

			return;

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
