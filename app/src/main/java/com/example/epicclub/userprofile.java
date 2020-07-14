package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.EventListener;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


    public class userprofile extends AppCompatActivity
    {
        private String receiverUserID, senderUserID, Current_State;

        private CircleImageView userProfileImage;
        private TextView userProfileName, userProfileStatus;
        private Button SendMessageRequestButton, DeclineMessageRequestButton;
        private FloatingActionButton fab;

        private DatabaseReference UserRef, ChatRequestRef, ContactsRef,NotificationRef;;
        private FirebaseAuth mAuth;


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_userprofile);


            mAuth = FirebaseAuth.getInstance();
            UserRef = FirebaseDatabase.getInstance().getReference().child("users");
            ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
            ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
            NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");



            receiverUserID = getIntent().getExtras().get("reciever").toString();
            senderUserID = mAuth.getCurrentUser().getUid();


            userProfileImage = (CircleImageView) findViewById(R.id.dp);
            userProfileName = (TextView) findViewById(R.id.username);
            userProfileStatus = (TextView) findViewById(R.id.desc);
            SendMessageRequestButton = (Button) findViewById(R.id.sendrequest);
            DeclineMessageRequestButton = (Button) findViewById(R.id.cancelrequest);
            fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
            Current_State = "new";
            fab.setVisibility(View.INVISIBLE);
            fab.setEnabled(false);
            DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
            DeclineMessageRequestButton.setEnabled(true);





            RetrieveUserInfo();
        }



        private void RetrieveUserInfo()
        {
            UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if ((dataSnapshot.exists())  &&  (dataSnapshot.hasChild("dp")))
                    {
                        final String userImage = dataSnapshot.child("dp").getValue().toString();
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String userstatus = dataSnapshot.child("status").getValue().toString();

                        Picasso.get().load(userImage).placeholder(R.drawable.email).into(userProfileImage);
                        userProfileName.setText(userName);
                        userProfileStatus.setText(userstatus);
                        userProfileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(userprofile.this,profilepic.class);
                                intent.putExtra("dp",userImage);
                                startActivity(intent);
                            }
                        });


                        ManageChatRequests();
                    }
                    else
                    {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String userstatus = dataSnapshot.child("status").getValue().toString();

                        userProfileName.setText(userName);
                        userProfileStatus.setText(userstatus);


                        ManageChatRequests();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        private void ManageChatRequests()
        {
            ChatRequestRef.child(senderUserID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.hasChild(receiverUserID))
                            {
                                String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                                if (request_type.equals("sent"))
                                {
                                    Current_State = "request_sent";
                                    SendMessageRequestButton.setText("Cancel Follow Request");
                                }
                                else if (request_type.equals("received"))
                                {
                                    Current_State = "request_received";
                                    SendMessageRequestButton.setText("Accept Follow Request");

                                    DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                    DeclineMessageRequestButton.setEnabled(true);

                                    DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            CancelChatRequest();
                                        }
                                    });
                                }
                            }
                            else
                            {
                                ContactsRef.child(senderUserID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.hasChild(receiverUserID))
                                                {
                                                    Current_State = "friends";
                                                    fab.setVisibility(View.VISIBLE);
                                                    fab.setEnabled(true);



                                                    SendMessageRequestButton.setText("Remove this Contact");
                                                    fab.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            new AlertDialog.Builder(userprofile.this)
                                                                    .setTitle("Delete Chat?")
                                                                    .setMessage("Are you sure you want to delete all chat? ")


                                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            FirebaseDatabase.getInstance().getReference().child("message").child(senderUserID)
                                                                                    .child(receiverUserID).addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    for(DataSnapshot ds: dataSnapshot.getChildren())
                                                                                    {
                                                                                        String user = ds.getRef().getKey();
                                                                                        FirebaseDatabase.getInstance().getReference().child("message").child(senderUserID)
                                                                                                .child(receiverUserID).child(user).removeValue();
                                                                                        FirebaseDatabase.getInstance().getReference().child("message").child(receiverUserID)
                                                                                                .child(senderUserID).child(user).removeValue();
                                                                                       
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

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



            if (!senderUserID.equals(receiverUserID))
            {
                SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        SendMessageRequestButton.setEnabled(false);

                        if (Current_State.equals("new"))
                        {
                            SendChatRequest();
                        }
                        if (Current_State.equals("request_sent"))
                        {
                            CancelChatRequest();
                        }
                        if (Current_State.equals("request_received"))
                        {
                            AcceptChatRequest();
                        }
                        if (Current_State.equals("friends"))
                        {
                            RemoveSpecificContact();
                        }
                    }
                });
            }
            else
            {
                SendMessageRequestButton.setVisibility(View.INVISIBLE);
            }
        }



        private void RemoveSpecificContact()
        {
            ContactsRef.child(senderUserID).child(receiverUserID)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ContactsRef.child(receiverUserID).child(senderUserID)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    SendMessageRequestButton.setEnabled(true);
                                                    Current_State = "new";
                                                    SendMessageRequestButton.setText("FOLLOW");

                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                    DeclineMessageRequestButton.setEnabled(false);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }



        private void AcceptChatRequest()
        {
            ContactsRef.child(senderUserID).child(receiverUserID)
                    .child("Contacts").setValue("Saved")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ContactsRef.child(receiverUserID).child(senderUserID)
                                        .child("Contacts").setValue("Saved")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {

                                                    ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        SendMessageRequestButton.setEnabled(true);
                                                                                        Current_State = "friends";
                                                                                        SendMessageRequestButton.setText("Remove this Contact");

                                                                                        DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineMessageRequestButton.setEnabled(false);
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }




        private void CancelChatRequest()
        {
            ChatRequestRef.child(senderUserID).child(receiverUserID)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ChatRequestRef.child(receiverUserID).child(senderUserID)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    SendMessageRequestButton.setEnabled(true);
                                                    Current_State = "new";
                                                    SendMessageRequestButton.setText("FOLLOW");

                                                    DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                    DeclineMessageRequestButton.setEnabled(false);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }




        private void SendChatRequest()
        {
            ChatRequestRef.child(senderUserID).child(receiverUserID)
                    .child("request_type").setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful()) {
                                ChatRequestRef.child(receiverUserID).child(senderUserID)
                                        .child("request_type").setValue("received")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    if (task.isSuccessful()) {
                                                        SendMessageRequestButton.setEnabled(true);
                                                        Current_State = "request_sent";
                                                        SendMessageRequestButton.setText("Cancel Follow Request");

                                                        HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                        chatNotificationMap.put("from", senderUserID);
                                                        chatNotificationMap.put("type", "request");

                                                        NotificationRef.child(receiverUserID).push()
                                                                .setValue(chatNotificationMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                    {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            SendMessageRequestButton.setEnabled(true);
                                                                            Current_State = "request_sent";
                                                                            SendMessageRequestButton.setText("Cancel Chat Request");
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }


                                            }
                                        });
                            }
        }
    });


}}

