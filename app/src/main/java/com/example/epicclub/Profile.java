package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class Profile extends AppCompatActivity {
    private Button signout;
    private FirebaseAuth mAuth;
    private ImageView setimage;
    private EditText  status;
    private TextView username;
    private Button save;
    private FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference ref;

    private StorageReference filepath;
    private ProgressBar pb;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        signout = findViewById(R.id.button);
        save = findViewById(R.id.sendrequest);
        setimage = findViewById(R.id.dp);
        username = findViewById(R.id.username);
        status = findViewById(R.id.desc);

        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        ref = FirebaseStorage.getInstance().getReference().child("users").child("profile_images");


        setimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick an Image"), 1);
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = (String) dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("username").getValue();
                    String statusi = (String) dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("status").getValue();
                    username.setText(name);
                    status.setText(statusi);
                    if((String) dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("dp").getValue()!=null)
                    {String Downloadurl = (String) dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("dp").getValue();

                    Glide.with(Profile.this).asBitmap().load(Downloadurl).into(setimage);}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, "failed to update", Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().isEmpty() && !status.getText().toString().isEmpty() && setimage.getDrawable()!= null) {
                    pb.setVisibility(View.VISIBLE);

                    String Name = username.getText().toString();
                    String Status = status.getText().toString();

                    final FirebaseUser user = mAuth.getCurrentUser();
                    final String userid = user.getUid();

                    myRef.child("users").child(userid).child("username").setValue(Name);
                    myRef.child("users").child(userid).child("status").setValue(Status);


                    mAuth = FirebaseAuth.getInstance();
                    StorageReference filePath = ref.child("users").child(mAuth.getCurrentUser().getUid() + ".jpg");

                    filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String fileLink = task.getResult().toString();

                                            myRef.child("users").child(userid).child("dp").setValue(fileLink);
                                            Intent intent = new Intent(Profile.this,mainscreeen.class);
                                            startActivity(intent);
                                        }
                                    });}});

                } else if (!username.getText().toString().isEmpty() && !status.getText().toString().isEmpty() && setimage.getDrawable() == null) {
                    pb.setVisibility(View.VISIBLE);
                    String Name = username.getText().toString();
                    String Status = status.getText().toString();

                    FirebaseUser user = mAuth.getCurrentUser();
                    String userid = user.getUid();

                    myRef.child("users").child(userid).child("username").setValue(Name);
                    myRef.child("users").child(userid).child("status").setValue(Status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Profile.this, "yaaay", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Profile.this, mainscreeen.class);
                            startActivity(intent);
                            pb.setVisibility(View.INVISIBLE);
                        }
                    });

                } else {
                    Toast.makeText(Profile.this, "You have missing fields", Toast.LENGTH_SHORT).show();
                }
            }

        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(Profile.this, "SIGNED OUT", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Profile.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    setimage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

}