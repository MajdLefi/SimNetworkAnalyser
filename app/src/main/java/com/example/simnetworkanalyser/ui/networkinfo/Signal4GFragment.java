package com.example.simnetworkanalyser.ui.networkinfo;

import android.os.Bundle;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.tools.DeviceTools;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

public class Signal4GFragment extends Fragment {

    public static Signal4GFragment newInstance() {
        return new Signal4GFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_4g, container, false);

        final TextView rsrp1 = root.findViewById(R.id.rsrp1);
        final TextView rsrp2 = root.findViewById(R.id.rsrp2);
        final TextView cellid = root.findViewById(R.id.cellid);
        final TextView mcc = root.findViewById(R.id.mcc);
        final TextView mnc = root.findViewById(R.id.mnc);
        final TextView pci = root.findViewById(R.id.pci);
        final TextView tac = root.findViewById(R.id.tac);

        List<CellInfo> infos = new DeviceTools(getContext()).getAllCellInfo();
        for (int i = 0; i < infos.size(); ++i) {

            CellInfo info = infos.get(i);

            if (info instanceof CellInfoLte)  //if LTE connection
            {
                CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();

                //rsrp1.setText(lte.getRsrp());
            }
        }

        return root;
    }
}
