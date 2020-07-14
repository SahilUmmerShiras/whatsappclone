package com.example.epicclub;

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

public class messageadapter extends RecyclerView.Adapter<messageadapter.MyViewHolder> {
    private Context mcontext;
    private ArrayList<String> mresults;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference myref;
    public String senduserid;
    public String mreciever;





     messageadapter(Context context, ArrayList<String> results, String reciever ) {
        mcontext = context;
        mresults = results;
        mreciever = reciever;

    }




    @NonNull
    @Override
     public messageadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.messages, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final messageadapter.MyViewHolder holder, final int position) {

        if(mcontext==null||mresults==null)
        {



            return;

        }




        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        senduserid = mAuth.getCurrentUser().getUid();



        myref.child("message").child(senduserid).child(mreciever).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                        myref.child("message").child(senduserid).child(mreciever).child(mresults.get(position)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                                    holder.From.setText(dataSnapshot.child("message").getValue().toString());
                                    holder.time2.setText(dataSnapshot.child("time").getValue().toString());
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

        public TextView To, From, time, time2;
        public LinearLayout fromlayout, tolayout;


        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            To = itemView.findViewById(R.id.totext);
            From = itemView.findViewById(R.id.fromtext);
            time = itemView.findViewById(R.id.time);
            time2 = itemView.findViewById(R.id.time2);
            fromlayout = itemView.findViewById(R.id.relativefrom);
            tolayout = itemView.findViewById(R.id.torelativelayout);


        }
    }
}
