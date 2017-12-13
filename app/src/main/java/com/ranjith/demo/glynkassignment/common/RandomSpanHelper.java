package com.ranjith.demo.glynkassignment.common;

import android.graphics.Color;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

/**
 * Created by Satori - Ranjith on 13-12-2017.
 */

public class RandomSpanHelper {

//    public static final ArrayList<CharacterStyle> styleSpans = new ArrayList<CharacterStyle>() {{
//        add(new BackgroundColorSpan(Color.parseColor("#FFA726")));
//        add(new ForegroundColorSpan(Color.parseColor("#FFD54F")));
//        add(new BackgroundColorSpan(Color.parseColor("#26C6DA")));
//        add(new ForegroundColorSpan(Color.parseColor("#FFF176")));
//    }};

    public static final ArrayList<String> colorList = new ArrayList<String>() {{
        add("#FFA726");
        add("#FFD54F");
        add("#26C6DA");
        add("#AB47BC");
        add("#5C6BC0");
        add("#EC407A");
    }};

    public static final ArrayList<String> fontList = new ArrayList<String>() {{
        add("Comfortaa-Bold.ttf");
        add("PatuaOne-Regular.ttf");
        add("Lobster-Regular.ttf");
        add("Bungee-Regular.ttf");
        add("Shrikhand-Regular.ttf");
        add("Inconsolata-Bold.ttf");
    }};


    public static final ArrayList<Integer> sizeList = new ArrayList<Integer>() {{
        add(72);
        add(52);
        add(80);
        add(64);
        add(88);
        add(96);
    }};


}
