package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class chatting extends AppCompatActivity {
    public RecyclerView recyclerView;
    public TextView Nametitle,onlineoroffline;
    public ImageView Dp;
    public EditText textfield;
    public Button send;
    public FirebaseDatabase database;
    public FirebaseAuth mAuth;
    public DatabaseReference myref;
    public String senduser;
    public String reciever;
    public messageadapter messageadapter;
    public final ArrayList<String> MessageList = new ArrayList<>();
    public Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        recyclerView = findViewById(R.id.chattingrecycler);
        Nametitle = findViewById(R.id.name);
        Dp = findViewById(R.id.dp);
        textfield = findViewById(R.id.text);
        send = findViewById(R.id.button);

        toolbar = findViewById(R.id.toolbar);
        onlineoroffline =findViewById(R.id.onlineoroffline);
        onlineoroffline.setText("offline");






        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();

        senduser = mAuth.getCurrentUser().getUid();
        reciever = getIntent().getStringExtra("reciever");

        final String Name = getIntent().getStringExtra("name");
        Nametitle.setText(Name);
        if(getIntent().getStringExtra("dp")!=null)
        {

            Glide.with(this).load(getIntent().getStringExtra("dp")).into(Dp);
        }

        Calendar c = Calendar.getInstance();

        final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        final String Date = dateformat.format(c.getTime());
        final String Time = timeformat.format(c.getTime());


        Dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatting.this,profilepic.class);
                intent.putExtra("dp",getIntent().getStringExtra("dp"));
                startActivity(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatting.this,userprofile.class);
                intent.putExtra("name",Name);
                intent.putExtra("status",getIntent().getStringExtra("status"));
                intent.putExtra("dp",getIntent().getStringExtra("dp"));
                intent.putExtra("reciever",reciever);
                startActivity(intent);
            }
        });

        String currentstatus = getIntent().getStringExtra("onlineoroffline");
        if(currentstatus.equals("online"))
        {
            onlineoroffline.setText("online");

        }
        else if(!currentstatus.equals("online"))
        {
            onlineoroffline.setText(getIntent().getStringExtra("onlineat"));
        }


        if(textfield.getText()!=null)
        {
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = textfield.getText().toString();
                    String messagekey = myref.child("message").child(senduser).child(reciever).push().getKey();


                    HashMap<String,String> Messagedetails = new HashMap<>();
                    Messagedetails.put("message",message);
                    Messagedetails.put("from",senduser);
                    Messagedetails.put("type","text");
                    Messagedetails.put("to",reciever);
                    Messagedetails.put("time",Time);
                    Messagedetails.put("date",Date);

                    MessageList.add(messagekey);


                    myref.child("message").child(senduser).child(reciever).child(messagekey).setValue(Messagedetails);

                    myref.child("message").child(reciever).child(senduser).child(messagekey).setValue(Messagedetails);
                    textfield.setText("");
                    recyclerView.smoothScrollToPosition(MessageList.size());

                    messageadapter.notifyDataSetChanged();







                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Text Field Empty",Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    protected void onStart() {
        super.onStart();
        myref.child("message").child(senduser).child(reciever).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MessageList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String key = ds.getRef().getKey();
                    MessageList.add(key);

                }
                messageadapter = new messageadapter(chatting.this,MessageList,reciever);
                recyclerView.setLayoutManager(new LinearLayoutManager(chatting.this));

                recyclerView.setAdapter(messageadapter);
                recyclerView.smoothScrollToPosition(MessageList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Calendar c = Calendar.getInstance();
        myRef.child("users").child(mAuth.getCurrentUser().getUid()).child("currentstate").setValue("online");
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
