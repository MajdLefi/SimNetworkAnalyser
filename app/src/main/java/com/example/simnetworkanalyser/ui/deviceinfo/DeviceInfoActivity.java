package com.example.simnetworkanalyser.ui.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SignalStrength;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.tools.DeviceTools;
import com.example.simnetworkanalyser.tools.GsmUtils;
import com.example.simnetworkanalyser.tools.NetworkUtil;
import com.example.simnetworkanalyser.ui.BaseActivity;
import com.example.simnetworkanalyser.services.MonitorService;
import com.example.simnetworkanalyser.viewmodel.SignalViewModel;


public class DeviceInfoActivity extends BaseActivity {

    private TextView connectStatus;
    private TextView networkType;
    private ImageView signalStrengthImg;

    private DeviceTools deviceTools;

    private SignalViewModel signalViewModel;
    private boolean isBound = false;
    private MonitorService.MonitorBinder binder;


    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkMobileDataConnectionStatus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        // --

        deviceTools = new DeviceTools(this);

        signalViewModel = ViewModelProviders.of(this)
                .get(SignalViewModel.class);

        // --

        connectStatus = findViewById(R.id.connect_status);

        // --

        signalStrengthImg = findViewById(R.id.signal_strength);

        // --

        TextView deviceId = findViewById(R.id.device_id);
        deviceId.setText(deviceTools.getDeviceId());

        TextView phoneType = findViewById(R.id.phone_type);
        phoneType.setText(deviceTools.getPhoneType());

        TextView softwareVersion = findViewById(R.id.software_version);
        softwareVersion.setText(deviceTools.getSoftwareVersion());

        TextView phoneNumber = findViewById(R.id.sim_phone_number);
        phoneNumber.setText(deviceTools.getSimPhoneNumber());

        TextView simOperatorName = findViewById(R.id.sim_operator_name);
        simOperatorName.setText(deviceTools.getSimOperatorName());

        TextView simCountry = findViewById(R.id.sim_country);
        simCountry.setText(deviceTools.getSimCountry());

        TextView simSerialNumber = findViewById(R.id.sim_serial_number);
        simSerialNumber.setText(deviceTools.getSimSerialNumber());

        networkType = findViewById(R.id.network_type);
        networkType.setText(deviceTools.getNetworkType());

        // --

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, MonitorService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkMobileDataConnectionStatus();
        observeSignalStrength();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(mConnection);
        isBound = false;
    }


    @SuppressWarnings("deprecation")
    private void checkMobileDataConnectionStatus() {

        if (!NetworkUtil.isMobileDataActive(this)) {

            connectStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            connectStatus.setText("Mobile Data not Active");

        } else if (!NetworkUtil.isMobileDataConnected(this)) {

            connectStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            connectStatus.setText("Disconnected");

        } else {

            connectStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            connectStatus.setText("Connected");
        }
    }

    private void observeSignalStrength() {

        signalViewModel.getSignalStrengthLiveData().observe(this, new Observer<SignalStrength>() {
            @Override
            public void onChanged(@Nullable SignalStrength signalStrength) {

                if (signalStrength != null) {
                    signalStrengthImg.setImageLevel(GsmUtils.getGsmLevel(signalStrength));
                }

                networkType.setText(deviceTools.getNetworkType());
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            binder = (MonitorService.MonitorBinder) service;
            signalViewModel.observe(binder);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            signalViewModel.stopObserving();
            isBound = false;
        }
    };

}
