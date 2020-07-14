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
import java.util.Objects;


public class group extends Fragment {
    public RecyclerView recyclerView;
    public FirebaseAuth mAuth;
    public FirebaseDatabase database;
    public DatabaseReference myref;
    public String userid;
    public groupadapter customadapter;
    public SearchView searchView;
    final ArrayList<String> userids = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View group = inflater.inflate(R.layout.group, container, false);

        final RecyclerView recyclerView = group.findViewById(R.id.recyclerview);


        searchView = group.findViewById(R.id.friendssearch);

        searchView.setQueryHint("Search");



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();
        userid = mAuth.getCurrentUser().getUid();

        myref.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren())
                {
                    for(DataSnapshot ds: dataSnapshot.getChildren())
                    {
                        final String key = ds.getRef().getKey();

                        myref.child("groups").child(key).child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChildren())
                                {
                                    for(DataSnapshot member: dataSnapshot.getChildren())
                                    {
                                        if(member.getValue().toString().equals(userid))
                                        {
                                            userids.add(key);
                                            customadapter= new groupadapter(getActivity(),userids);
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
                    myref.child("groups").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChildren())
                            {
                                for(DataSnapshot ds: dataSnapshot.getChildren()) {

                                    final String key = ds.getRef().getKey();
                                    if (newText.equals(key)) {
                                        myref.child("groups").child(key).child(key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    for (DataSnapshot member : dataSnapshot.getChildren()) {
                                                        if (member.getValue().toString().equals(userid)) {
                                                            userids.add(key);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                customadapter= new groupadapter(getActivity(),userids);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(customadapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else if(newText.equals(""))
                {
                    myref.child("groups").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChildren())
                            {
                                for(DataSnapshot ds: dataSnapshot.getChildren())
                                {
                                    final String key = ds.getRef().getKey();
                                    userids.clear();

                                    myref.child("groups").child(key).child(key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChildren())
                                            {
                                                for(DataSnapshot member: dataSnapshot.getChildren())
                                                {
                                                    if(member.getValue().toString().equals(userid))
                                                    {
                                                        userids.add(key);
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                customadapter= new groupadapter(getActivity(),userids);
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



        return group;
    }


}






