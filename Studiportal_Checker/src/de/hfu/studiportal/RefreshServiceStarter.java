package de.hfu.studiportal;

import de.hfu.funfpunktnull.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RefreshServiceStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		startRefreshTask(context);
	}

	static void startRefreshTask(Context context) {
		if(isMyServiceRunning(RefreshService.class, context))
			return;
		
		//Check if user and password is available, if not start Login
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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
		Intent i = new Intent(context, RefreshService.class);
		context.startService(i);

	}

	private static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
