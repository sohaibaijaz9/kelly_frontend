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
import android.view.animation.Animation;
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
import com.rhenox.kelly.UserDetails;
import com.rhenox.kelly.VerifyActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountFragment extends Fragment {

    private View fragmentView;
    private SharedPreferences sharedPreferences;
    private TextView textView;
    private TextView resultText;

    private PercentageChartView progressChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_account, container, false);
        sharedPreferences= Objects.requireNonNull(this.getActivity()).getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE);

        textView = (TextView) fragmentView.findViewById(R.id.textView);
        resultText = (TextView) fragmentView.findViewById(R.id.resulttext);
        resultText.setText("");
        textView.setText("In Process");

        progressChart = fragmentView.findViewById(R.id.progress_wellness);
        progressChart.setProgress(0, true);

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





        return fragmentView;
    }


    private void refreshUserDetails(){
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String token = sharedPreferences.getString("Token", "");

        String URL = LoginActivity.baseurl+"/bdi/depression/check/";
        JSONObject jsonBody = new JSONObject();

        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("VOLLEY", response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("status").equals("200")) {
                        if(json.getInt("score")  > 0){
                            textView.setText("Depression");
                            int score = json.getInt("score");
                            progressChart.setProgress(score * 100/63, true);

                            if(score >=1 && score <= 10){
                                resultText.setText("These ups and downs are considered normal");
                            }else if(score >=11 && score <= 16){
                                resultText.setText(" Mild mood disturbance");
                            }else if(score >=17 && score <= 20){
                                resultText.setText("Borderline clinical depression");
                            }else if(score >=21 && score <= 30){
                                resultText.setText("Moderate depression ");
                            }else if(score >=31 && score <= 40){
                                resultText.setText("Severe depression ");
                            }else if(score > 40){
                                resultText.setText("Extreme depression ");
                            }

                        }else{
                            textView.setText("In Process");
                            resultText.setText("");
                        }


                    }
                    else if (json.getString("status").equals("401")||json.getString("status").equals("400")||json.getString("status").equals("404")) {
                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getActivity(), "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
                Log.e("VOLLEY", error.toString());
                System.out.println(error.toString());
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
//                                params.put("otp", otp);
//                                params.put("email_or_phone", email_phone);
//
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


    }


}
