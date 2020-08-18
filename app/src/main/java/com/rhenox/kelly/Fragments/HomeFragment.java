package com.rhenox.kelly.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.rhenox.kelly.DatabaseHandler;
import com.rhenox.kelly.LoginActivity;
import com.rhenox.kelly.Message;
import com.rhenox.kelly.MessageAdapter;
import com.rhenox.kelly.R;
import com.rhenox.kelly.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment{


    private FrameLayout spinner_frame;
    private ProgressBar spinner;
    private View fragmentView;
    private EditText editText;
    private androidx.appcompat.widget.AppCompatImageButton btn_send;
    private SharedPreferences sharedPreferences;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        editText = (EditText) fragmentView.findViewById(R.id.editText);
        btn_send =  fragmentView.findViewById(R.id.btn_send);
        messageAdapter = new MessageAdapter(fragmentView.getContext());
        messagesView = (ListView) fragmentView.findViewById(R.id.messages_view);
        messagesView.setStackFromBottom(true);
        messagesView.setAdapter(messageAdapter);

        spinner = (ProgressBar) fragmentView.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner_frame = fragmentView.findViewById(R.id.spinner_frame);
        spinner_frame.setVisibility(View.GONE);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE );
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

//        UserDetails.getUserMessages(getContext());
        DatabaseHandler db = new DatabaseHandler(getContext());


        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String token = sharedPreferences.getString("Token", "");



        if(!token.equals("")) {

            //Getting user details
            try {
                String URL = LoginActivity.baseurl + "/user/chat/history/";
                JSONObject jsonBody = new JSONObject();

                final String requestBody = jsonBody.toString();
                spinner.setVisibility(View.VISIBLE);
                spinner_frame.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("VOLLEY", response.toString());
                        try {
                            JSONObject json = new JSONObject(response);

                            if (json.getString("status").equals("200")) {

                                JSONArray chatHistory = json.getJSONArray("chat_history");
                                db.deleteAllMessages();
                                for(int i=0; i < chatHistory.length(); i++){
                                    String message = chatHistory.getJSONObject(i).getString("message");
                                    String timestamp = chatHistory.getJSONObject(i).getString("timestamp");
                                    boolean is_user_chat = chatHistory.getJSONObject(i).getBoolean("is_user_chat");

                                    messageAdapter.add(new Message(message, timestamp, is_user_chat));
                                }

                                spinner.setVisibility(View.GONE);
                                spinner_frame.setVisibility(View.GONE);

                            } else if (json.getString("status").equals("400") || json.getString("status").equals("404") || json.getString("status").equals("405")) {
                                spinner.setVisibility(View.GONE);
                                spinner_frame.setVisibility(View.GONE);
                                Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            spinner.setVisibility(View.GONE);
                            spinner_frame.setVisibility(View.GONE);
                            Log.e("VOLLEY", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinner.setVisibility(View.GONE);
                        spinner_frame.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
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
//                {
//                    @Override
//                    protected Map<String, String> getParams() {
//                        Map<String, String> params = new HashMap<String, String>();
//
//                        return params;
//                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("x-access-token", token);
                        return headers;
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
                spinner.setVisibility(View.GONE);
                spinner_frame.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Slow Internet Connection.", Toast.LENGTH_SHORT).show();
            }




        }
        else{
            Toast.makeText(getContext(), "There was problem connecting to the server", Toast.LENGTH_SHORT).show();
        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();

                if (message.length() > 0) {
                    System.out.println(message);
                    Date date = new Date();

                    messageAdapter.add(new Message(message, formatter.format(date), true));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.add(new Message("", formatter.format(date), false));
                        }
                    }, 1000);


                    String URL = LoginActivity.baseurl+"/user/chatting/";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("user_chat", message);
                        btn_send.setClickable(false);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonBody.toString();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            btn_send.setClickable(true);

                            Log.i("VOLLEY", response.toString());
                            try {

                                JSONObject json = new JSONObject(response);

                                if (json.getString("status").equals("200")) {
                                    Date date = new Date();
                                    messageAdapter.removeLast();
                                    messageAdapter.add(new Message(json.getString("bot"), formatter.format(date), false));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                    } else {
                                        //deprecated in API 26
                                        v.vibrate(500);
                                    }
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


                    editText.getText().clear();
                }
            }
        });


        //on Back Pressed
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
        });

        return fragmentView;
    }


    @Override
    public void onResume() {
//        UserDetails.getUserMessages(getContext());
//        DatabaseHandler db = new DatabaseHandler(getContext());
//
//        for(Message message: db.getAllMessages()){
//            messageAdapter.add(message);
//        }
        super.onResume();

    }
}

