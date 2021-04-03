package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class Adapter_Chat extends ArrayAdapter<String> {
    private ArrayList<String> dataSet;
    Context mContext;

   public Adapter_Chat(ArrayList<String>arrayList,Context context){
       super(context,R.layout.chating,arrayList);
       dataSet=arrayList;
       mContext=context;
   }



    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }





    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chating, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.txtName.setText(dataSet.get(position));

        return convertView;
    }
}