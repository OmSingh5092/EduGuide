package com.example.eduguide;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.internal.GmsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminClasses.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminClasses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminClasses extends Fragment {



    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.class_recycler_layout,parent,false);
            return new RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.topic.setText(Global.classes.get(position).topic);
            holder.des.setText(Global.classes.get(position).des);
            holder.time.setText(Global.classes.get(position).getTimeString());
            holder.date.setText(Global.classes.get(position).getDateString());
            holder.venue.setText(Global.classes.get(position).venue);
        }

        @Override
        public int getItemCount() {
            return Global.classes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView topic, des, time,date,venue;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                topic = itemView.findViewById(R.id.class_recycler_topic);
                des = itemView.findViewById(R.id.class_recycler_des);
                time = itemView.findViewById(R.id.class_recycler_time);
                date = itemView.findViewById(R.id.class_recycler_date);
                venue = itemView.findViewById(R.id.class_reycler_venue);

                itemView.findViewById(R.id.class_recycler_reminder).setVisibility(View.GONE);
                itemView.findViewById(R.id.class_recycler_addfeedback).setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index",getAdapterPosition());

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        AddTimeClasses addTimeClasses = new AddTimeClasses();
                        addTimeClasses.setArguments(bundle);
                        addTimeClasses.show(ft,"Hello");
                    }
                });
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

    public AdminClasses() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminClasses.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminClasses newInstance(String param1, String param2) {
        AdminClasses fragment = new AdminClasses();
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

    FloatingActionButton add;
    RecyclerView rv;

    public static AdminClasses.RecyclerViewAdapter adapter  ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_admin_classes, container, false);

        //Refrencing

        rv = rootview.findViewById(R.id.admin_class_rv);
        add = rootview.findViewById(R.id.admin_class_add);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTimeClasses classes = new AddTimeClasses();
                FragmentTransaction ft= getFragmentManager().beginTransaction();
                classes.show(ft,"HelloWorld");
            }
        });

        //Getting classes
        adapter = new AdminClasses.RecyclerViewAdapter();
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
