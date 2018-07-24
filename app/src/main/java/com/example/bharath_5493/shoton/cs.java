package com.example.bharath_5493.shoton;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class cs extends Service {
    public cs() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(getApplicationContext(),"Staretd service...",Toast.LENGTH_LONG).show();

        final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera";

        final String TAG = "SHOT";


        HandlerThread handlerThread = new HandlerThread("content_observer");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                new ContentObserver(handler) {
                    @Override
                    public boolean deliverSelfNotifications() {
                        Log.d(TAG, "deliverSelfNotifications");
                        return super.deliverSelfNotifications();
                    }

                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                    }

                    public void onChange(boolean selfChange, Uri uri) {
                        Log.d(TAG, "onChange " + uri.toString());
                        if (uri.toString().matches(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/[0-9]+")) {

                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(uri, new String[] {
                                        MediaStore.Images.Media.DISPLAY_NAME,
                                        MediaStore.Images.Media.WIDTH,
                                        MediaStore.Images.Media.HEIGHT,
                                        MediaStore.Images.Media.ORIENTATION,
                                        MediaStore.Images.Media.DATA
                                }, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    final String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                                    final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                    // TODO: apply filter on the file name to ensure it's screen shot event
                                    Log.d(TAG, "screen shot added " + fileName + " " + path);

                                    if(path.contains("DCIM")){

                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inJustDecodeBounds = true;
                                        BitmapFactory.decodeFile(path, options);
                                        String imageHeight = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                                        String imageWidth = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));;
                                        String orientation = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION));;

                                        Bitmap bmp = BitmapFactory.decodeFile(path);
                                        boolean isSelfie = false;
                                        if(orientation.equals("90")){
                                            Matrix matrix = new Matrix();
                                            matrix.postRotate(90);
                                            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                                        }else if(orientation.equals("270")){
                                            Matrix matrix = new Matrix();
                                            matrix.postRotate(270);
                                            isSelfie = true;
                                            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                                        }
                                        bmp = drawTextToBitmap(getApplicationContext(),bmp,"பரத்தால் சுடப்பட்டது\nஒன் ப்ளஸ்ல்",isSelfie);

                                        //Intent i = new Intent(cs.this,MainActivity.class);
                                        //i.putExtra("path",path);
                                        //startActivity(i);
                                        SaveImage(bmp);
                                        Log.d(TAG, "Width " + imageWidth + " height" + imageHeight + " orientation " + orientation);
                                    }

                                }
                            } finally {
                                if (cursor != null)  {
                                    cursor.close();
                                }
                            }
                        }
                        super.onChange(selfChange, uri);
                    }
                });
        Log.d("SHOT",""+PATH);
        //createNotification();

        return START_NOT_STICKY;

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
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            MediaScannerConnection.scanFile(this, new String[] { file.getPath() }, new String[] { "image/jpg" }, null);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String gText,boolean isSelfie) {
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
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
            // text shadow
            //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
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
                    R.drawable.camera1);

            if(isSelfie){
                icon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.selfie);
            }

            canvas.drawBitmap(icon,40,bitmap.getHeight() - bounds.height()*noOfLines - 80,null);
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception



            return null;
        }

    }
//
//    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
//        ExifInterface ei = new ExifInterface(image_absolute_path);
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//        Log.d("SHOT", "" + orientation);
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                return rotate(bitmap, 90);
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                return rotate(bitmap, 180);
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                return rotate(bitmap, 270);
//
//            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                return flip(bitmap, true, false);
//
//            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                return flip(bitmap, false, true);
//
//            default:
//                return bitmap;
//        }
//    }
//
//    public static Bitmap rotate(Bitmap bitmap, float degrees) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degrees);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }
//
//    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
//        Matrix matrix = new Matrix();
//        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }




}
