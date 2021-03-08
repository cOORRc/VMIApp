package com.KioskApp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.KioskApp.ui.hideSystemUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GuideActivity extends AppCompatActivity {

    /* SharedPreferences */
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    /* key SharedPreferences*/
    final String PREF_NAME = "Preferences";
    final String USER_NAME = "Username";
    final String USER_PROJECT_NAME = "ProjectName";
    final String USER_TYPE_USAGE = "type_usage";
    /* variables SharedPre */
    String gui_sp_tx_username, gui_sp_tx_project_name;
    String gui_sp_str_type_usage = "str_type_usage";

    /* variables ui */
    private WebView web_view;
    private Button guide_bt_back;
    private TextView guide_tv_title_cus_service, guide_tv_welcome, guide_tv_gdj_vmi_system, guide_tv_customer, guide_tv_customer_name, guide_tv_datetime, guide_tv_ddmmyy;
    private ImageView guide_img_logo_gdj;
    private ProgressDialog proDi_guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI.hideNavigations(this);
        setContentView(R.layout.activity_guide);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        lodeSharedpref();
        init();
        settingWedView();
    }
/* set wed view */
    @SuppressLint("SetJavaScriptEnabled")
        private void settingWedView() {
        web_view = findViewById(R.id.pdf_view);
        web_view.setWebViewClient(new WebViewClient());
        WebSettings webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true); // อนุญาติให้ WebView ทำงานร่วมกับ JavaScript ของฝั่งเว็บไซต์
        webSettings.setDisplayZoomControls(false); // ไม่อนุญาติให้ zoom
        String filename ="https://lac-apps.albatrossthai.com/vmi/manual/VMI-terminal_and_mobile.pdf";
        web_view.loadUrl("https://docs.google.com/gview?embedded=true&url=" + filename);
        web_view.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                proDi_guide.dismiss();
            }
        });

    }

    private void lodeSharedpref() {
        sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE); // set sp file
        editor = sp.edit();  // keep data in Shared Preferences
        gui_sp_tx_username = sp.getString(USER_NAME, ""); // ข้อมูลที่จะเก็บ
        gui_sp_tx_project_name = sp.getString(USER_PROJECT_NAME, "");
        gui_sp_str_type_usage = sp.getString(USER_TYPE_USAGE, "");
    }

/* set ui */
    private void init() {
        guide_tv_title_cus_service = findViewById(R.id.tv_cus_service);
        guide_tv_title_cus_service.setText(R.string.tx_cus_service);
        guide_tv_title_cus_service.setVisibility(View.VISIBLE);
        guide_tv_title_cus_service.setTextSize(40);
        guide_tv_welcome = findViewById(R.id.tv_welcome_to);
        guide_tv_welcome.setText(R.string.tx_welcome);
        guide_tv_welcome.setVisibility(View.VISIBLE);
        guide_tv_gdj_vmi_system = findViewById(R.id.tv_gdj_vmi_system);
        guide_tv_customer = findViewById(R.id.tv_customer);
        guide_tv_customer_name = findViewById(R.id.tv_customer_name);
        guide_tv_customer_name.setText(gui_sp_tx_username);
        guide_img_logo_gdj = findViewById(R.id.img_logo);
        guide_bt_back = findViewById(R.id.bt_back);
        guide_bt_back.setVisibility(View.VISIBLE);
        guide_bt_back.setText(R.string.tx_back);
        guide_tv_datetime = findViewById(R.id.tv_datetime);
        guide_tv_ddmmyy = findViewById(R.id.tv_time);
        /* set date&&time every min. */
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
                                guide_tv_ddmmyy.setText(currentdate + " : " + currenttime);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        /*end set time*/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                        LoginHandler();
            }
        }, 1000);
        /*set progress*/
        proDi_guide = new ProgressDialog(GuideActivity.this, R.style.Dialg);
        proDi_guide.setMessage("Loading...");
        proDi_guide.setTitle(getResources().getString(R.string.tx_vmi_system));
        proDi_guide.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDi_guide.show();
        proDi_guide.setCancelable(false);
    }

    public void onClickBack(View view) {
        if (view.getId() == R.id.bt_back) {
            startActivity(new Intent(getApplicationContext(), CenterActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please wait a moment", Toast.LENGTH_SHORT).show();
    }
}
