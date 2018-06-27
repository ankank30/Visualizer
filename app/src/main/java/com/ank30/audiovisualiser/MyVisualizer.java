package com.ank30.audiovisualiser;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyVisualizer extends Fragment implements MainActivityToMyVisualizerHandler {
    private Paint mForePaint = new Paint();
    private static byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private static ImageView imageView;

    public MyVisualizer (){
    }

    @Override
    public void MyVisualizerCtor(Context context) {
        initialize();
    }

    @Override
    public void MyVisualizerCtor(Context context, AttributeSet attrs) {
        initialize();
    }

    @Override
    public void MyVisualizerCtor(Context context, AttributeSet attrs, int defStyleAttr) {
        initialize();
    }

    void initialize() {
        mBytes = null;
        mForePaint.setStrokeWidth(1f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public static void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        imageView.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_visualizer, container, false);

        imageView = view.findViewById(R.id.visualizerImageView);
        imageView.setImageDrawable(new MyDrawable());

        return view;
    }

    private class MyDrawable extends Drawable {
        @Override
        public void draw(Canvas canvas) {
            if (mBytes == null) {
                return;
            }

            if (mPoints == null || mPoints.length < mBytes.length * 4) {
                mPoints = new float[mBytes.length * 4];
            }

            mRect.set(0, 0, getView().getWidth(), getView().getHeight());

            for (int i = 0; i < mBytes.length - 1; i++) {
                mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
                mPoints[i * 4 + 1] = mRect.height() / 2 + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
                mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
                mPoints[i * 4 + 3] = mRect.height() / 2 + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
            }
            canvas.drawLines(mPoints, mForePaint);
        }

        @Override
        public int getOpacity() {return PixelFormat.UNKNOWN;}

        @Override
        public void setAlpha(int alpha) {}

        @Override
        public void setColorFilter(ColorFilter cf) {}
    }
}
