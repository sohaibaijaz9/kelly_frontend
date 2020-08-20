package com.rhenox.kelly.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rhenox.kelly.LoginActivity;
import com.rhenox.kelly.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.rhenox.kelly.LoginActivity.AppPreferences;

public class ChangeEmailActivity extends AppCompatActivity {

    ImageView button_updatemail;
    EditText text_email;
    SharedPreferences sharedPreferences;
    TextView textViewerror;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        getSupportActionBar().setTitle("Email Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = ChangeEmailActivity.this.getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferences = getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", "");

        textViewerror=findViewById(R.id.error_message_email);
        button_updatemail=findViewById(R.id.changeEmail);
        text_email= findViewById(R.id.update_email);
        textViewerror.setVisibility(View.GONE);
        text_email.setText(email);

        button_updatemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_email.setCursorVisible(false);
                update_email();
            }
        });
        text_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_email.setCursorVisible(true);
                textViewerror.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  void update_email(){

        final String new_email = text_email.getText().toString();
        if(new_email.equals(""))
        {
            textViewerror.setText("Field cannot be empty");
            textViewerror.setVisibility(View.VISIBLE);
            text_email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    text_email.setCursorVisible(true);
                    textViewerror.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //  Toast.makeText(Verifypassword.this, "Field cannot be empty", Toast.LENGTH_LONG).show();
        }
        else {

            try {
                final SharedPreferences sharedPreferences = getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);
                final String token = sharedPreferences.getString("Token", "");
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", new_email);
                final String requestBody = jsonBody.toString();

                String url = LoginActivity.baseurl+"/update/email/";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getString("status").equals("200")) {

                                        // SharedPreferences.Editor editor = sharedPreferences.edit();
                                        //editor.putString("phone_number", new_phonenumber);
                                        //editor.apply();

                                        Toast.makeText(ChangeEmailActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                        SharedPreferences.Editor edit = sharedPreferences.edit();
                                        edit.putString("email", json.getString("email"));
                                        edit.apply();
//                                        Intent i = new Intent(ChangePhoneNumberActivity.this, VerifyPhoneNoActivity.class);
//                                        i.putExtra("change_number", new_phonenumber );
//                                        startActivity(i);

                                    } else if (json.getString("status").equals("400")) {

                                        textViewerror.setText(json.getString("message"));
                                        textViewerror.setVisibility(View.VISIBLE);
                                        Toast.makeText(ChangeEmailActivity.this, new_email, Toast.LENGTH_LONG).show();

                                        text_email.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                text_email.setCursorVisible(true);
                                                textViewerror.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                            }
                                        });
                                        // Toast.makeText(Verifypassword.this, json.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                    else if(json.getString("status").equals("404")){
                                        Toast.makeText(ChangeEmailActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
//                                        SettingsFragment.forcedLogout(ChangeEmailActivity.this);
                                        // flag = false;
                                    }
                                    else{
                                        textViewerror.setText(json.getString("message"));
                                        textViewerror.setVisibility(View.VISIBLE);
                                        Toast.makeText(ChangeEmailActivity.this, "ya ye hai", Toast.LENGTH_LONG).show();

                                        text_email.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                text_email.setCursorVisible(true);
                                                textViewerror.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ChangeEmailActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
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

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("x-access-token", token);
                        return params;
                    }

                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
