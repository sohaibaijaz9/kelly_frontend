package com.rhenox.kelly.Fragments;

import android.os.Bundle;
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

import com.rhenox.kelly.Message;
import com.rhenox.kelly.MessageAdapter;
import com.rhenox.kelly.R;


public class HomeFragment extends Fragment{


    private FrameLayout spinner_frame;
    private ProgressBar spinner;
    private View fragmentView;
    private EditText editText;
    private androidx.appcompat.widget.AppCompatImageButton btn_send;



    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        editText = (EditText) fragmentView.findViewById(R.id.editText);
        btn_send =  fragmentView.findViewById(R.id.btn_send);
        messageAdapter = new MessageAdapter(fragmentView.getContext());
        messagesView = (ListView) fragmentView.findViewById(R.id.messages_view);

        messagesView.setAdapter(messageAdapter);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                if (message.length() > 0) {
                    System.out.println(message);
                    Message message1 = new Message(message, "20:00", true);
                    Message message2 = new Message(message, "20:00", false);
                    messageAdapter.add(message1);
                    messageAdapter.add(message2);
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









}
