package com.apps.jaywalker.kaveri_b;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by karthick on 28-07-2017.
 */

public class seva_adapter extends ArrayAdapter {
    List<String> Sname;
    List<String> Sqty;
    List<String> Sprice;
    List<String> Tprice;
    Context appcontext;

    holder Holder;

    public seva_adapter(Context context, int lid, List<String> Sname, List<String> Sqty, List<String> spr, List<String> Tamt)
    {
        super(context,lid,Sname);
        appcontext=context;
        this.Sname = Sname;
        this.Sqty = Sqty;
        this.Sprice = spr;
        this.Tprice = Tamt;
        Holder= new holder();
    }
    public class holder
    {
        TextView Sevanm,Sprice;
        EditText editText;
        TextView Stotal;
    }
    @Override
    public int getCount() {
        return Sname.size();
    }

    @Override
    public Object getItem(int position) {
        return Sname.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) appcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.menu_ly,null);

        Holder.Sevanm = (TextView)convertView.findViewById(R.id.seva_nm);
        Holder.editText= (EditText)convertView.findViewById(R.id.s_ql);
        Holder.Sprice= (TextView) convertView.findViewById(R.id.Sper);
        Holder.Stotal= (TextView) convertView.findViewById(R.id.T_amt);
        Holder.Stotal.setText(Tprice.get(position));
        Holder.Sevanm.setText(Sname.get(position));
        Holder.Sprice.setText(" : " + appcontext.getResources().getString(R.string.Rup) + " " + Sprice.get(position) + " X ");
        Holder.editText.setText(Sqty.get(position));
        Holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int val = 0;

                if (s.toString().isEmpty() )
                {
                     val = 0;

                }
                else
                {
                    int val1 = Integer.parseInt(s.toString());
                    val = calculaterate(val1,Integer.valueOf(Sprice.get(position)));


                }
                Sqty.set(position,s.toString());
                Tprice.set(position,String.valueOf(val));
                notifyDataSetChanged();
                Holder.editText.requestFocus();
            }
        });

        return convertView;

    }
    private int calculaterate(int qty, int prc)
    {

        int amt_T = prc * qty;;
        return amt_T;
    }
    public void dataset()
    {
        notifyDataSetChanged();
    }
}
