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

public class groupadapter extends RecyclerView.Adapter<groupadapter.MyViewHolder> {
    private Context mcontext;
    private ArrayList<String> mresults;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference myref;
    public String userid;



    public groupadapter(Context context, ArrayList<String> results) {
        mcontext = context;
        mresults = results;

    }




    @NonNull
    @Override
    public groupadapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.group_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final groupadapter.MyViewHolder holder, final int position) {

        if(mcontext==null||mresults.get(position)==null)
        {

            return;
        }



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        userid = mAuth.getCurrentUser().getUid();




       myref.child("groups").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.hasChildren())
               {
                   for(DataSnapshot ds: dataSnapshot.getChildren()) {
                       final String Key = ds.getRef().getKey();

                       if (Key.equals(mresults.get(position)))
                       {
                           myref.child("groups").child(Key).addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                   if (dataSnapshot.hasChildren()) {
                                       holder.tvName.setText(dataSnapshot.child("groupname").getValue().toString());
                                       holder.tvOver.setText(dataSnapshot.child("groupdesc").getValue().toString());
                                       Glide.with(mcontext).load(dataSnapshot.child("dp").getValue().toString()).into(holder.image);
                                       Log.d("details",dataSnapshot.child("groupname").getValue().toString());



                                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent intent = new Intent(mcontext,groupchatting.class);
                                               intent.putExtra("groupname",dataSnapshot.child("groupname").getValue().toString());
                                               intent.putExtra("groupstatus",dataSnapshot.child("groupdesc").getValue().toString());
                                               intent.putExtra("groupdp",dataSnapshot.child("dp").getValue().toString());
                                               intent.putExtra("reciever",Key);
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
                   }
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
