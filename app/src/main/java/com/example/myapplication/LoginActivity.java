package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button providerLoginBtn;
    private Button providerRegBtn;
    private TextView providerRegisterLink;
    private TextView providerStatus;
    private EditText providerMail;
    private EditText providerPassword;
    private ProgressDialog loadingbar;
    private ImageButton eyeToggle;


    private  FirebaseAuth mAuth;

    boolean show = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reg);
        getSupportActionBar().hide();

        getSupportActionBar().setElevation(0);

        mAuth = FirebaseAuth.getInstance();

        providerLoginBtn =  findViewById(R.id.provider_btn_login);
        providerRegBtn =  findViewById(R.id.provider_btn_register);
        providerRegisterLink = findViewById(R.id.provider_signup_link);
        eyeToggle = findViewById(R.id.password_toggle);

        loadingbar = new ProgressDialog(this);

        providerMail =  findViewById(R.id.provider_mail);
        providerPassword = findViewById(R.id.provider_password);

        providerRegBtn.setVisibility(View.INVISIBLE);
        providerRegBtn.setEnabled(false);

        eyeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show)
                {
                    show = false;
                    eyeToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                    providerPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                else
                {
                    show = true;
                    eyeToggle.setImageResource(R.drawable.ic_baseline_visibility_24);
                    providerPassword.setTransformationMethod(null);
                }
            }
        });
        providerMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    providerMail.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit_text_bg));
                    providerPassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit_text_bg));
                }
            }
        });

        providerPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    providerPassword.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit_text_bg));
                    providerMail.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit_text_bg));
                }
            }
        });
        providerMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!TextUtils.isEmpty(providerPassword.getText()) || TextUtils.isEmpty(s))
                {
                    providerLoginBtn.setEnabled(true);
                    providerLoginBtn.setClickable(true);
                    providerLoginBtn.setTextColor(getResources().getColor(R.color.black));
                    providerLoginBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_bg_active));
                }
                else
                {
                    providerLoginBtn.setEnabled(true);
                    providerLoginBtn.setClickable(true);
                    providerLoginBtn.setTextColor(getResources().getColor(R.color.browser_actions_bg_grey));
                    providerLoginBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edit_text_bg));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Link to redirect to the account registration form, for new users
        providerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                providerLoginBtn.setVisibility(View.INVISIBLE);
                providerRegisterLink.setVisibility(View.INVISIBLE);
                    //to be edited later
//                providerStatus.setText("Register Provider");

                providerRegBtn.setVisibility(View.VISIBLE);
                providerRegBtn.setEnabled(true);

            }
        });

        //Register new user when the registration button is set.
        providerRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = providerMail.getText().toString();
                String password = providerPassword.getText().toString();

                RegisterProvider(email, password);



            }
        });

        providerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = providerMail.getText().toString();
                String password = providerPassword.getText().toString();

                ProviderLogin(email, password);
            }
        });


    }

    //A method to log in  the service provider and redirect them to the map activity after successful log in.
    private void ProviderLogin(String email, String password)
    {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please fill the Email field", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please fill password field", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("AI MUSIC PLAYER Login");
            loadingbar.setMessage("Checking credentials please wait...");
            loadingbar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, " account login  is Successful", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent providerIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(providerIntent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Login Unsuccessful. Check Credentials and Try Again", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }

    }
        //The method to register new servicve provides and redirect them to the map activity if successful.
        //New users provides their email and password for both registration and log in processes.
    private void RegisterProvider(String email, String password) {

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please fill the Email field", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please fill password field", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("AI MUSIC PLAYER Registration");
            loadingbar.setMessage("Please wait while registering your account...");
            loadingbar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, " Account Registration is Successful", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent providerIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(providerIntent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Registration Unsuccessful. Check Credentials and Try Again", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }

    }
}
