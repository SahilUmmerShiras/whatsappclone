package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class groupprofile extends AppCompatActivity {
    private String receiverUserID, senderUserID, Current_State;

    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button DeclineMessageRequestButton;
    private FloatingActionButton fab;
    private String groupcode;
    private String groupname;
    private String groupstatus;
    private String groupdp;

    private DatabaseReference myref;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupprofile);


        mAuth = FirebaseAuth.getInstance();
        myref = FirebaseDatabase.getInstance().getReference();

        groupcode = getIntent().getExtras().get("reciever").toString();
        groupdp = getIntent().getStringExtra("groupdp");
        groupname = getIntent().getStringExtra("groupname");
        groupstatus = getIntent().getStringExtra("groupstatus");





        senderUserID = mAuth.getCurrentUser().getUid();


        userProfileImage = (CircleImageView) findViewById(R.id.dp);
        userProfileName = (TextView) findViewById(R.id.username);
        userProfileStatus = (TextView) findViewById(R.id.desc);

        DeclineMessageRequestButton = (Button) findViewById(R.id.cancelrequest);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        Current_State = "new";
        fab.setVisibility(View.VISIBLE);
        fab.setEnabled(true);
        DeclineMessageRequestButton.setVisibility(View.VISIBLE);
        DeclineMessageRequestButton.setEnabled(true);


        Glide.with(this).load(groupdp).into(userProfileImage);
        userProfileName.setText(groupname);
        userProfileStatus.setText(groupstatus);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(groupprofile.this).setTitle("Delete Chat?")
                        .setMessage("Are you sure you want to delete all chat? ")


                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myref.child("groupmessage").child(groupname).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChildren())
                                        {

                                            for(DataSnapshot ds: dataSnapshot.getChildren())
                                            {

                                                ds.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(getApplicationContext(),"Chat Deleted",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }


                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(groupprofile.this).setTitle("Delete Chat?")
                        .setMessage("Are you sure you want to delete all chat? ")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myref.child("groups").child(groupname).child(groupname).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            for(DataSnapshot ds: dataSnapshot.getChildren())
                                            {
                                                Log.d("look here broooo",ds.getKey());
                                                String memberid = (String) ds.getValue();
                                                if(mAuth.getCurrentUser().getUid().equals(memberid))
                                                {
                                                    ds.getRef().setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Intent intent = new Intent(groupprofile.this,group.class);
                                                            Toast.makeText(getApplicationContext(),"quit "+groupname,Toast.LENGTH_SHORT).show();
                                                            startActivity(intent);

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
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });





    }




}
