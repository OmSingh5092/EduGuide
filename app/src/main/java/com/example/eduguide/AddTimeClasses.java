package com.example.eduguide;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class AddTimeClasses extends DialogFragment {

    TextInputEditText topic,des,venue;
    TextView timeview,dateview;
    ImageButton timebutton,datebutton,done;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.addclass_alert_layout,container,false);

        //Setting up Toolbar
        Toolbar toolbar = rootview.findViewById(R.id.add_class_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Refrencing

        topic = rootview.findViewById(R.id.add_class_topic);
        des =rootview.findViewById(R.id.add_class_des);
        venue =rootview.findViewById(R.id.add_class_venue);
        timeview = rootview.findViewById(R.id.add_class_time_view);
        dateview = rootview.findViewById(R.id.add_class_date_view);
        timebutton = rootview.findViewById(R.id.add_class_time);
        datebutton = rootview.findViewById(R.id.add_class_date);
        done = rootview.findViewById(R.id.add_class_done);

        if(getArguments()!=null){
            position = getArguments().getInt("index");
        }


        AddMethods addMethods = new AddMethods();
        addMethods.makeViews();





        return rootview;
    }

    Integer position ;

    private  class AddMethods{
        String time_string, date_string;

        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

        Calendar currentTime = GregorianCalendar.getInstance();







        private  void makeViews() {


            TimePickerDialog timePickerDialog  = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time_string = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                }
            },currentTime.get(Calendar.HOUR_OF_DAY),currentTime.get(Calendar.MINUTE),false);

            timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    timeview.setText(time_string);
                }
            });


            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date_string = String.valueOf(dayOfMonth)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
                    calendar.set(year,month,dayOfMonth);
                }
            },currentTime.get(Calendar.YEAR),currentTime.get(Calendar.MONTH),currentTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dateview.setText(date_string);
                }
            });

            datebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });
            timebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timePickerDialog.show();
                }
            });

            if(position!=null){
                topic.setText(Global.classes.get(position).topic);
                des.setText(Global.classes.get(position).des);
                timeview.setText(Global.classes.get(position).time);
                dateview.setText(Global.classes.get(position).date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Global.classes.get(position).timestamp);

                timePickerDialog.updateTime(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isEmpty()){
                            Toast.makeText(getContext(), "Please enter all the entries.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            updateValues();
                            Toast.makeText(getContext(), "Loading the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isEmpty()){
                            Toast.makeText(getContext(), "Please enter all the entries.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            putValues();
                            Toast.makeText(getContext(), "Loading the data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }




        }

        private boolean isEmpty(){
            if(topic.getText().toString().isEmpty()){
                return true;
            }else if(des.getText().toString().isEmpty()){
                return true;
            }else if(time_string.isEmpty()){
                return true;
            }else if(date_string.isEmpty()){
                return true;
            }

            return false;
        }

        private void putValues(){

            //Getting timestamp;
            String timeStamp = String.valueOf(calendar.getTimeInMillis());


            // Generating object

            Global.Modal.ClassDataModal classModal = new Global.Modal.ClassDataModal(topic.getText().toString(),
                    des.getText().toString(),time_string,date_string, venue.getText().toString(),false ,Long.valueOf(timeStamp));


            //Loacal Data
            Global.classes.add(classModal);
            Global.Modal.ClassDataModal.sortList();
            AdminClasses.adapter.notifyDataSetChanged();

            FirebaseFirestore db = FirebaseFirestore.getInstance();



            db.collection("classes").document(timeStamp).set(classModal).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    getDialog().dismiss();
                }
            });
        }

        private void updateValues(){

            //Getting timestamp;
            String timeStamp = String.valueOf(calendar.getTimeInMillis());


            // Generating object

            Global.Modal.ClassDataModal classModal = new Global.Modal.ClassDataModal(topic.getText().toString(),
                    des.getText().toString(),time_string,date_string,venue.getText().toString(), false,Long.valueOf(timeStamp));


            //Loacal Data
            Global.classes.add(classModal);
            Global.Modal.ClassDataModal.sortList();
            AdminClasses.adapter.notifyDataSetChanged();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("classes").document(Global.classes.get(position).timestamp.toString()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    db.collection("classes").document(timeStamp).set(classModal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getDialog().dismiss();
                        }
                    });
                }
            });




        }


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
