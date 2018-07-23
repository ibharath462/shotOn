package com.example.bharath_5493.shoton;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    RelativeLayout n;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rg = (RadioGroup)findViewById(R.id.theme);

        Button fab = (Button) findViewById(R.id.start);
        final EditText sOnId = (EditText)findViewById(R.id.sOnId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startService(new Intent(MainActivity.this,cs.class));
                Toast.makeText(getApplicationContext(),"" + sOnId.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });


        //img = (ImageView)findViewById(R.id.img);
        //n = (RelativeLayout) findViewById(R.id.n);

        int selectedId = rg.getCheckedRadioButtonId();
        Spinner spinner = (Spinner) findViewById(R.id.mySpinner);
        RadioButton theme = (RadioButton) findViewById(selectedId);

        String[] textArray = { "clouds", "mark", "techcrunch", "times" };
        Integer[] imageArray = { R.drawable.camera1, R.drawable.camera1,
                R.drawable.camera1, R.drawable.camera1};

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spinner_value_layout, textArray, imageArray);
        spinner.setAdapter(adapter);


        if(getIntent() != null && getIntent().getStringExtra("path") != null){
            Toast.makeText(getApplicationContext(),"new photo taken..",Toast.LENGTH_LONG).show();
            Bitmap bmp = BitmapFactory.decodeFile(getIntent().getStringExtra("path"));



            bmp = drawTextToBitmap(getApplicationContext(),bmp,"Shot on OnePlus\nBy Bharath Asokan");
//            img.setImageBitmap(bmp);
//            img.setMaxHeight(bmp.getHeight());
//            img.setMaxWidth(bmp.getWidth());

            SaveImage(bmp);
        }

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

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String gText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;

            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.WHITE);
            // text size in pixels
            paint.setTextSize((int) (48 * scale));
            // text shadow
            //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            int noOfLines = 0;
            for (String line: gText.split("\n")) {
                noOfLines++;
            }

            paint.getTextBounds(gText, 0, gText.length(), bounds);
            int x = 20;
            int y = (bitmap.getHeight() - bounds.height()*noOfLines);

            Paint mPaint = new Paint();
            mPaint.setColor(getResources().getColor(R.color.transparentBlack));
            int left = 0;
            int top = (bitmap.getHeight() - bounds.height()*(noOfLines+1));
            int right = bitmap.getWidth();
            int bottom = bitmap.getHeight();
            canvas.drawRect(left, top, right, bottom, mPaint);

            for (String line: gText.split("\n")) {
                canvas.drawText(line, x, y, paint);
                y += paint.descent() - paint.ascent();
            }

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception



            return null;
        }

    }
}
