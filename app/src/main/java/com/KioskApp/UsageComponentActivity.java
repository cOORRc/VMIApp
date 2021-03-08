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

import com.KioskApp.ui.hideSystemUI;
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

public class UsageComponentActivity extends AppCompatActivity implements OrderSetAdapter.OnItemClickListener {
	/* variables ui */
	private TextView usage_com_tv_title_cus_service, usage_com_tv_gdj_vmi_system
			, usage_com_tv_datetime, usage_com_tv_ddmmyy, usage_com_tv_customer
			, usage_com_tv_customer_name, usage_com_tv_emergency_set;
	private ImageView usage_com_img_logo_gdj;
	private Button usage_com_bt_back;
	private RecyclerView usage_com_rv_itemMenu;
	private ProgressDialog usage_com_progDi;

	/* SharedPreferences */
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	/* key SharedPreferences */
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_BOM = "Bom";
	/* variables SharedPreferences */
	String ord_com_sp_tx_username, ord_com_sp_tx_project_name;

	/*set recyclerView*/
	private ArrayList<DataIMCSet> orderList;
	private OrderSetAdapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	/* Request to connect server for get json */
	private RequestQueue comQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_special_order_set);
		hideSystemUI.hideNavigations(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		ord_com_sp_tx_username = sp.getString(USER_NAME, "");
		ord_com_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		comQueue = Volley.newRequestQueue(this);
		init();
		getAllItem();
	}
/*set ui*/
	private void init() {
		usage_com_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		usage_com_tv_title_cus_service.setText(R.string.tx_cus_service);
		usage_com_tv_title_cus_service.setVisibility(View.VISIBLE);
		usage_com_tv_title_cus_service.setTextSize(32);
		usage_com_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		usage_com_tv_customer = findViewById(R.id.tv_customer);
		usage_com_tv_customer_name = findViewById(R.id.tv_customer_name);
		usage_com_tv_customer_name.setText(ord_com_sp_tx_username);
		usage_com_img_logo_gdj = findViewById(R.id.img_logo);
		usage_com_bt_back = findViewById(R.id.bt_back);
		usage_com_bt_back.setVisibility(View.VISIBLE);
		usage_com_bt_back.setText(R.string.tx_back);
		usage_com_tv_emergency_set = findViewById(R.id.order_set_tv_title);
		usage_com_tv_emergency_set.setText(R.string.tx_order_usageCon_component);
		usage_com_tv_datetime = findViewById(R.id.tv_datetime);
		usage_com_tv_ddmmyy = findViewById(R.id.tv_time);
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
								usage_com_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		/*set recyclerView*/
		usage_com_rv_itemMenu = findViewById(R.id.special_set_item_manu);
		usage_com_rv_itemMenu.setHasFixedSize(true);
		usage_com_rv_itemMenu.setLayoutManager(new GridLayoutManager(this, 2));
		ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_padding);
		usage_com_rv_itemMenu.addItemDecoration(itemDecoration);
		orderList = new ArrayList<>();
		/* set handler for progress*/
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

			}
		}, 1000);
		usage_com_progDi = new ProgressDialog(UsageComponentActivity.this, R.style.Dialg);
		usage_com_progDi.setMessage("Loading..."); // Setting Message
		usage_com_progDi.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		usage_com_progDi.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		usage_com_progDi.show(); // Display Progress Dialog
		usage_com_progDi.setCancelable(false);
	}

	/*call api for set menu(IMCxxx)*/
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
					usage_com_progDi.dismiss();
					adapter = new OrderSetAdapter(UsageComponentActivity.this, orderList);
					usage_com_rv_itemMenu.setAdapter(adapter);
					adapter.setOnItemClickListener(UsageComponentActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				usage_com_progDi.dismiss();
				error.printStackTrace();
			}
		});
		comQueue.add(request);
	}

	/*when click menu and keep carton code (IMCxxx)*/
	@Override
	public void onItemClick(int position, String tx_snp) {
		String data_snp_bom = tx_snp;
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		editor = sp.edit();
		editor.putString(USER_BOM, data_snp_bom);
		editor.apply();
		startActivity(new Intent(UsageComponentActivity.this, UsageSNPComActivity.class));
	}

	/*back button*/
	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_back) {
			startActivity(new Intent(getApplicationContext(), CenterActivity.class));
		}
	}
}