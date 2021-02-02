package com.KioskApp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.KioskApp.adapter.OrderSetAdapter;
import com.KioskApp.model.DataIMCSet;
import com.KioskApp.ui.ItemOffsetDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class SpecialOrderSetActivity extends AppCompatActivity implements OrderSetAdapter.OnItemClickListener {

	private TextView speOrdSet_tv_title_cus_service, speOrdSet_tv_gdj_vmi_system,
			speOrdSet_tv_datetime, speOrdSet_tv_ddmmyy, speOrdSet_tv_customer,
			speOrdSet_tv_customer_name, speOrdSet_tv_emergency_set;
	private ImageView speOrdSet_img_logo_gdj;
	private Button speOrdSet_bt_back;

	String name_bom;

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_BOM = "Bom";
	String ord_set_sp_tx_username, ord_set_sp_tx_project_name;
	private RecyclerView speOrdSet_rv_itemMenu;
	private ArrayList<DataIMCSet> orderList;
	private OrderSetAdapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private RequestQueue mQueue;
	private String EXTRA_TEXT = "extra_tx";
	private ProgressDialog speOrdSet_prog_di;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special_order_set);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		editor = sp.edit();
		ord_set_sp_tx_username = sp.getString(USER_NAME, "");
		ord_set_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		mQueue = Volley.newRequestQueue(this);
		init();
		getAllItem();
	}

	private void init() {
		speOrdSet_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		speOrdSet_tv_title_cus_service.setText(R.string.tx_cus_service);
		speOrdSet_tv_title_cus_service.setVisibility(View.VISIBLE);
		speOrdSet_tv_title_cus_service.setTextSize(40);
		speOrdSet_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		speOrdSet_tv_customer = findViewById(R.id.tv_customer);
		speOrdSet_tv_customer_name = findViewById(R.id.tv_customer_name);
		speOrdSet_tv_customer_name.setText(ord_set_sp_tx_username);
		speOrdSet_img_logo_gdj = findViewById(R.id.img_logo);
		speOrdSet_bt_back = findViewById(R.id.bt_back);
		speOrdSet_bt_back.setVisibility(View.VISIBLE);
		speOrdSet_bt_back.setText(R.string.tx_back);
		speOrdSet_tv_emergency_set = findViewById(R.id.order_set_tv_title);
		speOrdSet_tv_emergency_set.setText(R.string.tx_order_emergency_set);
		speOrdSet_tv_datetime = findViewById(R.id.tv_datetime);
		speOrdSet_tv_ddmmyy = findViewById(R.id.tv_time);
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
								speOrdSet_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		speOrdSet_rv_itemMenu = findViewById(R.id.special_set_item_manu);
		speOrdSet_rv_itemMenu.setHasFixedSize(true);
		speOrdSet_rv_itemMenu.setLayoutManager(new GridLayoutManager(this, 2));
		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_padding);
		speOrdSet_rv_itemMenu.addItemDecoration(itemDecoration);
		orderList = new ArrayList<>();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

			}
		}, 1000);
		speOrdSet_prog_di = new ProgressDialog(SpecialOrderSetActivity.this, R.style.Dialg);
		speOrdSet_prog_di.setMessage("Loading..."); // Setting Message
		speOrdSet_prog_di.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		speOrdSet_prog_di.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		speOrdSet_prog_di.show(); // Display Progress Dialog
		speOrdSet_prog_di.setCancelable(false);
	}

	private void getAllItem() {
		String urlOrderSet = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/Part_fgCode.php";
		String User_Project_Name = ord_set_sp_tx_project_name;
		HashMap<String, String> par_OrderSet = new HashMap<>();
		par_OrderSet.put("User_Project_Name", User_Project_Name);
		JSONObject data_orderset = new JSONObject(par_OrderSet);
		JsonObjectRequest request = new JsonObjectRequest(urlOrderSet, data_orderset, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSet = new JSONObject(String.valueOf(response));
					String status_id = jsonSet.getString("Status");
					JSONArray ja_SetResultData = jsonSet.getJSONArray("ResultData");
					ArrayList<String> ar_resultItem = new ArrayList<String>();
					for (int i = 0; i < ja_SetResultData.length(); i++) {
						JSONObject menu_resulItem = ja_SetResultData.getJSONObject(i);
						String bom_item = menu_resulItem.getString("bom_ctn_code_normal");
						name_bom = bom_item;
						String url_imgItem = "https://lac-apps.albatrossthai.com/api/VMI/Image_GDJ/" + name_bom + ".png";
						orderList.add(new DataIMCSet(url_imgItem, bom_item));
					}
					speOrdSet_prog_di.dismiss();
					adapter = new OrderSetAdapter(SpecialOrderSetActivity.this, orderList);
					speOrdSet_rv_itemMenu.setAdapter(adapter);
					adapter.setOnItemClickListener(SpecialOrderSetActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				speOrdSet_prog_di.dismiss();
				error.printStackTrace();
			}
		});
		mQueue.add(request);
	}

	@Override
	public void onItemClick(int position, String tx_snp) {
		String data_snp_bom = tx_snp;
		editor.putString(USER_BOM, data_snp_bom);
		editor.apply();
		startActivity(new Intent(SpecialOrderSetActivity.this, SpecialOrderSNPSetActivity.class));
	}

	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_back) {
			startActivity(new Intent(getApplicationContext(), CenterActivity.class));
		}
	}
}