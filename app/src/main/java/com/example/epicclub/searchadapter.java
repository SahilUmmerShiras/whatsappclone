package com.example.epicclub;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class searchadapter extends RecyclerView.Adapter <searchadapter.MyViewHolder> {
    private Context mcontext;
    private ArrayList<String> mresults;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference myref;
    public String userid;



    public searchadapter(Context context, ArrayList<String> results) {
        mcontext = context;
        mresults = results;

    }




    @NonNull
    @Override
    public searchadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.myroom, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final searchadapter.MyViewHolder holder, final int position) {

        if(mcontext==null||mresults.get(position)==null)
        {

            Log.d("Ayyyoo","itha probelm");
            return;
        }



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        userid = mAuth.getCurrentUser().getUid();




        myref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final String reciever = mresults.get(position);

                    Log.d("here is the thing", reciever);
                    holder.tvName.setText((dataSnapshot.child(mresults.get(position)).child("username").getValue().toString()));
                    holder.tvOver.setText((dataSnapshot.child(mresults.get(position)).child("status").getValue().toString()));
                    String link = (String) dataSnapshot.child(mresults.get(position)).child("dp").getValue();
                    if(link!=null) {
                        Objects.requireNonNull(Glide.with(mcontext).load(link)).into(holder.image);
                    }


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mcontext, userprofile.class);
                            intent.putExtra("name",(dataSnapshot.child(mresults.get(position)).child("username").getValue().toString()) );
                            intent.putExtra("status", (dataSnapshot.child(mresults.get(position)).child("status").getValue().toString()));
                            intent.putExtra("dp",(dataSnapshot.child(mresults.get(position)).child("dp").getValue().toString()) );
                            intent.putExtra("reciever", reciever);
                            mcontext.startActivity(intent);


                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mresults.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvOver;
        public ImageView image;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.Name);
            tvOver = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);

        }
    }
}
