package com.rhenox.kelly;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import java.util.Objects;

public class ConsentActivity extends AppCompatActivity {

    private Button agreeButton;
    private Button disagreeButton;


    private FrameLayout spinner_frame;
    private ProgressBar spinner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        getSupportActionBar().hide();

        String name  = getIntent().getStringExtra("name");
        String phone = getIntent().getStringExtra("phone");
        String password = getIntent().getStringExtra("password");

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);


        agreeButton = (Button)findViewById(R.id.agree_btn);
        disagreeButton =(Button) findViewById(R.id.disagree_btn);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsentActivity.this, SignupActivity.class);
                finish();
                startActivity(intent);
            }
        });


        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String URL = LoginActivity.baseurl+"/register/";
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("name", name);
                    jsonBody.put("email", "");
                    jsonBody.put("phone_number", phone);
                    jsonBody.put("password", password);

                    final String requestBody = jsonBody.toString();
                    System.out.println(requestBody);
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
                                    Toast.makeText(ConsentActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(ConsentActivity.this, VerifyActivity.class);//Optional parameter
                                    Bundle b = new Bundle();
                                    b.putString("Token", token);
                                    myIntent.putExtras(b);
                                    finish();
                                    ConsentActivity.this.startActivity(myIntent);

                                }
                                else if (json.getString("status").equals("400")||json.getString("status").equals("404") ||
                                        json.getString("status").equals("401")) {
                                    Toast.makeText(ConsentActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ConsentActivity.this, "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
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
    }
}
