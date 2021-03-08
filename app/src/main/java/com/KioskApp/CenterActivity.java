package com.KioskApp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.KioskApp.ui.hideSystemUI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CenterActivity extends AppCompatActivity {
	/* variables ui */
	private TextView center_tv_welcome, center_tv_gdj_vmi_system, center_tv_datetime,
			center_tv_ddmmyy, center_tv_customer, center_tv_customer_name, center_tv_title_cus_service;
	private RelativeLayout center_relativelayout_bt_cus, center_relativelayout_bt_gdj;
	private Button center_bt_usage_confirm, center_bt_special_order, center_bt_stock_replenishment;
	private ImageView center_img_logo_gdj;
	private Button center_bt_back;
	
	/* SharedPreferences */
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	/* key SharedPreferences*/
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_TYPE_USAGE = "type_usage";
	/* variables SharedPre */
	String cen_sp_tx_username, cen_sp_tx_project_name;
	String cen_sp_str_type_usage = "str_type_usage";

	/* CountDown */
	private CountDownTimer cdt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		hideSystemUI.hideNavigations(this);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		editor = sp.edit();
		cen_sp_tx_username = sp.getString(USER_NAME, "");
		cen_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		cen_sp_str_type_usage = sp.getString(USER_TYPE_USAGE, "");
		init();
	}

	private void init() {
		center_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		center_tv_title_cus_service.setText(R.string.tx_cus_service);
		center_tv_title_cus_service.setVisibility(View.VISIBLE);
		center_tv_title_cus_service.setTextSize(32);
		center_tv_welcome = findViewById(R.id.tv_welcome_to);
		center_tv_welcome.setText(R.string.tx_welcome);
		center_tv_welcome.setVisibility(View.VISIBLE);
		center_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		center_tv_customer = findViewById(R.id.tv_customer);
		center_tv_customer_name = findViewById(R.id.tv_customer_name);
		center_tv_customer_name.setText(cen_sp_tx_username);
		center_img_logo_gdj = findViewById(R.id.img_logo);
		center_bt_back = findViewById(R.id.bt_back);
		center_bt_back.setVisibility(View.GONE);
		center_tv_datetime = findViewById(R.id.tv_datetime);
		center_tv_ddmmyy = findViewById(R.id.tv_time);
		Thread thread = new Thread(){
			@Override
			public void run() {
				try {
					while (!isInterrupted()){
						Thread.sleep(100);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Calendar calendar = Calendar.getInstance();
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
								String currenttime = simpleDateFormat.format(calendar.getTime());
								String currentdate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
								center_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		center_relativelayout_bt_cus = findViewById(R.id.layout_cen_bt_cus);
		center_bt_usage_confirm = findViewById(R.id.cen_bt_usage_confirm);
		center_bt_special_order = findViewById(R.id.cen_bt_special_order);
		center_relativelayout_bt_gdj = findViewById(R.id.layout_cen_bt_gdj);
		center_bt_stock_replenishment = findViewById(R.id.cen_bt_stock_replenishment);
	}

	/* click on button (usage confirm, special order)*/
	public void onClickCustomers(View view) {
		if (view.getId() == R.id.cen_bt_usage_confirm) {
			Button bt_usage = findViewById(R.id.cen_bt_usage_confirm);
			bt_usage.setTag("usage");
			String btTag = (String)bt_usage.getTag();
			dialog(btTag);

		}
		if (view.getId() == R.id.cen_bt_special_order) {
			Button bt_special = findViewById(R.id.cen_bt_special_order);
			bt_special.setTag("special");
			String btTag = (String)bt_special.getTag();
			dialog(btTag);
		}

	}

	private void dialog(String btTag) {
		Dialog dialog_choose = new Dialog(CenterActivity.this);  // create dialog
		dialog_choose.setContentView(R.layout.layout_alert_choose); // set view dialog
		dialog_choose.setCancelable(true);
		TextView tv_title = dialog_choose.findViewById(R.id.tv_title);
		Button bt_order_set = dialog_choose.findViewById(R.id.bt_order_set);
		Button bt_order_compo = dialog_choose.findViewById(R.id.bt_order_component);
		if (btTag.equals("usage")) {
			tv_title.setText(R.string.tx_usage_confirm); // set title dialog
			switch (cen_sp_str_type_usage) {   // set button, lock button
				case "Set":
					bt_order_set.setEnabled(true);
					bt_order_compo.setEnabled(false);
					break;
				case "Com":
					bt_order_set.setEnabled(false);
					bt_order_compo.setEnabled(true);
					break;
				default:
					bt_order_set.setEnabled(true);
					bt_order_compo.setEnabled(true);
					break;
			}
		} else {
			tv_title.setText(R.string.tx_special_order);
		}
		final String tx_title = tv_title.getText().toString();

		bt_order_set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tx_title.equals("Usage Confirm")){
					startActivity(new Intent(CenterActivity.this,UsageSetActivity.class));
				}else {
					startActivity(new Intent(CenterActivity.this,SpecialOrderSetActivity.class));
				}
			}
		});
		bt_order_compo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tx_title.equals("Usage Confirm")){
					startActivity(new Intent(CenterActivity.this,UsageComponentActivity.class));
				}else {
					startActivity(new Intent(CenterActivity.this,SpecialOrderComponentActivity.class));
				}
			}
		});
		dialog_choose.show();
	}

	/* on click replenishment button will create qr code */
	Dialog Dialog_qr;
	public void onClickDriver(View view) {
		if (view.getId() == R.id.cen_bt_stock_replenishment) {
			Dialog_qr = new Dialog(CenterActivity.this); // create dialog
			Dialog_qr.setContentView(R.layout.layout_alert_qr); // set view dialog
			Dialog_qr.setCancelable(false); // dismiss when touching outside Dialog
			ImageView al_img_qr = (ImageView) Dialog_qr.findViewById(R.id.alert_img_qr_code);
			TextView al_tv_ple = (TextView) Dialog_qr.findViewById(R.id.alert_tv_ple_qr_code);
			final TextView al_tv_time = (TextView) Dialog_qr.findViewById(R.id.alert_tv_time);
			MultiFormatWriter multiFormatWriter = new MultiFormatWriter(); // create qr code
			try {
				BitMatrix bitMatrix = multiFormatWriter.encode(cen_sp_tx_project_name
						, BarcodeFormat.QR_CODE, 400, 400);
				BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
				Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

				al_img_qr.setImageBitmap(bitmap); // set qr code on imageView
				cdt = new CountDownTimer(15000, 60) { // countdown for close dialog
					public void onTick(long millisUntilFinish) {
						String strTime = String.format("%.2f", (double) millisUntilFinish / 1000); // set time to close dialog
						al_tv_time.setText(strTime); // set on view
					}
					public void onFinish() {
						Dialog_qr.dismiss();
					}
				};
				cdt.start();
				Log.e("TAG", "bitmap != null : " + bitmap);
			} catch (WriterException e) {
				e.printStackTrace();
			}
			Dialog_qr.show();
		}
	}

/* close button on replenishment Dialog*/
	public void onClickClose(View view) {
		cdt.cancel();
		Dialog_qr.dismiss();
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(this, "Please wait a moment", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				sp.edit().clear().commit(); // clear sharedPre
				moveTaskToBack(true); // clear process
				startActivity(new Intent(CenterActivity.this, SplashActivity.class));
				finish(); // clear process
			}
		}, 1000);
	}

/* manual button */
	public void onClickHelp(View view) {
		startActivity(new Intent(CenterActivity.this, GuideActivity.class));
	}
}
