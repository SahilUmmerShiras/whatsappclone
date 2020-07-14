package com.example.epicclub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class profilepic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepic);

        ImageView image = findViewById(R.id.image);
        Glide.with(this).load(getIntent().getStringExtra("dp")).into(image);
    }
}
