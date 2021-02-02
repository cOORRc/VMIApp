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

public class SpecialOrderComponentActivity extends AppCompatActivity implements OrderSetAdapter.OnItemClickListener {

	private TextView speOrdCom_tv_title_cus_service, speOrdCom_tv_gdj_vmi_system, speOrdCom_tv_datetime, speOrdCom_tv_ddmmyy, speOrdCom_tv_customer, speOrdCom_tv_customer_name, speOrdCom_tv_emergency_set;
	private ImageView speOrdCom_img_logo_gdj;
	private Button speOrdCom_bt_back;
	private RecyclerView speOrdCom_rv_itemMenu;

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_BOM = "Bom";
	String ord_com_sp_tx_username, ord_com_sp_tx_project_name;


	private ArrayList<DataIMCSet> orderList;
	private OrderSetAdapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private RequestQueue comQueue;
	private String EXTRA_TEXT = "extra_tx";
	private ProgressDialog speOrdCom_prog_di;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special_order_set);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		ord_com_sp_tx_username = sp.getString(USER_NAME, "");
		ord_com_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		comQueue = Volley.newRequestQueue(this);

		init();
		getAllItem();

	}

	private void init() {
		speOrdCom_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		speOrdCom_tv_title_cus_service.setText(R.string.tx_cus_service);
		speOrdCom_tv_title_cus_service.setVisibility(View.VISIBLE);
		speOrdCom_tv_title_cus_service.setTextSize(40);
		speOrdCom_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		speOrdCom_tv_customer = findViewById(R.id.tv_customer);
		speOrdCom_tv_customer_name = findViewById(R.id.tv_customer_name);
		speOrdCom_tv_customer_name.setText(ord_com_sp_tx_username);
		speOrdCom_img_logo_gdj = findViewById(R.id.img_logo);
		speOrdCom_bt_back = findViewById(R.id.bt_back);
		speOrdCom_bt_back.setVisibility(View.VISIBLE);
		speOrdCom_bt_back.setText(R.string.tx_back);
		speOrdCom_tv_emergency_set = findViewById(R.id.order_set_tv_title);
		speOrdCom_tv_emergency_set.setText(R.string.tx_order_emergency_component);

		speOrdCom_tv_datetime = findViewById(R.id.tv_datetime);
		speOrdCom_tv_ddmmyy = findViewById(R.id.tv_time);
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
								speOrdCom_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		speOrdCom_rv_itemMenu = findViewById(R.id.special_set_item_manu);
		speOrdCom_rv_itemMenu.setHasFixedSize(true);
		speOrdCom_rv_itemMenu.setLayoutManager(new GridLayoutManager(this, 2));
		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_padding);
		speOrdCom_rv_itemMenu.addItemDecoration(itemDecoration);
		orderList = new ArrayList<>();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

			}
		}, 1000);
		speOrdCom_prog_di = new ProgressDialog(SpecialOrderComponentActivity.this, R.style.Dialg);
		speOrdCom_prog_di.setMessage("Loading..."); // Setting Message
		speOrdCom_prog_di.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		speOrdCom_prog_di.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		speOrdCom_prog_di.show(); // Display Progress Dialog
		speOrdCom_prog_di.setCancelable(false);
	}
	private void getAllItem() {
		String urlOrderCom = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/Part_fgCode.php";
		String User_Project_Name = ord_com_sp_tx_project_name;
		HashMap<String, String> par_OrderCom = new HashMap<>();
		par_OrderCom.put("User_Project_Name", User_Project_Name);
		JSONObject data_ordercom = new JSONObject(par_OrderCom);
		JsonObjectRequest request = new JsonObjectRequest(urlOrderCom, data_ordercom, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonCom = new JSONObject(String.valueOf(response));
					String status_id = jsonCom.getString("Status");
					JSONArray ja_ComResultData = jsonCom.getJSONArray("ResultData");
					ArrayList<String> ar_resultItem = new ArrayList<String>();
					for (int i = 0; i < ja_ComResultData.length(); i++) {
						JSONObject menu_resulItem = ja_ComResultData.getJSONObject(i);
						String bom_item = menu_resulItem.getString("bom_ctn_code_normal");
						String name_bom = bom_item;
						String url_imgItem = "https://lac-apps.albatrossthai.com/api/VMI/Image_GDJ/" + name_bom + ".png";
						orderList.add(new DataIMCSet(url_imgItem, bom_item));
					}
					speOrdCom_prog_di.dismiss();
					adapter = new OrderSetAdapter(SpecialOrderComponentActivity.this, orderList);
					speOrdCom_rv_itemMenu.setAdapter(adapter);
					adapter.setOnItemClickListener(SpecialOrderComponentActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				speOrdCom_prog_di.dismiss();
				error.printStackTrace();
			}
		});
		comQueue.add(request);
	}

	@Override
	public void onItemClick(int position, String tx_snp) {
		String data_snp_bom = tx_snp;
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(USER_BOM, data_snp_bom);
		editor.apply();
		startActivity(new Intent(SpecialOrderComponentActivity.this, SpecialOrderSNPComActivity.class));
	}

	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_back) {
			startActivity(new Intent(getApplicationContext(), CenterActivity.class));
		}
	}
}