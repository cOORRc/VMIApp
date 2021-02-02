package com.KioskApp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.KioskApp.ui.hideKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginKioskActivity extends AppCompatActivity {
	private TextView login_tv_welcome, login_tv_gdj_vmi_system, login_tv_datetime, login_tv_ddmmyy, login_tv_customer, login_tv_customer_name;
	private ImageView login_img_logo_gdj;
	private Button login_bt_back;

	private RequestQueue mQueue;
	private String str_id = "0";

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_TERMINAL_TYPE = "TerminalID";

	private EditText usernameEditText, passwordEditText;
	private Button loginButton;
	private String user;
	private String pass;
	private android.text.TextWatcher TextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			user = usernameEditText.getText().toString().trim();
			pass = passwordEditText.getText().toString().trim();
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!user.isEmpty() && !pass.isEmpty()) {
				loginButton.setEnabled(true);
				loginButton.setTextColor(Color.WHITE);
				loginButton.setBackgroundResource(R.drawable.button_selector);
			}
			else {
				loginButton.setEnabled(false);
				loginButton.setBackgroundResource(R.color.zxing_custom_viewfinder_mask);
			}

		}
	};

	ProgressDialog progressDialog;

	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_kios);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mQueue = Volley.newRequestQueue(this);
		// hidden keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		hideSystemUI.hideNavigations(this);
		hideKeyboard.setupUI(findViewById(R.id.container_login_kiosk), this);
		hideKeyboard.setupUI(findViewById(R.id.card_customer_service), this);
		init();
		usernameEditText = findViewById(R.id.et_username);
		usernameEditText.addTextChangedListener(TextWatcher);
		passwordEditText = findViewById(R.id.et_password);
		passwordEditText.addTextChangedListener(TextWatcher);
		loginButton = findViewById(R.id.bt_login);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Handler handler = new Handler();

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
//                        LoginHandler();
					}
				}, 1000);

				Log.e("TAG", "elog");
				progressDialog = new ProgressDialog(LoginKioskActivity.this, R.style.Dialg);
				progressDialog.setMessage("Loading..."); // Setting Message
				progressDialog.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
				progressDialog.show(); // Display Progress Dialog
				progressDialog.setCancelable(false);
				Log.e("TAG", "show log");

				LoginHandler();

			}
		});
	}

	private void init() {
		login_tv_welcome = findViewById(R.id.tv_welcome_to);
		login_tv_welcome.setText(R.string.tx_welcome);
		login_tv_welcome.setVisibility(View.VISIBLE);
		login_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		login_tv_customer = findViewById(R.id.tv_customer);
		login_tv_customer.setVisibility(View.GONE);
		login_tv_customer_name = findViewById(R.id.tv_customer_name);
		login_tv_customer_name.setVisibility(View.GONE);
		login_img_logo_gdj = findViewById(R.id.img_logo);
		login_tv_datetime = findViewById(R.id.tv_datetime);
		login_tv_ddmmyy = findViewById(R.id.tv_time);
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
								login_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}


	private void LoginHandler() {
		String url = "https://lac-apps.albatrossthai.com/api/VMI/php/Login/Login_Kiosk.php";
		String Login_User = user;
		String Login_Password = pass;
		HashMap<String, String> par_login = new HashMap<>();
		par_login.put("Login_User", Login_User);
		par_login.put("Login_Password", Login_Password);
		JSONObject data_login = new JSONObject(par_login);
		JsonObjectRequest request = new JsonObjectRequest(url, data_login, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject api_jsonObj = new JSONObject(String.valueOf(response));
					String status_id = api_jsonObj.getString("Status");
					JSONArray ja_ResultData = api_jsonObj.getJSONArray("ResultData");
					ArrayList<String> ar_resultData = new ArrayList<String>();
					for (int i = 0; i < ja_ResultData.length(); i++) {
						JSONObject unpack_resulData = ja_ResultData.getJSONObject(i);
						String user_code = unpack_resulData.getString("cus_code");
						String user_name_en = unpack_resulData.getString("cus_name_en");
						String user_with_bom_pj_name = unpack_resulData.getString("cus_with_bom_pj_name");
						String user_terminal_type = unpack_resulData.getString("cus_terminal_type");
						ar_resultData.add(user_code);
						ar_resultData.add(user_name_en);
						ar_resultData.add(user_with_bom_pj_name);
						ar_resultData.add(user_terminal_type);
					}
					String str_user_code = ar_resultData.get(0);
					String str_user_name_en = ar_resultData.get(1);
					String str_user_bom_pj_name = ar_resultData.get(2);
					String str_user_terminal_type = ar_resultData.get(3);

					str_id = status_id;
					sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
					editor = sp.edit();
					editor.putString(USER_NAME, str_user_code);
					editor.putString(USER_PROJECT_NAME, str_user_bom_pj_name);
					editor.putString(USER_TERMINAL_TYPE, str_user_terminal_type);
					editor.apply();

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (str_id.equals("1")) {
					progressDialog.dismiss();
					Intent center_menu = new Intent(getApplicationContext(), CenterActivity.class);
					setResult(RESULT_OK, center_menu);
					startActivity(center_menu);
				} else {
					progressDialog.dismiss();
					final Dialog di_sta = new Dialog(LoginKioskActivity.this);
					di_sta.setContentView(R.layout.layout_alert_login);
					di_sta.setCancelable(false);
					Button bt_close = di_sta.findViewById(R.id.al_log_bt_close);
					bt_close.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							di_sta.dismiss();
						}
					});
					Button bt_acc = di_sta.findViewById(R.id.al_log_bt_acc);
					bt_acc.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							di_sta.dismiss();
						}
					});
					di_sta.show();
				}
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				progressDialog.dismiss();
				error.printStackTrace();
			}
		});
		mQueue.add(request);

	}
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() { }

}