package com.demo.magnifierdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnLongClickListener {

    private TextView contentText;
    private WindowManager wm;
    private LayoutParams params;
    private ImageView magnifier;
    private boolean isAdded = false;
    private int lastX;
    private int lastY;
    private int paramX;
    private int paramY;
    private int windowWidth = 400;
    private int windowHeight = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMagWindowParams();

        contentText = (TextView) findViewById(R.id.test_content);
        contentText.setOnLongClickListener(this);
        contentText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        saveTouchPoint(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isAdded) {
                            moveMagnifier(x, y);
                            magnifier.setBackground(getCurrentImage(x, y));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        removeMagnifier();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onLongClick(View v) {
        createMagnifier();
        return false;
    }

    private void initMagWindowParams() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.width = windowWidth;
        params.height = windowHeight;
    }

    private void createMagnifier() {
        magnifier = new ImageView(getApplicationContext());
        wm.addView(magnifier, params);
        isAdded = true;
    }

    private void removeMagnifier() {
        if (isAdded) {
            wm.removeView(magnifier);
            isAdded = false;
        }
    }

    private void saveTouchPoint(int x, int y) {
        lastX = x;
        lastY = y;

        params.x = x - windowWidth * 3 / 2;
        params.y = y - windowHeight * 6;
        paramX = params.x;
        paramY = params.y;
    }

    private void moveMagnifier(int x, int y) {
        int dx = x - lastX;
        int dy = y - lastY;
        params.x = paramX + dx;
        params.y = paramY + dy;

        wm.updateViewLayout(magnifier, params);
    }

    private BitmapDrawable getCurrentImage(int x, int y) {
        Bitmap magWindow = Bitmap.createBitmap(windowWidth, windowHeight, Config.ARGB_8888);
        Bitmap currentScreen = Bitmap.createBitmap(windowWidth, windowHeight, Config.ARGB_8888);

        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        currentScreen = decorview.getDrawingCache();

        Paint p = new Paint();
        Canvas c = new Canvas(magWindow);
        c.scale(2f, 2f);
        c.drawBitmap(currentScreen, -x, -y, p);

        BitmapDrawable outputDrawable = new BitmapDrawable(getResources(), magWindow);
        return outputDrawable;
    }

}
