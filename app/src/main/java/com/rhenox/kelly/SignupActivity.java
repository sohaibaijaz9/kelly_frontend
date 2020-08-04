package com.rhenox.kelly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    String phone = "";
    String email = "";
    String password = "";
    String password2 = "";
    String is_customer = "True";
    String email_or_phone = "";

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

        final Button btn_signup = findViewById(R.id.btn_signup);
        final EditText txt_email = findViewById(R.id.txt_email);
        final EditText txt_phone = findViewById(R.id.txt_phone);
        final EditText txt_password = findViewById(R.id.txt_password);
        final EditText txt_password2 = findViewById(R.id.txt_password2);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);
//       final EditText txt_login = findViewById(R.id.txt_login);
        txt_password2.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(i== EditorInfo.IME_ACTION_DONE){
                    btn_signup.performClick();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btn_signup.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                return false;
            }
        });
        txt_password2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == txt_password2.getId()){
                    txt_password2.setCursorVisible(true);
                }
            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = txt_phone.getText().toString();
                email = txt_email.getText().toString();
                password = txt_password.getText().toString();
                password2 = txt_password2.getText().toString();
//               String email_or_phone;

                if(!(email.equals("") && phone.equals(""))){
                    email_or_phone = phone;
                }
                else if(email.equals("")){
                    email_or_phone = phone;
                }
                else if(phone.equals("")){
                    email_or_phone = email;
                }

                txt_password2.setCursorVisible(false);

                if(!password.equals(password2)){
                    Toast.makeText(SignupActivity.this, "Password and Confirm Password doesn't match", Toast.LENGTH_LONG).show();
                }

                if((!phone.equals("")||!email.equals(""))&&!password.equals("")&&!password2.equals("")){
                    if(password.equals(password2))
                    {
                        try {
                            String URL = LoginActivity.baseurl+"/register/";
                            JSONObject jsonBody = new JSONObject();
                            jsonBody.put("email", email);
                            jsonBody.put("phone_number", phone);
                            jsonBody.put("password", password);
                            jsonBody.put("confirm_password", password2);
                            jsonBody.put("is_customer", is_customer);
                            final String requestBody = jsonBody.toString();
                            spinner.setVisibility(View.VISIBLE);
                            spinner_frame.setVisibility(View.VISIBLE);
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    spinner.setVisibility(View.GONE);
                                    spinner_frame.setVisibility(View.GONE);
                                    Log.i("VOLLEY", response.toString());
                                    try {

                                        JSONObject json = new JSONObject(response);
                                        if (json.getString("status").equals("200")) {
                                            String token = json.getString("token");

                                            //Shared Preferences
                                            Toast.makeText(SignupActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                            Intent myIntent = new Intent(SignupActivity.this, VerifyActivity.class);//Optional parameter
                                            Bundle b = new Bundle();
                                            b.putString("Token", token);
                                            b.putString("email_phone", email_or_phone);
                                            myIntent.putExtras(b);
                                            finish();
                                            SignupActivity.this.startActivity(myIntent);

                                        }
                                        else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                            Toast.makeText(SignupActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Log.e("VOLLEY", e.toString());

                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    spinner.setVisibility(View.GONE);
                                    spinner_frame.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this, "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
                                    Log.e("VOLLEY", error.toString());
                                }
                            }){
                                @Override
                                protected Map<String,String> getParams(){
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("email",email);
                                    params.put("phone_number", phone);
                                    params.put("password",password);
                                    params.put("confirm_password", password2);
                                    params.put("is_customer", is_customer);
                                    return params;
                                }
                            };

                            stringRequest.setRetryPolicy(new RetryPolicy() {
                                @Override
                                public int getCurrentTimeout() {
                                    return 50000;
                                }

                                @Override
                                public int getCurrentRetryCount() {
                                    return 50000;
                                }

                                @Override
                                public void retry(VolleyError error) throws VolleyError {

                                }
                            });

                            requestQueue.add(stringRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else{
                    Toast.makeText(SignupActivity.this, "Required fields empty!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
