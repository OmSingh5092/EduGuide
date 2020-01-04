package com.example.eduguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignupAdmin extends AppCompatActivity implements DialogInterface.OnDismissListener, OTPViewInterface{
    TextInputEditText enroll,firstname, lastname,phonenumber,password,email;
    
    Button submit,verify;
    RadioGroup branch;
    TextView verified;
    ProgressBar verifying,loading;
    ImageButton see;
    
    Boolean phoneverified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_admin);

        //Setting up actionbar

        Toolbar toolbar = findViewById(R.id.admin_signup_toolbar);
        toolbar.setTitle("Admin Singup");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Refrencing

        enroll = findViewById(R.id.signup_admin_enroll);
        submit = findViewById(R.id.signup_admin_submit);
        firstname = findViewById(R.id.signup_admin_firstname);
        lastname = findViewById(R.id.signup_admin_lastname);
        phonenumber = findViewById(R.id.signup_admin_phone);
        password = findViewById(R.id.signup_admin_password);
        email = findViewById(R.id.admin_signup_email);
        verify = findViewById(R.id.signup_admin_verify);
        branch = findViewById(R.id.signup_admin_branch);
        verified = findViewById(R.id.signup_admin_verified);
        verifying = findViewById(R.id.signup_admin_verifying);
        loading = findViewById(R.id.signup_admin_loading);
        see = findViewById(R.id.signup_admin_pass_visibility);


        Map<String,String> data = new HashMap<>();
        
        
        branch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                data.put("branch",String.valueOf(group.indexOfChild(findViewById(group.getCheckedRadioButtonId()))));
            }
        });

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



        loading.setVisibility(View.GONE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFieldEmpty()){
                    Toast.makeText(SignupAdmin.this, "Please Enter all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phoneverified){
                    data.put("phone",phonenumber.getText().toString());
                }
                else{
                    Toast.makeText(SignupAdmin.this, "Please verify the phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Making progress bar

                loading.setVisibility(View.VISIBLE);

                //Name
                data.put("password",password.getText().toString());
                data.put("firstname",firstname.getText().toString());
                data.put("lastname", lastname.getText().toString());
                data.put("email",email.getText().toString());

                //Enrollment Number

                data.put("enroll",enroll.getText().toString());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("phonenumbers");
                databaseReference.child(phonenumber.getText().toString()).setValue(1);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("admins").document(enroll.getText().toString()).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            phoneverified = true;
        }
        else{
            verified.setVisibility(View.GONE);
        }

        verifying.setVisibility(View.GONE);
        

        
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifying.setVisibility(View.VISIBLE);

                verifyUserExistence(phonenumber.getText().toString());
                
            }
        });



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
                    phoneverified = true;
                }
                else{
                    verified.setVisibility(View.GONE);
                    verifying.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    phoneverified = false;
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
    }



    private boolean isFieldEmpty() {
        if (firstname.getText().toString().equals(null)) {
            return true;
        } else if (lastname.getText().toString().equals(null)) {
            return true;
        } else if (branch.getCheckedRadioButtonId() == -1) {
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
    
    
    String verificationID;
    
    OTPView dialog;
    FragmentTransaction ft ;


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

            Toast.makeText(SignupAdmin.this, "Verification Completed", Toast.LENGTH_SHORT).show();
            phoneverified = true;
            verifying.setVisibility(View.GONE);
            verify.setVisibility(View.GONE);
            verified.setVisibility(View.VISIBLE);
            if(dialog!= null){
                dialog.dismiss();
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            verifying.setVisibility(View.GONE);
            Toast.makeText(SignupAdmin.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    void verifyUserExistence(String phone){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(getApplicationContext(),"Checking Existing users",Toast.LENGTH_SHORT).show();

        db.child("phonenumbers").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(SignupAdmin.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                    verifying.setVisibility(View.GONE);
                }
                else{
                    String phonenum = "+91"+ phonenumber.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenum,60, TimeUnit.SECONDS,SignupAdmin.this,mCallbacks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignupAdmin.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        verifying.setVisibility(View.GONE);
        //Checking verificaiton
        if(phoneverified){
            verifying.setVisibility(View.GONE);
            verify.setVisibility(View.GONE);
            verified.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void phoneVerificationState(Boolean state) {
        phoneverified = state;
    }
}
