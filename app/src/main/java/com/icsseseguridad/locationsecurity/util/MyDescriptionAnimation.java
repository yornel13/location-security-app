package com.icsseseguridad.locationsecurity.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.glide.slider.library.Animations.BaseAnimationInterface;

public class MyDescriptionAnimation implements BaseAnimationInterface {

    public MyDescriptionAnimation() {
    }

    public void onPrepareCurrentItemLeaveScreen(View current) {
        View descriptionLayout = current.findViewById(com.glide.slider.library.R.id.description_layout);
        if (descriptionLayout != null) {
            current.findViewById(com.glide.slider.library.R.id.description_layout).setVisibility(View.GONE);
        }

    }

    public void onPrepareNextItemShowInScreen(View next) {
        View descriptionLayout = next.findViewById(com.glide.slider.library.R.id.description_layout);
        if (descriptionLayout != null) {
            next.findViewById(com.glide.slider.library.R.id.description_layout).setVisibility(View.GONE);
        }

    }

    public void onCurrentItemDisappear(View view) {
    }

    public void onNextItemAppear(View view) {
        View descriptionLayout = view.findViewById(com.glide.slider.library.R.id.description_layout);
        if (descriptionLayout != null) {
            view.findViewById(com.glide.slider.library.R.id.description_layout).setVisibility(View.GONE);
        }

    }
}
