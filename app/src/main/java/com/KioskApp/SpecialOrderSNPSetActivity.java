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
import android.widget.Toast;

import com.KioskApp.ui.hideKeyboard;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.KioskApp.adapter.AlertPartSetAdapter;
import com.KioskApp.adapter.SNPOrderAdapter;
import com.KioskApp.adapter.SpinnerAdapter;
import com.KioskApp.model.DataAlertSet;
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

public class SpecialOrderSNPSetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
																				SNPOrderAdapter.OnItemClickListener,
																					DatePickerDialog.OnDateSetListener {

	private TextView speOrdSNPSet_tv_title_cus_service, speOrdSNPSet_tv_gdj_vmi_system,
			speOrdSNPSet_tv_customer, speOrdSNPSet_tv_customer_name, speOrdSNPSet_tv_emergency_set;
	private ImageView speOrdSNPSet_img_logo_gdj;
	private Button speOrdSNPSet_bt_back;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	final String PREF_NAME = "Preferences";
	final String USER_NAME = "Username";
	final String USER_PROJECT_NAME = "ProjectName";
	final String USER_BOM = "Bom";
	private String ord_set_snp_sp_tx_username, ord_set_snp_sp_tx_project_name, ord_set_snp_sp_tx_bom,
			ord_set_snp_sp_tx_sku_code_abt, ord_set_snp_sp_tx_fg_code_set_abt,
			ord_set_snp_sp_tx_ship_type, ord_set_snp_sp_tx_cus,
			ord_set_snp_sp_tx_unit_type;
	private RequestQueue SetSnpQueue;
	private Spinner spinner;
	private ArrayList<DataSpin> spinList;
	SpinnerAdapter spinnerAdapter;
	String clickedSpin;
	private RecyclerView speOrdSNPSet_rv_itemMenu, alert_rv_itemMenu;
	private ArrayList<DataSNPSet> snpList;
	private SNPOrderAdapter snpAdapter;
	private ArrayList<DataAlertSet> dataAlertSetArrayList;
	private AlertPartSetAdapter part_setAdapter;
	private RecyclerView.LayoutManager layoutManager;
	LinearLayoutManager linearLayoutManager;
	private String USER_PART_CUS = "cus";
	private String data;
	int a;
	int num_count_spe_set =0;
	private TextView speOrdSNPSet_tv_emergency_set_imc;
	private TextView dateView;
	private int mYear, mMonth, mDay, mHour, mMinute;
	private EditText alert_input_refer;
	private Button alert_bt_cart;
	private String str_refer;
	private String input_date;
	private String input_refer;
	private android.text.TextWatcher TextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			input_refer = alert_input_refer.getText().toString().trim();
			input_date = dateView.getText().toString().trim();
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (input_refer.length() > 2 && input_date.length() > 2) {
				alert_bt_cart.setEnabled(true);
				alert_bt_cart.setBackgroundResource(R.drawable.button_selector);
			} else {
				alert_bt_cart.setEnabled(false);
				alert_bt_cart.setBackgroundResource(R.color.color_dum);
			}
		}
	};
	private TextView speOrdSNPSet_tv_datetime, speOrdSNPSet_tv_ddmmyy;
	private ProgressDialog speOrdSNPSet_prog_di;
	private EditText speOrdSNPSet_et_filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_spe_order_snp_set);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		hideKeyboard.setupUI(findViewById(R.id.container_partNumber), this);
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		ord_set_snp_sp_tx_username = sp.getString(USER_NAME, "");
		ord_set_snp_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		ord_set_snp_sp_tx_bom = sp.getString(USER_BOM, "");
		SetSnpQueue = Volley.newRequestQueue(this);
		getSNP_spinner();
		init_snpSet();
	}

	private void init_snpSet() {
		speOrdSNPSet_tv_title_cus_service = findViewById(R.id.tv_cus_service);
		speOrdSNPSet_tv_title_cus_service.setText(R.string.tx_cus_service);
		speOrdSNPSet_tv_title_cus_service.setVisibility(View.VISIBLE);
		speOrdSNPSet_tv_title_cus_service.setTextSize(40);
		speOrdSNPSet_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
		speOrdSNPSet_tv_customer = findViewById(R.id.tv_customer);
		speOrdSNPSet_tv_customer_name = findViewById(R.id.tv_customer_name);
		speOrdSNPSet_tv_customer_name.setText(ord_set_snp_sp_tx_username);
		speOrdSNPSet_img_logo_gdj = findViewById(R.id.img_logo);
		speOrdSNPSet_bt_back = findViewById(R.id.bt_back);
		speOrdSNPSet_bt_back.setVisibility(View.VISIBLE);
		speOrdSNPSet_bt_back.setText(R.string.tx_back);
		speOrdSNPSet_tv_datetime = findViewById(R.id.tv_datetime);
		speOrdSNPSet_tv_ddmmyy = findViewById(R.id.tv_time);
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
								speOrdSNPSet_tv_ddmmyy.setText(currentdate +" : "+ currenttime);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		speOrdSNPSet_tv_emergency_set_imc = findViewById(R.id.spe_order_set_tv_snp_imc);
		speOrdSNPSet_tv_emergency_set_imc.setText("CARTON CODE NORMAL " + ord_set_snp_sp_tx_bom);
		speOrdSNPSet_tv_emergency_set = findViewById(R.id.spe_order_set_snp_tv_title);
		speOrdSNPSet_tv_emergency_set.setText(R.string.tx_order_emergency_set);
		spinner = (Spinner) findViewById(R.id.spe_order_set_snp_spin_snp);
		spinList = new ArrayList<DataSpin>();
		spinList.add(new DataSpin("choose"));
		speOrdSNPSet_rv_itemMenu = findViewById(R.id.special_set_item_manu);
		speOrdSNPSet_rv_itemMenu.setHasFixedSize(false);
		speOrdSNPSet_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
		snpList = new ArrayList<DataSNPSet>();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		DataSpin clickedItem = (DataSpin) parent.getItemAtPosition(position);
		clickedSpin = clickedItem.getSpinner();
		if (clickedSpin.equals("choose")) {
			progDi_SpeSNPSet();
			snpList.clear();
			clickedSpin = "";
			getPartNumber();
		} else {
			progDi_SpeSNPSet();
			snpList.clear();
			getPartNumber();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Toast.makeText(parent.getContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
	}

	private void progDi_SpeSNPSet() {
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

			}
		}, 1000);
		speOrdSNPSet_prog_di = new ProgressDialog(SpecialOrderSNPSetActivity.this, R.style.Dialg);
		speOrdSNPSet_prog_di.setMessage("Loading..."); // Setting Message
		speOrdSNPSet_prog_di.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
		speOrdSNPSet_prog_di.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
		speOrdSNPSet_prog_di.show(); // Display Progress Dialog
		speOrdSNPSet_prog_di.setCancelable(false);
		/*	26/01/64 add filter*/
		speOrdSNPSet_et_filter = findViewById(R.id.spe_order_set_snp_ed_filter);
		speOrdSNPSet_et_filter.addTextChangedListener(new TextWatcher() {
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
		for (DataSNPSet item : snpList) {
			if (item.getTextPartNumber().toLowerCase().contains(str_fil.toLowerCase())) {
				filteredList.add(item);
			}
		}
		snpAdapter.filterList(filteredList);
	}


	private void getPartNumber() {
		String urlOrderSet = "https://lac-apps.albatrossthai.com/api/VMI_Test/php/Master_Data/Part_fgCodeCus.php";
		String FG_Code = ord_set_snp_sp_tx_bom;
		String User_Project_Name = ord_set_snp_sp_tx_project_name;
		String FG_Snp = clickedSpin;
		HashMap<String, String> par_OrderSetSnp = new HashMap<>();
		par_OrderSetSnp.put("FG_Code_Normal", FG_Code);
		par_OrderSetSnp.put("User_Project_Name", User_Project_Name);
		par_OrderSetSnp.put("FG_Snp", FG_Snp);
		JSONObject data_orderSetSNP = new JSONObject(par_OrderSetSnp);
		JsonObjectRequest request_setSnp = new JsonObjectRequest(urlOrderSet, data_orderSetSNP, new Response.Listener<JSONObject>() {
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
						int speOrdSNPSet_bom_snp = part_resulItem.getInt("bom_snp");
						String speOrdSNPSet_str_bom_snp = String.valueOf(speOrdSNPSet_bom_snp);
						snpList.add(new DataSNPSet(part_number, speOrdSNPSet_str_bom_snp, ship_type));
						sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
						editor = sp.edit();
						editor.putString(USER_PART_CUS, part_number);
						editor.apply();
					}
					speOrdSNPSet_prog_di.dismiss();
					snpAdapter = new SNPOrderAdapter(getApplicationContext(), snpList);
					speOrdSNPSet_rv_itemMenu.setAdapter(snpAdapter);
					snpAdapter.setOnItemClickListener(SpecialOrderSNPSetActivity.this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				speOrdSNPSet_prog_di.dismiss();
				error.printStackTrace();
			}
		});
		SetSnpQueue.add(request_setSnp);
	}

	private void getSNP_spinner() {
		String sp_url = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/fg_codeSnp.php";
		String fg_code_itemcode = ord_set_snp_sp_tx_bom;
		String sp_project_name = ord_set_snp_sp_tx_project_name;
		HashMap<String, String> par_OrderSpin = new HashMap<>();
		par_OrderSpin.put("FG_Code_Normal", fg_code_itemcode);
		par_OrderSpin.put("User_Project_Name", sp_project_name);
		JSONObject data_orderSetSpin = new JSONObject(par_OrderSpin);
		JsonObjectRequest request_setSpin = new JsonObjectRequest(sp_url, data_orderSetSpin, new Response.Listener<JSONObject>() {
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
					spinnerAdapter = new SpinnerAdapter(SpecialOrderSNPSetActivity.this, spinList);
					spinner.setAdapter(spinnerAdapter);
					spinner.setOnItemSelectedListener(SpecialOrderSNPSetActivity.this);
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

	String date_send = "date";
	@Override
	public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		String currentDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
		dateView.setText(currentDateString);
		date_send = (year + "-" + (month + 1) + "-" + dayOfMonth);
	}

	@Override
	public void onItemClick(final int position, String str_partNumber, final String str_snp, String str_ship ){
		speOrdSNPSet_et_filter.getText().clear();
		final Dialog dialog_snpSet = new Dialog(SpecialOrderSNPSetActivity.this);
		dialog_snpSet.setContentView(R.layout.layout_alert_order_set);
		dialog_snpSet.setCancelable(false);
		Button alert_bt_close = (Button) dialog_snpSet.findViewById(R.id.alert_bt_close);
		alert_bt_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog_snpSet.dismiss();
			}
		});
		final TextView alert_title_snp = (TextView) dialog_snpSet.findViewById(R.id.alert_tv_title_order);
		final ImageView alert_img_box = (ImageView) dialog_snpSet.findViewById(R.id.alert_img_order);
		final TextView alert_name_boxIMG = (TextView) dialog_snpSet.findViewById(R.id.alert_tv_name_box);
		final TextView alert_name_box = (TextView) dialog_snpSet.findViewById(R.id.alert_tv_name_box2);
		final Button alert_bt_minus = (Button) dialog_snpSet.findViewById(R.id.alert_number_minus);
		final EditText alert_data_customerBOX = (EditText) dialog_snpSet.findViewById(R.id.alert_number_customer);
		alert_data_customerBOX.setText("1");
		dateView = (TextView) dialog_snpSet.findViewById(R.id.alert_input_emer_date);
		dateView.addTextChangedListener(TextWatcher);
		dateView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment datePicker = new DatePickerFragment();
				datePicker.show(getSupportFragmentManager(), "date picker");
			}
		});
		alert_input_refer = (EditText) dialog_snpSet.findViewById(R.id.alert_input_ord_refer);
		alert_input_refer.addTextChangedListener(TextWatcher);
		str_refer = alert_input_refer.getText().toString().trim();
		final Button alert_add_bt = (Button) dialog_snpSet.findViewById(R.id.alert_number_add);
		alert_bt_cart = (Button) dialog_snpSet.findViewById(R.id.alert_set_bt_cart);
		alert_rv_itemMenu = (RecyclerView) dialog_snpSet.findViewById(R.id.tit_alert_rv_part_set);
		alert_rv_itemMenu.setHasFixedSize(false);
		alert_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
		dataAlertSetArrayList = new ArrayList<DataAlertSet>();
		api_alert(str_partNumber, str_snp);

		data = alert_data_customerBOX.getText().toString().trim();
		alert_bt_minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("TAG", "a : " + a);
				int edit_int = Integer.parseInt(data);
				a = edit_int - 1;

				if (a >= 1 && a != 0) {
					data = String.valueOf(a);
					alert_data_customerBOX.setText(data);
//					String data_minus = String.valueOf(a - 1);
//					alert_data_customerBOX.setText(data_minus);

					for (int i = 0; i < part_setAdapter.getItemCount(); i++) {
						View view = alert_rv_itemMenu.getLayoutManager().findViewByPosition(i);
						TextView tx_packing = (TextView) view.findViewById(R.id.alert_tv_packing_snp);
						TextView tx_qty = (TextView) view.findViewById(R.id.alert_tv_qty);
						String str_packing = tx_packing.getText().toString().trim();
						String str_qty = tx_qty.getText().toString().trim();
						int int_packing = Integer.parseInt(str_packing);
						int int_qty = Integer.parseInt(str_qty);

						if (int_packing > 0 && int_qty > 0 && int_qty != 0) {
							Log.e("TAG", " int_packing > 0 && int_qty != 0 ");

							for (int x = 0; x < part_setAdapter.getItemCount(); x++) {
								int minus_qty = int_qty - int_packing;
								tx_qty.setText(String.valueOf(minus_qty));
							}
						}
						if (int_packing > 0 && int_qty == 0) {
							Log.e("TAG", " int_packing > 0 && int_qty == 0 ");
							alert_data_customerBOX.getText().clear();
							alert_data_customerBOX.setText(String.valueOf(1));

						}
					}
				} else {
					alert_data_customerBOX.getText().clear();
					alert_data_customerBOX.setText(String.valueOf(1));
				}

			}
		});
		alert_add_bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int data_minteger = 0;
				int minteger = Integer.parseInt(data);
				data_minteger = minteger + 1;
				data = String.valueOf(data_minteger);
				alert_data_customerBOX.setText(data);
				for (int y = 0; y < alert_rv_itemMenu.getChildCount(); y++) {
					RecyclerView.ViewHolder holder = alert_rv_itemMenu.getChildViewHolder(alert_rv_itemMenu.getChildAt(y));
					Log.i("TAG_holder", "change holder : " + holder);
					Log.i("TAG_holder", "change getChildCount() : " + alert_rv_itemMenu.getChildCount());
					Log.i("TAG_holder", "change getAdapterPosition() : " + holder.getAdapterPosition());

					for (int i = 0; i < part_setAdapter.getItemCount(); i++) {
						View view = alert_rv_itemMenu.getLayoutManager().findViewByPosition(i);
						TextView tx_packing = (TextView) view.findViewById(R.id.alert_tv_packing_snp);
						TextView tx_qty = (TextView) view.findViewById(R.id.alert_tv_qty);
						String str_packing = tx_packing.getText().toString().trim();
						int int_packing = Integer.parseInt(str_packing);
						for (int x = 0; x < part_setAdapter.getItemCount(); x++) {
							int result_num2 = Integer.parseInt(data);
							data_minteger = int_packing * result_num2;
							tx_qty.setText(String.valueOf(data_minteger));
						}
					}
				}
			}
		});

		alert_bt_cart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String input_ref = alert_input_refer.getText().toString().trim();
				final String part_num = alert_name_box.getText().toString().trim();
				final Dialog di_cartSave = new Dialog(SpecialOrderSNPSetActivity.this);
				di_cartSave.setContentView(R.layout.layout_alert_confrim);
				di_cartSave.setCancelable(false); // dismiss when touching outside Dialog
				Button al_cart_bt_close = (Button) di_cartSave.findViewById(R.id.al2_bt_close);
				al_cart_bt_close.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						di_cartSave.dismiss();
					}
				});
				Button al_no = (Button) di_cartSave.findViewById(R.id.al2_bt_no);
				al_no.setVisibility(View.GONE);
				Button al_yes = (Button) di_cartSave.findViewById(R.id.al2_bt_yes);
				al_yes.setGravity(Gravity.CENTER);
				al_yes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (int i = 0; i < part_setAdapter.getItemCount(); i++) {
							View view = alert_rv_itemMenu.getLayoutManager().findViewByPosition(i);
							TextView tx_part_com = (TextView) view.findViewById(R.id.alert_tv_part_com);
							TextView tx_qty = (TextView) view.findViewById(R.id.alert_tv_qty);
							TextView tx_fg_code_gdj = (TextView) view.findViewById(R.id.set_tx_fg_code_gdj_usCom);
							String str_fg_code_gdj = tx_fg_code_gdj.getText().toString().trim();
							String ord_set_snp_sp_str_qty = tx_qty.getText().toString().trim();
							int int_qty = Integer.parseInt(ord_set_snp_sp_str_qty);
							String ord_set_snp_sp_str_sku_code_abt = tx_part_com.getText().toString().trim();
							api_saveOrderset(ord_set_snp_sp_str_sku_code_abt, ord_set_snp_sp_str_qty,
									input_refer,part_num,str_fg_code_gdj, str_snp);
						}
						startActivity(new Intent(SpecialOrderSNPSetActivity.this, CenterActivity.class));
						di_cartSave.dismiss();
						dialog_snpSet.dismiss();
					}
				});
				di_cartSave.show();
			}
		});
		setDataToViewTest(position, str_partNumber, alert_name_boxIMG, alert_img_box, alert_name_box); //mAmount, al_img_box, al_name_boxIMG, al_name_box, al_size_box
		dialog_snpSet.show();
	}


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
						String str_part_sku_code_abt = num_resulItem.getString("bom_fg_sku_code_abt");
						String str_part_usage = num_resulItem.getString("bom_usage");
						String str_part_pack = num_resulItem.getString("bom_packing");
						String str_part_fg_code_set_abt = num_resulItem.getString("bom_fg_code_set_abt");
						String str_part_fg_code_gdj = num_resulItem.getString("bom_fg_code_gdj");
						String str_part_ship_type = num_resulItem.getString("bom_ship_type");
						ord_set_snp_sp_tx_fg_code_set_abt = str_part_fg_code_set_abt;
						ord_set_snp_sp_tx_ship_type = str_part_ship_type;
						dataAlertSetArrayList.add(new DataAlertSet(str_part_sku_code_abt,
								str_part_usage, str_part_pack, str_part_pack,str_part_fg_code_gdj));
					}

					ViewGroup.LayoutParams params=alert_rv_itemMenu.getLayoutParams();
					params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
					alert_rv_itemMenu.setLayoutParams(params);
					part_setAdapter = new AlertPartSetAdapter(getApplicationContext(), dataAlertSetArrayList);
					alert_rv_itemMenu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
					alert_rv_itemMenu.setAdapter(part_setAdapter);
					part_setAdapter.notifyDataSetChanged();
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

	private void api_saveOrderset(String ord_set_snp_sp_tx_sku_code_abt, String ord_set_snp_sp_tx_qty,
								  String input_refer, String part_num, String str_fg_code_gdj, String str_snp) {
		String urlSave = "https://lac-apps.albatrossthai.com/api/VMI/php/Order_Emer/Emer_OrderSave.php";
		sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		ord_set_snp_sp_tx_unit_type = "Set";
		ord_set_snp_sp_tx_username = sp.getString(USER_NAME, "");
		ord_set_snp_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
		ord_set_snp_sp_tx_cus = sp.getString(USER_PART_CUS, "");
		String fg_code_set_abt = ord_set_snp_sp_tx_fg_code_set_abt;
		String SKU_Code = ord_set_snp_sp_tx_sku_code_abt;
		String Replen_Qty = ord_set_snp_sp_tx_qty;
		String Terminal_ID = ord_set_snp_sp_tx_project_name;
		String Replen_Type = "Special Order";
		String Replen_Unit_Type = ord_set_snp_sp_tx_unit_type;
		String Project_Name = ord_set_snp_sp_tx_project_name;
		String User_ID = ord_set_snp_sp_tx_username;
		String repn_fg_code_gdj = str_fg_code_gdj;
		String repn_ship_type = ord_set_snp_sp_tx_ship_type;
		String repn_part_customer = part_num;
		String repn_delivery_date = date_send;
		String repn_order_ref = input_refer;
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
		SetSnpQueue.add(request_partNum);
	}

	private void setDataToViewTest(int position, String str_partNumber, TextView boxName, ImageView boxIMG, TextView boxSub) { // int mAmount
		boxName.setText(ord_set_snp_sp_tx_bom);
		boxSub.setText(str_partNumber);
		String url_img = "https://lac-apps.albatrossthai.com/api/VMI/Image_GDJ/" + ord_set_snp_sp_tx_bom + ".png";
		Glide.with(this)
				.load(url_img)
				.centerInside()
				.placeholder(R.drawable.ic_add_photo)
				.into(boxIMG);
		snpAdapter.notifyItemChanged(position);
	}

	public void onClickBack(View view) {
		if (view.getId() == R.id.bt_back) {
			speOrdSNPSet_et_filter.getText().clear();
			startActivity(new Intent(getApplicationContext(), SpecialOrderSetActivity.class));
		}
	}
}