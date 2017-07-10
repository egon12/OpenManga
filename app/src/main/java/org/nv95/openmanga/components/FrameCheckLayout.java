package org.nv95.openmanga.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.nv95.openmanga.R;
import org.nv95.openmanga.utils.LayoutUtils;

/**
 * Created by nv95 on 30.06.16.
 */

public class FrameCheckLayout extends FrameLayout implements Checkable {

    private boolean mChecked;
    private static int mPadding;
    private ImageView mCheckMark;

    public FrameCheckLayout(Context context) {
        super(context);
        init();
    }

    public FrameCheckLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameCheckLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FrameCheckLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mChecked = false;
        if (mPadding == 0) {
            mPadding = getResources().getDimensionPixelOffset(R.dimen.padding8);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (mChecked) {
            getCheckMark().setVisibility(VISIBLE);
            LayoutUtils.animatePress(this);
        } else {
            getCheckMark().setVisibility(View.GONE);
            LayoutUtils.animatePress(this);
        }
    }

    private View getCheckMark() {
        if (mCheckMark == null) {
            mCheckMark = new ImageView(getContext());
            mCheckMark.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mCheckMark.setBackgroundResource(R.drawable.selector_frame);
            mCheckMark.setPadding(mPadding, mPadding, mPadding, mPadding);
            mCheckMark.setScaleType(ImageView.ScaleType.MATRIX);
            mCheckMark.setImageResource(R.drawable.ic_checkmark);
            mCheckMark.setVisibility(GONE);
            ViewCompat.setElevation(mCheckMark, 10000);
            addView(mCheckMark);
        }
        return mCheckMark;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
