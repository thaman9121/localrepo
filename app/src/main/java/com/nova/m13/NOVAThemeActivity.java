package com.nova.m13;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class NOVAThemeActivity extends Activity {
    LinearLayout root;
    int bg=Color.rgb(5,6,10), panel=Color.rgb(15,18,25), text=Color.rgb(246,247,252), muted=Color.rgb(143,150,170), cyan=Color.rgb(82,215,255), violet=Color.rgb(139,92,255);
    @Override public void onCreate(Bundle b){super.onCreate(b); build();}
    TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(text);t.setTextSize(size);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setPadding(0,8,0,8);return t;}
    void section(String title,String value){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(20,12,20,12);row.setBackgroundColor(panel);
        LinearLayout words=new LinearLayout(this);words.setOrientation(LinearLayout.VERTICAL);TextView a=label(title,15);TextView v=label(value,11);v.setTextColor(muted);words.addView(a);words.addView(v);
        row.addView(words,new LinearLayout.LayoutParams(0,-2,1));
        Switch sw=new Switch(this);sw.setChecked(true);row.addView(sw,new LinearLayout.LayoutParams(-2,-2));
        root.addView(row,new LinearLayout.LayoutParams(-1,-2));
    }
    void build(){
        ScrollView scroll=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(20,24,20,30);root.setBackgroundColor(bg);
        TextView h=label("NOVA Personalization",26);h.setTextColor(cyan);root.addView(h);
        TextView sub=label("Make every part of NOVA yours",13);sub.setTextColor(muted);root.addView(sub);
        root.addView(label("APPEARANCE",12));
        section("Dynamic wallpaper","Adaptive NOVA glow"); section("Icon shape","Rounded"); section("Glass effects","Enabled"); section("Smooth animations","120 Hz friendly");
        root.addView(label("HOME SCREEN",12));
        section("Clock customization","Size, weight and position"); section("App labels","Show labels"); section("Gesture navigation","Enabled"); section("Edge panel","Enabled");
        root.addView(label("MODES",12));
        section("Focus mode","Minimal distractions"); section("Student mode","Study dashboard"); section("Gaming mode","Game shortcuts"); section("Night mode","Low light");
        root.addView(label("NOVA INTELLIGENCE",12));
        section("Now Bar","Live activity cards"); section("Now Brief","Daily briefing"); section("Smart suggestions","On-device preference");
        Button reset=new Button(this);reset.setText("RESET NOVA STYLE");reset.setTextColor(Color.WHITE);reset.setBackgroundColor(violet);root.addView(reset,new LinearLayout.LayoutParams(-1,58));
        scroll.addView(root);setContentView(scroll);
    }
}
