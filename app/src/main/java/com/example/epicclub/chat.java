package com.example.epicclub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class chat extends Fragment {
    public RecyclerView recyclerView;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference myref;
    public String userid;
    public customadapter customadapter;
    public SearchView searchView;
    final ArrayList<String> userids = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View chat = inflater.inflate(R.layout.chat, container, false);

        final RecyclerView recyclerView = chat.findViewById(R.id.recyclerview);


        searchView = chat.findViewById(R.id.friendssearch);

        searchView.setQueryHint("Search");



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        userid = mAuth.getCurrentUser().getUid();

        myref.child("Contacts").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d("usercode","yeet");

                    userids.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final String usercode = ds.getRef().getKey();

                        myref.child("Contacts").child(userid).child(usercode).child("Contacts").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    userids.add(usercode);

                                    customadapter= new customadapter(getActivity(),userids);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerView.setAdapter(customadapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }




                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if(!newText.equals(""))
                {
                    userids.clear();
                    myref.child("users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Log.d("see",ds.child("username").getValue().toString());
                                    if (newText.equals(ds.child("username").getValue().toString())) {
                                        String usercode = ds.getRef().getKey();


                                        userids.add(usercode);
                                        customadapter = new customadapter(getActivity(), userids);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                else if(newText.equals(""))
                {
                    myref.child("Contacts").child(userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {

                                userids.clear();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    final String usercode = ds.getRef().getKey();

                                    myref.child("Contacts").child(userid).child(usercode).child("Contacts").addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {

                                                userids.add(usercode);
                                                customadapter= new customadapter(getActivity(),userids);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                recyclerView.setAdapter(customadapter);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }

                                customadapter= new customadapter(getActivity(),userids);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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



        return chat;
    }


}







