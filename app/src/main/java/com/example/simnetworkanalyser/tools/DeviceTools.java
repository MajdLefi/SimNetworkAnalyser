package com.example.simnetworkanalyser.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;


@SuppressLint({"HardwareIds", "MissingPermission"})
public class DeviceTools {

    private TelephonyManager mTelephonyMgr;
    private ConnectivityManager mConnectivityMgr;

    private Boolean mIsAllPermissionAllowed = false;

    public DeviceTools(Context context) {

        mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mConnectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mIsAllPermissionAllowed = (ActivityCompat.checkSelfPermission(context, READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
    }

    public String getDeviceId() {
        return (mIsAllPermissionAllowed) ? mTelephonyMgr.getDeviceId() : "";
    }

    public String getSimPhoneNumber() {
        return (mIsAllPermissionAllowed) ? mTelephonyMgr.getLine1Number() : "";
    }

    public String getSimOperatorName() {
        return (mIsAllPermissionAllowed) ? mTelephonyMgr.getSimOperatorName() : "";
    }

    public String getSimCountry() {
        return (mIsAllPermissionAllowed) ? mTelephonyMgr.getSimCountryIso() : "";
    }

    public String getSimSerialNumber() {
        return (mIsAllPermissionAllowed) ? mTelephonyMgr.getSimSerialNumber() : "";
    }

    public String getSoftwareVersion() {
        return (mIsAllPermissionAllowed) ? mTelephonyMgr.getDeviceSoftwareVersion() : "";
    }

    public String getPhoneType() {
        return (mIsAllPermissionAllowed) ? (Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE) : "";
    }

    public List<CellInfo> getAllCellInfo() {
        return mTelephonyMgr.getAllCellInfo();
    }

    public String getNetworkType() {

        if (!mIsAllPermissionAllowed) {
            return "";
        }

        int networkType = mTelephonyMgr.getNetworkType();

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "2G(EDGE)";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "2G(GPRS)";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "3G(HSDPA)";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "3G(HSPA)";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G(HSPA+)";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "3G(HSUPA)";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G(LTE)";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "3G(UMTS)";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return "Unknown";
        }
    }

}
