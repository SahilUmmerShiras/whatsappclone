package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class groupchatting extends AppCompatActivity {
    public RecyclerView recyclerView;
    public TextView Nametitle;
    public ImageView Dp;
    public EditText textfield;
    public Button send;
    public FirebaseDatabase database;
    public FirebaseAuth mAuth;
    public DatabaseReference myref;
    public String senduser;
    public String reciever;
    public custommessageadapter messageadapter;
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



        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myref = database.getReference();

        senduser = mAuth.getCurrentUser().getUid();
        reciever = getIntent().getStringExtra("reciever");

        final String Name = getIntent().getStringExtra("groupname");
        Nametitle.setText(Name);
        if(getIntent().getStringExtra("groupdp")!=null)
        {

            Glide.with(this).load(getIntent().getStringExtra("groupdp")).into(Dp);
        }

        Calendar c = Calendar.getInstance();

        final SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        final String Date = dateformat.format(c.getTime());
        final String Time = timeformat.format(c.getTime());


        Dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(groupchatting.this,profilepic.class);
                intent.putExtra("dp",getIntent().getStringExtra("groupdp"));
                startActivity(intent);
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(groupchatting.this,groupprofile.class);
                intent.putExtra("groupname",getIntent().getStringExtra("groupname"));
                intent.putExtra("groupstatus",getIntent().getStringExtra("groupstatus"));
                intent.putExtra("groupdp",getIntent().getStringExtra("groupdp"));
                intent.putExtra("reciever",reciever);
                startActivity(intent);
            }
        });


        if(textfield.getText()!=null)
        {
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = textfield.getText().toString();
                    String messagekey = myref.child("groupmessage").child(Name).push().getKey();


                    HashMap<String,String> Messagedetails = new HashMap<>();
                    Messagedetails.put("message",message);
                    Messagedetails.put("from",senduser);
                    Messagedetails.put("type","text");
                    Messagedetails.put("to",reciever);
                    Messagedetails.put("time",Time);
                    Messagedetails.put("date",Date);

                    MessageList.add(messagekey);


                    myref.child("groupmessage").child(Name).child(messagekey).setValue(Messagedetails);

                    textfield.setText("");


                    messageadapter.notifyDataSetChanged();



                    recyclerView.smoothScrollToPosition(MessageList.size());



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
        final String Name = getIntent().getStringExtra("groupname");
        myref.child("groupmessage").child(Name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                MessageList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {

                    String key = ds.getRef().getKey();
                    MessageList.add(key);

                }
                messageadapter = new custommessageadapter(groupchatting.this,MessageList,reciever);
                recyclerView.setLayoutManager(new LinearLayoutManager(groupchatting.this));
                recyclerView.smoothScrollToPosition(MessageList.size());
                recyclerView.setAdapter(messageadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }





}
