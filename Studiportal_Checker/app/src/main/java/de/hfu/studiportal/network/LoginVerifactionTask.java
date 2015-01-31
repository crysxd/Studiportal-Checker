package de.hfu.studiportal.network;

import de.hfu.studiportal.view.LoginActivity;


public class LoginVerifactionTask extends RefreshTask {
	
	private final LoginActivity LOGIN_ACTIVITY;
	
	public LoginVerifactionTask(LoginActivity logAct, String userName, String password) {
		super(logAct, userName, password);
		this.LOGIN_ACTIVITY = logAct;
		
	}

	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);
		
		if(result == null || result instanceof NoChangeException) {
			this.LOGIN_ACTIVITY.saveEnteredData();
		}
	}

}
