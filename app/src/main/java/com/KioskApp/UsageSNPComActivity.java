package com.KioskApp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.KioskApp.ui.hideKeyboard;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.KioskApp.adapter.AlertUsageComAdapter;
import com.KioskApp.adapter.SNPOrderAdapter;
import com.KioskApp.adapter.SpinnerAdapter;
import com.KioskApp.model.DataAlertUsageCom;
import com.KioskApp.model.DataSNPSet;
import com.KioskApp.model.DataSpin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class UsageSNPComActivity extends AppCompatActivity implements SNPOrderAdapter.OnItemClickListener,
        AdapterView.OnItemSelectedListener {

    private TextView usageSNPCom_tv_title_cus_service, usageSNPCom_tv_gdj_vmi_system, usageSNPCom_tv_customer, usageSNPCom_tv_customer_name, usageSNPCom_tv_emergency_set_imc, usageSNPCom_tv_emergency_set;

    private ImageView usageSNPCom_img_logo_gdj;
    private Button usageSNPCom_bt_back;
    private Button usageSNPCom_bt_cart;

    private Spinner spinner;
    private ArrayList<DataSpin> List;
    private SpinnerAdapter spinnerAdapter;
    String clickedSpinner;

    private RecyclerView usageSNPCom_rv_itemMenu;
    private ArrayList<DataSNPSet> usageComList;
    private SNPOrderAdapter com_imc_Adapter;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    final String PREF_NAME = "Preferences";
    final String USER_NAME = "Username";
    final String USER_PROJECT_NAME = "ProjectName";
    final String USER_BOM = "Bom";
    final String USER_TYPE_USAGE = "type_usage";
    String usage_com_snp_sp_tx_username, usage_com_snp_sp_tx_project_name, usage_com_snp_sp_tx_bom;
    String part_com_number, ship_type_com;

    private RequestQueue usageSNPComQueue;

    private int xx = 0;
    private String sta_idNum;
    private Dialog di_usage_snpCom;
    private TextView usageSNPCom_tv_datetime, usageSNPCom_tv_ddmmyy;
    private ProgressDialog usageSNPCom_progDi;
    private EditText usageSNPCom_et_filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_spe_order_snp_set);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideKeyboard.setupUI(findViewById(R.id.container_partNumber), this);
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        usage_com_snp_sp_tx_username = sp.getString(USER_NAME, "");
        usage_com_snp_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
        usage_com_snp_sp_tx_bom = sp.getString(USER_BOM, "");
        usageSNPComQueue = Volley.newRequestQueue(this);
        getSNP_spinner();
        init_snpCom();
        callApi_MyCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callApi_MyCart();
    }

    private void init_snpCom() {
        usageSNPCom_tv_title_cus_service = findViewById(R.id.tv_cus_service);
        usageSNPCom_tv_title_cus_service.setText(R.string.tx_cus_service);
        usageSNPCom_tv_title_cus_service.setVisibility(View.VISIBLE);
        usageSNPCom_tv_title_cus_service.setTextSize(40);
        usageSNPCom_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
        usageSNPCom_tv_customer = findViewById(R.id.tv_customer);
        usageSNPCom_tv_customer_name = findViewById(R.id.tv_customer_name);
        usageSNPCom_tv_customer_name.setText(usage_com_snp_sp_tx_username);
        usageSNPCom_tv_datetime = findViewById(R.id.tv_datetime);
        usageSNPCom_tv_ddmmyy = findViewById(R.id.tv_time);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String currenttime = simpleDateFormat.format(calendar.getTime());
                                String currentdate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
                                usageSNPCom_tv_ddmmyy.setText(currentdate + " : " + currenttime);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        usageSNPCom_img_logo_gdj = findViewById(R.id.img_logo);
        usageSNPCom_bt_back = findViewById(R.id.bt_back);
        usageSNPCom_bt_back.setVisibility(View.VISIBLE);
        usageSNPCom_bt_back.setText(R.string.tx_back);
        usageSNPCom_tv_emergency_set_imc = findViewById(R.id.spe_order_set_tv_snp_imc);
        usageSNPCom_tv_emergency_set_imc.setText("CARTON CODE NORMAL " + usage_com_snp_sp_tx_bom);
        usageSNPCom_tv_emergency_set = findViewById(R.id.spe_order_set_snp_tv_title);
        usageSNPCom_tv_emergency_set.setText(R.string.tx_order_usageCon_component);
        usageSNPCom_bt_cart = findViewById(R.id.spe_order_set_snp_bt_cart_count);
        usageSNPCom_bt_cart.setVisibility(View.VISIBLE);
        spinner = findViewById(R.id.spe_order_set_snp_spin_snp);
        List = new ArrayList<DataSpin>();
        List.add(new DataSpin("choose"));

        /*	26/01/64 add filter*/
        usageSNPCom_et_filter = findViewById(R.id.spe_order_set_snp_ed_filter);
        usageSNPCom_et_filter.addTextChangedListener(new TextWatcher() {
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

        usageSNPCom_rv_itemMenu = findViewById(R.id.special_set_item_manu);
        usageSNPCom_rv_itemMenu.setHasFixedSize(false);
        usageSNPCom_rv_itemMenu.setLayoutManager(new LinearLayoutManager(this));
        usageComList = new ArrayList<DataSNPSet>();


    }

    /*	26/01/64 add filter*/
    private void filter(String str_fil) {
        ArrayList<DataSNPSet> filteredList = new ArrayList<>();
        for (DataSNPSet item : usageComList) {
            if (item.getTextPartNumber().toLowerCase().contains(str_fil.toLowerCase())) {
                filteredList.add(item);
            }
        }
        com_imc_Adapter.filterList(filteredList);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DataSpin clickedItem = (DataSpin) parent.getItemAtPosition(position);
        clickedSpinner = clickedItem.getSpinner();
        if (clickedSpinner.equals("choose")) {
            progDialog();
            usageComList.clear();
            clickedSpinner = "";
            getPartNumber();
        } else {
            progDialog();
            usageComList.clear();
            getPartNumber();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void progDialog() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
        usageSNPCom_progDi = new ProgressDialog(UsageSNPComActivity.this, R.style.Dialg);
        usageSNPCom_progDi.setMessage("Loading..."); // Setting Message
        usageSNPCom_progDi.setTitle(getResources().getString(R.string.tx_vmi_system)); // Setting Title
        usageSNPCom_progDi.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        usageSNPCom_progDi.show(); // Display Progress Dialog
        usageSNPCom_progDi.setCancelable(false);
    }

    private void getPartNumber() {
        String urlUsageCom = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/Part_fgCodeCus.php";
        String FG_Code = usage_com_snp_sp_tx_bom;
        String User_Project_Name = usage_com_snp_sp_tx_project_name;
        String FG_Snp = clickedSpinner;
        HashMap<String, String> par_usageComSnp = new HashMap<>();
        par_usageComSnp.put("FG_Code_Normal", FG_Code);
        par_usageComSnp.put("User_Project_Name", User_Project_Name);
        par_usageComSnp.put("FG_Snp", FG_Snp);
        JSONObject data_usageComSNP = new JSONObject(par_usageComSnp);
        JsonObjectRequest request_ComSnp = new JsonObjectRequest(urlUsageCom, data_usageComSNP, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject js_snp_com = new JSONObject(String.valueOf(response));
                    String str_idSNPCom = js_snp_com.getString("Status");
                    JSONArray ja_snpResultComData = js_snp_com.getJSONArray("ResultData");
                    ArrayList<String> ar_resultCom = new ArrayList<String>();
                    for (int i = 0; i < ja_snpResultComData.length(); i++) {
                        JSONObject part_resultComItem = ja_snpResultComData.getJSONObject(i);
                        part_com_number = part_resultComItem.getString("bom_part_customer");
                        ship_type_com = part_resultComItem.getString("bom_ship_type");
                        int usageSNPCom_bom_snp = part_resultComItem.getInt("bom_snp");
                        String usageSNPCom_str_bom_snp = String.valueOf(usageSNPCom_bom_snp);
                        usageComList.add(new DataSNPSet(part_com_number, usageSNPCom_str_bom_snp, ship_type_com));
                    }
                    usageSNPCom_progDi.dismiss();
                    com_imc_Adapter = new SNPOrderAdapter(getApplicationContext(), usageComList);
                    usageSNPCom_rv_itemMenu.setAdapter(com_imc_Adapter);
                    com_imc_Adapter.setOnItemClickListener(UsageSNPComActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                usageSNPCom_progDi.dismiss();
                error.printStackTrace();
            }
        });
        usageSNPComQueue.add(request_ComSnp);
    }

    private void getSNP_spinner() {
        String url_usageSNPcom = "https://lac-apps.albatrossthai.com/api/VMI/php/Master_Data/fg_codeSnp.php";
        String fg_code_itemcode = usage_com_snp_sp_tx_bom;
        String sp_project_name = usage_com_snp_sp_tx_project_name;
        HashMap<String, String> par_usageComSpin = new HashMap<>();
        par_usageComSpin.put("FG_Code_Normal", fg_code_itemcode);
        par_usageComSpin.put("User_Project_Name", sp_project_name);
        JSONObject data_usageSetSpin = new JSONObject(par_usageComSpin);
        JsonObjectRequest request_ComSpin = new JsonObjectRequest(url_usageSNPcom, data_usageSetSpin, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsSpinCom = new JSONObject(String.valueOf(response));
                    String str_idSpinCom = jsSpinCom.getString("Status");
                    JSONArray ja_spinComResultData = jsSpinCom.getJSONArray("ResultData");
                    ArrayList<String> ar_resultComSpin = new ArrayList<String>();
                    for (int i = 0; i < ja_spinComResultData.length(); i++) {
                        JSONObject spin_resulComItem = ja_spinComResultData.getJSONObject(i);
                        String str_spinCom = spin_resulComItem.getString("bom_snp");
                        List.add(new DataSpin(str_spinCom));
                    }
                    spinnerAdapter = new SpinnerAdapter(UsageSNPComActivity.this, List);
                    spinner.setAdapter(spinnerAdapter);
                    spinner.setOnItemSelectedListener(UsageSNPComActivity.this);
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
        usageSNPComQueue.add(request_ComSpin);
    }

    public void onClickUsage(View view) {
        if (view.getId() == R.id.spe_order_set_snp_bt_cart_count) {
            usageSNPCom_et_filter.getText().clear();
            startActivity(new Intent(getApplicationContext(), OrderListAllActivity.class));
        }
    }

    public void onClickBack(View view) {
        if (view.getId() == R.id.bt_back) {
            usageSNPCom_et_filter.getText().clear();
            startActivity(new Intent(getApplicationContext(), UsageComponentActivity.class));
        }
    }

    private ImageView alert_usage_snp_com_img_imc;
    private TextView alert_usage_snp_com_tv_ctn_code, alert_usage_snp_com_tv_part_num;
    private RecyclerView alert_usage_snp_com_rv_part_com;
    private Button alert_usage_snp_com_bt_close, alert_usage_snp_com_bt_cart;
    private TextView alert_usage_snp_com_tv_subSec_ctn_code, alert_usage_snp_com_tv_subSec_part_num;
    private ArrayList<DataAlertUsageCom> alertUsageComArrayList;
    private TextView tx_pack, tx_stock, tx_qty;
    private EditText ed_picker;
    private String tx_rv_stock, tx_rv_qty, tx_rv_picker, tx_rv_pack;
    private EditText et_picker;
    private Button bt_add;
    private Button bt_minus;
    private View view;

    @Override
    public void onItemClick(int position, final String str_partNumber, final String str_snp, final String str_ship) {
        usageSNPCom_et_filter.getText().clear();
        com_snp_queue = Volley.newRequestQueue(this);
        di_usage_snpCom = new Dialog(UsageSNPComActivity.this);
        di_usage_snpCom.setContentView(R.layout.layout_alert_usage_com);
        di_usage_snpCom.setCancelable(false);
        alert_usage_snp_com_bt_close = di_usage_snpCom.findViewById(R.id.al_com_bt_tit_close);
        alert_usage_snp_com_bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                di_usage_snpCom.dismiss();
            }
        });
        alert_usage_snp_com_img_imc = di_usage_snpCom.findViewById(R.id.al_com_img_tit_order);
        String url_img = "https://lac-apps.albatrossthai.com/api/VMI/Image_GDJ/" + usage_com_snp_sp_tx_bom + ".png";
        Glide.with(this)
                .load(url_img)
                .centerInside()
                .placeholder(R.drawable.ic_add_photo)
                .into(alert_usage_snp_com_img_imc);
        alert_usage_snp_com_tv_ctn_code = di_usage_snpCom.findViewById(R.id.al_com_tv_tit_name_box);
        alert_usage_snp_com_tv_subSec_ctn_code = di_usage_snpCom.findViewById(R.id.al_com_tv_subSec_cotton);
        alert_usage_snp_com_tv_subSec_ctn_code.setText(usage_com_snp_sp_tx_bom);
        alert_usage_snp_com_tv_part_num = di_usage_snpCom.findViewById(R.id.al_com_tv_tit_name_box2);
        alert_usage_snp_com_tv_subSec_part_num = di_usage_snpCom.findViewById(R.id.al_com_tv_subSec_partCom);
        alert_usage_snp_com_tv_subSec_part_num.setText(str_partNumber);
        alert_usage_snp_com_rv_part_com = di_usage_snpCom.findViewById(R.id.al_com_rv_part_com);
        alert_usage_snp_com_rv_part_com.setHasFixedSize(false);
        alert_usage_snp_com_rv_part_com.setLayoutManager(new LinearLayoutManager(this));
        alertUsageComArrayList = new ArrayList<DataAlertUsageCom>();
        api_alert(str_partNumber, str_ship, str_snp);
        alert_usage_snp_com_bt_cart = di_usage_snpCom.findViewById(R.id.al_com_bt_cart);
        alert_usage_snp_com_bt_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usageCom_snp_saveQueue = Volley.newRequestQueue(UsageSNPComActivity.this);
                int count_array = usageComList.size();
                for (int i = 0; i < alert_part_ComAdapter.getItemCount(); i++) {
                    view = alert_usage_snp_com_rv_part_com.getLayoutManager().findViewByPosition(i);
                    TextView hip_tx_fg_code_gdj = (TextView) view.findViewById(R.id.tx_fg_code_gdj_usCom);
                    TextView hip_tx_fg_code_set_abt = (TextView) view.findViewById(R.id.tx_fg_code_set_abt_usCom);
                    TextView tx_part_com = (TextView) view.findViewById(R.id.al_us_snp_com_tv_part);
                    TextView tx_stock = (TextView) view.findViewById(R.id.al_us_snp_com_tv_stock);
                    TextView tx_qty = (TextView) view.findViewById(R.id.al_us_snp_com_tv_qty);
                    EditText ed_picker = (EditText) view.findViewById(R.id.al_us_snp_com_num_cus);
                    String tx_rv_sku_code_abt = tx_part_com.getText().toString().trim();
                    String tx_rv_stock = tx_stock.getText().toString().trim();
                    String tx_rv_qty = tx_qty.getText().toString().trim();
                    String tx_rv_picker = ed_picker.getText().toString().trim();
                    int in_picker = Integer.parseInt(tx_rv_picker);
                    if (in_picker != 0) {
                        xx = xx + 1;
                    }
                }
                if (xx > 0) {
                    final Dialog di_usage_snpComNor = new Dialog(UsageSNPComActivity.this);
                    di_usage_snpComNor.setContentView(R.layout.layout_alert_confrim);
                    di_usage_snpComNor.setCancelable(true);
                    Button al_close = di_usage_snpComNor.findViewById(R.id.al2_bt_close);
                    al_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            xx = 0;
                            di_usage_snpComNor.dismiss();
                        }
                    });
                    Button confirm_bt_yes = di_usage_snpComNor.findViewById(R.id.al2_bt_yes);
                    confirm_bt_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int s = 0; s < alert_part_ComAdapter.getItemCount(); s++) {
                                View view = alert_usage_snp_com_rv_part_com.getLayoutManager().findViewByPosition(s);
                                TextView tv_rv_part_sku = (TextView) view.findViewById(R.id.al_us_snp_com_tv_part);
                                TextView hip_tx_fg_code_gdj = (TextView) view.findViewById(R.id.tx_fg_code_gdj_usCom);
                                TextView hip_tx_fg_code_set_abt = (TextView) view.findViewById(R.id.tx_fg_code_set_abt_usCom);
                                TextView tv_packing_snp = (TextView) view.findViewById(R.id.al_us_snp_com_tv_packing_snp);
                                String tx_rv_fg_code_gdj = hip_tx_fg_code_gdj.getText().toString().trim();
                                String tx_rv_fg_code_set_abt = hip_tx_fg_code_set_abt.getText().toString().trim();
                                TextView tx_qty = (TextView) view.findViewById(R.id.al_us_snp_com_tv_qty);
                                EditText ed_picker = (EditText) view.findViewById(R.id.al_us_snp_com_num_cus);
                                String tx_rv_sku = tv_rv_part_sku.getText().toString().trim();
                                String tx_rv_pack = tv_packing_snp.getText().toString().trim();
                                String tx_rv_pic = ed_picker.getText().toString().trim();
                                String tx_rv_qty = tx_qty.getText().toString().trim();
                                int numPic = Integer.parseInt(tx_rv_pic);
                                int numQ = Integer.parseInt(tx_rv_qty);
                                callApi_MyCart();
                                if (numQ != 0) {
                                    api_saveUsagecom(str_partNumber, str_ship, tx_rv_sku, tx_rv_pack,
                                            tx_rv_fg_code_gdj, tx_rv_fg_code_set_abt, numPic, str_snp);
                                }
                            }
                            di_usage_snpComNor.dismiss();
                            di_usage_snpCom.dismiss();
                        }
                    });
                    Button confirm_bt_no = di_usage_snpComNor.findViewById(R.id.al2_bt_no);
                    confirm_bt_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            xx = 0;
                            di_usage_snpComNor.dismiss();
                            di_usage_snpCom.dismiss();
                        }
                    });
                    di_usage_snpComNor.show();
                } else {
                    final Dialog di_usage_snpComNull = new Dialog(UsageSNPComActivity.this);
                    di_usage_snpComNull.setContentView(R.layout.layout_alert_zero);
                    di_usage_snpComNull.setCancelable(true);
                    Button al_close = di_usage_snpComNull.findViewById(R.id.al3_bt_close);
                    al_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            xx = 0;
                            di_usage_snpComNull.dismiss();
                        }
                    });
                    Button confrim_bt_yes = di_usage_snpComNull.findViewById(R.id.al3_bt_yes);
                    confrim_bt_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            xx = 0;
                            di_usage_snpComNull.dismiss();
                        }
                    });
                    di_usage_snpComNull.show();
                }
            }
        });
        di_usage_snpCom.show();
    }

    private AlertUsageComAdapter alert_part_ComAdapter;
    private RequestQueue com_snp_queue;

    private void api_alert(String str_partNumber, String str_ship, String str_snp) {
        String url_usage_partNum = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Part_Com_Stock.php";
        final String fg_code_partNumber = str_partNumber;
        String proj_name = usage_com_snp_sp_tx_project_name;
        HashMap<String, String> partNumber = new HashMap<>();
        partNumber.put("FG_Code_Customer", fg_code_partNumber);
        partNumber.put("Project_Name", proj_name);
        partNumber.put("Ship_Type", str_ship);
        partNumber.put("bom_snp", str_snp);
        JSONObject data_partNumber = new JSONObject(partNumber);
        JsonObjectRequest request_partNum = new JsonObjectRequest(url_usage_partNum, data_partNumber, new Response.Listener<JSONObject>() {
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
                        String str_ps_t_sku_code_abt = num_resultItem.getString("bom_fg_sku_code_abt");
                        String str_part_usage = num_resultItem.getString("bom_usage");
                        String str_part_stock = num_resultItem.getString("Stocm_VMI");
                        String str_part_pack = num_resultItem.getString("bom_packing");
                        alertUsageComArrayList.add(new DataAlertUsageCom(str_ps_t_sku_code_abt,
                                str_part_usage, str_part_pack, str_part_stock, "0", "0",
                                str_fg_code_gdj, str_fg_code_set_abt));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (sta_idNum.equals("1")) {
                    ViewGroup.LayoutParams params = alert_usage_snp_com_rv_part_com.getLayoutParams();
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    alert_usage_snp_com_rv_part_com.setLayoutParams(params);

                    alert_part_ComAdapter = new AlertUsageComAdapter(getApplicationContext(), alertUsageComArrayList);
                    alert_usage_snp_com_rv_part_com.setAdapter(alert_part_ComAdapter);
                    alert_part_ComAdapter.notifyDataSetChanged();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int s = 0; s < alert_part_ComAdapter.getItemCount(); s++) {
                                View view = alert_usage_snp_com_rv_part_com.getLayoutManager().findViewByPosition(s);
                                TextView tx_stock = (TextView) view.findViewById(R.id.al_us_snp_com_tv_stock);
                                TextView tx_qty = (TextView) view.findViewById(R.id.al_us_snp_com_tv_qty);
                                String tx_rv_stock = tx_stock.getText().toString().trim();
                                int int_rv_stock = Integer.parseInt(tx_rv_stock);
                                String tx_rv_qty = tx_qty.getText().toString().trim();
                                int int_rv_qty = Integer.parseInt(tx_rv_qty);

                                bt_add = (Button) view.findViewById(R.id.al_us_snp_com_number_add);
                                bt_minus = (Button) view.findViewById(R.id.al_us_snp_com_number_minus);
                                et_picker = (EditText) view.findViewById(R.id.al_us_snp_com_num_cus);

                                if (tx_rv_stock.equals(0) || tx_rv_stock.equals("0")) {
                                    alert_usage_snp_com_bt_cart.setEnabled(false);
                                    bt_add.setEnabled(false);
                                    bt_minus.setEnabled(false);
                                    et_picker.setEnabled(false);
                                    alert_usage_snp_com_bt_cart.setBackgroundResource(R.color.color_dum);
                                }
                                if (int_rv_stock <= int_rv_qty) {
                                    alert_usage_snp_com_bt_cart.setEnabled(false);
                                    bt_add.setEnabled(false);
                                    bt_minus.setEnabled(true);
                                    et_picker.setEnabled(false);
                                    view.setBackgroundColor(0xFFFF8F6F);
                                } else {
                                    alert_usage_snp_com_bt_cart.setEnabled(true);
                                    bt_add.setEnabled(true);
                                    bt_minus.setEnabled(true);
                                    et_picker.setEnabled(true);
                                    alert_usage_snp_com_bt_cart.setBackgroundResource(R.drawable.button_selector);
                                }
                                //TODO:old edit 17-11-20
/*								if (tx_rv_stock.equals(0) || tx_rv_stock.equals("0")) {
									alert_usage_snp_com_bt_cart.setEnabled(false);
									bt_add.setEnabled(false);
									bt_minus.setEnabled(false);
									et_picker.setEnabled(false);
									alert_usage_snp_com_bt_cart.setBackgroundResource(R.color.color_dum);
								} else {
									alert_usage_snp_com_bt_cart.setEnabled(true);
									bt_add.setEnabled(true);
									bt_minus.setEnabled(true);
									et_picker.setEnabled(true);
									alert_usage_snp_com_bt_cart.setBackgroundResource(R.drawable.button_selector);
								}*/
                            }
                        }
                    }, 500);
                } else {
                    alertUsageComArrayList.add(new DataAlertUsageCom("Data not found",
                            "", "", "", "", "0",
                            "", ""));
                    alert_part_ComAdapter = new AlertUsageComAdapter(getApplicationContext(), alertUsageComArrayList);
                    alert_usage_snp_com_rv_part_com.setAdapter(alert_part_ComAdapter);
                    alert_part_ComAdapter.notifyDataSetChanged();
                    alert_usage_snp_com_bt_cart.setEnabled(false);
                    alert_usage_snp_com_bt_cart.setBackgroundResource(R.color.color_dum);
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

    private RequestQueue usageCom_snp_saveQueue;

    private void api_saveUsagecom(String str_partNumber, String str_ship, String tx_rv_sku, String tx_rv_pack,
                                  String tx_rv_fg_code_gdj, String tx_rv_fg_code_set_abt, int numPic, String str_snp) {
        String urlSave = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Save_Pre_Com.php";
        String sku = tx_rv_sku;
        String fg_code_set_abt = tx_rv_fg_code_set_abt;
        String fg_code_gdj = tx_rv_fg_code_gdj;
        String Project_Name = usage_com_snp_sp_tx_project_name;
        String order_Type = "Component";
        String bom_packing = tx_rv_pack;
        String pick_qty = String.valueOf(numPic);
        HashMap<String, String> hashMap_save = new HashMap<>();
        hashMap_save.put("Pick_Qty", pick_qty);
        hashMap_save.put("fg_code_set_abt", fg_code_set_abt);
        hashMap_save.put("fg_code_gdj", fg_code_gdj);
        hashMap_save.put("Project_Name", Project_Name);
        hashMap_save.put("ship_type", str_ship);
        hashMap_save.put("part_customer", str_partNumber);
        hashMap_save.put("orderType", order_Type);
        hashMap_save.put("bom_packing", bom_packing);
        hashMap_save.put("fg_sku_code_abt", sku);
        hashMap_save.put("bom_snp", str_snp);
        JSONObject data_save = new JSONObject(hashMap_save);
        JsonObjectRequest request_partNum = new JsonObjectRequest(urlSave, data_save, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonSave = new JSONObject(String.valueOf(response));
                    String sta_id = jsonSave.getString("Status");
                    if (sta_id.equals("1")) {
                        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        editor = sp.edit();
                        editor.putString(USER_TYPE_USAGE, "Com");
                        editor.apply();
                        String f = sp.getString(USER_TYPE_USAGE, "");
                        Log.e("TAG", "com : " + f);
                        startActivity(new Intent(UsageSNPComActivity.this, OrderListAllActivity.class));
                    } else {
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
        usageCom_snp_saveQueue.add(request_partNum);
    }


    private String sta_idMyCart = "0";
    private int sta_tx_MyCart = 0;

    private void callApi_MyCart() {
        String urlMyCart = "https://lac-apps.albatrossthai.com/api/VMI/php/Usage_Confirm/Usage_Qty_MyCart.php";
        String Project_Name = usage_com_snp_sp_tx_project_name;
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
                if (sta_idMyCart.equals("1")) {
                    String num_mycart = String.valueOf(sta_tx_MyCart);
                    usageSNPCom_bt_cart.setText(num_mycart);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        usageSNPComQueue.add(request_myCart);
    }
}