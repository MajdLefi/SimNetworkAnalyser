package com.example.simnetworkanalyser.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;


public class MonitorService extends Service {

    private static final String TAG = "MonitorService";

    private IBinder binder = new MonitorBinder();

    private BehaviorSubject<SignalStrength> signalStrengthBehaviorSubject =
            BehaviorSubject.create();

    private BehaviorSubject<Integer> dataConnectionStateBehaviorSubject =
            BehaviorSubject.create();

    @Override
    public void onCreate() {
        super.onCreate();
        beginListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int startMode = super.onStartCommand(intent, flags, startId);
        beginListening();
        return startMode;
    }

    private void beginListening() {

        Log.d(TAG, "Beginning to listen");

        final TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            telephonyManager.listen(monitoringListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    private MonitoringPhoneStateListener monitoringListener = new MonitoringPhoneStateListener();

    private class MonitoringPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            Log.d(TAG, "Signal strengths changed: " + signalStrength);
            signalStrengthBehaviorSubject.onNext(signalStrength);
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            super.onDataConnectionStateChanged(state);
            dataConnectionStateBehaviorSubject.onNext(state);
        }
    }

    public class MonitorBinder extends Binder {

        public Observable<SignalStrength> getSignalStrengthObservable() {
            return signalStrengthBehaviorSubject;
        }

        public Observable<Integer> getDataConnectionStateObservable() {
            return dataConnectionStateBehaviorSubject;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
