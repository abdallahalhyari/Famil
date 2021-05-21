package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_calling extends RecyclerView.Adapter<Adapter_calling.myviewholder> {
    ArrayList<User> datalist;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public Adapter_calling(ArrayList<User> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }



    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_call, parent, false);
        myviewholder evh = new myviewholder(view, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.t2.setText(datalist.get(position).getEmail());
        holder.t3.setText(datalist.get(position).getPhone());
        holder.t1.setText(datalist.get(position).getName());
        String id = datalist.get(position).getEmail();
        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/" + id + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(holder.profileImage);
            }
        });

    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }

    static class myviewholder extends RecyclerView.ViewHolder {
        TextView t1, t2, t3;
        ImageView profileImage,delete;

        public myviewholder(@NonNull View itemView , OnItemClickListener listener) {
            super(itemView);
            t1 = itemView.findViewById(R.id.name);
            t2 = itemView.findViewById(R.id.email_text);
            t3 = itemView.findViewById(R.id.phone);
            delete=itemView.findViewById(R.id.remove);
            profileImage = itemView.findViewById(R.id.image_view);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private void removeItem(int position) {
        datalist.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datalist.size());
    }

}
