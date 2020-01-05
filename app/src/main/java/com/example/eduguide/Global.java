package com.example.eduguide;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class Global {

    static Map<String, Object> userData = new HashMap<>();

    static String enroll;
    static String phone;
    static Integer usertype;
    static Bitmap image;

    public static class Modal{
        public static class CourseDataModal {
            public String title;
            public String des;
            public String courseid;
            public String admin;
            public List<Global.Resource> resources;
            public List<Global.Tasks>tasks;

            public CourseDataModal(){
            }

            public CourseDataModal(String title, String des, String courseid,String admin, List<Resource> resources, List<Tasks> tasks) {
                this.title = title;
                this.des = des;
                this.courseid = courseid;
                this.admin = admin;
                this.resources = resources;
                this.tasks = tasks;
            }
        }
    }
    public static class CourseData{

        Modal.CourseDataModal data;
        Bitmap image;

        public CourseData(){

        }

        public CourseData(Modal.CourseDataModal data) {
            this.data = data;
            this.image = null;
        }
    }
    public static class Resource{
        public String des;
        public String link;
        public String topic;

        public Resource(){

        }

        public Resource(String des, String link,String topic) {
            this.des = des;
            this.link = link;
            this.topic = topic;
        }
    }
    public static class Tasks{
        public String des;
        public String topic;
        public Boolean done;

        public Tasks(){}

        public Tasks(String des, String link,String topic) {
            this.des = des;
            this.link = link;
            this.topic = topic;
            this.done = false;
        }

        String link;
    }





}
