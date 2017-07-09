package com.example.uberv.mediaworkshop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RoundedImageView extends View {
    public static final String LOG_TAG = RoundedImageView.class.getSimpleName();

    private Bitmap mImage;
    private Paint mBitmapPaint;

    private RectF mBounds;
    private float mRadius = 50.0f;

    public RoundedImageView(Context context) {
        super(context);
        init();
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RoundedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // Create image paint
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Create rect for drawing bounds
        mBounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int width = 0;

        //Requested size is the image content size
        int imageHeight, imageWidth;
        if (mImage == null) {
            imageHeight = imageWidth = 0;
        } else {
            imageHeight = mImage.getHeight();
            imageWidth = mImage.getWidth();
        }
        // Get the best measurement ans set it on the view
        width = getMeasurement(widthMeasureSpec, imageWidth);
        height = getMeasurement(heightMeasureSpec, imageHeight);

        setMeasuredDimension(width, height);
    }

    /**
     * Helper method to measure width and height
     */
    private int getMeasurement(int measureSpec, int contentSize) {
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                return Math.min(specSize, contentSize);
            case MeasureSpec.UNSPECIFIED:
                return contentSize;
            case MeasureSpec.EXACTLY:
                return specSize;
            default:
                return 0;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(LOG_TAG, "onSizeChanged()");
        if (w != oldw || h != oldh) {
            // We want to center the image, so we offset our
            // valuse whenever the view changes size
            int imageWidth, imageHeight;
            if (mImage == null) {
                imageWidth = imageHeight = 0;
            } else {
                imageWidth = mImage.getWidth();
                imageHeight = mImage.getHeight();
            }

            // offsets
            int left = (w - imageWidth) / 2;
            int top = (h - imageHeight) / 2;
            Log.d(LOG_TAG, String.format("Size changed, w=%d, h=%d, olw=%d, oldh=%d left=%d, top=%d",
                    w, h, oldw, oldh, left, top));

            // Set the bounds to offset the rounded rectangle
            mBounds.set(left, top, left + imageWidth, top + imageHeight);

            // Offset the shader to draw the Bitmap inside the rect
            // Without this, the bitmap will be at 0,0 in the view
            if (mBitmapPaint.getShader() != null) {
                Matrix m = new Matrix();
                m.setTranslate(left, top);
                mBitmapPaint.getShader().setLocalMatrix(m);
            }
        }
    }

    public void setRadius(float radius) {
        if (mRadius != radius) {
            mRadius = radius;
            requestLayout();
            invalidate();
        }
    }

    public void setImage(Bitmap bitmap) {
        Log.d(LOG_TAG, "setImage()");
        if (mImage != bitmap) {
            mImage = bitmap;
            if (mImage != null) {
                // set paint to draw our image as texture
                BitmapShader shader = new BitmapShader(mImage,
                        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                mBitmapPaint.setShader(shader);
            } else {
                mBitmapPaint.setShader(null);
            }
            // redraw
            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG_TAG, "onDraw()");
        // Let the view draw backgrounds, etc.
        super.onDraw(canvas);

        // Draw the image with the calculated values
        if (mBitmapPaint != null) {
            canvas.drawRoundRect(mBounds, mRadius, mRadius, mBitmapPaint);
        }
    }
}
