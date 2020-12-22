package com.rhenox.kelly;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    SharedPreferences sharedPreferences;

    public MessageAdapter(Context context) {
        this.context = context;
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }


    public void removeLast(){
        this.messages.remove(this.messages.size()-1);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        sharedPreferences = Objects.requireNonNull(context).getSharedPreferences(LoginActivity.AppPreferences, Context.MODE_PRIVATE );
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);
        String token = sharedPreferences.getString("Token", "");

        if(message.responseType().equals("OPTION")){
            convertView = messageInflater.inflate(R.layout.option_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
            holder.messageBody.setClickable(true);
            holder.messageBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RequestQueue requestQueue = Volley.newRequestQueue(context);
                    try {
                        String URL = LoginActivity.baseurl+"/bdi/chatting/";
                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("x-access-token", token);
                        jsonBody.put("question_no", message.getQuestionNo());
                        jsonBody.put("answer", holder.messageBody.getText());
                        jsonBody.put("answer_value", message.getAnswerNo());

                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.i("VOLLEY", response.toString());
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getInt("status") == 200) {
                                        messages.remove(messages.size()-1);
                                        messages.remove(messages.size()-1);
                                        messages.remove(messages.size()-1);
                                        messages.remove(messages.size()-1);
                                        messages.remove(messages.size()-1);
                                        notifyDataSetChanged();

                                    }
                                    else if (json.getString("status").equals("400")||json.getString("status").equals("404")) {
                                        System.out.println(json.getString("status"));
                                        Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
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

                                Toast.makeText(context, "Server is temporarily down, sorry for your inconvenience", Toast.LENGTH_SHORT).show();
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(message.responseType().equals("SIMPLE")){
            if (message.isBelongsToCurrentUser()) {
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getText());
            }
            else {
                convertView = messageInflater.inflate(R.layout.their_message, null);
                holder.avatar = (View) convertView.findViewById(R.id.avatar);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);

                holder.name.setText("Kelly");

                if(message.getText().equals("")) {
                    holder.messageBody.setTextColor(R.color.colorCascadeGrey);
                    holder.messageBody.setText("typing ...");
                }
                else {
                    holder.messageBody.setText(message.getText());

                }
//            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();

            }

        }

        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}