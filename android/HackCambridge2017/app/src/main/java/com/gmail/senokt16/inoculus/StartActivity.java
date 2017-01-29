package com.gmail.senokt16.inoculus;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class StartActivity extends AppCompatActivity {

    EditText codeText;
    ProgressBar loading;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/session/");
    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        connectWebSocket();

        codeText = (EditText) findViewById(R.id.code);
        //Force typing uppercase. (From: http://stackoverflow.com/questions/15961813/in-android-edittext-how-to-force-writing-uppercase)
        codeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        loading = (ProgressBar) findViewById(R.id.loading);
        setLoading(false);

        //Hides the notification bar
        codeText.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String text = editable.toString();
                if (text.length() == 6) {
                    setLoading(true);
                    ref.child(text).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                setLoading(false);
                                Intent i = new Intent(StartActivity.this, BuilderActivity.class);
                                i.putExtra("code", text);
                                //TODO: Do transition animation for the code.
                                startActivity(i);
                            } else {
                                setLoading(false);
                                setMessageAndClear("TRY AGAIN");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            setLoading(false);
                            setMessageAndClear("NO CONNECTION");
                        }
                    });
                }
            }
        });
    }

    private void setLoading(boolean b) {
        if (b) {
            loading.setVisibility(View.VISIBLE);
            codeText.setEnabled(false);
        } else {
            loading.setVisibility(View.INVISIBLE);
            codeText.setEnabled(true);
        }
    }

    private void setMessageAndClear(String msg) {
        codeText.setText("");
        codeText.setHint(msg);
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://websockethost:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.messages);
                        textView.setText(textView.getText() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
