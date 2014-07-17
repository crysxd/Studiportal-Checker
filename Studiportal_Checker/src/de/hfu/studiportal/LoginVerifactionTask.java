package de.hfu.studiportal;

import android.content.Context;

public class LoginVerifactionTask extends RefreshTask {
	
	public LoginVerifactionTask(Context c, String userName, String password) {
		super(c, userName, password);
	}

	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);
		
		if(result == null || result instanceof NoChangeException) {
			if(this.getContext() instanceof LoginActivity)
				((LoginActivity) this.getContext()).saveEnteredData();
		}
	}

}
