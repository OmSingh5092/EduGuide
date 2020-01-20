package com.example.eduguide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentSchedule.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentSchedule#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentSchedule extends Fragment {


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.class_recycler_layout,parent,false);
            return new StudentSchedule.RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.topic.setText(Global.classes.get(position).topic);
            holder.des.setText(Global.classes.get(position).des);
            holder.time.setText(Global.classes.get(position).getTimeString());
            holder.date.setText(Global.classes.get(position).getDateString());
            holder.venue.setText(Global.classes.get(position).venue);






            holder.reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("students").document(Global.enroll).update("classes", FieldValue.arrayUnion(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Reminder added successfully.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return Global.classes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView topic, des, time,date,venue;
            ImageButton menu, reminder;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                topic = itemView.findViewById(R.id.class_recycler_topic);
                des = itemView.findViewById(R.id.class_recycler_des);
                time = itemView.findViewById(R.id.class_recycler_time);
                date = itemView.findViewById(R.id.class_recycler_date);
                venue = itemView.findViewById(R.id.class_reycler_venue);
                reminder = itemView.findViewById(R.id.class_recycler_reminder);

                //Removing add Feedback button

                itemView.findViewById(R.id.class_recycler_addfeedback).setVisibility(View.GONE);
            }
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StudentSchedule() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentSchedule.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentSchedule newInstance(String param1, String param2) {
        StudentSchedule fragment = new StudentSchedule();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if(Global.classes.size()==0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("classes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().getDocuments()!=null){
                        for(DocumentSnapshot snap: task.getResult().getDocuments()){
                            Global.Modal.ClassDataModal tempData = snap.toObject(Global.Modal.ClassDataModal.class);
                            if(tempData.done){
                                Global.completedclass.add(tempData);
                            }
                            else{
                                Global.classes.add(tempData);
                            }
                            Global.Modal.ClassDataModal.sortList();
                            rv.setAdapter(adapter);
                        }
                    }
                }
            });
        }


    }

    RecyclerView rv;
    RecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_student_schedule, container, false);

        rv= rootview.findViewById(R.id.student_class_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);

        return rootview;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }  */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
