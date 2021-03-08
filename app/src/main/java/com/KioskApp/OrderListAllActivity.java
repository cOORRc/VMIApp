package com.KioskApp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.KioskApp.ui.hideSystemUI;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.KioskApp.adapter.AllOrderListAdapter;
import com.KioskApp.model.DataOrder;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderListAllActivity extends AppCompatActivity {
	/* SharedPreferences */
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	/* key SharedPreferences */
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_PART_CUS = "cus";
	final String USER_BOM = "Bom";
	final String USER_TYPE_USAGE = "type_usage";

	/* Request to connect server for get json */
	private RequestQueue OrderQueue;
	private String status_id = "0";

	/* variables ui */
	private String order_all_tx_username;
	private String order_all_tx_project_name;
	private String order_all_tx_bom;
	private String order_all_tx_type_usage;
	private TextView order_all_tv_title_cus_service, order_all_tv_gdj_vmi_system, order_all_tv_customer,
			order_all_tv_title, order_all_tv_customer_name;
	private Button order_all_bt_home;
	private Button order_all_bt_del;
	private Button order_all_bt_con;
	private TextView order_all_tv_datetime, order_all_tv_ddmmyy;

	/* variables recyclerView */
	private RecyclerView order_all_rv_itemMenu;
	private ArrayList<DataOrder> order_list_all;
	private AllOrderListAdapter order_adapter;

	/* variables dialog qr code */
	private Dialog Dialog_qr;

	/* variables progress */
	private ProgressDialog order_all_progDi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_list_all);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //set screen portrait
		hideSystemUI.hideNavigations(this); // hide navigation

		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);// ชื่อไฟล์ที่จะทำการเก็บ, รูปแบบการจัดเก็บไฟล์
		order_all_tx_username = sp.getString(USER_NAME, ""); // ดึงข้อมูลในไฟล์
		order_all_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		order_all_tx_type_usage = sp.getString(USER_TYPE_USAGE,"");
		order_all_tx_bom = sp.getString(USER_BOM, "");

		OrderQueue = Volley.newRequestQueue(this);
		init_snpCom();
		Usage_Data_MyCart();
	}
	/* set view */
	private void init_snpCom() {
		order_all_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		order_all_tv_title_cus_service.setText(R.string.tx_cus_service);
		order_all_tv_title_cus_service.setVisibility(View.VISIBLE);
		order_all_tv_title_cus_service.setTextSize(32);
		order_all_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		order_all_tv_customer = findViewById(R.id.tv_customer);
		order_all_tv_customer_name = findViewById(R.id.tv_customer_name);
		order_all_tv_customer_name.setText(order_all_tx_username);
		order_all_tv_datetime = findViewById(R.id.tv_datetime);
		order_all_tv_ddmmyy = findViewById(R.id.tv_time);
		/*set time*/
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
								order_all_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();

		order_all_bt_home = findViewById(R.id.bt_home);
		order_all_bt_home.setVisibility(View.VISIBLE);
		order_all_bt_home.setText(R.string.tx_home);
		order_all_tv_title = findViewById(R.id.order_set_tv_title);
		order_all_tv_title.setText(R.string.tx_part_number_list);
		order_all_rv_itemMenu = findViewById(R.id.order_set_item_menu);
		order_all_rv_itemMenu.setHasFixedSize(false);
		order_all_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
		order_list_all = new ArrayList<DataOrder>();
		order_all_bt_con = findViewById(R.id.bt_con);
		order_all_bt_del = findViewById(R.id.order_la_bt_del);
		order_all_bt_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog di_del = new Dialog(OrderListAllActivity.this);
				di_del.setContentView(R.layout.layout_alert_zero);
				di_del.setCancelable(false);
				TextView tx_en = di_del.findViewById(R.id.al3_tv_detailEN);
				tx_en.setText(R.string.tx_del_en);
				TextView tx_th = di_del.findViewById(R.id.al3_tv_detailTH);
				tx_th.setText(R.string.tx_del_th);
				Button al_close = di_del.findViewById(R.id.al3_bt_close);
				al_close.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						di_del.dismiss();
					}
				});
				Button confirm_bt_yes = di_del.findViewById(R.id.al3_bt_yes);
				confirm_bt_yes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						callApi_Del();
						di_del.dismiss();
					}
				});
				Button confirm_bt_no = di_del.findViewById(R.id.al3_bt_no);
				confirm_bt_no.setVisibility(View.VISIBLE);
				confirm_bt_no.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						di_del.dismiss();
					}
				});
				di_del.show();
			}
		});
	}

	/*create progress*/
	private void start_progressOrder() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
			}
		}, 1000);
		order_all_progDi = new ProgressDialog(OrderListAllActivity.this, R.style.Dialg);
		order_all_progDi.setMessage("Loading..."); // Setting Message
		order_all_progDi.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		order_all_progDi.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		order_all_progDi.show(); // Display Progress Dialog
		order_all_progDi.setCancelable(false);
	}

	/* call api for get data in cart  (order list)*/
	private void Usage_Data_MyCart() {
		start_progressOrder();
		String urlMyCart = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Data_MyCart.php";
		String Project_Name = order_all_tx_project_name;
		HashMap<String, String> hashMap_MyCart = new HashMap<>();
		hashMap_MyCart.put("Project_Name", Project_Name);
		JSONObject data_MyCart = new JSONObject(hashMap_MyCart);
		JsonObjectRequest request_partNum = new JsonObjectRequest(urlMyCart, data_MyCart, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSet = new JSONObject(String.valueOf(response));
					status_id = jsonSet.getString("Status");
					JSONArray ja_OrderResultData = jsonSet.getJSONArray("ResultData");
					ArrayList<String> ar_resultItem = new ArrayList<String>();
					for (int i = 0; i < ja_OrderResultData.length(); i++) {
						JSONObject order_resultItem = ja_OrderResultData.getJSONObject(i);
						String pre_fg_code_gdj_item = order_resultItem.getString("pre_fg_code_gdj");
						String pre_sku_code_abt_item = order_resultItem.getString("pre_sku_code_abt");
						String sum_pre_qty_item = order_resultItem.getString("sum_pre_qty");
						String sum_pre_pack_item = order_resultItem.getString("sum_pre_pack");
						String oty_total_item = order_resultItem.getString("Qty_Total");
						order_list_all.add(new DataOrder(sum_pre_qty_item, pre_fg_code_gdj_item,
								pre_sku_code_abt_item, oty_total_item));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (status_id.equals("1")) {
					order_all_progDi.dismiss();
					order_all_bt_con.setEnabled(true);
					order_all_bt_con.setTextColor(Color.WHITE);
					order_all_bt_con.setBackgroundResource(R.drawable.button_selector);
					order_adapter = new AllOrderListAdapter(OrderListAllActivity.this, order_list_all);
					order_all_rv_itemMenu.setAdapter(order_adapter);
				} else {
					order_all_progDi.dismiss();
					final Dialog di_no_num_cart = new Dialog(OrderListAllActivity.this);
					di_no_num_cart.setContentView(R.layout.layout_alert_zero);
					di_no_num_cart.setCancelable(false);
					TextView no_num_tx_en = di_no_num_cart.findViewById(R.id.al3_tv_detailEN);
					no_num_tx_en.setText(R.string.tx_no_data_en);
					TextView no_num_tx_th = di_no_num_cart.findViewById(R.id.al3_tv_detailTH);
					no_num_tx_th.setText(R.string.tx_no_data_th);
					Button no_num_bt_close = di_no_num_cart.findViewById(R.id.al3_bt_close);
					no_num_bt_close.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(OrderListAllActivity.this, CenterActivity.class));
							di_no_num_cart.dismiss();
						}
					});
					Button no_num_confirm_bt_yes = di_no_num_cart.findViewById(R.id.al3_bt_yes);
					no_num_confirm_bt_yes.setText(R.string.tx_accept);
					no_num_confirm_bt_yes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getApplicationContext(), CenterActivity.class));
							di_no_num_cart.dismiss();
						}
					});
					di_no_num_cart.show();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				order_all_progDi.dismiss();
				error.printStackTrace();
			}
		});
		OrderQueue.add(request_partNum);
	}

	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_home) {
			startActivity(new Intent(getApplicationContext(), CenterActivity.class));
		}
	}

	/* create dialog*/
	public void onClickConfirm(View view) {
		Log.i("TAG", "count goods on cart");
		mToastRunnable.run();
		Log.i("TAG", "alert QR");
		Dialog_qr = new Dialog(OrderListAllActivity.this);
		Dialog_qr.setContentView(R.layout.layout_alert_qr);
		Dialog_qr.setCancelable(false); // dismiss when touching outside Dialog
		TextView al_tv_title_qr_code = Dialog_qr.findViewById(R.id.alert_tv_title_qr_code);
		al_tv_title_qr_code.setText(R.string.tx_usage_confirm);
		ImageView al_img_qr = (ImageView) Dialog_qr.findViewById(R.id.alert_img_qr_code);
		TextView al_tv_ple = (TextView) Dialog_qr.findViewById(R.id.alert_tv_ple_qr_code);
		al_tv_ple.setText(R.string.tx_pls_scan_qr);
		final TextView al_tv_time = (TextView) Dialog_qr.findViewById(R.id.alert_tv_time);
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter(); //create qr code
		try {
			BitMatrix bitMatrix = multiFormatWriter.encode(order_all_tx_project_name, BarcodeFormat.QR_CODE, 400, 400);
			BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
			Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
			al_img_qr.setImageBitmap(bitmap);
			al_img_qr.setPadding(0,-10,0,-10);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		Dialog_qr.show();
	}

	public void onClickClose(View view) {
		if (view.getId() == R.id.alert_bt_close_confirm){
			mHandler.removeCallbacks(mToastRunnable);
			Dialog_qr.dismiss();
		}
	}
	/* variables json for del data*/
	private String sta_idDel;
	/* call api for delete data (order list) */
	private void callApi_Del() {
		String urlMyCart = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Del_Pre.php";
		String Project_Name = order_all_tx_project_name;
		HashMap<String, String> hashMap_MyCart = new HashMap<>();
		hashMap_MyCart.put("Project_Name", Project_Name);
		JSONObject data_MyCart = new JSONObject(hashMap_MyCart);
		JsonObjectRequest request_partNum = new JsonObjectRequest(urlMyCart, data_MyCart, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSet = new JSONObject(String.valueOf(response));
					String status_idDel = jsonSet.getString("Status");
					String ja_resultDataDel = jsonSet.getString("Status_Text");
					sta_idDel = status_idDel;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (sta_idDel.equals("1")) {
					clearSharedPreferences();
					startActivity(new Intent(getApplicationContext(), CenterActivity.class));
				} else {
					startActivity(new Intent(getApplicationContext(), CenterActivity.class));
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		});
		OrderQueue.add(request_partNum);
	}

	/* clear data in SharedPre */
	private void clearSharedPreferences() {
		editor = sp.edit();
		editor.remove(USER_TYPE_USAGE);
		editor.remove(USER_BOM);
		editor.remove(USER_PART_CUS);
		editor.commit();
	}

	/*Handler for check data in a cart when user scan tags (done)
	*,dialog will close and back to centerActivity.page
	* */
	private Handler mHandler = new Handler();
	private Runnable mToastRunnable = new Runnable() {
		@Override
		public void run() {
			callApi_CheckMyCart();
			mHandler.postDelayed(this, 5000);
		}
	};

	/* variables json data (api:Usage_Qty_MyCart.php)*/
	private String sta_idcheckCart = "0";
	private int sta_tx_checkCart = 0;
	/* call api for check qty on cart */
	private void callApi_CheckMyCart() {
		String url_checkCart = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Qty_MyCart.php";
		String Project_Name = order_all_tx_project_name;
		HashMap<String, String> hashMap_checkCart = new HashMap<>();
		hashMap_checkCart.put("Project_Name", Project_Name);
		JSONObject data_checkCart = new JSONObject(hashMap_checkCart);
		JsonObjectRequest request_myCart = new JsonObjectRequest(url_checkCart, data_checkCart, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSNPcheckCart = new JSONObject(String.valueOf(response));
					String status_id = jsonSNPcheckCart.getString("Status");
					int resultData = jsonSNPcheckCart.getInt("ResultData");
					sta_idcheckCart = status_id;
					sta_tx_checkCart = resultData;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (sta_idcheckCart.equals("1")){
					if (sta_tx_checkCart==0){
						clearSharedPreferences();
						mHandler.removeCallbacks(mToastRunnable);
						Dialog_qr.dismiss();
						startActivity(new Intent(OrderListAllActivity.this, CenterActivity.class));
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mHandler.removeCallbacks(mToastRunnable);
				Dialog_qr.dismiss();
				error.printStackTrace();
			}
		});
		OrderQueue.add(request_myCart);
	}
}