package de.hfu.studiportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import de.hfu.funfpunktnull.R;

public class DialogHostImplementation extends Handler implements DialogHost {

	private final String KEY_TITLE = "message_title";
	private final String KEY_TEXT = "message_text";
	private final String KEY_MESSAGE_TYPE = "message_type_42";
	private final int MESSAGE_TYPE_SHOW_PROGRESS = 42;
	private final int MESSAGE_TYPE_HIDE_PROGRESS = 43;
	private final int MESSAGE_TYPE_SHOW_TEXT = 44;

	private final Context CONTEXT;
	private final ProgressDialog PROGRESS_DIALOG;

	public DialogHostImplementation(Context c) {
		this.CONTEXT = c;
		PROGRESS_DIALOG = new ProgressDialog(c);

	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		int type = msg.getData().getInt(this.KEY_MESSAGE_TYPE);
		String title = msg.getData().getString(this.KEY_TITLE);
		String text = msg.getData().getString(this.KEY_TEXT);

		if(type == this.MESSAGE_TYPE_SHOW_PROGRESS) {
			this.showIndeterminateProgressDialog0(title, text);

		}

		if(type == this.MESSAGE_TYPE_HIDE_PROGRESS) {
			this.cancelProgressDialog0();

		}

		if(type == this.MESSAGE_TYPE_SHOW_TEXT) {
			this.showDialog0(title, text);

		}

	}

	private void sendMessage(int messageType, String title, String text) {
		//Create message
		Message m = new Message();
		Bundle b = new Bundle();
		b.putInt(this.KEY_MESSAGE_TYPE, messageType);
		b.putString(this.KEY_TEXT, text);
		b.putString(this.KEY_TITLE, title);
		m.setData(b);

		this.sendMessage(m);
	}

	@Override
	public void showIndeterminateProgressDialog(String title, String text) {
		this.sendMessage(this.MESSAGE_TYPE_SHOW_PROGRESS, title, text);

	}

	private void showIndeterminateProgressDialog0(String title, String text) {
		PROGRESS_DIALOG.setTitle(title);
		PROGRESS_DIALOG.setMessage(text);
		PROGRESS_DIALOG.show();

	}

	@Override
	public void showDialog(String title, String text) {
		this.sendMessage(this.MESSAGE_TYPE_SHOW_TEXT, title, text);

	}

	private void showDialog0(String title, String text) {
		//Login is wrong
		AlertDialog.Builder builder = new AlertDialog.Builder(this.CONTEXT);
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
		this.showDialog("", e.getLocalizedMessage());

	}

	@Override
	public void cancelProgressDialog() {
		this.sendMessage(this.MESSAGE_TYPE_HIDE_PROGRESS, "", "");

	}

	private void cancelProgressDialog0() {
		this.PROGRESS_DIALOG.hide();

	}

}
