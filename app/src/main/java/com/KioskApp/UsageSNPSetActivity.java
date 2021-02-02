package com.KioskApp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.KioskApp.ui.hideKeyboard;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.KioskApp.adapter.AlertUsageSetAdapter;
import com.KioskApp.adapter.SNPOrderAdapter;
import com.KioskApp.adapter.SpinnerAdapter;
import com.KioskApp.model.DataAlertUsageSet;
import com.KioskApp.model.DataSNPSet;
import com.KioskApp.model.DataSpin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class UsageSNPSetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
																		SNPOrderAdapter.OnItemClickListener {

	private TextView usageSNPSet_tv_title_cus_service, usageSNPSet_tv_gdj_vmi_system, usageSNPSet_tv_customer, usageSNPSet_tv_customer_name, usageSNPSet_tv_emergency_set;
	private ImageView usageSNPSet_img_logo_gdj;
	private Button usageSNPSet_bt_cart;
	private Button usageSNPSet_bt_back;

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	private String USER_PART_CUS = "cus";
	final String USER_BOM = "Bom";
	final String USER_TYPE_USAGE = "type_usage";
	private String usage_set_snp_sp_tx_username, usage_set_snp_sp_tx_project_name,
			usage_set_snp_sp_tx_fg_code_set_abt, usage_set_snp_sp_tx_bom,
			usage_set_snp_sp_tx_sku_code_abt, usage_set_snp_sp_tx_fg_code_gdj,
			usage_set_snp_sp_tx_cus,
			usage_set_snp_sp_tx_unit_type, usage_set_snp_sp_tx_part_usage,
			usage_set_snp_sp_tx_stock_vmi, usage_set_snp_sp_tx_part_pack;

	private String str_ps_t_sku_code_abt;

	private RequestQueue SetSnpQueue;

	private Spinner spinner;
	private ArrayList<DataSpin> spinList;
	SpinnerAdapter spinnerAdapter;
	String clickedSpin;

	private RecyclerView usageSNPSet_rv_itemMenu, alert_us_set_rv_itemMenu;
	private ArrayList<DataSNPSet> usageSNPList;
	private SNPOrderAdapter snpAdapter;
	private ArrayList<DataAlertUsageSet> usageSetArrayList;
	private AlertUsageSetAdapter alertUsageSetAdapter;
	private RecyclerView.LayoutManager layoutManager;
	private int int_stock, int_qty;
	LinearLayoutManager linearLayoutManager;
	public String[] mColors = {"#ffffff", "#E1EFEFEF"};

	private String data;

	int a;
	private TextView usageSNPSet_tv_emergency_set_imc;
	private String sta_idNum="0";
	private Button alert_us_set_bt_cart, alert_us_set_minus_bt, alert_us_set_add_bt;
	private EditText alert_us_set_data_customerBOX;
	private TextView usageSNPSet_tv_datetime, usageSNPSet_tv_ddmmyy;
	private ProgressDialog usage_set_snp_prog_di;
	private EditText usageSNPSet_et_filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_spe_order_snp_set);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		hideKeyboard.setupUI(findViewById(R.id.container_partNumber), this);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		usage_set_snp_sp_tx_username = sp.getString(USER_NAME, "");
		usage_set_snp_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		usage_set_snp_sp_tx_bom = sp.getString(USER_BOM, "");
		SetSnpQueue = Volley.newRequestQueue(this);
		getSNP_spinner();
		init_snpSet();
		callApi_MyCart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		callApi_MyCart();
	}

	private void init_snpSet() {
		usageSNPSet_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		usageSNPSet_tv_title_cus_service.setText(R.string.tx_cus_service);
		usageSNPSet_tv_title_cus_service.setVisibility(View.VISIBLE);
		usageSNPSet_tv_title_cus_service.setTextSize(40);
		usageSNPSet_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		usageSNPSet_tv_customer = findViewById(R.id.tv_customer);
		usageSNPSet_tv_customer_name = findViewById(R.id.tv_customer_name);
		usageSNPSet_tv_customer_name.setText(usage_set_snp_sp_tx_username);
		usageSNPSet_tv_datetime = findViewById(R.id.tv_datetime);
		usageSNPSet_tv_ddmmyy = findViewById(R.id.tv_time);
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
								usageSNPSet_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		usageSNPSet_img_logo_gdj = findViewById(R.id.img_logo);
		usageSNPSet_bt_back = findViewById(R.id.bt_back);
		usageSNPSet_bt_back.setVisibility(View.VISIBLE);
		usageSNPSet_bt_back.setText(R.string.tx_back);
		usageSNPSet_tv_emergency_set_imc = findViewById(R.id.spe_order_set_tv_snp_imc);
		usageSNPSet_tv_emergency_set_imc.setText("CARTON CODE NORMAL " + usage_set_snp_sp_tx_bom);
		usageSNPSet_tv_emergency_set = findViewById(R.id.spe_order_set_snp_tv_title);
		usageSNPSet_tv_emergency_set.setText(R.string.tx_order_usageCon_set);
		usageSNPSet_bt_cart = findViewById(R.id.spe_order_set_snp_bt_cart_count);
		usageSNPSet_bt_cart.setVisibility(View.VISIBLE);
		spinner = (Spinner) findViewById(R.id.spe_order_set_snp_spin_snp);
		spinList = new ArrayList<DataSpin>();
///*	26/01/64 add filter*/
		usageSNPSet_et_filter = findViewById(R.id.spe_order_set_snp_ed_filter);
		usageSNPSet_et_filter.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				filter(s.toString());
			}
		});
///
		spinList.add(new DataSpin("choose"));
		usageSNPSet_rv_itemMenu = findViewById(R.id.special_set_item_manu);
		usageSNPSet_rv_itemMenu.setHasFixedSize(false);
		usageSNPSet_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
		usageSNPList = new ArrayList<DataSNPSet>();
	}

	/*	26/01/64 add filter*/
	private void filter(String str_fil) {
		ArrayList<DataSNPSet> filteredList = new ArrayList<>();
		for (DataSNPSet item : usageSNPList) {
			if (item.getTextPartNumber().toLowerCase().contains(str_fil.toLowerCase())) {
				filteredList.add(item);
			}
		}
		snpAdapter.filterList(filteredList);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DataSpin clickedItem = (DataSpin) parent.getItemAtPosition(position);
		clickedSpin = clickedItem.getSpinner();
		if (clickedSpin.equals("choose")) {
			progDi_usageSNPSet();
			usageSNPList.clear();
			clickedSpin = "";
			getPartNumber();
		} else {
			progDi_usageSNPSet();
			usageSNPList.clear();
			getPartNumber();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private void progDi_usageSNPSet() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

			}
		}, 1000);
		usage_set_snp_prog_di = new ProgressDialog(UsageSNPSetActivity.this, R.style.Dialg);
		usage_set_snp_prog_di.setMessage("Loading..."); // Setting Message
		usage_set_snp_prog_di.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		usage_set_snp_prog_di.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		usage_set_snp_prog_di.show(); // Display Progress Dialog
		usage_set_snp_prog_di.setCancelable(false);
	}

	private void getPartNumber() {
		String urlUsageSet = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/Part_fgCodeCus.php";
		String FG_Code = usage_set_snp_sp_tx_bom;
		String User_Project_Name = usage_set_snp_sp_tx_project_name;
		String FG_Snp = clickedSpin;
		HashMap<String, String> par_UsageSetSnp = new HashMap<>();
		par_UsageSetSnp.put("FG_Code_Normal", FG_Code);
		par_UsageSetSnp.put("User_Project_Name", User_Project_Name);
		par_UsageSetSnp.put("FG_Snp", FG_Snp);
		JSONObject data_UsageSetSNP = new JSONObject(par_UsageSetSnp);
		JsonObjectRequest request_setSnp = new JsonObjectRequest(urlUsageSet, data_UsageSetSNP, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSnp = new JSONObject(String.valueOf(response));
					String sta_idSNP = jsonSnp.getString("Status");
					JSONArray ja_snpResultData = jsonSnp.getJSONArray("ResultData");
					ArrayList<String> ar_result = new ArrayList<String>();
					for (int i = 0; i < ja_snpResultData.length(); i++) {
						JSONObject part_resulItem = ja_snpResultData.getJSONObject(i);
						String part_number = part_resulItem.getString("bom_part_customer");
						String ship_type = part_resulItem.getString("bom_ship_type");
						int usageSNPSet_bom_snp = part_resulItem.getInt("bom_snp");
						String usageSNPSet_str_bom_snp = String.valueOf(usageSNPSet_bom_snp);
						usageSNPList.add(new DataSNPSet(part_number, usageSNPSet_str_bom_snp, ship_type));
						sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
						editor = sp.edit();
						editor.putString(USER_PART_CUS, part_number);
						//TODO: นี้ๆในเทสไม่มี
						editor.apply();
					}
					usage_set_snp_prog_di.dismiss();
					snpAdapter = new SNPOrderAdapter(getApplicationContext(), usageSNPList);
					usageSNPSet_rv_itemMenu.setAdapter(snpAdapter);
					snpAdapter.setOnItemClickListener(UsageSNPSetActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				usage_set_snp_prog_di.dismiss();
				error.printStackTrace();
			}
		});
		SetSnpQueue.add(request_setSnp);
	}

	private void getSNP_spinner() {
		String sp_url = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/fg_codeSnp.php";
		String fg_code_itemcode = usage_set_snp_sp_tx_bom;
		String sp_project_name = usage_set_snp_sp_tx_project_name;
		HashMap<String, String> par_UsageSpin = new HashMap<>();
		par_UsageSpin.put("FG_Code_Normal", fg_code_itemcode);
		par_UsageSpin.put("User_Project_Name", sp_project_name);
		JSONObject data_usageSetSpin = new JSONObject(par_UsageSpin);
		JsonObjectRequest request_setSpin = new JsonObjectRequest(sp_url, data_usageSetSpin, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSpin = new JSONObject(String.valueOf(response));
					String sta_idSpin = jsonSpin.getString("Status");
					JSONArray ja_spinResultData = jsonSpin.getJSONArray("ResultData");
					ArrayList<String> ar_resultSpin = new ArrayList<String>();
					for (int i = 0; i < ja_spinResultData.length(); i++) {
						JSONObject spin_resulItem = ja_spinResultData.getJSONObject(i);
						String str_spin = spin_resulItem.getString("bom_snp");
						spinList.add(new DataSpin(str_spin));
					}
					spinnerAdapter = new SpinnerAdapter(UsageSNPSetActivity.this, spinList);
					spinner.setAdapter(spinnerAdapter);
					spinner.setOnItemSelectedListener(UsageSNPSetActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		});
		SetSnpQueue.add(request_setSpin);
	}

	@Override
	public void onItemClick(final int position, final String str_partNumber, final String str_snp, final String str_ship) {
		usageSNPSet_et_filter.getText().clear();
		final Dialog dia_usage_snpSet = new Dialog(UsageSNPSetActivity.this);
		dia_usage_snpSet.setContentView(R.layout.layout_alert_usage_set);
		dia_usage_snpSet.setCancelable(false);
		Button alert_us_set_bt_close = (Button) dia_usage_snpSet.findViewById(R.id.alert_usage_bt_close);
		alert_us_set_bt_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dia_usage_snpSet.dismiss();
			}
		});
		final ImageView alert_us_set_img_box = (ImageView) dia_usage_snpSet.findViewById(R.id.alert_usage_img_order);
		final TextView alert_us_set_name_boxIMG = (TextView) dia_usage_snpSet.findViewById(R.id.alert_tv_subSec_cotton);
		final TextView alert_us_set_name_box = (TextView) dia_usage_snpSet.findViewById(R.id.alert_usage_tv_name_box2);
		alert_us_set_minus_bt = (Button) dia_usage_snpSet.findViewById(R.id.alert_usage_number_minus);
		alert_us_set_data_customerBOX = (EditText) dia_usage_snpSet.findViewById(R.id.alert_usage_number_customer);
		alert_us_set_data_customerBOX.setText("1");
		alert_us_set_add_bt = (Button) dia_usage_snpSet.findViewById(R.id.alert_usage_number_add);
		alert_us_set_bt_cart = (Button) dia_usage_snpSet.findViewById(R.id.alert_usage_bt_cart);
		alert_us_set_rv_itemMenu = (RecyclerView) dia_usage_snpSet.findViewById(R.id.alert_usage_tit_rv_part_set);
		alert_us_set_rv_itemMenu.setHasFixedSize(false);
		alert_us_set_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
		usageSetArrayList = new ArrayList<DataAlertUsageSet>();
		api_al_usSnpSet(str_partNumber, str_snp, str_ship);
		data = alert_us_set_data_customerBOX.getText().toString().trim();
		alert_us_set_minus_bt.setEnabled(true);
		alert_us_set_minus_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int edit_int = Integer.parseInt(data);
				a = edit_int - 1;
				if (a >= 1 && a != 0) {
					data = String.valueOf(a);
					alert_us_set_data_customerBOX.setText(data);
					for (int i = 0; i < alertUsageSetAdapter.getItemCount(); i++) {
						View view = alert_us_set_rv_itemMenu.getLayoutManager().findViewByPosition(i);
						TextView tx_packing = (TextView) view.findViewById(R.id.al_us_snp_tv_packing_snp);
						TextView tx_stock = (TextView) view.findViewById(R.id.al_us_snp_tv_vmi_stock);
						TextView tx_qty = (TextView) view.findViewById(R.id.al_us_snp_tv_qty);
						String str_packing = tx_packing.getText().toString().trim();
						String str_stock = tx_stock.getText().toString().trim();
						String str_qty = tx_qty.getText().toString().trim();
						int int_packing = Integer.parseInt(str_packing);
						int_stock = Integer.parseInt(str_stock);
						int_qty = Integer.parseInt(str_qty);
						for (int x = 0; x < alertUsageSetAdapter.getItemCount(); x++) {
							int minus_qty = int_qty - int_packing;
							if (minus_qty != 0 && int_qty < int_stock) { //&& int_qty != 0
								tx_qty.setText(String.valueOf(minus_qty));
							}
							if (minus_qty == 0 && int_stock <= int_qty || int_qty <= int_stock) {
								tx_qty.setText(String.valueOf(int_packing));
								alert_us_set_add_bt.setEnabled(true);
								alert_us_set_bt_cart.setEnabled(true);
								alert_us_set_bt_cart.setBackgroundResource(R.drawable.button_selector);
								view.setBackgroundColor(Color.parseColor(mColors[position % 2]));
							}
						}
					}
				} else {
					alert_us_set_data_customerBOX.getText().clear();
					alert_us_set_data_customerBOX.setText(String.valueOf(1));
				}
			}
		});
		alert_us_set_add_bt.setEnabled(true);
		alert_us_set_add_bt.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public void onClick(View v) {
				int data_minteger = 0;
				int minteger = Integer.parseInt(data);
				data_minteger = minteger + 1;
				data = String.valueOf(data_minteger);
				alert_us_set_data_customerBOX.setText(data);
				for (int i = 0; i < alertUsageSetAdapter.getItemCount(); i++) {
					View view = alert_us_set_rv_itemMenu.getLayoutManager().findViewByPosition(i);
					TextView tx_packing = (TextView) view.findViewById(R.id.al_us_snp_tv_packing_snp);
					TextView tx_stock = (TextView) view.findViewById(R.id.al_us_snp_tv_vmi_stock);
					TextView tx_qty = (TextView) view.findViewById(R.id.al_us_snp_tv_qty);
					String str_packing = tx_packing.getText().toString().trim();
					String str_stock = tx_stock.getText().toString().trim();
					String str_qty = tx_qty.getText().toString().trim();
					int int_packing = Integer.parseInt(str_packing);
					int_stock = Integer.parseInt(str_stock);
					int_qty = Integer.parseInt(str_qty);
					for (int x = 0; x < alertUsageSetAdapter.getItemCount(); x++) {
						int result_num2 = Integer.parseInt(data);
						data_minteger = int_packing * result_num2;
						if (int_stock > data_minteger){
							tx_qty.setText(String.valueOf(data_minteger));
							String str_qty2 = tx_qty.getText().toString().trim();
							int int_qty2 = Integer.parseInt(str_qty2);
						}
						if (int_stock == data_minteger){
							tx_qty.setText(String.valueOf(data_minteger));
							String str_qty2 = tx_qty.getText().toString().trim();
							int int_qty2 = Integer.parseInt(str_qty2);
							alert_us_set_add_bt.setEnabled(false);
							view.setBackgroundColor(0xFFFF8F6F);
						}
						if (int_stock < data_minteger){
							view.setBackgroundColor(0xFFFF8F6F);
							alert_us_set_add_bt.setEnabled(false);
							alert_us_set_bt_cart.setEnabled(false);
							alert_us_set_bt_cart.setBackgroundResource(R.color.color_dum);
						}
					}
				}
			}
		});
		alert_us_set_bt_cart.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ResourceType")
			@Override
			public void onClick(View v) {
				String box_data_custom = alert_us_set_data_customerBOX.getText().toString().trim();
				final int int_box_data = Integer.parseInt(box_data_custom);
				final Dialog dia_save_Uset = new Dialog(UsageSNPSetActivity.this);
				dia_save_Uset.setContentView(R.layout.layout_alert_confrim);
				dia_save_Uset.setCancelable(true);
				Button al_close = dia_save_Uset.findViewById(R.id.al2_bt_close);
				al_close.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dia_save_Uset.dismiss();
					}
				});
				Button confirm_bt_yes = dia_save_Uset.findViewById(R.id.al2_bt_yes);
				confirm_bt_yes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						api_saveUsageset(int_box_data, str_partNumber, str_snp, str_ship);
						dia_usage_snpSet.dismiss();
					}
				});
				Button confirm_bt_no = dia_save_Uset.findViewById(R.id.al2_bt_no);
				confirm_bt_no.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dia_save_Uset.dismiss();
						dia_usage_snpSet.dismiss();
					}
				});
				dia_save_Uset.show();
			}
		});
		setDataToViewTest(position, str_partNumber, alert_us_set_name_boxIMG, alert_us_set_img_box, alert_us_set_name_box); //mAmount, al_img_box, al_name_boxIMG, al_name_box, al_size_box
		dia_usage_snpSet.show();
	}

	private void api_al_usSnpSet(String str_partNumber, String str_snp, String str_ship) {
		String urlusage_partNumber = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Part_Com_Stock.php";
		final String fg_code_partNumber = str_partNumber;
		String proj_name = usage_set_snp_sp_tx_project_name;
		HashMap<String, String> partNumber = new HashMap<>();
		partNumber.put("FG_Code_Customer", fg_code_partNumber);
		partNumber.put("Project_Name", proj_name);
		partNumber.put("bom_snp",str_snp);
		partNumber.put("Ship_Type",str_ship);
		JSONObject data_partNumber = new JSONObject(partNumber);
		JsonObjectRequest request_partNum = new JsonObjectRequest(urlusage_partNumber, data_partNumber, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonNum = new JSONObject(String.valueOf(response));
					sta_idNum = jsonNum.getString("Status");
					JSONArray ja_numResultData = jsonNum.getJSONArray("ResultData");
					ArrayList<String> ar_result = new ArrayList<String>();
					for (int i = 0; i < ja_numResultData.length(); i++) {
						JSONObject num_resultItem = ja_numResultData.getJSONObject(i);
						String str_fg_code_gdj = num_resultItem.getString("bom_fg_code_gdj");
						String str_fg_code_set_abt = num_resultItem.getString("bom_fg_code_set_abt");
						str_ps_t_sku_code_abt = num_resultItem.getString("bom_fg_sku_code_abt");
						String str_part_usage = num_resultItem.getString("bom_usage");
						String str_stock_vmi = num_resultItem.getString("Stocm_VMI");
						String str_part_pack = num_resultItem.getString("bom_packing");
						ar_result.add(str_fg_code_gdj);
						ar_result.add(str_fg_code_set_abt);
						usage_set_snp_sp_tx_part_usage = str_part_usage;
						usage_set_snp_sp_tx_stock_vmi = str_stock_vmi;
						usage_set_snp_sp_tx_part_pack = str_part_pack;
						usageSetArrayList.add(new DataAlertUsageSet(str_ps_t_sku_code_abt,
								usage_set_snp_sp_tx_part_usage, usage_set_snp_sp_tx_part_pack,
								usage_set_snp_sp_tx_stock_vmi, usage_set_snp_sp_tx_part_pack,
								str_fg_code_gdj, str_fg_code_set_abt));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (sta_idNum.equals("1")) {

					ViewGroup.LayoutParams params=alert_us_set_rv_itemMenu.getLayoutParams();
					params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
					alert_us_set_rv_itemMenu.setLayoutParams(params);

					alertUsageSetAdapter = new AlertUsageSetAdapter(getApplicationContext(), usageSetArrayList);
					alert_us_set_rv_itemMenu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
					alert_us_set_rv_itemMenu.setAdapter(alertUsageSetAdapter);
					snpAdapter.notifyDataSetChanged();
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < alertUsageSetAdapter.getItemCount(); i++) {
								View view = alert_us_set_rv_itemMenu.getLayoutManager().findViewByPosition(i);
								TextView tx_stock = (TextView) view.findViewById(R.id.al_us_snp_tv_vmi_stock);
								TextView tx_qty = (TextView) view.findViewById(R.id.al_us_snp_tv_qty);
								String tx_rv_stock = tx_stock.getText().toString().trim();
								String tx_rv_qty = tx_qty.getText().toString().trim();
								int int_rv_stock = Integer.parseInt(tx_rv_stock);
								int int_rv_qty = Integer.parseInt(tx_rv_qty);
								if (tx_rv_stock.equals(0) || tx_rv_stock.equals("0")) {
									alert_us_set_bt_cart.setEnabled(false);
									alert_us_set_add_bt.setEnabled(false);
									alert_us_set_minus_bt.setEnabled(false);
									alert_us_set_data_customerBOX.setEnabled(false);
									alert_us_set_bt_cart.setBackgroundResource(R.color.color_dum);
								}
								if (int_rv_stock == int_rv_qty) {
									alert_us_set_bt_cart.setEnabled(true);
									alert_us_set_add_bt.setEnabled(false);
									alert_us_set_minus_bt.setEnabled(false);
									alert_us_set_data_customerBOX.setEnabled(false);
									view.setBackgroundColor(0xFFFF8F6F);
								}
								if (int_rv_stock < int_rv_qty) {
									alert_us_set_bt_cart.setEnabled(false);
									alert_us_set_add_bt.setEnabled(false);
									alert_us_set_minus_bt.setEnabled(false);
									alert_us_set_data_customerBOX.setEnabled(false);
									view.setBackgroundColor(0xFFFF8F6F);
								}
							}
						}
					}, 500);
				} else {
					usageSetArrayList.add(new DataAlertUsageSet("Data not found",
							"", "", "", "", "",
							""));
					alertUsageSetAdapter = new AlertUsageSetAdapter(getApplicationContext(), usageSetArrayList);
					alert_us_set_rv_itemMenu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
					alert_us_set_rv_itemMenu.setAdapter(alertUsageSetAdapter);
					snpAdapter.notifyDataSetChanged();
					alert_us_set_add_bt.setEnabled(false);
					alert_us_set_minus_bt.setEnabled(false);
					alert_us_set_data_customerBOX.setEnabled(false);
					alert_us_set_bt_cart.setEnabled(false);
					alert_us_set_bt_cart.setBackgroundResource(R.color.color_dum);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		});
		SetSnpQueue.add(request_partNum);
	}

	private void api_saveUsageset(int int_box_data, String str_partNumber, String str_snp, String str_ship) {
		String urlSave = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Save_Pre.php";
		usage_set_snp_sp_tx_unit_type = "Set";
		usage_set_snp_sp_tx_cus = sp.getString(USER_PART_CUS, "");
		String pick_qty = String.valueOf(int_box_data);
		String Project_Name = usage_set_snp_sp_tx_project_name;
		String order_Type = "Set";
		HashMap<String, String> hashMap_save = new HashMap<>();
		hashMap_save.put("Pick_Qty",pick_qty);
		hashMap_save.put("Project_Name", Project_Name);
		hashMap_save.put("bom_snp", str_snp);
		hashMap_save.put("ship_type", str_ship);
		hashMap_save.put("part_customer", str_partNumber);
		hashMap_save.put("orderType", order_Type);

		JSONObject data_save = new JSONObject(hashMap_save);
		JsonObjectRequest request_partNum = new JsonObjectRequest(urlSave, data_save, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSave = new JSONObject(String.valueOf(response));
					String sta_id = jsonSave.getString("Status");
					if (sta_id.equals("1")){
						sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
						editor = sp.edit();
						editor.putString(USER_TYPE_USAGE, "Set");
						editor.apply();
						startActivity(new Intent(UsageSNPSetActivity.this, OrderListAllActivity.class));
					}
					else {
						String sta_idSave = jsonSave.getString("tStatus");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		});
		SetSnpQueue.add(request_partNum);
	}

	private void setDataToViewTest(int position, String str_partNumber, TextView boxName, ImageView boxIMG, TextView boxSub) { // int mAmount
		boxName.setText(usage_set_snp_sp_tx_bom);
		boxSub.setText(str_partNumber);
		String url_img = "https://lac-apps.albatrossthai.com/api/VMI/Image_GDJ/" + usage_set_snp_sp_tx_bom + ".png";
		Glide.with(this)
				.load(url_img)
				.centerInside()
				.placeholder(R.drawable.ic_add_photo)
				.into(boxIMG);
		snpAdapter.notifyItemChanged(position);
	}

	public void onClickUsage(View view) {
		usageSNPSet_et_filter.getText().clear();
		startActivity(new Intent(getApplicationContext(), OrderListAllActivity.class));
	}

	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_back) {
			startActivity(new Intent(getApplicationContext(), UsageSetActivity.class));
		}
	}

	private String sta_idMyCart = "0";
	private int sta_tx_MyCart = 0;

	private void callApi_MyCart() {
		String urlMyCart = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Qty_MyCart.php";
		String Project_Name = usage_set_snp_sp_tx_project_name;
		HashMap<String, String> hashMap_MyCart = new HashMap<>();
		hashMap_MyCart.put("Project_Name", Project_Name);
		JSONObject data_MyCart = new JSONObject(hashMap_MyCart);
		JsonObjectRequest request_myCart = new JsonObjectRequest(urlMyCart, data_MyCart, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSNPSetMyCart = new JSONObject(String.valueOf(response));
					String status_id_cart = jsonSNPSetMyCart.getString("Status");
					int resultData_cart = jsonSNPSetMyCart.getInt("ResultData");
					sta_idMyCart = status_id_cart;
					sta_tx_MyCart = resultData_cart;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (sta_idMyCart.equals("1") || sta_idMyCart.equals(1)) {
					String num_mycart = String.valueOf(sta_tx_MyCart);
					usageSNPSet_bt_cart.setText(num_mycart);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
			}
		});
		SetSnpQueue.add(request_myCart);
	}
}