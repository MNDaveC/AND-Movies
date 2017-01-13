package com.mndavec.movies;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Utils {

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);

        if (px > 780)
            return 780;
        if (px > 500)
            return 500;
        if (px > 342)
            return 342;
        return 185;
    }
}
