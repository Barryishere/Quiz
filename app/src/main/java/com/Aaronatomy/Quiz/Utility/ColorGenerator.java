package com.Aaronatomy.Quiz.Utility;

import android.content.Context;

import com.Aaronatomy.Quiz.R;

import java.util.Random;

/**
 * Created by AaronAtomy on 2018/4/7.
 * ColorGenerator
 */

public class ColorGenerator {
    private int[] drawables;
    private static int last;

    public ColorGenerator(Context context) {
        last = 0;
        drawables = new int[]{
                context.getResources().getColor(R.color.colorCell1),
                context.getResources().getColor(R.color.colorCell2),
                context.getResources().getColor(R.color.colorCell3),
                context.getResources().getColor(R.color.colorCell4),
                context.getResources().getColor(R.color.colorCell5),
                context.getResources().getColor(R.color.colorCell6),
                context.getResources().getColor(R.color.colorCell7),
        };
    }

    public int generate() {
        Random random = new Random();
        int lucky = random.nextInt(drawables.length);
        if (last == lucky)
            lucky = random.nextInt(drawables.length);
        last = lucky;
        return drawables[lucky];
    }
}
