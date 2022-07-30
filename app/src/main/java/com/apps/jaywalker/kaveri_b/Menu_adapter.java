package com.apps.jaywalker.kaveri_b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by karthick on 27-02-2018.
 */

public class Menu_adapter extends BaseAdapter {
    Context context;
    String[] menuitm;
    String[] rtitem;
    public class Holder
    {
        TextView txt;TextView txt1;
    }
    public Menu_adapter(Context context,String[] menuitem,String[] rtitem)
    {
        menuitm = menuitem;
        this.rtitem =rtitem;
        this.context = context;
    }
    @Override
    public int getCount() {
        return menuitm.length;
    }

    @Override
    public Object getItem(int i) {
        return menuitm[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view =inflater.inflate(R.layout.listly,null);
        Holder holder = new Holder();
        holder.txt = (TextView) view.findViewById(R.id.item_nm);
        holder.txt1 = (TextView) view.findViewById(R.id.item_rt);
        holder.txt.setText(menuitm[i]);
        holder.txt1.setText(context.getResources().getString(R.string.Rup) + " " + rtitem[i]);
        return view;
    }
}
