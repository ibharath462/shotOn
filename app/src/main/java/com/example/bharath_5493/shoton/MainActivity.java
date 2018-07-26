package com.example.bharath_5493.shoton;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    ImageView img;
    RadioGroup rg;
    EditText sText;
    RelativeLayout n;
    int wb = 0;
    Button startService,preview;
    Spinner rear,front;
    String[] iconText;
    boolean isPreviewButtonLongPressed = false;
    Integer[] white;
    Integer[] black;
    SpinnerAdapter whiteAdapter,blackAdapter;
    SharedPreferences prefs;
    int exitCount = 0;
    LinearLayout mainLL;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getApplicationContext().getSharedPreferences("com.pg.shoton", Context.MODE_PRIVATE);

        if(prefs.getBoolean("firstrun", true)){
            prefs.edit().putString("theme", "white").commit();
            prefs.edit().putBoolean("firstrun", false).commit();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        rg = (RadioGroup)findViewById(R.id.theme);

        startService = (Button) findViewById(R.id.startService);
        preview = (Button) findViewById(R.id.preview);
        sText = (EditText)findViewById(R.id.sText);
        mainLL = (LinearLayout) findViewById(R.id.mainLL);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Long press for preview",Toast.LENGTH_SHORT).show();
            }
        });

        preview.setOnTouchListener(previewTouchListener);

        preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isPreviewButtonLongPressed = true;
                Intent i = new Intent(MainActivity.this,preview.class);
                startActivity(i);
                mainLL.setAlpha(0.25f);
                return true;
            }
        });


        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMyServiceRunning(cs.class)){
                    startService(new Intent(MainActivity.this,cs.class));
                    startService.setBackground(getResources().getDrawable(R.drawable.rounded_buttonstop));
                    startService.setText("STOP");
                }else{
                    Intent shotOnService = new Intent(getApplicationContext(),cs.class);
                    stopService(shotOnService);
                    NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancelAll();
                    startService.setBackground(getResources().getDrawable(R.drawable.rounded_buttonstart));
                    startService.setText("START");
                }
            }
        });

        rear = (Spinner) findViewById(R.id.rear);
        front = (Spinner) findViewById(R.id.front);
        setActionBar("#shotOn");

        iconText = new String[]{"Camera Adjust", "Cake", "Altered camera", "Selfie", "Camera 1", "Focus", "HDR", "Grain", "Nature & People", "Nature", "Camera 2", "Portrait", "Faces", "Tonality", "Whatshot"};
        white = new Integer[]{ R.drawable.badjust1, R.drawable.cake1, R.drawable.cameraalt1, R.drawable.selfie1,R.drawable.bcamera1, R.drawable.cfocus1, R.drawable.hdr1, R.drawable.grain1,
                                R.drawable.naturepeople1, R.drawable.nature1, R.drawable.pcamera1, R.drawable.portrait1,R.drawable.tagfaces1, R.drawable.tonality1, R.drawable.whatshot1};
        black = new Integer[]{ R.drawable.badjust2, R.drawable.cake2, R.drawable.cameraalt2, R.drawable.selfie,R.drawable.bcamera2, R.drawable.cfocus2, R.drawable.hdr2, R.drawable.grain2,
                R.drawable.naturepeople2, R.drawable.nature2, R.drawable.pcamera2, R.drawable.portrait,R.drawable.tagfaces2, R.drawable.tonality2, R.drawable.whatshot2};

        whiteAdapter = new SpinnerAdapter(this, R.layout.spinner_value_layout, iconText, white);
        blackAdapter = new SpinnerAdapter(this, R.layout.spinner_value_layout, iconText, black);

        initialize();






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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initialize(){

        String theme = prefs.getString("theme","white");

        Log.d("SHITT THEME",""+theme);

        setAdapters(theme,false);

        String shotOn = prefs.getString("sText","Shot on OnePlus\nBy Bharath Asokan");

        sText.setText(""+shotOn);

        if(isMyServiceRunning(cs.class)){

            startService.setBackground(getResources().getDrawable(R.drawable.rounded_buttonstop));
            startService.setText("STOP");

        }


    }

    public void setAdapters(String theme,boolean fromListener){

        String rearDef = "2131165296";
        String frontDef = "2131165325";

        if(!fromListener){
            if(theme.equals("white")){
                ((RadioButton)rg.getChildAt(0)).setChecked(true);
            }else{
                rearDef = "2131165297";
                frontDef = "2131165324";
                ((RadioButton)rg.getChildAt(1)).setChecked(true);
            }
        }

        Integer rearIcon = Integer.parseInt(prefs.getString("rear",rearDef));
        Integer frontIcon = Integer.parseInt(prefs.getString("front",frontDef));


        if(theme.equals("white")){
            rear.setAdapter(whiteAdapter);
            front.setAdapter(whiteAdapter);
            int index = Arrays.asList(white).indexOf(rearIcon);
            if(fromListener == true && index == -1){
                index = Arrays.asList(black).indexOf(rearIcon);
            }
            Log.d("SHITT W",""+rearIcon + "  " + Arrays.asList(white).toString() + " " + index);
            rear.setSelection(index);
            index = Arrays.asList(white).indexOf(frontIcon);
            if(fromListener == true && index == -1){
                index = Arrays.asList(black).indexOf(frontIcon);
            }
            Log.d("SHITT W",""+rearIcon + "  " + Arrays.asList(white).toString() + " " + index);
            front.setSelection(index);
        }else{
            wb = 1;
            rear.setAdapter(blackAdapter);
            front.setAdapter(blackAdapter);
            int index = Arrays.asList(black).indexOf(rearIcon);
            if(fromListener == true && index == -1){
                index = Arrays.asList(white).indexOf(rearIcon);
            }
            rear.setSelection(index);
            Log.d("SHITT B",""+rearIcon + "  " + Arrays.asList(black).toString() + " "  + index);
            index = Arrays.asList(black).indexOf(frontIcon);
            if(fromListener == true && index == -1){
                index = Arrays.asList(white).indexOf(frontIcon);
            }
            Log.d("SHITT B",""+rearIcon + "  " + Arrays.asList(black).toString() + " "  + index);
            front.setSelection(index);
        }

        addListeners();

    }

    public void addListeners(){

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                if(checkedId == R.id.white && isChecked){
                    prefs.edit().putString("theme", "white").commit();
                    wb= 0;
                    setAdapters("white",true);


                }else{
                    prefs.edit().putString("theme", "black").commit();
                    wb= 1;
                    setAdapters("black",true);
                }
            }
        });


        rear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Integer selectedIcon = white[position];
                if(wb == 1){
                    selectedIcon = black[position];
                    Log.d("SHITT BCR",""+selectedIcon + "  " + Arrays.asList(black).toString());
                }else{
                    Log.d("SHITT WCR",""+selectedIcon + "  " + Arrays.asList(white).toString());
                }
                prefs.edit().putString("rear", ""+selectedIcon).commit();
                Toast.makeText(getApplicationContext(),"Settings updated T:-)",Toast.LENGTH_SHORT).show();

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
                    Log.d("SHITT BCF",""+selectedIcon + "  " + Arrays.asList(black).toString());
                }else{
                    Log.d("SHITT WCF",""+selectedIcon + "  " + Arrays.asList(white).toString());
                }
                prefs.edit().putString("front", ""+selectedIcon).commit();
                Toast.makeText(getApplicationContext(),"Settings updated T:-)",Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getApplicationContext(),"Settings updated Text:-)",Toast.LENGTH_SHORT).show();

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),"Permisions granted :-)",Toast.LENGTH_SHORT).show();
                    getPermissions();
                }else{
                    Toast.makeText(getApplicationContext(),"#shotOn wont work unless granted these access permission :-(, you can always grant permissions again by clicking on 3dots on top & allow :-)",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void getPermissions(){

        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {

                try{
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1234);
                }catch (ActivityNotFoundException e){

                    Log.d("SHOT", "Exception" + e );
                }

            }


        }


    }

    public View.OnTouchListener previewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View pView, MotionEvent pEvent) {
            pView.onTouchEvent(pEvent);
            // We're only interested in when the button is released.
            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                // We're only interested in anything if our speak button is currently pressed.
                if (isPreviewButtonLongPressed) {
                    // Do something when the button is released.
                    com.example.bharath_5493.shoton.preview.fa.finish();
                    mainLL.setAlpha(1.0f);
                    isPreviewButtonLongPressed = false;
                }
            }
            return false;
        }
    };


    @Override
    public void onBackPressed() {

        exitCount++;
        if(exitCount == 1){
            Toast.makeText(getApplicationContext(), "Press back once again to exit", Toast.LENGTH_SHORT).show();
        }else{
            finish();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
