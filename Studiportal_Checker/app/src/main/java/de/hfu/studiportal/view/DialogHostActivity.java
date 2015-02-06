package de.hfu.studiportal.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import de.hfu.funfpunktnull.R;

@SuppressLint("Registered")
public abstract class DialogHostActivity extends ActionBarActivity implements DialogHost {

	private ProgressDialog progressDialog;
	private MessageHandler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		handler = new MessageHandler(this);

	}

	private void sendMessage(int messageType, String title, String text) {
		//Create message
		Message m = new Message();
		Bundle b = new Bundle();
		b.putInt(handler.KEY_MESSAGE_TYPE, messageType);
		b.putString(handler.KEY_TEXT, text);
		b.putString(handler.KEY_TITLE, title);
		m.setData(b);

		handler.sendMessage(m);
	}

	@Override
	public void showIndeterminateProgressDialog(String title, String text) {
		this.sendMessage(handler.MESSAGE_TYPE_SHOW_PROGRESS, title, text);

	}

	private void showIndeterminateProgressDialog0(String title, String text) {
		progressDialog.setTitle(title);
		progressDialog.setMessage(text);
		progressDialog.show();

	}

	@Override
	public void showDialog(String title, String text) {
		this.sendMessage(handler.MESSAGE_TYPE_SHOW_TEXT, title, text);

	}

	private void showDialog0(String title, String text) {
		//Login is wrong
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(text)
		.setTitle(title)
		.setPositiveButton(R.string.text_close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// FIRE ZE MISSILES!
			}
		});

		// Create the AlertDialog object and return it
		builder.create().show();

	}

	@Override
	public void showErrorDialog(Exception e) {
		this.showDialog(getString(R.string.text_error), getString(R.string.exception_general));

	}

	@Override
	public void cancelProgressDialog() {
		this.sendMessage(handler.MESSAGE_TYPE_HIDE_PROGRESS, "", "");

	}

	private void cancelProgressDialog0() {
		this.progressDialog.hide();

	}

	public void dismiss() {
		this.progressDialog.dismiss();

	}
	
	private static class MessageHandler extends Handler {
		private final String KEY_TITLE = "message_title";
		private final String KEY_TEXT = "message_text";
		private final String KEY_MESSAGE_TYPE = "message_type_42";
		private final int MESSAGE_TYPE_SHOW_PROGRESS = 42;
		private final int MESSAGE_TYPE_HIDE_PROGRESS = 43;
		private final int MESSAGE_TYPE_SHOW_TEXT = 44;
		
		private final DialogHostActivity MY_ACTIVITY;
		
		public MessageHandler(DialogHostActivity myActivity) {
			this.MY_ACTIVITY = myActivity;
			
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int type = msg.getData().getInt(KEY_MESSAGE_TYPE);
			String title = msg.getData().getString(KEY_TITLE);
			String text = msg.getData().getString(KEY_TEXT);

			if(type == MESSAGE_TYPE_SHOW_PROGRESS) {
				this.MY_ACTIVITY.showIndeterminateProgressDialog0(title, text);

			}

			if(type == MESSAGE_TYPE_HIDE_PROGRESS) {
				this.MY_ACTIVITY.cancelProgressDialog0();

			}

			if(type == MESSAGE_TYPE_SHOW_TEXT) {
				this.MY_ACTIVITY.showDialog0(title, text);

			}

		}
	}

}
