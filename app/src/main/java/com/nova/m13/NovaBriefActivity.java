package com.nova.m13;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NovaBriefActivity extends Activity {
    int bg=Color.rgb(5,6,10),panel=Color.rgb(14,17,24),text=Color.rgb(246,247,252),muted=Color.rgb(143,150,170),cyan=Color.rgb(82,215,255),violet=Color.rgb(139,92,255);
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(20,28,20,20);root.setBackgroundColor(bg);setContentView(root);
        TextView h=tv("Now Brief",25,text);h.setTypeface(Typeface.DEFAULT_BOLD);root.addView(h);root.addView(tv(new SimpleDateFormat("EEEE, dd MMMM",Locale.getDefault()).format(new Date()),12,muted));
        card(root,"GOOD MORNING","Your NOVA day is ready","Start with a calm 45-minute focus session.",violet);
        card(root,"WEATHER","27° · Hyderabad","Clear skies · Feels like 29°",cyan);
        card(root,"FOCUS","Deep Work","45 minutes suggested · Notifications minimized",violet);
        card(root,"DEVICE","Battery 78% · 5G","Everything looks ready for your day.",Color.rgb(91,225,155));
        card(root,"NOVA TIP","Quick Control","Swipe up from Home to open your custom Control Center.",cyan);
    }
    TextView tv(String s,float z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);v.setPadding(0,6,0,6);return v;}
    void card(LinearLayout root,String k,String t,String s,int accent){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(18,14,18,14);c.setBackgroundColor(panel);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,12,0,0);root.addView(c,lp);TextView a=tv(k,10,accent);a.setTypeface(Typeface.DEFAULT_BOLD);c.addView(a);TextView x=tv(t,19,text);x.setTypeface(Typeface.DEFAULT_BOLD);c.addView(x);c.addView(tv(s,11,muted));}
}
