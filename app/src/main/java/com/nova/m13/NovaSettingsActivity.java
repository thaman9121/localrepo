package com.nova.m13;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class NovaSettingsActivity extends Activity {
    LinearLayout root;
    int bg=Color.rgb(5,6,10), panel=Color.rgb(14,17,24), text=Color.rgb(246,247,252), muted=Color.rgb(143,150,170), cyan=Color.rgb(82,215,255), violet=Color.rgb(139,92,255);
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg); build();}
    TextView tv(String s,float size,int color){TextView v=new TextView(this);v.setText(s);v.setTextColor(color);v.setTextSize(size);v.setTypeface(Typeface.create("sans",Typeface.NORMAL));v.setPadding(22,12,22,12);return v;}
    TextView title(String s){TextView v=tv(s,22,text);v.setTypeface(Typeface.DEFAULT_BOLD);v.setPadding(22,28,22,18);return v;}
    LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(4,4,4,4);c.setBackgroundColor(panel);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(16,8,16,8);c.setLayoutParams(lp);return c;}
    void row(LinearLayout c,String name,String sub,boolean checked){LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);TextView t=tv(name+"\n"+sub,15,text);t.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1));Switch sw=new Switch(this);sw.setChecked(checked);sw.setOnCheckedChangeListener((b,v)->Toast.makeText(this,name+(v?" enabled":" disabled"),Toast.LENGTH_SHORT).show());r.addView(t);r.addView(sw);c.addView(r);}
    void build(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(bg);ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
        root.addView(title("NOVA Settings"));root.addView(tv("Personalize your mobile experience",13,muted));
        LinearLayout appearance=card();appearance.addView(tv("APPEARANCE",11,cyan));row(appearance,"Dynamic theme","Use NOVA accent colors across panels",true);row(appearance,"Dark mode","Always use the NOVA dark surface",true);row(appearance,"Animations","Smooth launcher transitions",true);root.addView(appearance);
        LinearLayout quick=card();quick.addView(tv("QUICK PANEL",11,cyan));row(quick,"Wi‑Fi tile","Show in NOVA Control Center",true);row(quick,"Bluetooth tile","Show in NOVA Control Center",true);row(quick,"Mobile data","Show network shortcut",true);row(quick,"Location","Show location shortcut",true);row(quick,"Rotation","Show display shortcut",true);row(quick,"Flashlight","Show device shortcut",true);root.addView(quick);
        LinearLayout smart=card();smart.addView(tv("SMART FEATURES",11,cyan));row(smart,"Now Bar","Show live-style status cards in NOVA",true);row(smart,"Now Brief","Show personalized day cards",true);row(smart,"Edge Panel","Enable quick access from the screen edge",true);row(smart,"Modes & Routines","Enable Focus, Student, Gaming and Night",true);root.addView(smart);
        LinearLayout privacy=card();privacy.addView(tv("PRIVACY",11,cyan));row(privacy,"Local-first data","Keep NOVA preferences on this phone",true);row(privacy,"Usage learning","Use only local app usage signals",false);root.addView(privacy);
        Button reset=new Button(this);reset.setText("Reset NOVA preferences");reset.setTextColor(text);reset.setBackgroundColor(Color.rgb(30,34,44));reset.setOnClickListener(v->Toast.makeText(this,"NOVA preferences reset",Toast.LENGTH_SHORT).show());LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,58);rp.setMargins(16,18,16,30);root.addView(reset,rp);
    }
}
