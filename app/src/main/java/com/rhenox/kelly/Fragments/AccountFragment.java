package com.rhenox.kelly.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ramijemli.percentagechartview.PercentageChartView;
import com.ramijemli.percentagechartview.callback.ProgressTextFormatter;
import com.rhenox.kelly.LoginActivity;
import com.rhenox.kelly.R;
import com.rhenox.kelly.ChangePasswordActivity;
import com.rhenox.kelly.UpdateNameActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountFragment extends Fragment {

    private View fragmentView;
    private SharedPreferences sharedPreferences;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_email;
    private Button btn_update_name;

    private TextView txt_change_password;
    private TextView txt_delete_account;

    private PercentageChartView progressChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_account, container, false);
        sharedPreferences= Objects.requireNonNull(this.getActivity()).getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE);
        tv_name = fragmentView.findViewById(R.id.tv_name);
        tv_phone = fragmentView.findViewById(R.id.tv_phone);
        tv_email = fragmentView.findViewById(R.id.tv_email);
        btn_update_name = fragmentView.findViewById(R.id.btn_update_name);
        txt_change_password = fragmentView.findViewById(R.id.txt_change_password);
        txt_delete_account = fragmentView.findViewById(R.id.txt_delete_account);

        progressChart = fragmentView.findViewById(R.id.progress_wellness);

        refreshUserDetails();

        fragmentView.setFocusableInTouchMode(true);
        fragmentView.requestFocus();
        fragmentView.setOnKeyListener( new View.OnKeyListener()
        {
            int backpress = 0;
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {

                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    backpress = (backpress + 1);
                    Toast.makeText(getContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

                    if (backpress > 2) {
                        getActivity().finish();
                        System.exit(0);
                    }
                    return true;
                }
                return false;
            }
        } );


        txt_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        txt_delete_account.setOnClickListener(new View.OnClickListener() {

            String token = sharedPreferences.getString("Token", "");
            @Override
            public void onClick(View view) {
                String URL = LoginActivity.baseurl+"/register/";
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();


                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("VOLLEY", response.toString());
                        try {

                            JSONObject json = new JSONObject(response);

                            if (json.getString("status").equals("200")) {
                                String token = json.getString("token");

                                //Shared Preferences
                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getActivity(), LoginActivity.class);//Optional parameter
                                Bundle b = new Bundle();
                                b.putString("Token", token);
                                myIntent.putExtras(b);
                                getActivity().finish();
                                startActivity(myIntent);

                            }
                            else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("VOLLEY", e.toString());

                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(), "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
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

            }
        });

        btn_update_name.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity().getApplicationContext(), UpdateNameActivity.class);
                startActivity(i);
                getActivity().finish();

            }
        });


        return fragmentView;
    }


    private void refreshUserDetails(){

        String token = sharedPreferences.getString("Token", "");
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email","");
        String phone_number = sharedPreferences.getString("phone_number", "");

        if (!token.equals("") ){

            if(name.equals("")){
                tv_name.setText("No registered name");
            }
            else {

                tv_name.setText(name);
            }
            if(email.equals(""))
            {
                tv_email.setText("No registered email");
            }
            else{
                tv_email.setText(email);
            }

            if(phone_number.equals(""))
            {
                tv_phone.setText("No registered phone number");
            }
            else{
                tv_phone.setText(phone_number);
            }

        }
    }
}
