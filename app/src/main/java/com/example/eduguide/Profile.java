package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Profile extends AppCompatActivity {

    Toolbar toolbar;
    TextView name, phone,enroll, branch, email,year;
    Button edit;
    ImageButton photoedit;
    ImageView photo;
    ProgressBar pb;

    Integer PICK_IMAGE = 100, STORAGE_WRITE_REQUEST_CODE= 100, STORAGE_READ_REQUET_CODE = 100;



    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(300, 300)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    String currentPhotoPath = "";

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){

            Uri sourceUri = data.getData(); // 1
            File file = null; // 2

            try {

                file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri imageURI = UCrop.getOutput(data);

            File file = new File(imageURI.getPath());

            try {
                InputStream stream =new FileInputStream(file);
                photo.setImageBitmap(BitmapFactory.decodeStream(stream));

                pb.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Uploading Photo....",Toast.LENGTH_SHORT).show();

                StorageReference storage = FirebaseStorage.getInstance().getReference();

                storage.child("profile/"+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() +"/profile.jpeg").putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pb.setVisibility(View.GONE);
                        Global.image = BitmapFactory.decodeFile(imageURI.getPath());
                        Toast.makeText(getApplicationContext(),"Profile Photo Successfully Uploaded",Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Upload Unsuccessful",Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void fill(){
        name.setText(Global.userData.get("firstname") + " " +Global.userData.get("lastname"));
        phone.setText(String.valueOf(Global.userData.get("phone")));
        enroll.setText(Global.enroll);
        branch.setText(getResources().getStringArray(R.array.branch)[Integer.valueOf(String.valueOf(Global.userData.get("branch")))]);
        email.setText(Global.userData.get("email").toString());

        if(Global.userData.get("year") != null){{
            year.setText(String.valueOf(Global.userData.get("year"))+" Year");
        }}
    }

    private void update(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(Global.usertype == 1){
            db.collection("admins").document(Global.enroll).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Global.userData = task.getResult().getData();
                    fill();
                }
            });
        }
        else{
            db.collection("students").document(Global.enroll).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Global.userData = task.getResult().getData();
                    fill();
                }
            });

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Refrencing

        pb = findViewById(R.id.profile_progress);
        pb.setVisibility(View.GONE);

        //Setting up Text Views

        name = findViewById(R.id.profile_name);
        phone = findViewById(R.id.profile_phone);
        enroll = findViewById(R.id.profile_enroll);
        branch = findViewById(R.id.profile_branch);
        email = findViewById(R.id.profile_email);
        year = findViewById(R.id.profile_year);

        fill();





        //Profile Edit

        edit = findViewById(R.id.profile_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Global.usertype==1){
                    Intent i = new Intent(Profile.this,SignupAdmin.class);
                    i.putExtra("task",1);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(Profile.this,SignupStudents.class);
                    i.putExtra("task",1);
                    startActivity(i);
                }
            }
        });

        //Photo

        photo = findViewById(R.id.profile_photo);
        photoedit = findViewById(R.id.profile_edit_image);

        photoedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pictureIntent.setType("image/*");  // 1
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
                    pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
                startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_IMAGE);  // 4


            }
        });

        //Setting up the image

        photo.setImageBitmap(Global.image);


        //Setting up the toolbar

        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }
}
