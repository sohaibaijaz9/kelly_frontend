
package com.rhenox.kelly.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rhenox.kelly.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.rhenox.kelly.LoginActivity.AppPreferences;

public class UpdateFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.update_account_setting_preference);
        final PreferenceScreen preferenceScreen = this.getPreferenceScreen();
//        User user =User.getInstance();
        sharedPreferences = this.getActivity().getSharedPreferences(AppPreferences, Context.MODE_PRIVATE);
//        sharedPreferences = this.getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");
        String first_name = sharedPreferences.getString("name", "");
//        String last_name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        if(email.equals("")){
            email = "kelly@rhenox-innovation.com";
        }
        String phone_number = sharedPreferences.getString("phone_number", "");
//        firstName = user.getFirstName();
//        lastName = user.getLastName();
//        phoneNumber = user.getPhoneNumber();
//        email = user.getEmail();

        try{
            final EditTextPreference editTextFirstName = findPreference("firstName");
            final Preference preferenceEmail = findPreference("email");
            final Preference preferencePhoneNumber = findPreference("phoneNumber");
            final Preference preferencePassword = findPreference("password");
            final Preference preference_profile = findPreference("userDetails");

            preference_profile.setTitle(first_name);
            preference_profile.setSummary(email + "\n" + phone_number);

            editTextFirstName.setSummary(first_name);
            editTextFirstName.setText(first_name);

//            editTextLastName.setSummary(last_name);
//            editTextLastName.setText(last_name);

            preferenceEmail.setSummary(email);
            preferenceEmail.setIconSpaceReserved(false);

            preferencePhoneNumber.setSummary(phone_number);

            editTextFirstName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    String firstNameEditValue = newValue.toString();
//                    name_update(firstNameEditValue, "");
                    return false;

                }
            });


            preferencePhoneNumber.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

//                    editTextPhoneNumber.setIntent(new Intent(getContext(), Login.class));
//                    Intent i = new Intent(getActivity(), Verifypassword.class);
//                    i.putExtra("coming_from", "phone_number_preference");
//                    UpdateFragment.this.startActivity(i);
                    return true;
                }
            });

            preferenceEmail.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

//                    Intent i = new Intent(getActivity(), ChangeEmailActivity.class);
//                    UpdateFragment.this.startActivity(i);

                    return true;
                }
            });

            preferencePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
//                    Intent i = new Intent(getActivity(), Verifypassword.class);
//                    i.putExtra("coming_from", "password_preference");
//                    UpdateFragment.this.startActivity(i);

                    return true;
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }


   // private boolean flag = true;
//    public void name_update(final String firstNameEditValue, final String lastNameEditValue) {
//
//        String url = MainActivity.baseurl+"/update/name/";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject json = new JSONObject(response);
//                            if (json.getString("status").equals("200")) {
//
////                                SharedPreferences.Editor editor = sharedPreferences.edit();
////
////                                editor.putString("first_name", json.getString("first_name"));
////                                editor.putString("last_name", json.getString("last_name"));
////                                editor.apply();
//                                User user = User.getInstance();
//                                user.setFirstName(json.getString("first_name"));
//                                user.setLastName(json.getString("last_name"));
//                                if(lastNameEditValue.equals("")){
//                                    EditTextPreference editTextFirstName = findPreference("firstName");
//                                    editTextFirstName.setSummary(firstNameEditValue);
//                                    editTextFirstName.setText(firstNameEditValue);
//                                }
//                                if(firstNameEditValue.equals("")){
//                                    EditTextPreference editTextLastName = findPreference("lastName");
//                                    editTextLastName.setSummary(lastNameEditValue);
//                                    editTextLastName.setText(lastNameEditValue);
//                                }
//                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
//                            //  check1 =1;
//                               // flag = true;
//
//                            }
//                            else if(json.getString("status").equals("400")){
//                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
//
//                               // flag = false;
//                            }
//                            else if(json.getString("status").equals("404")){
//                                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
//                                SettingsFragment.forcedLogout(getActivity());
//                                // flag = false;
//                            }
//
//
//                        }
//                        catch (JSONException e) {
//                          //  flag = false;
//                            e.printStackTrace();
//                        }
//                    }},
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
//                      //  flag = false;
//                    }
//                })
//
//        {
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<>();
//                params.put("first_name", firstNameEditValue);
//                params.put("last_name", lastNameEditValue);
//
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", sharedPreferences.getString("Token", ""));
//                return headers;
//            }
//
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
//        requestQueue.add(stringRequest);
//        //return check1;
//    }

//    private boolean flag = true;
//    public boolean name_update(final String firstNameEditValue, final String lastNameEditValue) {
//
//        String url = "http://52.15.104.184/update/name/";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject json = new JSONObject(response);
//                    if (json.getString("status").equals("200")) {
//
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                        editor.putString("first_name", json.getString("first_name"));
//                        editor.putString("last_name", json.getString("last_name"));
//                        editor.apply();
//
//                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
//                        flag = true;
//
//                    }
//                    else if(json.getString("status").equals("404") || json.getString("status").equals("400")){
//                        Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_LONG).show();
//                        flag = false;
//                    }
//
//                }
//                catch (JSONException e) {
//                    flag = false;
//                    e.printStackTrace();
//                }
//            }},
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
//                    flag = false;
//                }
//            })
//
//            {
//                @Override
//                protected Map<String,String> getParams(){
//                    Map<String,String> params = new HashMap<>();
//                    params.put("first_name", firstNameEditValue);
//                    params.put("last_name", lastNameEditValue);
//
//                    return params;
//                }
//
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Authorization", sharedPreferences.getString("Token", ""));
//                    return headers;
//                }
//
//            };
//
//            RequestQueueSingleton requestQueue = Volley.newRequestQueue(this.getActivity());
//            requestQueue.add(stringRequest);
//            return flag;
//    }

}
