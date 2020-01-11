package com.example.eduguide;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyCourses.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyCourses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyCourses extends Fragment {


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.courses,parent,false);
            return new MyCourses.RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            if(Global.regcoursedata.size()!=0){
                if(Global.regcoursedata.get(position).image!=null){
                    holder.image.setImageBitmap(Global.regcoursedata.get(position).image);
                    holder.pb.setVisibility(View.GONE);
                    holder.add.setClickable(true);
                }
                holder.des.setText(Global.regcoursedata.get(position).data.des);
                holder.title.setText(Global.regcoursedata.get(position).data.title);
                holder.admin.setText(Global.regcoursedata.get(position).data.admin);

                //Checking existence

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Functions.removeCourse(position);

                        Toast.makeText(getActivity(), "Course Successfully Removed", Toast.LENGTH_SHORT).show();

                        rv.setAdapter(adapter);
                        AddCourses.adapter.notifyDataSetChanged();
                    }
                });



            }

        }



        @Override
        public int getItemCount() {
            return Global.regcoursedata.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder{

            ImageView image;
            TextView title,des,admin;
            ImageButton add;
            ProgressBar pb;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.course_recycler_image);
                title = itemView.findViewById(R.id.course_recycler_title);
                des  = itemView.findViewById(R.id.course_recycler_des);
                admin = itemView.findViewById(R.id.courses_recycler_admin);
                add = itemView.findViewById(R.id.courses_recycler_add);
                pb = itemView.findViewById(R.id.courses_recycler_imageload);

                add.setImageResource(R.drawable.close_icon);
                add.setClickable(false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(),CourseDetails.class);
                        Bundle bundle = new Bundle();
                        i.putExtra("isadded",true);
                        i.putExtra("courseindex",getAdapterPosition());
                        startActivity(i);
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

    public MyCourses() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyCourses.
     */
    // TODO: Rename and change types and number of parameters
    public static MyCourses newInstance(String param1, String param2) {
        MyCourses fragment = new MyCourses();
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
    }



    RecyclerView rv;
    SearchView search;
    

    public static MyCourses.RecyclerViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_my_courses, container, false);

        //Refrencing
        rv = rootview.findViewById(R.id.mycourse_rv);
        search = rootview.findViewById(R.id.mycourse_searchview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        //Making Adapter

        adapter = new MyCourses.RecyclerViewAdapter();
        rv.setAdapter(adapter);

        //Getting course Data

        if(!Courses.stopdownload){
            for(int i = 0 ;i<Global.regcoursedata.size();i++){
                StorageReference imageRef = FirebaseStorage.getInstance().getReference();
                try {
                    final int index = i;
                    final File imageFile = File.createTempFile(Global.regcoursedata.get(i).data.courseid,"jpeg");
                    imageRef.child("/courses/"+Global.regcoursedata.get(i).data.courseid+"/coursephoto.jpeg").getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Global.regcoursedata.get(index).image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            adapter.notifyItemChanged(index);
                            MyCourses.adapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        




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
