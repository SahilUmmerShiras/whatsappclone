package com.example.epicclub;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.epicclub.ui.main.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class mainscreeen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreeen);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId())
        {
            case R.id.creategroup:intent = new Intent(mainscreeen.this,creategroup.class);
                startActivity(intent);
                break;


            case R.id.Settings: intent = new Intent(mainscreeen.this,Profile.class);
                startActivity(intent);
                break;

            case R.id.AddFriends:intent = new Intent(mainscreeen.this,AddFriends.class);
                startActivity(intent);

                break;


            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("currentstate").setValue("online");
        Calendar c = Calendar.getInstance();

        final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");



        final String Time = timeformat.format(c.getTime());
        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("onlineat").setValue(Time);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("currentstate").setValue("offline");
        Calendar c = Calendar.getInstance();

        final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");

        final String Time = timeformat.format(c.getTime());
        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("onlineat").setValue(Time);
    }
}

