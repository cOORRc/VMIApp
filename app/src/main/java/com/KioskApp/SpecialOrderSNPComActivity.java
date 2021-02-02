package com.KioskApp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.KioskApp.ui.hideKeyboard;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.KioskApp.adapter.AlertPartComAdapter;
import com.KioskApp.adapter.SNPOrderAdapter;
import com.KioskApp.adapter.SpinnerAdapter;
import com.KioskApp.model.DataAlertCom;
import com.KioskApp.model.DataSNPSet;
import com.KioskApp.model.DataSpin;
import com.KioskApp.ui.DatePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SpecialOrderSNPComActivity extends AppCompatActivity implements SNPOrderAdapter.OnItemClickListener,
		AdapterView.OnItemSelectedListener,
		DatePickerDialog.OnDateSetListener {
	private TextView speOrdSNPCom_tv_title_cus_service, speOrdSNPCom_tv_gdj_vmi_system,
			speOrdSNPCom_tv_customer, speOrdSNPCom_tv_customer_name,
			speOrdSNPCom_tv_emergency_set_imc, speOrdSNPCom_tv_emergency_set;
	private ImageView speOrdSNPCom_img_logo_gdj;
	private Button speOrdSNPCom_bt_back;
	private Spinner spinner;
	private ArrayList<DataSpin> List;
	private SpinnerAdapter spinnerAdapter;
	String clickedSpinner;
	private RecyclerView speOrdSNPCom_rv_itemMenu;
	private ArrayList<DataSNPSet> speCom_partNumList;
	private SNPOrderAdapter speOrd_part_adapter;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_BOM = "Bom";
	String ord_com_snp_sp_tx_username, ord_com_snp_sp_tx_project_name, ord_com_snp_sp_tx_bom;
	String part_com_number, ship_type_com;
	private RequestQueue snpComQueue;
	private int xx = 0;
	private String str_picker;
	private String input_refer;
	private String input_date;
	private android.text.TextWatcher TextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			input_refer = alert_snp_com_input_refer.getText().toString().trim();
			input_date = alert_snp_com_dateView.getText().toString().trim();
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (input_refer.length() > 2 && input_date.length() > 2) {
				alert_snp_com_bt_cart.setEnabled(true);
				alert_snp_com_bt_cart.setBackgroundResource(R.drawable.button_selector);
			}
			else {
				alert_snp_com_bt_cart.setEnabled(false);
				alert_snp_com_bt_cart.setBackgroundResource(R.color.color_dum);
			}

		}
	};
	private TextView alert_snp_com_tv_part_num;
	private TextView speOrdSNPCom_tv_datetime, speOrdSNPCom_tv_ddmmyy;
	private ProgressDialog speOrdSNPCom_prog_di;
	private EditText speOrdSNPCom_et_filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_spe_order_snp_set);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		hideKeyboard.setupUI(findViewById(R.id.container_partNumber), this);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		ord_com_snp_sp_tx_username = sp.getString(USER_NAME, "");
		ord_com_snp_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		ord_com_snp_sp_tx_bom = sp.getString(USER_BOM, "");
		snpComQueue = Volley.newRequestQueue(this);
		getSNP_spinner();
		init_snpCom();
	}

	private void init_snpCom() {
		speOrdSNPCom_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		speOrdSNPCom_tv_title_cus_service.setText(R.string.tx_cus_service);
		speOrdSNPCom_tv_title_cus_service.setVisibility(View.VISIBLE);
		speOrdSNPCom_tv_title_cus_service.setTextSize(40);
		speOrdSNPCom_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		speOrdSNPCom_tv_customer = findViewById(R.id.tv_customer);
		speOrdSNPCom_tv_customer_name = findViewById(R.id.tv_customer_name);
		speOrdSNPCom_tv_customer_name.setText(ord_com_snp_sp_tx_username);
		speOrdSNPCom_img_logo_gdj = findViewById(R.id.img_logo);
		speOrdSNPCom_bt_back = findViewById(R.id.bt_back);
		speOrdSNPCom_bt_back.setVisibility(View.VISIBLE);
		speOrdSNPCom_bt_back.setText(R.string.tx_back);
		speOrdSNPCom_tv_emergency_set_imc = findViewById(R.id.spe_order_set_tv_snp_imc);
		speOrdSNPCom_tv_emergency_set_imc.setText("CARTON CODE NORMAL " + ord_com_snp_sp_tx_bom);
		speOrdSNPCom_tv_emergency_set = findViewById(R.id.spe_order_set_snp_tv_title);
		speOrdSNPCom_tv_emergency_set.setText(R.string.tx_order_emergency_component);
		speOrdSNPCom_tv_datetime = findViewById(R.id.tv_datetime);
		speOrdSNPCom_tv_ddmmyy = findViewById(R.id.tv_time);
		Thread thread = new Thread(){
			@Override
			public void run() {
				try {
					while (!isInterrupted()){
						Thread.sleep(100);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
								SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
								String currenttime = simpleDateFormat.format(calendar.getTime());
								String currentdate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
								speOrdSNPCom_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		spinner = findViewById(R.id.spe_order_set_snp_spin_snp);
		List = new ArrayList<DataSpin>();
		List.add(new DataSpin("choose"));
		speOrdSNPCom_rv_itemMenu = findViewById(R.id.special_set_item_manu);
		speOrdSNPCom_rv_itemMenu.setHasFixedSize(false);
		speOrdSNPCom_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
		speCom_partNumList = new ArrayList<DataSNPSet>();
		/*	26/01/64 add filter*/
		speOrdSNPCom_et_filter = findViewById(R.id.spe_order_set_snp_ed_filter);
		speOrdSNPCom_et_filter.addTextChangedListener(new TextWatcher() {
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
	}
	/*	26/01/64 add filter*/
	private void filter(String str_fil) {
		ArrayList<DataSNPSet> filteredList = new ArrayList<>();
		for (DataSNPSet item : speCom_partNumList) {
			if (item.getTextPartNumber().toLowerCase().contains(str_fil.toLowerCase())) {
				filteredList.add(item);
			}
		}
		speOrd_part_adapter.filterList(filteredList);
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DataSpin clickedItem = (DataSpin) parent.getItemAtPosition(position);
		clickedSpinner = clickedItem.getSpinner();
		if (clickedSpinner.equals("choose")) {
			progDi_SpeSNPCom();
			speCom_partNumList.clear();
			clickedSpinner = "";
			getPartNumber();
		} else {
			progDi_SpeSNPCom();
			speCom_partNumList.clear();
			getPartNumber();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private void progDi_SpeSNPCom() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

			}
		}, 1000);
		speOrdSNPCom_prog_di = new ProgressDialog(SpecialOrderSNPComActivity.this, R.style.Dialg);
		speOrdSNPCom_prog_di.setMessage("Loading..."); // Setting Message
		speOrdSNPCom_prog_di.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		speOrdSNPCom_prog_di.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		speOrdSNPCom_prog_di.show(); // Display Progress Dialog
		speOrdSNPCom_prog_di.setCancelable(false);
	}


	private void getPartNumber() {
		String urlOrderCom = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/Part_fgCodeCus.php";
		String FG_Code = ord_com_snp_sp_tx_bom;
		String User_Project_Name = ord_com_snp_sp_tx_project_name;
		String FG_Snp = clickedSpinner;

		HashMap<String, String> par_OrderComSnp = new HashMap<>();
		par_OrderComSnp.put("FG_Code_Normal", FG_Code);
		par_OrderComSnp.put("User_Project_Name", User_Project_Name);
		par_OrderComSnp.put("FG_Snp", FG_Snp);
		Log.i("api_ord_partCom", "HashMap_Parame : " + String.valueOf(par_OrderComSnp));

		JSONObject data_orderComSNP = new JSONObject(par_OrderComSnp);
		Log.i("api_ord_partCom", "data_login : " + data_orderComSNP);

		JsonObjectRequest request_ComSnp = new JsonObjectRequest(urlOrderCom, data_orderComSNP, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Log.i("api_ord_partCom", "response : " + response);
					JSONObject js_snp_com = new JSONObject(String.valueOf(response));
					Log.i("api_ord_partCom", "Response_jsonObj : " + String.valueOf(js_snp_com));
					String str_idSNPCom = js_snp_com.getString("Status");
					JSONArray ja_snpResultComData = js_snp_com.getJSONArray("ResultData");

					Log.i("api_ord_partCom", "str_idSNPCom : " + str_idSNPCom +
							", ja_snpResultComData : " + ja_snpResultComData);
					ArrayList<String> ar_resultCom = new ArrayList<String>();
					for (int i = 0; i < ja_snpResultComData.length(); i++) {
						JSONObject part_resulComItem = ja_snpResultComData.getJSONObject(i);
						part_com_number = part_resulComItem.getString("bom_part_customer");
						ship_type_com = part_resulComItem.getString("bom_ship_type");
						int speOrdSNPCom_bom_snp = part_resulComItem.getInt("bom_snp");
						String speOrdSNPCom_str_bom_snp = String.valueOf(speOrdSNPCom_bom_snp);
						speCom_partNumList.add(new DataSNPSet(part_com_number, speOrdSNPCom_str_bom_snp, ship_type_com));

					}
					speOrdSNPCom_prog_di.dismiss();
					speOrd_part_adapter = new SNPOrderAdapter(getApplicationContext(), speCom_partNumList);
					speOrdSNPCom_rv_itemMenu.setAdapter(speOrd_part_adapter);
					speOrd_part_adapter.setOnItemClickListener(SpecialOrderSNPComActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				speOrdSNPCom_prog_di.dismiss();
				error.printStackTrace();
			}
		});
		snpComQueue.add(request_ComSnp);
	}

	private void getSNP_spinner() {
		String spCom_url = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/fg_codeSnp.php";
		String fg_code_itemcode = ord_com_snp_sp_tx_bom;
		String sp_project_name = ord_com_snp_sp_tx_project_name;

		HashMap<String, String> par_OrderComSpin = new HashMap<>();
		par_OrderComSpin.put("FG_Code_Normal", fg_code_itemcode);
		par_OrderComSpin.put("User_Project_Name", sp_project_name);
		Log.i("api_spin", "HashMap_Parame : " + String.valueOf(par_OrderComSpin));

		JSONObject data_orderSetSpin = new JSONObject(par_OrderComSpin);
		Log.i("api_spin", "data_login : " + data_orderSetSpin);

		JsonObjectRequest request_ComSpin = new JsonObjectRequest(spCom_url, data_orderSetSpin, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					Log.i("api_spin", "response : " + response);
					JSONObject jsSpinCom = new JSONObject(String.valueOf(response));
					Log.i("api_spin", "Response_jsonObj : " + String.valueOf(jsSpinCom));
					String str_idSpinCom = jsSpinCom.getString("Status");
					JSONArray ja_spinComResultData = jsSpinCom.getJSONArray("ResultData");

					Log.i("api_spin", "str_idSpinCom : " + str_idSpinCom +
							", ja_spinComResultData : " + ja_spinComResultData);
					ArrayList<String> ar_resultComSpin = new ArrayList<String>();
					for (int i = 0; i < ja_spinComResultData.length(); i++) {
						JSONObject spin_resulComItem = ja_spinComResultData.getJSONObject(i);
						String str_spinCom = spin_resulComItem.getString("bom_snp");
						Log.i("api_spin", "number : " + str_spinCom);

						List.add(new DataSpin(str_spinCom));
					}
					spinnerAdapter = new SpinnerAdapter(SpecialOrderSNPComActivity.this, List);
					spinner.setAdapter(spinnerAdapter);
					spinner.setOnItemSelectedListener(SpecialOrderSNPComActivity.this);
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
		snpComQueue.add(request_ComSpin);
	}

	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_back) {
			speOrdSNPCom_et_filter.getText().clear();
			startActivity(new Intent(getApplicationContext(), SpecialOrderComponentActivity.class));
		}
	}

	private ImageView alert_snp_com_img_imc;
	private TextView alert_snp_com_tv_ctn_code,
			alert_snp_com_tv_data_ctn_code, alert_snp_com_tv_data_part_num, alert_snp_com_dateView;
	private RecyclerView alert_snp_com_rv_part_com;
	private Button alert_snp_com_bt_close, alert_snp_com_bt_cart;
	private ArrayList<DataAlertCom> alert_list;
	private TextView tv_pack;
	private Button bt_minus, bt_add;
	private EditText alert_snp_com_input_refer, ed_picker;
	private String ord_com_snp_tx_qty, ord_com_snp_tx_sku_code_abt;
	String snp_com_date_send = "date";
	@Override
	public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
		alert_snp_com_dateView.setText(currentDateString);
		snp_com_date_send = (year + "-" + (month + 1) + "-" + dayOfMonth);
	}

	public int check_qty=0;
	@Override
	public void onItemClick(int position, String str_partNumber, final String str_snp, String str_ship) {
		speOrdSNPCom_et_filter.getText().clear();
		com_snp_queue = Volley.newRequestQueue(this);
		final Dialog dialog_snpCom = new Dialog(SpecialOrderSNPComActivity.this);
		dialog_snpCom.setContentView(R.layout.layout_alert_order_com);
		dialog_snpCom.setCancelable(false);
		alert_snp_com_bt_close = dialog_snpCom.findViewById(R.id.alert_bt_tit_close);
		alert_snp_com_bt_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_snpCom.dismiss();
			}
		});
		alert_snp_com_img_imc = dialog_snpCom.findViewById(R.id.alert_img_tit_order);
		String url_img = "https://lac-apps.albatrossthai.com/api/VMI/Image_GDJ/" + ord_com_snp_sp_tx_bom + ".png";
		Glide.with(this)
				.load(url_img)
				.centerInside()
				.placeholder(R.drawable.ic_add_photo)
				.into(alert_snp_com_img_imc);
		alert_snp_com_tv_ctn_code = dialog_snpCom.findViewById(R.id.alert_tv_tit_name_box);
		alert_snp_com_tv_data_ctn_code = dialog_snpCom.findViewById(R.id.alert_tv_subSec_cotton);
		alert_snp_com_tv_data_ctn_code.setText(ord_com_snp_sp_tx_bom);
		alert_snp_com_tv_part_num = dialog_snpCom.findViewById(R.id.alert_tv_tit_name_box2);
		alert_snp_com_tv_data_part_num = dialog_snpCom.findViewById(R.id.alert_tv_subSec_partCom);
		alert_snp_com_tv_data_part_num.setText(str_partNumber);
		alert_snp_com_dateView = (TextView) dialog_snpCom.findViewById(R.id.alert_input_emer_com_date);
		alert_snp_com_dateView.addTextChangedListener(TextWatcher);
		alert_snp_com_dateView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment datePicker = new DatePickerFragment();
				datePicker.show(getSupportFragmentManager(), "date picker");
			}
		});
		alert_snp_com_input_refer = (EditText) dialog_snpCom.findViewById(R.id.alert_input_emer_com_refer);
		alert_snp_com_input_refer.addTextChangedListener(TextWatcher);
		alert_snp_com_rv_part_com = dialog_snpCom.findViewById(R.id.alert_rv_part_com);
		alert_snp_com_rv_part_com.setHasFixedSize(false);
		alert_snp_com_rv_part_com.setLayoutManager(new LinearLayoutManager(this));
		alert_list = new ArrayList<DataAlertCom>();
		alert_part_ComAdapter = new AlertPartComAdapter(getApplicationContext(), alert_list);
		api_alert(str_partNumber, str_snp);
		alert_snp_com_bt_cart = dialog_snpCom.findViewById(R.id.alert_bt_cart);
		alert_snp_com_bt_cart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String str_part_cus = alert_snp_com_tv_data_part_num.getText().toString().trim();
				final String input_ord_refer_com = alert_snp_com_input_refer.getText().toString().trim();
				com_snp_saveQueue = Volley.newRequestQueue(SpecialOrderSNPComActivity.this);
				for (int i = 0; i < alert_part_ComAdapter.getItemCount(); i++) {
					View view = alert_snp_com_rv_part_com.getLayoutManager().findViewByPosition(i);
					TextView tx_part_com = (TextView) view.findViewById(R.id.alert_com_tv_part_com);
					TextView tx_qty = (TextView) view.findViewById(R.id.alert_com_tv_qty);
					EditText ed_picker = (EditText) view.findViewById(R.id.alert_com_number_customer);
					ord_com_snp_tx_qty = tx_qty.getText().toString().trim();
					ord_com_snp_tx_sku_code_abt = tx_part_com.getText().toString().trim();
					str_picker = ed_picker.getText().toString().trim();
					int in_picker = Integer.parseInt(str_picker);
					int in_qty = Integer.parseInt(ord_com_snp_tx_qty);
					if (in_picker == 0) {
						xx = in_picker + 1;
					}
				}
				int qty = 0;
				if (xx < 0) {
					final Dialog diCart_qtyN = new Dialog(SpecialOrderSNPComActivity.this);
					diCart_qtyN.setContentView(R.layout.layout_alert_zero);
					diCart_qtyN.setCancelable(false); // dismiss when touching outside Dialog
					Button al_cart_bt_close = (Button) diCart_qtyN.findViewById(R.id.al2_bt_close);
					al_cart_bt_close.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							diCart_qtyN.dismiss();
						}
					});
					Button al_no = (Button) diCart_qtyN.findViewById(R.id.al2_bt_no);
					al_no.setVisibility(View.GONE);
					Button al_yes = (Button) diCart_qtyN.findViewById(R.id.al2_bt_yes);
					al_yes.setGravity(Gravity.CENTER);
					al_yes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							diCart_qtyN.dismiss();
							dialog_snpCom.dismiss();
						}
					});
					diCart_qtyN.show();
				} else {
					final Dialog di_cartComSave = new Dialog(SpecialOrderSNPComActivity.this);
					di_cartComSave.setContentView(R.layout.layout_alert_confrim);
					di_cartComSave.setCancelable(false); // dismiss when touching outside Dialog
					Button al_cart_bt_close = (Button) di_cartComSave.findViewById(R.id.al2_bt_close);
					al_cart_bt_close.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							di_cartComSave.dismiss();
						}
					});
					Button al_no = (Button) di_cartComSave.findViewById(R.id.al2_bt_no);
					al_no.setVisibility(View.GONE);
					Button al_yes = (Button) di_cartComSave.findViewById(R.id.al2_bt_yes);
					al_yes.setGravity(Gravity.CENTER);
					al_yes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (int y = 0; y < alert_part_ComAdapter.getItemCount(); y++) {
								View view_zero = alert_snp_com_rv_part_com.getLayoutManager().findViewByPosition(y);
								TextView tv_part_com_zero = (TextView) view_zero.findViewById(R.id.alert_com_tv_part_com);
								TextView tv_qty_zero = (TextView) view_zero.findViewById(R.id.alert_com_tv_qty);
								TextView tv_com_fg_codeGDJ = (TextView) view_zero.findViewById(R.id.com_tx_fg_code_gdj_usCom);
								String str_com_fg_codeGDJ = tv_com_fg_codeGDJ.getText().toString().trim();
								String ord_com_snp_tx_qty_zero = tv_qty_zero.getText().toString().trim();
								String ord_com_snp_tx_sku_code_abt_zero = tv_part_com_zero.getText().toString().trim();
								int numQTY = Integer.parseInt(ord_com_snp_tx_qty_zero);
								if(numQTY != 0){
									api_saveOrdercom(ord_com_snp_tx_sku_code_abt_zero,
											ord_com_snp_tx_qty_zero, input_ord_refer_com,
											str_part_cus, str_com_fg_codeGDJ, str_snp);
								}
							}
							startActivity(new Intent(SpecialOrderSNPComActivity.this, CenterActivity.class));
							di_cartComSave.dismiss();
							dialog_snpCom.dismiss();
						}
					});
					di_cartComSave.show();
				}
			}
		});
		dialog_snpCom.show();
	}

	private String str_part_sku_code_abt, str_part_usage, str_part_pack, str_part_fg_code_set_abt,
			str_part_fg_code_gdj, str_part_ship_type;
	private AlertPartComAdapter alert_part_ComAdapter;
	private RequestQueue com_snp_queue;

	private void api_alert(String str_partNumber, String str_snp) {
		String urlPartNumber = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/Part_Com.php";
		final String fg_code_partNumber = str_partNumber;
		HashMap<String, String> partNumber = new HashMap<>();
		partNumber.put("FG_Code_Customer", fg_code_partNumber);
		partNumber.put("bom_snp", str_snp);
		JSONObject data_partNumber = new JSONObject(partNumber);
		JsonObjectRequest request_partNum = new JsonObjectRequest(urlPartNumber, data_partNumber, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonNum = new JSONObject(String.valueOf(response));
					String sta_idNum = jsonNum.getString("Status");
					JSONArray ja_numResultData = jsonNum.getJSONArray("ResultData");
					ArrayList<String> ar_result = new ArrayList<String>();
					for (int i = 0; i < ja_numResultData.length(); i++) {
						JSONObject num_resulItem = ja_numResultData.getJSONObject(i);
						str_part_sku_code_abt = num_resulItem.getString("bom_fg_sku_code_abt");
						str_part_usage = num_resulItem.getString("bom_usage");
						str_part_pack = num_resulItem.getString("bom_packing");
						str_part_fg_code_set_abt = num_resulItem.getString("bom_fg_code_set_abt");
						str_part_fg_code_gdj = num_resulItem.getString("bom_fg_code_gdj");
						str_part_ship_type = num_resulItem.getString("bom_ship_type");
						alert_list.add(new DataAlertCom(str_part_sku_code_abt, str_part_usage, str_part_pack, "0", "1",str_part_fg_code_gdj));
					}
					ViewGroup.LayoutParams params=alert_snp_com_rv_part_com.getLayoutParams();
					params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
					alert_snp_com_rv_part_com.setLayoutParams(params);
					alert_part_ComAdapter = new AlertPartComAdapter(getApplicationContext(), alert_list);
					alert_snp_com_rv_part_com.setAdapter(alert_part_ComAdapter);
					alert_part_ComAdapter.notifyDataSetChanged();
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
		com_snp_queue.add(request_partNum);
	}

	private RequestQueue com_snp_saveQueue;
	private void api_saveOrdercom(String ord_com_snp_tx_sku_code_abt, String ord_com_snp_tx_qty,
								  String input_ord_refer_com, String str_part_cus,
								  String str_com_fg_codeGDJ, String str_snp) {
		String urlSave = "https://lac-apps.albatrossthai.com/api/VMI/php/Order_Emer/Emer_OrderSave.php";
		String fg_code_set_abt = str_part_fg_code_set_abt;
		String SKU_Code = ord_com_snp_tx_sku_code_abt;
		String Replen_Qty = ord_com_snp_tx_qty;
		String Terminal_ID = ord_com_snp_sp_tx_project_name;
		String Replen_Type = "Special Order";
		String Replen_Unit_Type = "Component";
		String Project_Name = ord_com_snp_sp_tx_project_name;
		String User_ID = ord_com_snp_sp_tx_username;
		String repn_fg_code_gdj = str_com_fg_codeGDJ;
		String repn_ship_type = str_part_ship_type;
		String repn_part_customer = str_part_cus;
		String repn_delivery_date = snp_com_date_send;
		String repn_order_ref = input_ord_refer_com;

		HashMap<String, String> hashMap_save = new HashMap<>();
		hashMap_save.put("fg_code_set_abt", fg_code_set_abt);
		hashMap_save.put("SKU_Code", SKU_Code);
		hashMap_save.put("Replen_Qty", Replen_Qty);
		hashMap_save.put("Terminal_ID", Terminal_ID);
		hashMap_save.put("Replen_Type", Replen_Type);
		hashMap_save.put("Replen_Unit_Type", Replen_Unit_Type);
		hashMap_save.put("Project_Name", Project_Name);
		hashMap_save.put("User_ID", User_ID);
		hashMap_save.put("repn_fg_code_gdj", repn_fg_code_gdj);
		hashMap_save.put("repn_ship_type", repn_ship_type);
		hashMap_save.put("repn_part_customer", repn_part_customer);
		hashMap_save.put("repn_delivery_date", repn_delivery_date);
		hashMap_save.put("repn_order_ref", repn_order_ref);
		hashMap_save.put("bom_snp", str_snp);

		JSONObject data_save = new JSONObject(hashMap_save);
		JsonObjectRequest request_partNum = new JsonObjectRequest(urlSave, data_save, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONObject jsonSave = new JSONObject(String.valueOf(response));
					String sta_idSave = jsonSave.getString("tStatus");

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
		com_snp_saveQueue.add(request_partNum);
	}
}