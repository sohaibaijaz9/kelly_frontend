package com.rhenox.kelly;

import android.content.Context;
import android.content.Intent;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordVerify extends AppCompatActivity {


    private String email_or_phone;
    private String token_uuid;

    private FrameLayout spinner_frame;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_verify);
        getSupportActionBar().hide();

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);
        Bundle extras = getIntent().getExtras();
        email_or_phone = extras.getString("email_or_phone");
        token_uuid = extras.getString("token_uuid");

//        Toast.makeText(getApplicationContext(), email_or_phone + token_uuid, Toast.LENGTH_SHORT).show();

        final Button next_btn_fp = findViewById(R.id.next_btn_fp);
        final EditText otp_fp_txt = findViewById(R.id.otp_fp_txt);
        TextView resend_otp_txt = findViewById(R.id.resend_otp_txt);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);



        otp_fp_txt.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    next_btn_fp.performClick();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(next_btn_fp.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }
                return false;
            }
        });

        next_btn_fp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String otp = otp_fp_txt.getText().toString();
                if (otp.equals("")){
                    Toast.makeText(getApplicationContext(), "Enter OTP first", Toast.LENGTH_SHORT).show();
                }
                else if(!otp.equals("")){
                    try{
                        String URL = LoginActivity.baseurl+"/confirm/password/reset/?password_uuid="+ URLEncoder.encode(token_uuid, "UTF-8");
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("otp", otp);
                        jsonBody.put("email_or_phone", email_or_phone);

                        final String requestBody = jsonBody.toString();
                        spinner.setVisibility(View.VISIBLE);
                        spinner_frame.setVisibility(View.VISIBLE);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                spinner.setVisibility(View.GONE);
                                spinner_frame.setVisibility(View.GONE);
                                try{

                                    JSONObject json = new JSONObject(response);
                                    final String status = json.getString("status");
                                    if(json.getString("status").equals("200")){

                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                        Intent myIntent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                                        myIntent.putExtra("token_uuid", token_uuid);
                                        myIntent.putExtra("email_or_phone", email_or_phone);
                                        finish();
                                        startActivity(myIntent);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (Exception e){
                                    e.getStackTrace();
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("VOLLEY", error.toString());
                                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
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

                        };
//                        {
//                            @Override
//                            protected Map<String,String> getParams(){
//                                Map<String,String> params = new HashMap<String, String>();
//                                params.put("otp", otp);
//                                params.put("email_or_phone",email_or_phone);
//                                return params;
//                            }
//                        };

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
                    }
                    catch (UnsupportedEncodingException | JSONException e) {
                        spinner.setVisibility(View.GONE);
                        spinner_frame.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter OTP first", Toast.LENGTH_SHORT).show();
                }
            }
        });




        resend_otp_txt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                try{
                    String URL = LoginActivity.baseurl+"/password/reset/resend_otp/?password_uuid="+ URLEncoder.encode(token_uuid, "UTF-8");
                    JSONObject jsonBody = new JSONObject();
                    spinner.setVisibility(View.VISIBLE);
                    spinner_frame.setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            spinner.setVisibility(View.GONE);
                            spinner_frame.setVisibility(View.GONE);
                            try{

                                JSONObject json = new JSONObject(response);
                                final String status = json.getString("status");
                                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

                                if(json.getString("status").equals("200")){

                                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e){
                                e.getStackTrace();
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("VOLLEY", error.toString());
                            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    })
                    {
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("email_or_phone",email_or_phone);
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
                }
                catch (Exception e) {
                    spinner.setVisibility(View.GONE);
                    spinner_frame.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        });
    }
}
