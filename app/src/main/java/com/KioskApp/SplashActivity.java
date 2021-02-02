package com.KioskApp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (isConnected()) {
//			mHandler.removeCallbacks(mToastRunnable);
			Intent login_page = new Intent(getApplicationContext(), LoginKioskActivity.class);
			startActivity(login_page);
			finish();
		} else {
			Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG);
			snackbar.setActionTextColor(Color.BLUE);
			snackbar.getView().setBackgroundColor(Color.RED);
			snackbar.show();
		}
/*		Thread thread = new Thread(){
			@Override
			public void run() {
				try {
					while (!isInterrupted()){
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(SplashActivity.this, "test",Toast.LENGTH_SHORT).show();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();*/
	}

	private Handler mHandler = new Handler();
	private Runnable mToastRunnable = new Runnable() {
		@Override
		public void run() {
			if (isConnected()) {
				mHandler.removeCallbacks(mToastRunnable);
				Intent login_page = new Intent(getApplicationContext(), LoginKioskActivity.class);
				startActivity(login_page);
				finish();
			} else {
				Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG);
				snackbar.setActionTextColor(Color.BLUE);
				snackbar.getView().setBackgroundColor(Color.RED);
				snackbar.show();
			}
			mHandler.postDelayed(this, 5000);
		}
	};
	public boolean isConnected() {
		boolean connected = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nInfo = cm.getActiveNetworkInfo();
			connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
			return connected;
		} catch (Exception e) {
			Log.e("Connectivity Exception", e.getMessage());
		}
		return false;
	}
}