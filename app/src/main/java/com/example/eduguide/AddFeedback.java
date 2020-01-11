package com.example.eduguide;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.GoalRow;
import androidx.fragment.app.DialogFragment;

public class AddFeedback extends DialogFragment {

    SeekBar ratingseek;
    TextInputEditText feedback;
    ImageButton done;

    Integer rating;
    Long timeStamp;
    int task;

    Global.Modal.FeedbackDataModal data;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.addfeedback_layout,container,false);

        //Setting up Toolbar

        Toolbar toolbar = rootview.findViewById(R.id.add_feedback_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Getting Arguments

        timeStamp = getArguments().getLong("timestamp");
        task = getArguments().getInt("task");



        //Refrencing

        feedback = rootview.findViewById(R.id.add_feedback_feedback);
        ratingseek = rootview.findViewById(R.id.add_feedback_rating);
        done = rootview.findViewById(R.id.add_feedback_done);






        if(task==1){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("feedbacks").document(timeStamp.toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    data = documentSnapshot.toObject(Global.Modal.FeedbackDataModal.class);
                    setValues();
                }
            });
        }
        else{
            rating = 0;
            ratingseek.setProgress(0);



        }

        ratingseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rating = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFieldEmpty()){
                    Toast.makeText(getActivity(), "Please enter all the entries.", Toast.LENGTH_SHORT).show();
                }
                else{
                    addData();
                }
            }
        });





        return rootview;
    }

    private void setValues(){

        ratingseek.setProgress(data.rating.intValue());
        feedback.setText(data.feedback);
    }

    private void addData(){
        Toast.makeText(getActivity(), "Uploading Feedback", Toast.LENGTH_SHORT).show();
        Global.Modal.FeedbackDataModal feedbackDataModal = new Global.Modal.FeedbackDataModal(Long.valueOf(rating),feedback.getText().toString(),Global.enroll);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(task !=1){
            Global.feedbackgiven.add(timeStamp);
            FeedbackStudent.adapter.notifyDataSetChanged();
            db.collection("students").document(Global.enroll).update("feedback", FieldValue.arrayUnion(timeStamp));
        }
        db.collection("feedbacks").document(timeStamp.toString()).set(feedbackDataModal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Feedback uploaded successfully.", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
    }

    private boolean isFieldEmpty(){
        if(feedback.getText().toString().isEmpty()){
            return true;
        }
        return false;
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
