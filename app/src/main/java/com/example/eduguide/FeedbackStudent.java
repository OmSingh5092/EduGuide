package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedOutputStream;
import java.util.List;

public class FeedbackStudent extends AppCompatActivity {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        Global.Modal.FeedbackDataModal data;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.class_recycler_layout,parent,false);
            return new RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.topic.setText(Global.completedclass.get(position).topic);
            holder.des.setText(Global.completedclass.get(position).des);
            holder.time.setText(Global.completedclass.get(position).time);
            holder.date.setText(Global.completedclass.get(position).date);
            holder.venue.setText(Global.completedclass.get(position).venue);
            Bundle bundle = new Bundle();
            if(Global.feedbackgiven.contains(Global.completedclass.get(position).timestamp)){
                holder.addfeedback.setText("View Feedback");
                bundle.putInt("task",1);

            }
            else{
                bundle.putInt("task",2);
            }



            holder.addfeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    bundle.putLong("timestamp",Global.completedclass.get(position).timestamp);
                    AddFeedback addFeedback = new AddFeedback();
                    addFeedback.setArguments(bundle);
                    FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
                    addFeedback.show(ft,"Hello");



                }
            });
        }

        @Override
        public int getItemCount() {
            return Global.completedclass.size() ;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView topic, des, time,date,venue;
            ImageButton menu, reminder;
            Button addfeedback;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                topic = itemView.findViewById(R.id.class_recycler_topic);
                des = itemView.findViewById(R.id.class_recycler_des);
                time = itemView.findViewById(R.id.class_recycler_time);
                date = itemView.findViewById(R.id.class_recycler_date);
                venue = itemView.findViewById(R.id.class_reycler_venue);
                reminder = itemView.findViewById(R.id.classs_recycler_reminder);
                addfeedback = itemView.findViewById(R.id.class_recycler_addfeedback);

                itemView.findViewById(R.id.classs_recycler_reminder).setVisibility(View.GONE);
            }
        }
    }

    RecyclerView rv;
    public static RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_student);

        //Setting up the toolbar

        Toolbar toolbar = findViewById(R.id.feedback_student_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing

        rv = findViewById(R.id.feedback_student_rv);
        adapter = new RecyclerViewAdapter();
        rv.setLayoutManager(new LinearLayoutManager(FeedbackStudent.this));


        //Recycler view

        LinearLayoutManager layoutManager = new LinearLayoutManager(FeedbackStudent.this);
        rv.setLayoutManager(layoutManager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("students").document(Global.enroll).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().get("feedback") != null)
                Global.feedbackgiven = (List<Long>) task.getResult().get("feedback");

                rv.setAdapter(adapter);
            }
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}