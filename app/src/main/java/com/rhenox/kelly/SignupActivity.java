package com.rhenox.kelly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    String name="";
    String phone = "";
    String email = "";
    String password = "";
    String password2 = "";
    String email_or_phone = "";

    private Button btn_signup;
    private EditText txt_name;
    private EditText txt_phone;
    private EditText txt_password;

    private TextInputLayout txt_phone_layout;
    private TextInputLayout txt_password_layout;

    private FrameLayout spinner_frame;
    private ProgressBar spinner;
    SharedPreferences sharedPreferences;
    private int backpress = 0;
    @Override
    public void onBackPressed(){
        backpress = (backpress + 1);
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

        if (backpress>1) {
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        sharedPreferences = getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE );

        TextView txt_login = findViewById(R.id.txt_login);
        txt_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(i);

            }
        });

        btn_signup = findViewById(R.id.btn_signup);
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_password = findViewById(R.id.txt_password);

        txt_phone_layout = findViewById(R.id.txt_phone_layout);
        txt_password_layout = findViewById(R.id.txt_password_layout);


        txt_name.addTextChangedListener(txtNameWatcher);
        txt_phone.addTextChangedListener(txtPhoneWatcher);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);


      btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = txt_name.getText().toString();
                phone = txt_phone.getText().toString();

                password = txt_password.getText().toString();



                if(!name.equals("")&&(!phone.equals("")||!password.equals(""))){

                    Intent intent = new Intent(SignupActivity.this, ConsentActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    intent.putExtra("password", password);
                    finish();
                    startActivity(intent);

                }
                else{
                    Toast.makeText(SignupActivity.this, "Required fields empty!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    TextWatcher txtNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            txt_phone_layout.setVisibility(View.VISIBLE);
        }

    };

    TextWatcher txtPhoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            txt_password_layout.setVisibility(View.VISIBLE);
        }

    };




}
