package com.nova.m13;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;

public class NOVAControlActivity extends Activity {
    LinearLayout root; int bg=Color.rgb(5,6,10),panel=Color.rgb(15,18,25),text=Color.rgb(246,247,252),muted=Color.rgb(143,150,170),cyan=Color.rgb(82,215,255);
    @Override public void onCreate(Bundle b){super.onCreate(b);build();}
    TextView t(String s,int z){TextView v=new TextView(this);v.setText(s);v.setTextColor(text);v.setTextSize(z);v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);v.setPadding(20,14,20,14);return v;}
    void action(String title,String sub,final String setting){Button b=new Button(this);b.setText(title+"\n"+sub);b.setTextColor(text);b.setTextSize(13);b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);b.setBackgroundColor(panel);b.setOnClickListener(v->{try{startActivity(new Intent(setting));}catch(Exception ignored){}});root.addView(b,new LinearLayout.LayoutParams(-1,72));}
    void build(){ScrollView s=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(16,20,16,24);root.setBackgroundColor(bg);TextView h=t("NOVA Control Center",25);h.setTextColor(cyan);root.addView(h);root.addView(t("System shortcuts that stay honest about Android permissions",11));action("Wi‑Fi","Open Android Wi‑Fi panel",Settings.ACTION_WIFI_SETTINGS);action("Bluetooth","Open Bluetooth panel",Settings.ACTION_BLUETOOTH_SETTINGS);action("Mobile network","Open network settings",Settings.ACTION_WIRELESS_SETTINGS);action("Location","Open location controls",Settings.ACTION_LOCATION_SOURCE_SETTINGS);action("Display","Brightness and display",Settings.ACTION_DISPLAY_SETTINGS);action("Sound","Volume and audio",Settings.ACTION_SOUND_SETTINGS);action("Battery","Battery and device care",Settings.ACTION_BATTERY_SAVER_SETTINGS);action("Security","Device security",Settings.ACTION_SECURITY_SETTINGS);s.addView(root);setContentView(s);}
}
