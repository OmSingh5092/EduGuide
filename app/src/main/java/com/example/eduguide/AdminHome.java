package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

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

public class AdminHome extends AppCompatActivity {

    ListView drawerList;
    DrawerLayout drawerLayout;
    Button logout;
    ProgressBar pb;
    ViewPager viewPager;
    TabLayout tab;

    class ViewPagerAdapeter extends FragmentPagerAdapter{

        public ViewPagerAdapeter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0: return new AdminClasses();
                case 1: return new AdminNotifications();
                case 2 : return new AdminMessages();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    Integer STORAGE_WRITE_REQUEST_CODE =100, STORAGE_READ_REQUET_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = findViewById(R.id.admin_home_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing

        drawerLayout = findViewById(R.id.admin_home_drawer);
        drawerList = findViewById(R.id.drawer_listview);
        logout = findViewById(R.id.student_home_logout);
        pb = findViewById(R.id.admin_home_pb);
        viewPager = findViewById(R.id.admin_home_vp);
        tab = findViewById(R.id.admin_home_tablayout);

        //Setting up List View
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, this.getResources().getStringArray(R.array.admin_drawer));
        drawerList.setAdapter(adapter);


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
            ActivityCompat.requestPermissions(AdminHome.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(AdminHome.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_REQUET_CODE);
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

        drawerList = findViewById(R.id.drawer_listview);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: startActivity(new Intent(AdminHome.this,Profile.class)); break;
                    case 3: startActivity(new Intent(AdminHome.this, MakeCourse.class)); break;
                    case 5: startActivity(new Intent(AdminHome.this,FeedbackAdmin.class));
                }
            }
        });



        //Logout

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminHome.this,HomeActivity.class));
                finish();
            }
        });

        //Setting up ViewPager
        ViewPagerAdapeter adapeter = new ViewPagerAdapeter(getSupportFragmentManager());
        viewPager.setAdapter(adapeter);

        tab.setupWithViewPager(viewPager);
        tab.getTabAt(0).setText("Classes");
        tab.getTabAt(1).setText("Notifications");
        tab.getTabAt(2).setText("Messages");


    }

    @Override
    public boolean onSupportNavigateUp() {
        drawerLayout.openDrawer(Gravity.LEFT);
        return super.onSupportNavigateUp();
    }
}
