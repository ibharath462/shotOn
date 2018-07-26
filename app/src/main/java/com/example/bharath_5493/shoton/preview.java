package com.example.bharath_5493.shoton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class preview extends Activity {

    ImageView res;
    Button closePreview;
    public static Activity fa;
    Bitmap bitmap;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);

        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        wlp.height = (height) / 2;
        wlp.width = width;

        fa = this;

        getWindow().setAttributes(wlp);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.rounded_corners));

        res = (ImageView)findViewById(R.id.res);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        runOnBG bgThread = new runOnBG();

        bgThread.execute();



    }

    public  class runOnBG extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... params) {

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.preview);

            pb.animate();

            try {
                Resources resources = getApplicationContext().getResources();
                final SharedPreferences prefs = getApplicationContext().getSharedPreferences("com.pg.shoton", Context.MODE_PRIVATE);

                String gText = prefs.getString("sText","பரத்தால் சுடப்பட்டது\nஒன் ப்ளஸ்ல்");
                int selectedIcon = Integer.parseInt(prefs.getString("rear","2131165296"));
                String theme = prefs.getString("theme","white");


                float scale = resources.getDisplayMetrics().density;

                android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
                if(bitmapConfig == null) {
                    bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
                }
                bitmap = bitmap.copy(bitmapConfig, true);

                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.WHITE);
                if(theme.equals("black")){
                    paint.setColor(Color.BLACK);
                }
                paint.setTextSize((int) (48 * scale));
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
                Rect bounds = new Rect();
                int noOfLines = 0;
                for (String line: gText.split("\n")) {
                    noOfLines++;
                }

                paint.getTextBounds(gText, 0, gText.length(), bounds);
                int x = 350;
                int y = (bitmap.getHeight() - bounds.height()*noOfLines);

                Paint mPaint = new Paint();
                mPaint.setColor(getResources().getColor(R.color.transparentBlack));

                for (String line: gText.split("\n")) {
                    canvas.drawText(line, x, y, paint);
                    y += paint.descent() - paint.ascent();
                }

                Bitmap icon = BitmapFactory.decodeResource(getResources(),
                        selectedIcon);

                canvas.drawBitmap(icon,40,bitmap.getHeight() - bounds.height()*noOfLines - 80,null);

            } catch (Exception e) {
                // TODO: handle exception
                Log.d("SHOT", "ERRRORRORORORO" );
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            pb.setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("SHOT", "Done" );
                    res.setImageBitmap(bitmap);
                }
            });
            super.onPostExecute(s);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }
}
