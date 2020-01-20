package com.example.eduguide;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShowFeedback extends DialogFragment {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v  = getLayoutInflater().inflate(R.layout.show_feedback_recycler,parent,false);
            return new RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.student.setText(studentlist.get(position));
            holder.feedback.setText(datalist.get(position).feedback);
            holder.rating.setText(datalist.get(position).rating.toString());


            //Email

            holder.email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            holder.contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.contacts.getVisibility() == View.GONE){
                        holder.contacts.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.contacts.setVisibility(View.GONE);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return studentlist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView student,rating,feedback;
            Button contact;
            LinearLayout contacts;
            ImageButton whatsapp, call,email;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                student =itemView.findViewById(R.id.show_feedback_recycler_student);
                rating = itemView.findViewById(R.id.show_feedback_recycler_rating);
                feedback = itemView.findViewById(R.id.show_feedback_recycler_feedback);
                contact = itemView.findViewById(R.id.show_feedback_recycler_contact);
                contacts = itemView.findViewById(R.id.show_feedback_recycler_contacts);

                whatsapp = itemView.findViewById(R.id.show_feedback_recycler_whatsapp);
                call = itemView.findViewById(R.id.show_feedback_recycler_call);
                email = itemView.findViewById(R.id.show_feedback_recycler_email);

            }
        }
    }


    RecyclerView rv;
    List<Global.Modal.FeedbackDataModal> datalist = new ArrayList<>();
    List<String> studentlist = new ArrayList<>();
    Long timeStamp;
    RecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.show_feedback,container,false);

        //Setting up Toolbar
        Toolbar toolbar = rootview.findViewById(R.id.show_feedback_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        toolbar.setTitle(getArguments().getString("title"));

        //Getting arguments;
        timeStamp = getArguments().getLong("timestamp");

        rv= rootview.findViewById(R.id.show_feedback_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("feedbacks").document(timeStamp.toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){
                    Map<String,Object> data = task.getResult().getData();
                    for(String key : data.keySet()){

                        studentlist.add(key);

                    }
                    for(Object tempData: data.values()){
                        Map<String,Object> castedData= (Map<String,Object>)tempData;
                        Global.Modal.FeedbackDataModal feedbackDataModal = new Global.Modal.FeedbackDataModal((Long)castedData.get("rating"),(String)castedData.get("feedback"));
                        datalist.add(feedbackDataModal);

                    }

                    rv.setAdapter(adapter);
                }
            }
        });



        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
