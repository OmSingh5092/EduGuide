package com.example.eduguide;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCourses.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCourses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCourses extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddCourses() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCourses.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCourses newInstance(String param1, String param2) {
        AddCourses fragment = new AddCourses();
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



    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{


        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = getLayoutInflater().inflate(R.layout.courses,parent,false);
            return new RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

            if(courseData.size()!=0){
                if(courseData.get(position).image!=null){
                    holder.image.setImageBitmap(courseData.get(position).image);
                }
                holder.des.setText(courseData.get(position).data.des);
                holder.title.setText(courseData.get(position).data.title);
                holder.admin.setText(courseData.get(position).data.admin);
            }



        }

        @Override
        public int getItemCount() {
            return courseData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ImageView image;
            TextView title,des,admin;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.course_recycler_image);
                title = itemView.findViewById(R.id.course_recycler_title);
                des  = itemView.findViewById(R.id.course_recycler_des);
                admin = itemView.findViewById(R.id.courses_recycler_admin);
            }
        }
    }

    RecyclerView rv;
    SearchView search;

    int i ;

    List<Global.CourseData> courseData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview =inflater.inflate(R.layout.fragment_add_courses, container, false);

        //Refrencing

        rv = rootview.findViewById(R.id.add_course_rv);
        search = rootview.findViewById(R.id.add_course_search);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        //Making Adapter

        RecyclerViewAdapter adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);

        //Getting the course data
        courseData = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("courses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(i = 0 ;i< task.getResult().getDocuments().size(); i++){

                    Global.Modal.CourseDataModal course = task.getResult().getDocuments().get(i).toObject(Global.Modal.CourseDataModal.class);

                    courseData.add(new Global.CourseData(course));

                    StorageReference imageRef = FirebaseStorage.getInstance().getReference();
                    try {
                        final int index = i;
                        final File imageFile = File.createTempFile(course.courseid,"jpeg");
                        imageRef.child("/courses/"+course.courseid+"/coursephoto.jpeg").getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                courseData.get(index).image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                adapter.notifyItemChanged(index);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });







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
