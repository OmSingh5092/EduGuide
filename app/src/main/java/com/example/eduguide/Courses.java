package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Courses extends AppCompatActivity {

    class PagerAdapter extends FragmentPagerAdapter {


        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0: return new MyCourses();
                case 1: return new AddCourses();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    ViewPager vp;
    TabLayout tab;



    RecyclerView rv;
    List<Global.CourseData> data;

    public static  boolean stopdownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //Toolbar

        Toolbar toolbar = findViewById(R.id.course_toolbar);
        setSupportActionBar(toolbar);

        //Populating Global.regcourses

        if(Global.allcourses.size()!=0){
            stopdownload = true;
            setupViewPager();
        }
        else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection(Global.user(Global.usertype)).document(Global.enroll);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().get("courses")!=null){
                        Global.regcourses =  (Map<String, List<Long>>)task.getResult().get("courses");
                    }




                    getAllCourses();

                    //Setting up ViewPagers



                }
            });
        }



        //Refrencing







        //Downloading the course contents








    }
    int i;
    private void getAllCourses(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(Global.regcourses.size()==0){
                    for(i = 0 ;i< task.getResult().getDocuments().size(); i++){

                        Global.Modal.CourseDataModal course = task.getResult().getDocuments().get(i).toObject(Global.Modal.CourseDataModal.class);

                        Global.allcourses.add(new Global.CourseData(course));




                    }
                }
                else{
                    for(i = 0 ;i< task.getResult().getDocuments().size(); i++){

                        Global.Modal.CourseDataModal course = task.getResult().getDocuments().get(i).toObject(Global.Modal.CourseDataModal.class);

                        Global.allcourses.add(new Global.CourseData(course));

                        if(Global.regcourses.containsKey(course.courseid)){
                            Global.regcoursedata.add(new Global.CourseData(course));
                        }



                    }
                }


                setupViewPager();
            }
        });
    }

    private void setupViewPager(){
        vp = findViewById(R.id.courses_vp);
        tab = findViewById(R.id.courses_tab);

        vp.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tab.setupWithViewPager(vp);
        tab.getTabAt(0).setText("Your Courses");
        tab.getTabAt(1).setText("Add Courses");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
