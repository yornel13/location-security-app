package com.icsseseguridad.locationsecurity.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.icsseseguridad.locationsecurity.R;

public class AppTextView extends android.support.v7.widget.AppCompatTextView {
    public AppTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(attrs);
    }

    private void setCustomFont(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomFont,
                0, 0);

        try {
            String family = a.getString(R.styleable.CustomFont_fontFamily);
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + family);
            setTypeface(myTypeface);
        } finally {
            a.recycle();
        }
    }
}
