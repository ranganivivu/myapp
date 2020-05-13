package com.example.uberclone.NewUberClone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.uberclone.Model.User;
import com.example.uberclone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Main4Activity extends AppCompatActivity {
    Button singin,reg,regm2;
    FirebaseAuth auth;
    LinearLayout lv;
  //  EditText name,email,pass,mobilel;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        auth=FirebaseAuth.getInstance();
        singin=findViewById(R.id.subm);
        reg=findViewById(R.id.regm);
        lv=findViewById(R.id.lv);
       // regm2=findViewById(R.id.regm2);


        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference("LoginUser");


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRegister();
            }
        });

        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vivu();
            }
        });
    }

    public void vivu(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setMessage("please use to email register");

        LayoutInflater inflater=LayoutInflater.from(this);
        View l=inflater.inflate(R.layout.login,null);

        final EditText email=l.findViewById(R.id.email);
        final EditText pass=l.findViewById(R.id.pass);


        builder.setView(l);

        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(lv, "enter email", Snackbar.LENGTH_LONG).show();
                    return;

                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    Snackbar.make(lv, "enter pass", Snackbar.LENGTH_LONG).show();
                    return;

                }

                auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Intent intent=new Intent(Main4Activity.this, WelcomeAct.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(lv,"reg Faild"+e.getMessage(),Snackbar.LENGTH_LONG).show();

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }


    private void ShowRegister() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("please use to email register");

        LayoutInflater inflater=LayoutInflater.from(this);
        View registerlayout=inflater.inflate(R.layout.newregister,null);

        final EditText name=registerlayout.findViewById(R.id.namer);
        final EditText email=registerlayout.findViewById(R.id.emailr);
        final EditText mobilel=registerlayout.findViewById(R.id.mobiler);
        final EditText pass=registerlayout.findViewById(R.id.passr);


        builder.setView(registerlayout);

        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String sname = name.getText().toString();
                        if (sname.isEmpty()) {
                            Snackbar.make(lv, "enter name", Snackbar.LENGTH_LONG).show();
                            return;

                        }
                        if (TextUtils.isEmpty(email.getText().toString())) {
                            Snackbar.make(lv, "enter email", Snackbar.LENGTH_LONG).show();
                            return;

                        }
                        if (TextUtils.isEmpty(pass.getText().toString())) {
                            Snackbar.make(lv, "enter pass", Snackbar.LENGTH_LONG).show();
                            return;

                        }
                        if (TextUtils.isEmpty(mobilel.getText().toString())) {
                            Snackbar.make(lv, "enter mobile", Snackbar.LENGTH_LONG).show();
                            return;

                        }
                        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                        DatabaseReference mDatabase;
// ...
                                        mDatabase = FirebaseDatabase.getInstance().getReference();
                                        String id = mDatabase.push().getKey().toString();
                                        User user = new User();
                                        user.setId(id);
                                        user.setName(name.getText().toString());
                                        user.setEmail(email.getText().toString());
                                        user.setPass(pass.getText().toString());
                                        user.setPhone(mobilel.getText().toString());

                                        mDatabase.child("LoginUser").child(id).setValue(user);
                                        Snackbar.make(lv,"reg suc",Snackbar.LENGTH_LONG).show();

                               /* users.child(user.getEmail())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(lv,"reg suc",Snackbar.LENGTH_LONG).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(lv,"reg Faild"+e.getMessage(),Snackbar.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(lv,"reg Faild"+e.getMessage(),Snackbar.LENGTH_LONG).show();

                    }
                });*/
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(lv,"reg Faild"+e.getMessage(),Snackbar.LENGTH_LONG).show();

                            }
                        });
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
