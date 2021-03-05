package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class Adapter extends RecyclerView.ViewHolder {
    View mView;
    public Adapter(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setName(String name) {
        TextView Name = (TextView) mView.findViewById(R.id.name);
        Name.setText(name);
    }
    public void setEmail(String status) {
        TextView Email = (TextView) mView.findViewById(R.id.email_text);
        Email.setText(status);
    }


}