package com.example.uberv.mediaworkshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private float step = 1;
    private float radius = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        /*
        final RoundedImageView roundedImageView = new RoundedImageView(this);
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        roundedImageView.setImage(source);
        setContentView(roundedImageView);
        */

        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.CENTER);
        setContentView(iv);

        // Create and load images (immutable, typically)
        Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        Bitmap mask = BitmapFactory.decodeResource(getResources(), R.drawable.mask);

        // Create a *mutable* location, and a canvas to draw into it
        final Bitmap result =
                Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        // 1. draw a mask
        canvas.drawBitmap(mask, 0, 0, paint);
        // 2. configure transfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 3. draw a bitmap on a mask
        canvas.drawBitmap(source, 0, 0, paint);
        // reset
        paint.setXfermode(null);

        // apply dynamic elevation (Lollipop+)
        // Elevate the view to make a visible shadow
        iv.setElevation(32f);
        // Draw an outline that matches the mask to provide the proper shadow
        iv.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int x = (view.getWidth() - result.getWidth()) / 2;
                int y = (view.getHeight() - result.getHeight()) / 2;

                Path path = new Path();
                path.moveTo(x, y);
                path.lineTo(x + result.getWidth(), y);
                path.lineTo(x + result.getWidth() / 2, y + result.getHeight());
                path.lineTo(x, y);
                path.close();

                outline.setConvexPath(path);
            }
        });

        iv.setImageBitmap(result);

        // tint icon to be solid blue
//        iv.getDrawable().setColorFilter(0xFF0000AA,PorterDuff.Mode.SRC_ATOP);

    }
}
