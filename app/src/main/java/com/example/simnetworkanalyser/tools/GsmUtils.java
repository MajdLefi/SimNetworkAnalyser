package com.example.simnetworkanalyser.tools;

import android.os.Build;
import android.telephony.SignalStrength;


public class GsmUtils {

    private GsmUtils() {
    }

    /**
     * Taken from the AOSP SignalStrength class
     *
     * @param signalStrength
     * @return abstract level rating from 0 to 4, 0 being unknown, 1 poor and great 4. -1 if not gsm
     */
    public static int getGsmLevel(SignalStrength signalStrength) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return signalStrength.getLevel();
        } else if (signalStrength.isGsm()) {

            int gsmSignalStrength = signalStrength.getGsmSignalStrength();
            if (gsmSignalStrength <= 2 || gsmSignalStrength == 99) return 0;
            else if (gsmSignalStrength >= 18) return 5;
            else if (gsmSignalStrength >= 12) return 4;
            else if (gsmSignalStrength >= 8) return 3;
            else if (gsmSignalStrength >= 5) return 2;
            else return 1;

        } else return 0;
    }

}
