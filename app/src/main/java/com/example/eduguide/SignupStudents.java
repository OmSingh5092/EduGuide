package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignupStudents extends AppCompatActivity implements DialogInterface.OnDismissListener, OTPViewInterface {
    TextInputEditText enroll,firstname,lastname, phonenumber, password,email;
    RadioGroup branch, year;
    Button submit,verify;
    public ProgressBar pb,loading;
    TextView verified;
    Boolean phoneverificationstate = false;
    ImageButton see;

    //OTP View

    OTPView dialog;
    FragmentTransaction ft;
    private  DialogInterface dialogInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Refrencing all the objects

        enroll = findViewById(R.id.signup_student_enrollment);
        firstname = findViewById(R.id.signup_student_first);
        lastname = findViewById(R.id.signup_student_last);
        branch= findViewById(R.id.signup_student_branch);
        year = findViewById(R.id.signup_student_year);
        phonenumber = findViewById(R.id.student_signup_phone);
        email= findViewById(R.id.student_signup_email);
        password = findViewById(R.id.signup_student_pass);
        submit = findViewById(R.id.signup_student_submit);
        verify = findViewById(R.id.signup_student_verify);
        pb = findViewById(R.id.signup_student_pb);
        loading = findViewById(R.id.signup_student_loading);
        verified = findViewById(R.id.signup_student_verified);
        see = findViewById(R.id.signup_student_pass_visibility);

        Map<String, String> data = new HashMap<String,String>();

        Toolbar toolbar = findViewById(R.id.student_signup_toolbar);
        toolbar.setTitle("SignUp");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Task

        if(getIntent().getIntExtra("task",0) == 1){
            enroll.setText(Global.enroll);
            firstname.setText(String.valueOf(Global.userData.get("firstname")));
            lastname.setText(String.valueOf(Global.userData.get("lastname")));
            phonenumber.setText(String.valueOf(Global.userData.get("phone")));
            password.setText(String.valueOf(Global.userData.get("password")));
            email.setText(String.valueOf(Global.userData.get("email")));



            toolbar.setTitle("Edit Profile");
        }







        //Branch

        branch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                data.put("branch", String.valueOf(group.indexOfChild(findViewById(group.getCheckedRadioButtonId()))));
            }
        });


        //Year

        year.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                data.put("year", String.valueOf(radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId()))));
            }
        });

        //Putting data in the Firestore

        loading.setVisibility(View.GONE);




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFieldEmpty()){
                    Toast.makeText(SignupStudents.this, "Please Enter all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phoneverificationstate){
                    data.put("phone",phonenumber.getText().toString());
                }
                else{
                    Toast.makeText(SignupStudents.this, "Please verify the phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Making progress bar

                loading.setVisibility(View.VISIBLE);

                //Name
                data.put("password",password.getText().toString());
                data.put("firstname",firstname.getText().toString());
                data.put("lastname", lastname.getText().toString());
                data.put("email",email.getText().toString());

                //Enrollment number

                data.put("enroll", enroll.getText().toString());

                //Entering the phone number in the Realtime Database

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("phonenumbers");
                databaseReference.child(phonenumber.getText().toString()).setValue(2);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("students").document(enroll.getText().toString()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(),"User has been registered successfully",Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        finish();


                    }
                });
            }
        });




        //Phone Verification

        if(getIntent().getIntExtra("task",0) == 1){
            //Phone number is verified

            verified.setVisibility(View.VISIBLE);
            verify.setVisibility(View.GONE);
            phoneverificationstate = true;
        }
        else{
            verified.setVisibility(View.GONE);
        }
        pb.setVisibility(View.GONE);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb.setVisibility(View.VISIBLE);

                verifyUserExistence(phonenumber.getText().toString());

            }
        });

        //Setting up see button

        see.setVisibility(View.GONE);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals("")){
                    see.setVisibility(View.VISIBLE);
                }
                else{
                    see.setVisibility(View.GONE);
                }

            }
        });

        see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(password.getInputType() == (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT))
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    see.setImageResource(R.drawable.visibility_off_icon);
                }
                else{
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    see.setImageResource(R.drawable.visibility_on_icon);
                }

            }
        });

        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals(String.valueOf(Global.userData.get("phone")))){
                    verified.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.GONE);
                    phoneverificationstate = true;
                }
                else{
                    verified.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    phoneverificationstate = false;
                }

            }
        });











    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();

    }


    private boolean isFieldEmpty() {
        if (firstname.getText().toString().equals(null)) {
            return true;
        } else if (lastname.getText().toString().equals(null)) {
            return true;
        } else if (branch.getCheckedRadioButtonId() == -1) {
            return true;
        } else if (year.getCheckedRadioButtonId() == -1) {
            return true;
        }
        else if(password.getText().toString().equals(null)){
            return true;
        }
        else if(email.getText().toString().equals(null)){
            return true;
        }
        return false;
    }


   //Phone OTP Verification

    private String verificationID;
    private String code;







    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationID = s;

            dialog = new OTPView();
            ft = getFragmentManager().beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putString("verificationID",verificationID);
            bundle.putInt("task", 2);
            dialog.setArguments(bundle);
            dialog.show(ft, "Enter the OTP");
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            Toast.makeText(SignupStudents.this, "Verification Completed", Toast.LENGTH_SHORT).show();
            phoneverificationstate = true;
            pb.setVisibility(View.GONE);
            verify.setVisibility(View.GONE);
            verified.setVisibility(View.VISIBLE);
            if(dialog!= null){
                dialog.dismiss();
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            pb.setVisibility(View.GONE);
            Toast.makeText(SignupStudents.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onDismiss(DialogInterface dialog) {
        pb.setVisibility(View.GONE);
        //Checking verificaiton
        if(phoneverificationstate){
            pb.setVisibility(View.GONE);
            verify.setVisibility(View.GONE);
            verified.setVisibility(View.VISIBLE);
        }
    }



    void verifyUserExistence(String phone){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(getApplicationContext(),"Checking Existing users",Toast.LENGTH_SHORT).show();

        db.child("phonenumbers").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(SignupStudents.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                }
                else{
                    String phonenum = "+91"+ phonenumber.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum,60, TimeUnit.SECONDS,SignupStudents.this,mCallbacks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignupStudents.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void phoneVerificationState(Boolean state) {
        phoneverificationstate = state;

        if(state){
            FirebaseAuth.getInstance().signOut();
        }
    }
}
