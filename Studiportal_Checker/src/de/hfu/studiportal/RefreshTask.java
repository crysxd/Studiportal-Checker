package de.hfu.studiportal;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.hfu.funfpunktnull.R;

public class RefreshTask extends AsyncTask<Void, Void, Exception> {

	private final String URL_BASE = "https://studi-portal.hs-furtwangen.de/";
	private final String URL_LOGIN = "https://studi-portal.hs-furtwangen.de/qisserver/rds?state=user&type=1&category=auth.login&startpage=portal.vm&breadCrumbSource=portal";
	private final String URL_LOGOUT = "https://studi-portal.hs-furtwangen.de/qisserver/rds?state=user&type=4&re=last&category=auth.logout&breadCrumbSource=portal";
	private final String URL_OBSERVE = "https://studi-portal.hs-furtwangen.de/qisserver/rds?state=htmlbesch&moduleParameter=Student&menuid=notenspiegel&breadcrumb=notenspiegel&breadCrumbSource=menu&asi=%s";

	private final String USER_NAME;
	private final String PASSWORD;
	private final Context CONTEXT;

	public RefreshTask(Context c, String userName, String password) {
		this.CONTEXT = c;
		this.USER_NAME = userName;
		this.PASSWORD = password;

	}

	public RefreshTask(Context c) {
		this.CONTEXT = c;

		SharedPreferences sp = this.getSharedPreferences();
		this.USER_NAME = sp.getString(this.getStringResource(R.string.preference_user), "");
		this.PASSWORD = sp.getString(this.getStringResource(R.string.preference_password), "");

	}

	@Override
	protected Exception doInBackground(Void... params) {
		//Declare Client and occuredException
		HttpClient client = null;
		Exception occuredException = null;

		//Try 3 times
		for(int i=0; i<3; i++) {
			try {
				//Create client
				client = new DefaultHttpClient();

				//Login
				this.showProgressDialog(this.getStringResource(R.string.text_logging_in));
				Log.i(this.getClass().getSimpleName(), this.getStringResource(R.string.text_logging_in));
				String asi = this.login(client);

				//Check for change
				this.showProgressDialog(this.getStringResource(R.string.text_checking_update));
				Log.i(this.getClass().getSimpleName(),this.getStringResource(R.string.text_checking_update));
				boolean changed = this.checkDataChange(client, asi);

				//If no change -> save a NoChnageException in occuredException
				if(!changed) {
					occuredException = new NoChangeException();
				}

				//No error -> cancel (no further trys)
				break;

			}  catch (Exception e) {
				//Something went wrong. Print stack trace and save
				e.printStackTrace();
				occuredException = e;

			} finally {
				//try to Log out
				try {
					this.showProgressDialog(this.getStringResource(R.string.text_logging_out));
					Log.i(this.getClass().getSimpleName(),this.getStringResource(R.string.text_logging_out));
					this.logout(client);

				} catch(Exception e) {
					e.printStackTrace();

				}
			}
		}

		//Return exception (should be null or NoChangeException)
		return occuredException;
	}

	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);

		Context c = this.getContext();
		if(c instanceof DialogHost) {
			((MainActivity) c).cancelProgressDialog();

			if(result instanceof NoChangeException || result instanceof LoginException) {
				((DialogHost) c).showErrorDialog(result);

			}
		} else if(result instanceof LoginException){
			this.notifyAboutError(result);


		}
	}


	private String login(HttpClient client) throws Exception {

		//Check Preference
		if(this.PASSWORD.length() == 0 || this.USER_NAME.length() == 0)
			throw new LoginException(this.getStringResource(R.string.exception_no_user_password));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("asdf", this.USER_NAME));
		nameValuePairs.add(new BasicNameValuePair("fdsa", this.PASSWORD));
		nameValuePairs.add(new BasicNameValuePair("submit", "Anmelden"));

		//Load page (aka log in)
		String response = this.sendPost(client, this.URL_LOGIN, nameValuePairs);

		//Login failed
		if(response.contains("Anmeldung fehlgeschlagen")) {
			Log.i(this.getClass().getSimpleName(), "Login failed.");

			throw new LoginException(getStringResource(R.string.exception_wrong_user_password));

		}

		//Find asi
		int start = response.indexOf(";asi=") + ";asi=".length();
		int end = response.indexOf("\"", start);

		return response.substring(start, end);
	}

	private boolean checkDataChange(HttpClient client, String asi) throws Exception {
		//Load new Hash
		int newHash = this.calculateHashFromOnline(client, asi);

		//Load old Hash
		int oldHash = getSharedPreferences().getInt(this.getStringResource(R.string.preference_last_hash), newHash);

		//Save new to be used as old next time
		getSharedPreferences().edit().putInt(this.getStringResource(R.string.preference_last_hash), newHash).apply();
		getSharedPreferences().edit().putLong(this.getStringResource(R.string.preference_last_check), System.currentTimeMillis()).apply();		

		//Compare
		boolean changed = oldHash != newHash;
		if(changed) {
			this.notifyAboutChange();
		}

		return changed;
	}

	private void logout(HttpClient client) throws Exception {
		this.sendGet(client, this.URL_LOGOUT);

	}

	private int calculateHashFromOnline(HttpClient client, String asi) throws Exception {
		String response = this.sendGet(client, String.format(this.URL_OBSERVE, asi));
		int start = response.indexOf("<table cellspacing=\"0\" cellpadding=\"5\" border=\"0\" align=\"center\" width=\"100%\">");
		int end = response.indexOf("</table>", start);

		return response.substring(start, end).hashCode();
	}

	private SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this.getContext());
	}

	private Context getContext() {
		return this.CONTEXT;

	}

	private String getStringResource(int id) {
		return this.getContext().getResources().getString(id);

	}

	private String sendPost(HttpClient client, String url, List<NameValuePair> params) throws Exception {
		// Create a new HttpClient and Post Header
		HttpPost httppost = new HttpPost(url);

		httppost.setEntity(new UrlEncodedFormEntity(params));

		// Execute HTTP Post Request
		HttpResponse response = client.execute(httppost);
		String responseString = new BasicResponseHandler().handleResponse(response);

		return responseString;

	}

	private String sendGet(HttpClient client, String url) throws Exception {
		// Create a new HttpClient and Post Header
		HttpGet httppost = new HttpGet(url);

		// Execute HTTP Post Request
		HttpResponse response = client.execute(httppost);
		String responseString = new BasicResponseHandler().handleResponse(response);

		return responseString;
	}

	private void showNotification(String title, String text, int id, Intent resultIntent) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this.getContext())
		.setSmallIcon(R.drawable.ic_notification)
		.setContentTitle(title)
		.setContentText(text);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getContext());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

		//LED
		mBuilder.setLights(Color.GREEN, 3000, 3000);

		//Ton
		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		NotificationManager mNotificationManager =
				(NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());

	}

	private void notifyAboutChange() {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(this.URL_BASE));
		this.showNotification(this.getStringResource(R.string.text_new_data_title), this.getStringResource(R.string.text_new_data_detail), 0, i);

	}

	private void notifyAboutError(Exception e) {
		this.showNotification(this.getStringResource(R.string.text_error), e.getMessage(), 1, new Intent(this.getContext(), MainActivity.class));

	}

	private void showProgressDialog(String text) {
		if(this.CONTEXT instanceof DialogHost) {
			((DialogHost) this.CONTEXT).showIndeterminateProgressDialog(this.getStringResource(R.string.text_refresh), text);

		}
	}
}
