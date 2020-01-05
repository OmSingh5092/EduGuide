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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //Toolbar

        Toolbar toolbar = findViewById(R.id.makecourse_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing

        vp = findViewById(R.id.courses_vp);
        tab = findViewById(R.id.courses_tab);

        vp.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tab.setupWithViewPager(vp);
        tab.getTabAt(0).setText("Your Courses");
        tab.getTabAt(1).setText("Add Courses");





        //Downloading the course contents








    }

    @Override
    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        super.supportNavigateUpTo(upIntent);
        finish();
    }
}
