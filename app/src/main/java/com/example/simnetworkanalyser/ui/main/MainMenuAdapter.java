package com.example.simnetworkanalyser.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simnetworkanalyser.R;

import java.util.List;

public class MainMenuAdapter extends ArrayAdapter<MainMenuItem> {

    public MainMenuAdapter(Context context, List<MainMenuItem> menuList) {
        super(context, 0, menuList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MainMenuItem menuItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_main, parent, false);
        }

        ImageView itemIcon = convertView.findViewById(R.id.img_menu_item_icon);
        TextView itemTitle = convertView.findViewById(R.id.txt_menu_item_title);

        itemIcon.setImageResource(menuItem.getIcon());
        itemTitle.setText(menuItem.getTitle());

        return convertView;
    }
}
