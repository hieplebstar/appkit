package com.bstar.powerdata.views.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bstar.powerdata.R;


public class CircleButton extends RelativeLayout{

    private int mImageSrc;
    private String mOverlayText;
    private String mChildText;

    private TextView mOverlayTextView;
    private TextView mChildTextView;
    private ImageView mImageView;

    public CircleButton(Context context) {
        super(context);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
        if (a.hasValue(R.styleable.CircleButton_overlayText)) {
            mOverlayText = a.getString(R.styleable.CircleButton_overlayText);
        }

        if (a.hasValue(R.styleable.CircleButton_childText)) {
            mChildText = a.getString(R.styleable.CircleButton_childText);
        }

        if (a.hasValue(R.styleable.CircleButton_imageSrc)) {
            mImageSrc = a.getResourceId(R.styleable.CircleButton_imageSrc, 0);
        }
        a.recycle();
        View view = LayoutInflater.from(context).inflate(R.layout.circle_button_item, this, true);
        mOverlayTextView = (TextView) view.findViewById(R.id.textview_circle_button_overlay);
        mChildTextView = (TextView) view.findViewById(R.id.textview_circle_button_child);
        mImageView = (ImageView) view.findViewById(R.id.imageview_circle_button);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(!TextUtils.isEmpty(mOverlayText)){
            mOverlayTextView.setText(mOverlayText);
        }
        if(!TextUtils.isEmpty(mChildText)) {
            mChildTextView.setText(mChildText);
        }
        if(mImageSrc != 0){
            mImageView.setImageResource(mImageSrc);
        } else {
            mImageView.setVisibility(GONE);
        }
    }
}
