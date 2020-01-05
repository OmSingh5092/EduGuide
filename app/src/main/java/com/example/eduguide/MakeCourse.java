package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeCourse extends AppCompatActivity {

    Integer PICK_IMAGE = 100, STORAGE_WRITE_REQUEST_CODE= 100, STORAGE_READ_REQUET_CODE = 100;

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(300, 300)
                .withAspectRatio(5f, 5f)
                .start(this);
    }

    String currentPhotoPath = "";

    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){

            Uri sourceUri = data.getData(); // 1
            File file = null; // 2

            try {

                file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);  // 3
                openCropActivity(sourceUri, destinationUri);  // 4

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else if(requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK){
            imageURI = UCrop.getOutput(data);

            File file = new File(imageURI.getPath());

            try {
                InputStream stream =new FileInputStream(file);
                image.setImageBitmap(BitmapFactory.decodeStream(stream));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    TextInputEditText title,des,courseid;
    ImageButton resourceadd,taskadd,done;
    RecyclerView resources, tasks;
    FloatingActionButton addimage;
    ImageView image;
    Uri imageURI;
    ProgressBar imageloading;

    class ResourceRecyclerAdapter extends RecyclerView.Adapter<ResourceRecyclerAdapter.ViewHolder>{

        @NonNull
        @Override
        public ResourceRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resources_recycler,parent,false);
            return new ResourceRecyclerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ResourceRecyclerAdapter.ViewHolder holder, int position) {

            holder.des.setText(resourceData.get(position).des);
            holder.number.setText(String.valueOf(position+1));
            holder.link.setText(resourceData.get(position).link);

            if(position>0){
                if(resourceData.get(position).topic.equals(resourceData.get(position-1).topic)){
                    holder.topic.setVisibility(View.GONE);
                }
            }
            holder.topic.setText(resourceData.get(position).topic);



        }

        @Override
        public int getItemCount() {
            return resourceData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView number,des,link,topic;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                number = itemView.findViewById(R.id.resources_number);
                des = itemView.findViewById(R.id.resources_des);
                link = itemView.findViewById(R.id.resources_link);
                topic = itemView.findViewById(R.id.resources_topic);

            }
        }
    }


    class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.ViewHolder>{

        @NonNull
        @Override
        public TasksRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_recycler,parent,false);

            return new TasksRecyclerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TasksRecyclerAdapter.ViewHolder holder, int position) {

            holder.des.setText(taskData.get(position).des);
            holder.number.setText(String.valueOf(position+1));
            holder.link.setText(taskData.get(position).link);

            if(position>0){
                if(taskData.get(position).topic.equals(taskData.get(position-1).topic)){
                    holder.topic.setVisibility(View.GONE);
                }
            }
            holder.done.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return taskData.size();
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
            }
        }
    }

    private List<Global.Tasks> taskData;
    private List<Global.Resource> resourceData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_course);

        //Adapters

        ResourceRecyclerAdapter resourceAdapter = new ResourceRecyclerAdapter();
        TasksRecyclerAdapter tasksAdapter = new TasksRecyclerAdapter();

        //Setting up the toolbar

        Toolbar toolbar = findViewById(R.id.makecourse_toolbar);
        setSupportActionBar(toolbar);

        //Refrencing Objects
        title = findViewById(R.id.makecourse_title);
        des = findViewById(R.id.makecourse_des);
        courseid = findViewById(R.id.makecourse_courseid);
        resourceadd = findViewById(R.id.makecourse_addresource);
        taskadd = findViewById(R.id.makecourse_addtask);
        done = findViewById(R.id.makecourse_done);
        resources = findViewById(R.id.makecourse_resources);
        tasks = findViewById(R.id.makecourse_tasks);
        addimage = findViewById(R.id.makecourse_addimage);
        image = findViewById(R.id.makecourse_image);
        imageloading = findViewById(R.id.makecourse_imageload);

        //Adding LayoutManagers to the recycler View

        LinearLayoutManager resourcelayoutManager = new LinearLayoutManager(MakeCourse.this);
        resources.setLayoutManager(resourcelayoutManager);
        LinearLayoutManager taskslayoutManager = new LinearLayoutManager(MakeCourse.this);
        tasks.setLayoutManager(taskslayoutManager);

        //Adding Resource

        resourceData = new ArrayList<>();

        resources.setAdapter(resourceAdapter);

        resourceadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.Resource resource ;

                AlertDialog.Builder alert = new AlertDialog.Builder(MakeCourse.this);
                LinearLayout linearLayout = new LinearLayout(MakeCourse.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText description = new EditText(MakeCourse.this);
                EditText link = new EditText(MakeCourse.this);
                EditText topic = new EditText(MakeCourse.this);
                if(resourceData.size()!=0){
                    topic.setText(resourceData.get(resourceData.size()-1).topic);
                }
                else{
                    topic.setHint("Topic");
                }
                description.setHint("Description");
                link.setHint("Link");
                linearLayout.addView(topic);
                linearLayout.addView(description);
                linearLayout.addView(link);

                alert.setView(linearLayout);
                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resourceData.add(new Global.Resource(description.getText().toString(),link.getText().toString(),topic.getText().toString()));
                        resourceAdapter.notifyDataSetChanged();
                    }
                });

                alert.show();

            }
        });


        taskData = new ArrayList<>();


        tasks.setAdapter(tasksAdapter);


        taskadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(MakeCourse.this);
                LinearLayout linearLayout = new LinearLayout(MakeCourse.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText description = new EditText(MakeCourse.this);
                EditText link = new EditText(MakeCourse.this);
                EditText topic = new EditText(MakeCourse.this);
                if(taskData.size()!=0){
                    topic.setText(taskData.get(taskData.size()-1).topic);
                }
                else{
                    topic.setHint("Topic");
                }
                description.setHint("Description");
                link.setHint("Link");
                linearLayout.addView(topic);
                linearLayout.addView(description);
                linearLayout.addView(link);


                alert.setView(linearLayout);
                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskData.add(new Global.Tasks(description.getText().toString(),link.getText().toString(),topic.getText().toString()));
                        tasksAdapter.notifyDataSetChanged();
                    }
                });
                alert.show();

            }
        });

        //Imageloading
        imageloading.setVisibility(View.GONE);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEmpty()){
                    Toast.makeText(MakeCourse.this, "Please Enter all the entries!", Toast.LENGTH_SHORT).show();
                }
                else{
                    imageloading.setVisibility(View.VISIBLE);
                    StorageReference reference = FirebaseStorage.getInstance().getReference();
                    reference.child("/courses/"+courseid.getText().toString()+"/coursephoto.jpeg").putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            putValues();
                            imageloading.setVisibility(View.GONE);
                        }
                    });
                }



            }
        });


        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pictureIntent.setType("image/*");  // 1
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);  // 2
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String[] mimeTypes = new String[]{"image/jpeg", "image/png"};  // 3
                    pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
                startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_IMAGE);  // 4

            }
        });


    }

    private void putValues(){

        Toast.makeText(MakeCourse.this, "Uploading Course...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("courses").document(courseid.getText().toString());
        Global.Modal.CourseDataModal modal = new Global.Modal.CourseDataModal(title.getText().toString(),des.getText().toString(),courseid.getText().toString(),Global.enroll,resourceData,taskData);
        docRef.set(modal);
    }

    private boolean isEmpty(){
        if(title.getText().toString().equals(null))
            return true;
        else if(courseid.getText().toString().equals(null))
            return true;
        else if(des.getText().toString().equals(null))
            return true;
        else if(taskData.isEmpty())
            return true;
        else if(resourceData.isEmpty())
            return true;

        return false;
    }

    private boolean isFilled(){
        if(!title.getText().toString().equals(null))
            return true;
        else if(!courseid.getText().toString().equals(null))
            return true;
        else if(!des.getText().toString().equals(null))
            return true;
        else if(!taskData.isEmpty())
            return true;
        else if(!resourceData.isEmpty())
            return true;

        return false;

    }

    @Override
    public boolean onSupportNavigateUp() {
        if(isFilled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(MakeCourse.this);
            builder.setTitle("Do You Want to Quite. The Changes made will be discarded!");
            builder.setPositiveButton("Continue Anyway", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Cancle",null);
            builder.show();
        }
        else{
            finish();
        }

        return super.onSupportNavigateUp();
    }
}
