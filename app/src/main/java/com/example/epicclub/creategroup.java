package com.example.epicclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class creategroup extends AppCompatActivity {
    EditText groupname, groupdesc;
    ImageView groupdp;
    Button creategroup;
    RecyclerView recyclerView;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    ArrayList<String> groupmates = new ArrayList<>();
    private StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);

        groupdesc = findViewById(R.id.groupdesc);
        groupname = findViewById(R.id.groupname);
        groupdp = findViewById(R.id.groupimage);
        creategroup = findViewById(R.id.create);
        recyclerView = findViewById(R.id.grouprecyclerview);


        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        groupdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick an Image"), 1);
            }
        });



        if((!groupname.equals(""))&&(!groupdesc.equals("")))
        {
            {

            creategroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ref = FirebaseStorage.getInstance().getReference().child(groupname.getText().toString()).child("profile_images");
                    final StorageReference myref = ref.child("groups").child(mAuth.getCurrentUser().getUid()+".jpg");
                    myref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Toast.makeText(creategroup.this,"samba",Toast.LENGTH_SHORT).show();
                                    String fileLink = task.getResult().toString();
                                    Log.d("size of array", String.valueOf(groupmates.size()));
                                    String name = groupname.getText().toString();
                                    String desc = groupdesc.getText().toString();
                                    myRef.child("groups").child(name).child("dp").setValue(fileLink);
                                    myRef.child("groups").child(name).child("groupname").setValue(name);
                                    myRef.child("groups").child(name).child("groupdesc").setValue(desc);
                                    String key = myRef.child("groups").child(name).getKey();
                                    groupmates.add(mAuth.getCurrentUser().getUid());

                                    HashMap<String,String> members = new HashMap<>();
                                    for(int i =0;i<groupmates.size();i++)
                                    {
                                        members.put("member"+i,groupmates.get(i));


                                    }

                                   myRef.child("groups").child(name).child(key).setValue(members);
                                }
                            });

                        }
                    });
                }
            });

            }

        }
        DatabaseReference userreference = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userreference,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts, creategroup.findViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, creategroup.findViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final creategroup.findViewHolder holder, int position, @NonNull final Contacts model) {
                final String reciever = getRef(position).getKey();

                if(!reciever.equals(mAuth.getCurrentUser().getUid())) {
                    holder.UserName.setText((model.getUsername()));
                    holder.Status.setText((model.getStatus()));
                    Glide.with(creategroup.this).load(model.getDp()).into(holder.image);


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            groupmates.add(reciever);
                            Toast.makeText(getApplicationContext(), reciever + "added", Toast.LENGTH_SHORT).show();
                            holder.checker.setVisibility(View.VISIBLE);

                        }
                    });


                }
                else
                {
                    holder.relativeLayout.setVisibility(View.INVISIBLE);
                }

            }

            @NonNull
            @Override
            public creategroup.findViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.myroom, parent, false);
                creategroup.findViewHolder viewholder = new findViewHolder(view);
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
        ImageButton checker;
        RelativeLayout relativeLayout;
        public findViewHolder(@NonNull View itemView) {
            super(itemView);

            UserName = itemView.findViewById(R.id.Name);
            Status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.image);
            checker = itemView.findViewById(R.id.checker);
            relativeLayout =itemView.findViewById(R.id.relativelayout);
        }
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
                    groupdp.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



