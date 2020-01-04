package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class StudentHome extends AppCompatActivity {


    private  class Pageradapter extends FragmentPagerAdapter {

        public Pageradapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new StudentSchedule();
                case 1: return new StudentNotification();
                case 2: return new Contacts();
            }

            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }



        @Override
        public int getCount() {
            return 3;
        }
    }

    DrawerLayout drawerLayout;
    Button logout;
    TabLayout tabLayout;
    ViewPager vp;
    ProgressBar pb;
    ListView lv;

    Integer STORAGE_WRITE_REQUEST_CODE=100, STORAGE_READ_REQUET_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        //ProgressBar

        pb = findViewById(R.id.student_home_pb);
        pb.setVisibility(View.GONE);


        //Setting up Toolbar

        Toolbar toolbar = findViewById(R.id.student_home_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.student_home_drawer);

        //Setting up ViewPager

        tabLayout = findViewById(R.id.student_home_tab);
        vp = findViewById(R.id.student_home_vp);
        vp.setAdapter(new Pageradapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(vp);

        //Getting old Data

        SharedPreferences sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        Global.usertype= sharedPreferences.getInt("usertype",0);
        Global.enroll = sharedPreferences.getString("userId",null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(Global.usertype== 1){
            db.collection("admins").document(Global.enroll).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Global.userData = task.getResult().getData();
                }
            });
        }

        else{

            db.collection("students").document(Global.enroll).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Global.userData = task.getResult().getData();
                }
            });

        }



        //Getting profile Photo

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(StudentHome.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(StudentHome.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
        }

        try {
            //Setting up Profile Photo

            final File file = File.createTempFile("image",".jpeg");
            StorageReference profileimageref = FirebaseStorage.getInstance().getReference();
            profileimageref.child("profile/"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()+"/profile.jpeg").getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    ImageView imageView = findViewById(R.id.student_drawer_image);
                    try {
                        Global.image = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(Global.image);
                    pb.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pb.setVisibility(View.GONE);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        pb.setVisibility(View.VISIBLE);


        //List View

        lv = findViewById(R.id.drawer_listview);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: startActivity(new Intent(StudentHome.this,Profile.class));
                }
            }
        });




        //Logout

        logout = findViewById(R.id.student_home_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(StudentHome.this, HomeActivity.class));
                finish();
            }
        });







    }

    @Override
    public boolean onSupportNavigateUp() {

        drawerLayout.openDrawer(Gravity.LEFT);
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = findViewById(R.id.student_drawer_image);
        imageView.setImageBitmap(Global.image);
    }
}
