package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddFriends extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myref;
    String userid;
    SearchView searchView;
    searchadapter customadapter;
    ArrayList<String> userids = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        recyclerView = findViewById(R.id.recyclerView);



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        userid = mAuth.getCurrentUser().getUid();
        DatabaseReference userreference = myref.child("users");
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userreference,Contacts.class).build();


        searchView = findViewById(R.id.friendssearch);
        searchView.setQueryHint("Search by Username");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if(!newText.equals(""))
                {
                    myref.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Log.d("see",ds.child("username").getValue().toString());
                                    if (newText.equals(ds.child("username").getValue().toString())) {
                                        String usercode = ds.getRef().getKey();

                                        userids.clear();
                                        userids.add(usercode);
                                        customadapter = new searchadapter(AddFriends.this, userids);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(AddFriends.this));
                                        recyclerView.setAdapter(customadapter);


                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else if(newText.equals("")) {
                    myref.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {

                                userids.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    final String usercode = ds.getRef().getKey();

                                    userids.add(usercode);



                                }

                                customadapter= new searchadapter(AddFriends.this,userids);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddFriends.this));
                                recyclerView.setAdapter(customadapter);


                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                return true;
            }
        });


        FirebaseRecyclerAdapter<Contacts,findViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, findViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findViewHolder holder, int position, @NonNull final Contacts model) {


                holder.UserName.setText((model.getUsername()));
                holder.Status.setText((model.getStatus()));
                Glide.with(AddFriends.this).load(model.getDp()).into(holder.image);

                final String reciever = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddFriends.this,userprofile.class);
                        intent.putExtra("name",model.getUsername());
                        intent.putExtra("status",model.getStatus());
                        intent.putExtra("dp",model.getDp());
                        intent.putExtra("reciever",reciever);
                        startActivity(intent);


                    }
                });




            }

            @NonNull
            @Override
            public findViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.myroom, parent, false);
                findViewHolder viewholder = new findViewHolder(view);
                return viewholder;
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public class findViewHolder extends RecyclerView.ViewHolder
    {
        TextView UserName, Status;
        ImageView image;
        public findViewHolder(@NonNull View itemView) {
            super(itemView);

            UserName = itemView.findViewById(R.id.Name);
            Status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);


        }
    }




}
