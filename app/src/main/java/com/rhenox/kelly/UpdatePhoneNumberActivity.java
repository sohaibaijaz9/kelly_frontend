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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class UpdatePhoneNumberActivity extends AppCompatActivity {


    private EditText txt_phone_number;
    private EditText txt_confirm_phone_number;
    private Button phone_update_btn;
    SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;
    private FrameLayout spinner_frame;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_number);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE );
        final String token = sharedPreferences.getString("Token", "");
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);

        txt_phone_number =(EditText) findViewById(R.id.txt_phone_number);
        txt_confirm_phone_number = (EditText)findViewById(R.id.txt_confirm_phone_number);
        phone_update_btn = (Button)findViewById(R.id.phone_update_btn);


        txt_confirm_phone_number.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE || i== KeyEvent.KEYCODE_ENTER){
                    phone_update_btn.performClick();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(phone_update_btn.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return true;
                }

                return false;
            }
        });


        phone_update_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                final String phone_number = txt_phone_number.getText().toString();
                final String confirm_phone_number = txt_confirm_phone_number.getText().toString();

                if(!phone_number.equals(confirm_phone_number)){
                    Toast.makeText(getApplicationContext(), "Both fields doesn't match",Toast.LENGTH_SHORT).show();
                }
                else {

                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    try {
                        String URL = LoginActivity.baseurl + "/change/phonenumber/";
                        spinner.setVisibility(View.VISIBLE);
                        spinner_frame.setVisibility(View.VISIBLE);

                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("phone_number", phone_number);
                        
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
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                        finish();
                                        Intent i = new Intent(getApplicationContext(), VerifyPhoneNumberActivity.class);
                                        i.putExtra("phone_number", phone_number);
                                        startActivity(i);
                                    } else if (json.getString("status").equals("400") || json.getString("status").equals("404")) {
                                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
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
//                            protected Map<String, String> getParams() {
//                                Map<String, String> params = new HashMap<String, String>();
//                                params.put("phonenumber", phone_number);
//                                return params;
//                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
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
