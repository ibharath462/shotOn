package com.example.bharath_5493.shoton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    ImageView img;
    RadioGroup rg;
    EditText sText;
    RelativeLayout n;
    int wb = 0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.pg.shoton", Context.MODE_PRIVATE);

        if(prefs.getBoolean("firstrun", true)){
            prefs.edit().putString("theme", "white").commit();
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        rg = (RadioGroup)findViewById(R.id.theme);

        final Button fab = (Button) findViewById(R.id.startService);
        final EditText sOnId = (EditText)findViewById(R.id.sText);
        sText = (EditText)findViewById(R.id.sText);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this,cs.class));
                fab.setBackground(getResources().getDrawable(R.drawable.rounded_buttonstop));
                fab.setText("STOP");
            }
        });

        final Spinner rear = (Spinner) findViewById(R.id.rear);
        final Spinner front = (Spinner) findViewById(R.id.front);
        setActionBar("#shotOn");

        String[] textArray = { "Camera Adjust", "Cake", "Altered camera", "Selfie","Camera 1","Focus","HDR","Grain","Nature & People","Nature","Camera 2","Portrait","Faces","Tonality","Whatshot" };
        final Integer[] white = { R.drawable.badjust1, R.drawable.cake1, R.drawable.cameraalt1, R.drawable.selfie1,R.drawable.bcamera1, R.drawable.cfocus1, R.drawable.hdr1, R.drawable.grain1,
                                R.drawable.naturepeople1, R.drawable.nature1, R.drawable.pcamera1, R.drawable.portrait1,R.drawable.tagfaces1, R.drawable.tonality1, R.drawable.whatshot1};
        final Integer[] black = { R.drawable.badjust2, R.drawable.cake2, R.drawable.cameraalt2, R.drawable.selfie,R.drawable.bcamera2, R.drawable.cfocus2, R.drawable.hdr2, R.drawable.grain2,
                R.drawable.naturepeople2, R.drawable.nature2, R.drawable.pcamera2, R.drawable.portrait,R.drawable.tagfaces2, R.drawable.tonality2, R.drawable.whatshot2};

        final SpinnerAdapter whiteAdapter = new SpinnerAdapter(this, R.layout.spinner_value_layout, textArray, white);
        final SpinnerAdapter blackAdapter = new SpinnerAdapter(this, R.layout.spinner_value_layout, textArray, black);
        rear.setAdapter(whiteAdapter);
        front.setAdapter(whiteAdapter);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if(checkedId == R.id.white && isChecked){
                    prefs.edit().putString("theme", "white").commit();
                    wb= 0;
                    rear.setAdapter(whiteAdapter);
                    front.setAdapter(whiteAdapter);
                    whiteAdapter.notifyDataSetChanged();

                }else{
                    prefs.edit().putString("theme", "black").commit();
                    wb= 1;
                    rear.setAdapter(blackAdapter);
                    front.setAdapter(blackAdapter);
                    blackAdapter.notifyDataSetChanged();
                }
            }
        });


        rear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Integer selectedIcon = white[position];
                if(wb == 1){
                    selectedIcon = black[position];
                }
                prefs.edit().putString("rear", ""+selectedIcon).commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        front.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Integer selectedIcon = white[position];
                if(wb == 1){
                    selectedIcon = black[position];
                }
                prefs.edit().putString("rear", ""+selectedIcon).commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                prefs.edit().putString("sText", ""+sText.getText().toString()).commit();

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setActionBar(String heading) {
        // TODO Auto-generated method stub

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(heading);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00695C")));
        actionBar.show();

    }
}
