package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.List;

public class CourseDetails extends AppCompatActivity {

    TextView admin,des,courseid;
    RecyclerView resources;
    RecyclerView tasks;
    ImageView photo;
    ImageButton action;

    class TaskRecyclerAdapter extends RecyclerView.Adapter<CourseDetails.TaskRecyclerAdapter.ViewHolder>{
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.tasks_recycler,parent,false);
            return new TaskRecyclerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.des.setText(courseData.data.tasks.get(position).des);
            holder.number.setText(String.valueOf(position+1));
            holder.link.setText(courseData.data.tasks.get(position).link);

            if(position>0){
                if(courseData.data.tasks.get(position).topic.equals(courseData.data.tasks.get(position-1).topic)){
                    holder.topic.setVisibility(View.GONE);
                }
            }
            holder.topic.setText(courseData.data.tasks.get(position).topic);


            if(isadded){
                if(Global.regcourses.get(courseData.data.courseid).contains(Long.valueOf(position))){
                    holder.done.setImageResource(R.drawable.done_icon);
                }
                //Toast.makeText(getApplicationContext(), String.valueOf(Global.regcourses.get(courseData.data.courseid) + String.valueOf(position)), Toast.LENGTH_SHORT).show();
                holder.done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Global.regcourses.get(courseData.data.courseid).contains(Long.valueOf(position))){
                            Functions.removeTask(courseData.data.courseid,position);
                            holder.done.setImageResource(R.drawable.close_icon);
                        }
                        else{
                            Functions.addTask(courseData.data.courseid,position);
                            holder.done.setImageResource(R.drawable.done_icon);
                        }
                    }
                });
            }





        }

        @Override
        public int getItemCount() {
            return courseData.data.tasks.size() ;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView number,des,link,topic;
            ImageButton done;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                number = itemView.findViewById(R.id.tasks_number);
                des = itemView.findViewById(R.id.tasks_des);
                link = itemView.findViewById(R.id.tasks_link);
                topic = itemView.findViewById(R.id.tasks_topic);
                done = itemView.findViewById(R.id.tasks_done);

                if(!isadded){
                    done.setVisibility(View.GONE);
                }
            }
        }
    }




    class ResourceRecyclerAdapter extends RecyclerView.Adapter<CourseDetails.ResourceRecyclerAdapter.ViewHolder>{
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.resources_recycler,parent,false);
            return new ResourceRecyclerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.des.setText(courseData.data.resources.get(position).des);
            holder.number.setText(String.valueOf(position+1));
            holder.link.setText(courseData.data.resources.get(position).link);

            holder.web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(courseData.data.resources.get(position).link));
                    startActivity(i);
                }
            });

            if(position>0){
                if(courseData.data.resources.get(position).topic.equals(courseData.data.resources.get(position-1).topic)){
                    holder.topic.setVisibility(View.GONE);
                }
            }
            holder.topic.setText(courseData.data.resources.get(position).topic);
        }

        @Override
        public int getItemCount() {
            return courseData.data.resources.size() ;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView number,des,link,topic;
            ImageButton web;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                number = itemView.findViewById(R.id.resources_number);
                des = itemView.findViewById(R.id.resources_des);
                link = itemView.findViewById(R.id.resources_link);
                topic = itemView.findViewById(R.id.resources_topic);
                web = itemView.findViewById(R.id.resources_web);
            }
        }
    }

    int index;

    Boolean isadded;



    public Global.CourseData courseData;
    TaskRecyclerAdapter taskAdapter;
    ResourceRecyclerAdapter resourceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        //Setting up toolbar

        Toolbar toolbar = findViewById(R.id.coursedetail_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing

        admin= findViewById(R.id.coursedetails_admin);
        des = findViewById(R.id.coursedetail_des);
        courseid = findViewById(R.id.coursedetail_courseid);
        resources = findViewById(R.id.coursedetail_resources);
        tasks = findViewById(R.id.coursedetail_tasks);
        photo = findViewById(R.id.coursedetail_image);
        action = findViewById(R.id.coursedetail_action);

        //Getting coursedata

        index = getIntent().getIntExtra("courseindex",0);
        isadded = getIntent().getBooleanExtra("isadded",false);
        if(isadded){
            action.setImageResource(R.drawable.delete_icon);
            courseData = Global.regcoursedata.get(index);
        }
        else{
            courseData= Global.allcourses.get(index);
        }
        toolbar.setTitle(courseData.data.title);




        //setting up the data

        admin.setText(courseData.data.admin);
        des.setText(courseData.data.des);
        courseid.setText(courseData.data.courseid);
        photo.setImageBitmap(courseData.image);


        LinearLayoutManager resourcelayoutManager = new LinearLayoutManager(CourseDetails.this);
        resources.setLayoutManager(resourcelayoutManager);
        LinearLayoutManager taskslayoutManager = new LinearLayoutManager(CourseDetails.this);
        tasks.setLayoutManager(taskslayoutManager);

        resourceAdapter = new ResourceRecyclerAdapter();
        taskAdapter = new TaskRecyclerAdapter();
        resources.setAdapter(resourceAdapter);
        tasks.setAdapter(taskAdapter);

        //Setting up action

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isadded){
                    Functions.removeCourse(index);
                    Toast.makeText(CourseDetails.this, "Course successfully removed.", Toast.LENGTH_SHORT).show();

                }
                else{
                    Functions.addCourse(index);
                    Toast.makeText(CourseDetails.this, "Course successfully added.", Toast.LENGTH_SHORT).show();

                }
                AddCourses.adapter.notifyDataSetChanged();
                MyCourses.adapter.notifyDataSetChanged();
                finish();
            }
        });






    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
