package com.example.epicclub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class custommessageadapter extends RecyclerView.Adapter<custommessageadapter.MyViewHolder> {
    private Context mcontext;
    private ArrayList<String> mresults;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference myref;
    public String senduserid;
    public String mreciever;





    custommessageadapter(Context context, ArrayList<String> results, String reciever ) {
        mcontext = context;
        mresults = results;
        mreciever = reciever;

    }




    @NonNull
    @Override
    public custommessageadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.groupmessage, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final custommessageadapter.MyViewHolder holder, final int position) {

        if(mcontext==null||mresults==null)
        {



            return;

        }




        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        senduserid = mAuth.getCurrentUser().getUid();

        Log.d("yoooooooooooooooo",mreciever);

        myref.child("groupmessage").child(mreciever).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    myref.child("groupmessage").child(mreciever).child(mresults.get(position)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("from").getValue().toString().equals(senduserid))
                            {
                                holder.To.setText(dataSnapshot.child("message").getValue().toString());
                                holder.time.setText(dataSnapshot.child("time").getValue().toString());
                                holder.From.setText("");
                                holder.time2.setText("");
                                holder.fromlayout.setVisibility(View.INVISIBLE);
                                holder.tolayout.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                final String user_id =dataSnapshot.child("from").getValue().toString();
                                myref.child("users").child(user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ds) {

                                        if(ds.exists()) {
                                            holder.From.setText(dataSnapshot.child("message").getValue().toString());
                                            holder.time2.setText(dataSnapshot.child("time").getValue().toString());
                                            holder.fromName.setText(ds.child("username").getValue().toString());
                                            holder.To.setText("");
                                            holder.time.setText("");
                                            holder.fromlayout.setVisibility(View.VISIBLE);
                                            holder.tolayout.setVisibility(View.INVISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
                else
                {
                    Toast.makeText(mcontext,"Start a new Conversation",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("nahbroooooo", String.valueOf(mresults.size()));
        return mresults.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView To, From, time, time2, fromName;
        public LinearLayout  tolayout;
        public RelativeLayout fromlayout;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            To = itemView.findViewById(R.id.totext);
            From = itemView.findViewById(R.id.fromtext);
            time = itemView.findViewById(R.id.time);
            time2 = itemView.findViewById(R.id.time2);
            fromlayout = itemView.findViewById(R.id.relativefrom);
            tolayout = itemView.findViewById(R.id.torelativelayout);
            fromName = itemView.findViewById(R.id.fromName);



        }
    }
}