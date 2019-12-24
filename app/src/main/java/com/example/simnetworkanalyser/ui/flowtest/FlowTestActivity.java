package com.example.simnetworkanalyser.ui.flowtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.tools.NetworkUtil;
import com.example.simnetworkanalyser.ui.BaseActivity;
import com.example.simnetworkanalyser.ui.flowtest.chart.FlowLineChartActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FlowTestActivity extends BaseActivity {

    TextInputEditText pingUrl;
    Button pingActionButton;
    Button clearButton;
    ListView pingResult;
    TextView statusText;
    FloatingActionButton pingBtnChart;
    ProgressBar progressBar;

    UrlPingTask pingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_test);

        pingUrl = findViewById(R.id.ping_url);
        pingActionButton = findViewById(R.id.ping_action_btn);
        clearButton = findViewById(R.id.clear_result_btn);
        pingResult = findViewById(R.id.ping_result);
        statusText = findViewById(R.id.status);
        pingBtnChart = findViewById(R.id.ping_btn_chart);
        progressBar = findViewById(R.id.progress_circular);

        // --

        pingBtnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isMobileDataActive(FlowTestActivity.this)) {
                    displayAlertDialog("Please make sure to connect via SIM data!");
                } else {
                    FlowTestActivity.this.startActivity(new Intent(FlowTestActivity.this, FlowLineChartActivity.class));
                    overridePendingTransition(R.anim.slide_in_up_speed, R.anim.fade);
                }
            }
        });

        pingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = pingUrl.getText().toString().trim();

                if (url.isEmpty()) {
                    displayToast("Please make sure to pu un valid URL");
                } else if (!isValidUrl(url)) {
                    displayToast("Invalid web address");
                } else {

                    if (!NetworkUtil.isMobileDataActive(FlowTestActivity.this)) {
                        displayAlertDialog("Please make sure to connect via SIM data!");
                    } else {

                        pingTask = new UrlPingTask();
                        pingTask.execute(url);
                    }
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearButton.setVisibility(View.GONE);
                pingResult.setAdapter(null);
                statusText.setText("No operation in progress!");
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pingTask != null)
            pingTask.cancel(true);
    }


    // -------------------------------------------------------------- //
    // -------------------------------------------------------------- //


    private boolean isValidUrl(String url) {

        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }


    // -------------------------------------------------------------- //
    // -------------------------------------------------------------- //


    private void displayAlertDialog(String msg) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Alert !!");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void displayToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    // -------------------------------------------------------------- //
    // -------------------------------------------------------------- //


    private final class UrlPingTask extends AsyncTask<String, Void, List<String>> {

        private Boolean hasError = false;

        @Override
        protected void onPreExecute() {

            hasError = false;

            clearButton.setVisibility(View.GONE);

            pingResult.setAdapter(null);

            statusText.setText("Processing ..");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(String... urls) {

            List<String> PingResponseList = new ArrayList<String>();

            try {

                String cmdPing = "ping -c 4 " + urls[0];

                Runtime r = Runtime.getRuntime();
                Process p = r.exec(cmdPing);

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    PingResponseList.add(inputLine);
                }

            } catch (Exception e) {

                hasError = true;
                PingResponseList.add(e.getMessage());
            }

            return PingResponseList;
        }

        protected void onPostExecute(List<String> result) {

            statusText.setText("Operation finished.");
            progressBar.setVisibility(View.GONE);

            if (hasError) {

                progressBar.setVisibility(View.GONE);
                statusText.setText(String.format("operation failed : %s", result.get(0)));

            } else {

                pingResult.setAdapter(
                        new ArrayAdapter<String>(FlowTestActivity.this, android.R.layout.simple_list_item_1, result));

                clearButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
