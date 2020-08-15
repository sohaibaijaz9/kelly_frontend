package com.rhenox.kelly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VerifyPhoneNumberActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private FrameLayout spinner_frame;
    private ProgressBar spinner;
    private Button btn_verify_phone;
    private EditText txt_otp_phone;
    private TextView resend_otp_phone;
    private String otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE );

        Bundle b = getIntent().getExtras();
        final String phone_number = b.getString("phone_number");
        final String token = sharedPreferences.getString("Token", "");
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);
        btn_verify_phone = findViewById(R.id.btn_verify_phone);
        txt_otp_phone = findViewById(R.id.txt_otp_phone);
        resend_otp_phone = findViewById(R.id.resend_otp_phone);



        btn_verify_phone.setOnClickListener(new View.OnClickListener(){



            @Override
            public void onClick(View view) {
                otp = txt_otp_phone.getText().toString();

                if(otp.equals("")){
                    Toast.makeText(getApplicationContext(), "Enter OTP first!", Toast.LENGTH_SHORT).show();

                }
                else{

                    try {
                        String URL = LoginActivity.baseurl+"/verify/phonenumber/";
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("otp", otp);
                        jsonBody.put("phone_number", phone_number);
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
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
//
                                } catch (JSONException e) {
                                    Log.e("VOLLEY", e.toString());

                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                spinner.setVisibility(View.GONE);
                                spinner_frame.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
                                Log.e("VOLLEY", error.toString());
                            }
                        })
                        {

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }


                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                    return null;
                                }
                            }
//                        {
//                            @Override
//                            protected Map<String,String> getParams(){
//                                Map<String,String> params = new HashMap<String, String>();
//                                params.put("otp",otp);
//                                params.put("phone_number", phone_number);
////                                params.put(KEY_EMAIL, email);
//                                return params;
//                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("x-access-token", token);

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




        resend_otp_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String URL = LoginActivity.baseurl+"/register/resend_otp/";
                    spinner.setVisibility(View.VISIBLE);
                    spinner_frame.setVisibility(View.VISIBLE);
                    JSONObject jsonBody = new JSONObject();

                    final String requestBody = jsonBody.toString();
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
                                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                    UserDetails.getUserDetails(VerifyPhoneNumberActivity.this);
                                    Intent i = new Intent(getApplicationContext(), NavActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                    System.out.println(json.getString("status"));
                                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
//
                            } catch (JSONException e) {
                                Log.e("VOLLEY", e.toString());

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            spinner.setVisibility(View.GONE);
                            spinner_frame.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
                            Log.e("VOLLEY", error.toString());
                        }
                    })
                    {

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }


                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                return null;
                            }
                        }
//                    {
//                        @Override
//                        protected Map<String,String> getParams(){
//                            Map<String,String> params = new HashMap<String, String>();
//                            return params;
//                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("x-access-token", token);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), NavActivity.class);
        startActivity(i);
    }
}
