package de.hfu.studiportal;

public interface DialogHost {
	
	public void showIndeterminateProgressDialog(String title, String text);
	public void showDialog(String title, String text);
	public void showErrorDialog(Exception e);
	public void cancelProgressDialog();

}
