package com.rhenox.kelly;


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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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

public class VerifyActivity extends AppCompatActivity {

    public static final String AppPreferences = "AppPreferences";
    SharedPreferences sharedPreferences;
    private FrameLayout spinner_frame;
    private ProgressBar spinner;

    int backpress = 0;
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
        setContentView(R.layout.activity_verify);
        Bundle b = getIntent().getExtras();
        final String token  = b.getString("Token");
        final String email_phone = b.getString("email_phone");
        System.out.println("VerifyActivity: "+token);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final Button btn_verify = findViewById(R.id.btn_verify);
        TextView resend_otp = findViewById(R.id.resend_otp);
        final EditText txt_otp = findViewById(R.id.txt_otp);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);



        sharedPreferences = getSharedPreferences(AppPreferences, Context.MODE_PRIVATE );
        txt_otp.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE || i== KeyEvent.KEYCODE_ENTER){
                    btn_verify.performClick();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btn_verify.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }

                return false;
            }
        });


        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String URL = LoginActivity.baseurl+"/register/resend_otp/";
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("authorization", token);
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
                                    System.out.println(json.getString("status"));
                                    Toast.makeText(VerifyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                    System.out.println(json.getString("status"));
                                    Toast.makeText(VerifyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
//                                    else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
//                                        Toast.makeText(VerifyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//                                    }
                            } catch (JSONException e) {
                                Log.e("VOLLEY", e.toString());

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            spinner.setVisibility(View.GONE);
                            spinner_frame.setVisibility(View.GONE);
                            Toast.makeText(VerifyActivity.this, "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
                            Log.e("VOLLEY", error.toString());
                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("authorization", token);
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
        });




        txt_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == txt_otp.getId())
                {
                    txt_otp.setCursorVisible(true);
                }
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String otp = txt_otp.getText().toString();

                txt_otp.setCursorVisible(false);

                if (!otp.equals("")){
                    try {
                        String URL = LoginActivity.baseurl+"/is_verified/";
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("otp", otp);
                        jsonBody.put("email_or_phone", email_phone);

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

                                        Toast.makeText(VerifyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor edit = sharedPreferences.edit();
                                        edit.putString("Token", token);
                                        edit.apply();
                                        UserDetails.getUserDetails(VerifyActivity.this);
                                        Intent myIntent = new Intent(VerifyActivity.this, LoginActivity.class);//Optional parameters
                                        finish();
                                        VerifyActivity.this.startActivity(myIntent);
                                    }
                                    else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                        Toast.makeText(VerifyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
//                                    else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
//                                        Toast.makeText(VerifyActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//                                    }
                                } catch (JSONException e) {
                                    Log.e("VOLLEY", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                spinner.setVisibility(View.GONE);
                                spinner_frame.setVisibility(View.GONE);
                                Toast.makeText(VerifyActivity.this, "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
                                Log.e("VOLLEY", error.toString());
                                System.out.println(error.toString());
                            }
                        }){
                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("otp", otp);
                                params.put("email_or_phone", email_phone);

//                                params.put(KEY_EMAIL, email);
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("authorization", token);

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
        });
    }


}
