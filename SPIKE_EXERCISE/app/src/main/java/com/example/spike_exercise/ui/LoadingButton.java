package com.example.spike_exercise.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LoadingButton extends MaterialButton {

    private boolean isLoading;
    private CircularProgressDrawable loadingDrawable;
    private String textBackup;

    public LoadingButton(@NonNull Context context) {
        super(context);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void init(Context context) {

    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if(isLoading) {
            textBackup = getText().toString();
            setText("");
            setEnabled(false);
        } else {
            setText(textBackup);
            setEnabled(true);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isLoading) {
            drawLoadingDrawable(canvas);
        }
    }

    protected void drawLoadingDrawable(Canvas canvas) {
        if(loadingDrawable == null) {
            loadingDrawable = new CircularProgressDrawable(getContext());
            loadingDrawable.start();
        }
        loadingDrawable.draw(canvas);
    }
}
