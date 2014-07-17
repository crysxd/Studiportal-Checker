package de.hfu.studiportal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.hfu.funfpunktnull.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class RefreshService extends Service implements Runnable {

	private final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.EXECUTOR.execute(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	
		this.EXECUTOR.shutdownNow();
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			
			if(this.getWifiManager().isWifiEnabled() || 
					getSharedPreferences().getBoolean(getResources().getString(R.string.preference_use_mobile), false)) {
				new RefreshTask(this).execute();

			}
			
			try {
				Thread.sleep(getPauseTime());

			} catch(Exception e) {
				break;
				
			}
			
		}
		
	}
	
	private long getPauseTime() {
		int minutes = Integer.valueOf(getSharedPreferences().getString(getResources().getString(R.string.preference_refresh_rate), "60"));
		
		return TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
	}
	
	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	private WifiManager getWifiManager() {
		return (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
	}


}
