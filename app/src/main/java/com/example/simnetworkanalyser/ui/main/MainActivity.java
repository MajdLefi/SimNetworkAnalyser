package com.example.simnetworkanalyser.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.tools.PermissionsManager;
import com.example.simnetworkanalyser.ui.AboutUsActivity;
import com.example.simnetworkanalyser.ui.deviceinfo.DeviceInfoActivity;
import com.example.simnetworkanalyser.ui.flowtest.FlowTestActivity;
import com.example.simnetworkanalyser.ui.networkinfo.NetworkInfoActivity;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private GridView mMainMenuView;
    private MainMenuItem[] mMainMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new PermissionsManager(this).checkPermissions();

        // --

        mMainMenuView = findViewById(R.id.gdv_main_menu);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setMainMenu();
    }

    /**
     * Filling te menu list.
     */
    private void setMainMenu() {

        mMainMenuList = new MainMenuItem[]{
                new MainMenuItem(
                        1,
                        "Device Info",
                        R.drawable.ic_device_info_black_24dp
                ),
                new MainMenuItem(
                        2,
                        "Network Info",
                        R.drawable.ic_signal_black_24dp
                ),
                new MainMenuItem(
                        3,
                        "Flow test",
                        R.drawable.ic_network_check_black_24dp
                ),
                new MainMenuItem(
                        4,
                        "About",
                        R.drawable.ic_info_black_24dp
                )
        };


        mMainMenuView.setAdapter(new MainMenuAdapter(this, Arrays.asList(mMainMenuList)));

        mMainMenuView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                switch (mMainMenuList[position].getId()) {

                    case 1:
                        startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class));
                        break;

                    case 2:
                        startActivity(new Intent(MainActivity.this, NetworkInfoActivity.class));
                        break;

                    case 3:
                        startActivity(new Intent(MainActivity.this, FlowTestActivity.class));
                        break;

                    case 4:
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        break;
                }
            }
        });
    }
}