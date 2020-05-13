package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {
    private EditText e1,e2;
    Button btnGenerateOTP, btnSignIn;
    String phoneNumber, otp;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    String no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        btnGenerateOTP=findViewById(R.id.b2);
        btnSignIn=findViewById(R.id.b1);


        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                no = e1.getText().toString();
                Intent intent = new Intent(Otp.this,HomePage.class);
                intent.putExtra("mobile",no);
                startActivity(intent);
                Toast.makeText(Otp.this,no,Toast.LENGTH_LONG).show();
            }
        });


    }

}
