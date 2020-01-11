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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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


    public static View.OnClickListener courseAddEvent;



    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {
        List<Global.CourseData> data ;

        public RecyclerViewAdapter(List<Global.CourseData> data) {
            this.data = data;
        }

        List<Global.CourseData> filterdata;

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.courses,parent,false);
            return new RecyclerViewAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

            if(data.size()!=0){
                if(data.get(position).image!=null){
                    holder.image.setImageBitmap(data.get(position).image);
                    holder.pb.setVisibility(View.GONE);
                    holder.add.setClickable(true);
                }
                holder.des.setText(data.get(position).data.des);
                holder.title.setText(data.get(position).data.title);
                holder.admin.setText(data.get(position).data.admin);

                //Checking existence

                if(Global.regcourses.containsKey(data.get(position).data.courseid)){
                    holder.add.setImageResource(R.drawable.done_icon);
                    holder.add.setClickable(false);
                }
                else{
                    holder.add.setImageResource(R.drawable.add_icon);
                    holder.add.setClickable(true);
                }
                courseAddEvent = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Functions.addCourse(position);
                        Toast.makeText(getActivity(), "Course Registered", Toast.LENGTH_SHORT).show();

                        holder.add.setImageResource(R.drawable.done_icon);
                        holder.add.setClickable(false);
                        //Removing the visibility of add icon


                        MyCourses.adapter.notifyDataSetChanged();
                    }
                };
                holder.add.setOnClickListener(courseAddEvent);



            }



        }



        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public Filter getFilter() {
            return recyclerFilter;
        }


        public  class ViewHolder extends RecyclerView.ViewHolder{

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



                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(),CourseDetails.class);


                        if(Global.regcourses!= null){
                            if(Global.regcourses.containsKey(data.get(getAdapterPosition()).data.courseid)){
                                i.putExtra("isadded",true);
                            }
                            else{
                                i.putExtra("isadded",false);
                            }
                        }

                        i.putExtra("courseindex",getAdapterPosition());
                        startActivity(i);
                    }
                });





            }
        }

        Filter recyclerFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                if(constraint.toString().isEmpty()){
                    filterdata = Global.allcourses;
                }else{
                    filterdata = new ArrayList<>();

                    for(Global.CourseData courseData:data){
                        if(courseData.data.courseid.toLowerCase().contains(search)){
                            filterdata.add(courseData);
                        }else if(courseData.data.des.toLowerCase().contains(search)){
                            filterdata.add(courseData);
                        }else if(courseData.data.admin.toLowerCase().contains(search)){
                            filterdata.add(courseData);
                        }else if(courseData.data.title.toLowerCase().contains(search)){
                            filterdata.add(courseData);
                        }
                    }


                }

                FilterResults results = new FilterResults();
                results.values = filterdata;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (List<Global.CourseData>)results.values;
                notifyDataSetChanged();
            }
        };


    }

    RecyclerView rv;
    SearchView search;

    int i ;

    
    public static AddCourses.RecyclerViewAdapter adapter;

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

        adapter = new RecyclerViewAdapter(Global.allcourses);
        rv.setAdapter(adapter);

        //Getting the Images
        if(!Courses.stopdownload){
            for(int i = 0 ;i<Global.allcourses.size();i++){
                StorageReference imageRef = FirebaseStorage.getInstance().getReference();
                try {
                    final int index = i;
                    final File imageFile = File.createTempFile(Global.allcourses.get(i).data.courseid,"jpeg");
                    imageRef.child("/courses/"+Global.allcourses.get(i).data.courseid+"/coursephoto.jpeg").getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Global.allcourses.get(index).image = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            adapter.notifyItemChanged(index);
                            MyCourses.adapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                return false;
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
