package com.example.simnetworkanalyser.ui.networkinfo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.services.MonitorService;
import com.example.simnetworkanalyser.tools.DeviceTools;
import com.example.simnetworkanalyser.viewmodel.SignalViewModel;

import java.util.List;

public class Signal3GFragment extends Fragment {

    public static Signal3GFragment newInstance() {
        return new Signal3GFragment();
    }

    private SignalViewModel signalViewModel;
    private boolean isBound = false;
    private MonitorService.MonitorBinder binder;

    private TextView rscp;
    private TextView ecio;
    private TextView ber;
    private TextView psc;
    private TextView rssi;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        signalViewModel = ViewModelProviders.of(this)
                .get(SignalViewModel.class);

        // --

        View root = inflater.inflate(R.layout.fragment_3g, container, false);

        final TextView mnc = root.findViewById(R.id.mnc);
        final TextView mcc = root.findViewById(R.id.mcc);
        final TextView cid = root.findViewById(R.id.cid);
        final TextView lac = root.findViewById(R.id.lac);
        final TextView mobilerxbyte = root.findViewById(R.id.mobilerxbyte);
        final TextView mobiletxbyte = root.findViewById(R.id.mobiletxbyte);
        final TextView mobilerxpaquets = root.findViewById(R.id.mobilerxpaquets);
        final TextView mobiletxpaquets = root.findViewById(R.id.mobiletxpaquets);

        rscp = root.findViewById(R.id.rscp);
        ber = root.findViewById(R.id.ber);
        rssi = root.findViewById(R.id.rssi);
        ecio = root.findViewById(R.id.ecio);
        psc = root.findViewById(R.id.psc);

        //psc.setText(new DeviceTools(this).getAllCellInfo());

        // --

        mobilerxbyte.setText(String.valueOf(TrafficStats.getMobileRxBytes()));
        mobiletxbyte.setText(String.valueOf(TrafficStats.getMobileTxBytes()));
        mobilerxpaquets.setText(String.valueOf(TrafficStats.getMobileRxPackets()));
        mobiletxpaquets.setText(String.valueOf(TrafficStats.getMobileTxPackets()));

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(getActivity(), MonitorService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        observeSignalStrength();
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unbindService(mConnection);
        isBound = false;
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

    private void observeSignalStrength() {

        signalViewModel.getSignalStrengthLiveData().observe(this, new Observer<SignalStrength>() {
            @Override
            public void onChanged(@Nullable SignalStrength signalStrength) {

                if (signalStrength != null) {
                    rscp.setText(String.valueOf(signalStrength.getGsmSignalStrength()) + " dBm");
                    ber.setText(String.valueOf(signalStrength.getGsmBitErrorRate()));
                    rssi.setText(String.valueOf(signalStrength.getEvdoDbm())+" dBm");
                    ecio.setText(String.valueOf(signalStrength.getCdmaEcio() + " dBm"));
                    psc.setText(String.valueOf(signalStrength.getCdmaEcio()));
                }
            }
        });
    }

}
