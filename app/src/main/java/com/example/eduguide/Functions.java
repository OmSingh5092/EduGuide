package com.example.eduguide;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.MapValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Functions {

    public static void addCourse(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Global.user(Global.usertype)).document(Global.enroll);


        List<Long> indexList = new ArrayList<>();
        Map<String, Object> updates = new HashMap<>();
        updates.put("courses." + Global.allcourses.get(position).data.courseid, indexList);
        docRef.update(updates);

        Global.regcourses.put(Global.allcourses.get(position).data.courseid, indexList);
        Global.regcoursedata.add(Global.allcourses.get(position));
    }

    public static void removeCourse(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Global.user(Global.usertype)).document(Global.enroll);

        if (Global.regcourses.size() == 1) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("courses", FieldValue.delete());
            docRef.update(updates);
        } else {
            Map<String, Object> updates = new HashMap<>();
            updates.put("courses", FieldValue.arrayRemove(Global.allcourses.get(position).data.courseid));
            docRef.update(updates);
        }

        Global.regcourses.remove(Global.regcoursedata.get(position).data.courseid);
        Global.regcoursedata.remove(position);
    }

    public static void addTask(String courseid, int tasknum) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Global.user(Global.usertype)).document(Global.enroll);
        Global.regcourses.get(courseid).add(Long.valueOf(tasknum));

        Map<String, Object> updates = new HashMap<>();
        updates.put("courses." + courseid, FieldValue.arrayUnion(tasknum));

        docRef.update(updates);

    }

    public static void removeTask(String courseid, int tasknum) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Global.user(Global.usertype)).document(Global.enroll);
        Global.regcourses.get(courseid).remove(tasknum);

        Map<String, Object> updates = new HashMap<>();
        updates.put("courses." + courseid, FieldValue.arrayRemove(tasknum));

        docRef.update(updates);
    }



    public static String getDateString(Long timestamp) {

        DateFormat df = new SimpleDateFormat("dd:mm:yyyyy");
        Date date = new Date(timestamp);
        return df.format(date);

    }


}
