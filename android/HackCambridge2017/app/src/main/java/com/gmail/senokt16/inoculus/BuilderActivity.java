package com.gmail.senokt16.inoculus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;


public class BuilderActivity extends AppCompatActivity {

    EditText codeText;
    RecyclerView grid;
    String code;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_builder);
        code = getIntent().getStringExtra("code");
        if (code == null || code.length() != 6)
            Log.e("BuilderActivity", "Code is invalid: " + code);

        ref = FirebaseDatabase.getInstance().getReference("/map/").child(code);

        codeText = (EditText) findViewById(R.id.code_2);
        codeText.setText(code);
/*        grid = (RecyclerView) findViewById(R.id.grid);
        grid.setLayoutManager(new GridLayoutManager(this, 64));
        grid.setAdapter(new FirebaseRecyclerAdapter<Integer, GridViewHolder>(Integer.class, -1 *//*TODO: Use the actual layout*//*, GridViewHolder.class, ref.child("grid")) {
            @Override
            protected void populateViewHolder(GridViewHolder viewHolder, Integer type, int position) {
                //TODO: Modify layout to reflect the tile type.
            }
        });*/

        //Force typing uppercase. (From: http://stackoverflow.com/questions/15961813/in-android-edittext-how-to-force-writing-uppercase)
        codeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        //Hides the notification bar
        codeText.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}
