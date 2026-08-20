package com.nova.m13;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    NovaView nova;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(5, 6, 10));
        getWindow().setNavigationBarColor(Color.rgb(5, 6, 10));
        nova = new NovaView(this);
        setContentView(nova);
    }

    @Override public void onBackPressed() {
        if (nova.page != 0) { nova.page = 0; nova.invalidate(); }
        else super.onBackPressed();
    }

    static class AppEntry {
        String label, pkg; Drawable icon;
        AppEntry(String l, String p, Drawable i) { label=l; pkg=p; icon=i; }
    }

    static class NovaView extends View {
        final MainActivity a;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        final ArrayList<AppEntry> apps = new ArrayList<>();
        final Handler handler = new Handler();
        int page = 0; // 0 home, 1 apps, 2 control, 3 hub, 4 widgets
        float downX, downY;
        String time = "--:--";

        final int bg=Color.rgb(5,6,10), panel=Color.rgb(14,17,24), panel2=Color.rgb(21,24,33);
        final int text=Color.rgb(246,247,252), muted=Color.rgb(143,150,170);
        final int violet=Color.rgb(139,92,255), cyan=Color.rgb(82,215,255), green=Color.rgb(91,225,155);

        final Runnable ticker = new Runnable() {
            @Override public void run() {
                time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                invalidate(); handler.postDelayed(this, 1000);
            }
        };

        NovaView(MainActivity x) {
            super(x); a=x; setFocusable(true); stroke.setStyle(Paint.Style.STROKE);
            loadApps(); ticker.run();
        }

        void loadApps() {
            PackageManager pm=a.getPackageManager();
            Intent i=new Intent(Intent.ACTION_MAIN,null); i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list=pm.queryIntentActivities(i,0); apps.clear();
            for(ResolveInfo r:list) if(!r.activityInfo.packageName.equals(a.getPackageName()))
                apps.add(new AppEntry(r.loadLabel(pm).toString(),r.activityInfo.packageName,r.loadIcon(pm)));
            Collections.sort(apps,(x,y)->x.label.compareToIgnoreCase(y.label));
        }

        @Override protected void onDraw(Canvas c) {
            c.drawColor(bg);
            if(page==0) home(c); else if(page==1) apps(c); else if(page==2) control(c); else if(page==3) hub(c); else widgets(c);
        }

        void txt(Canvas c,String s,float x,float y,float size,int color,Paint.Align align){
            p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); p.setTextSize(size); p.setTextAlign(align);
            p.setTypeface(Typeface.create("sans",Typeface.NORMAL)); c.drawText(s,x,y,p);
        }
        void bold(Canvas c,String s,float x,float y,float size,int color,Paint.Align align){
            p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); p.setTextSize(size); p.setTextAlign(align);
            p.setTypeface(Typeface.create("sans",Typeface.BOLD)); c.drawText(s,x,y,p);
        }
        void box(Canvas c,float l,float t,float r,float b,float rad,int color){
            p.setShader(null); p.setStyle(Paint.Style.FILL); p.setColor(color); c.drawRoundRect(l,t,r,b,rad,rad,p);
        }
        void outline(Canvas c,float l,float t,float r,float b,float rad,int color){
            stroke.setColor(color); stroke.setStrokeWidth(1.5f); c.drawRoundRect(l,t,r,b,rad,rad,stroke);
        }
        void glow(Canvas c,float x,float y,float radius,int color){
            p.setShader(new RadialGradient(x,y,radius,new int[]{Color.argb(85,Color.red(color),Color.green(color),Color.blue(color)),Color.TRANSPARENT},null,Shader.TileMode.CLAMP));
            c.drawCircle(x,y,radius,p); p.setShader(null);
        }
        void header(Canvas c,String title){
            float w=getWidth(); bold(c,title,24,48,22,text,Paint.Align.LEFT); txt(c,time,w-24,48,14,muted,Paint.Align.RIGHT);
        }
        String trim(String s,int n){return s.length()>n?s.substring(0,n-1)+"…":s;}

        void home(Canvas c){
            float w=getWidth(),h=getHeight(); glow(c,w*.82f,150,260,violet); glow(c,w*.05f,h*.60f,220,cyan);
            txt(c,new SimpleDateFormat("EEEE, dd MMMM",Locale.getDefault()).format(new Date()).toUpperCase(Locale.getDefault()),w/2,54,11,muted,Paint.Align.CENTER);
            bold(c,time,w/2,151,68,text,Paint.Align.CENTER); txt(c,"NOVA OS",w/2,180,12,cyan,Paint.Align.CENTER);
            box(c,20,215,w-20,325,24,panel); outline(c,20,215,w-20,325,24,Color.rgb(37,43,57));
            bold(c,"27°",43,260,32,text,Paint.Align.LEFT); txt(c,"HYDERABAD",45,283,10,muted,Paint.Align.LEFT);
            txt(c,"Clear  •  Feels like 29°",w-43,262,12,text,Paint.Align.RIGHT); txt(c,"Good morning · 72% humidity",w-43,284,10,muted,Paint.Align.RIGHT);
            float gap=10, cw=(w-50)/2f;
            box(c,20,340,20+cw,452,22,panel); box(c,30+cw,340,w-20,452,22,panel);
            bold(c,"FOCUS",40,370,11,violet,Paint.Align.LEFT); bold(c,"Deep Work",40,402,18,text,Paint.Align.LEFT); txt(c,"45 min session",40,426,10,muted,Paint.Align.LEFT);
            bold(c,"MUSIC",40+cw+10,370,11,cyan,Paint.Align.LEFT); txt(c,"Nothing playing",40+cw+10,402,16,text,Paint.Align.LEFT); txt(c,"Tap to open player",40+cw+10,426,10,muted,Paint.Align.LEFT);
            bottom(c,0); txt(c,"↑ Control Center   ← NOVA Hub   → Glance",w/2,h-8,9,Color.rgb(95,101,118),Paint.Align.CENTER);
        }

        void bottom(Canvas c,int selected){
            float w=getWidth(),h=getHeight(); box(c,16,h-80,w-16,h-22,28,Color.rgb(17,20,28));
            String[] names={"Home","Apps","NOVA","Glance"}; float[] xs={w*.13f,w*.38f,w*.62f,w*.87f};
            for(int i=0;i<4;i++){
                if(i==selected) box(c,xs[i]-23,h-71,xs[i]+23,h-33,19,Color.rgb(38,32,62));
                p.setStyle(Paint.Style.FILL); p.setColor(i==2?violet:(i==selected?cyan:Color.rgb(105,112,130))); c.drawCircle(xs[i],h-53,8,p);
                txt(c,names[i],xs[i],h-27,9,i==selected?text:muted,Paint.Align.CENTER);
            }
        }

        void apps(Canvas c){
            float w=getWidth(),h=getHeight(); header(c,"Apps");
            box(c,20,70,w-20,120,18,panel); txt(c,"⌕  Search apps",40,101,14,muted,Paint.Align.LEFT);
            bold(c,"Favorites",22,151,12,text,Paint.Align.LEFT);
            String[] fav={"Phone","Camera","Gallery","Music"};
            for(int i=0;i<4;i++){float x=44+i*((w-88)/3f);box(c,x-21,168,x+21,210,15,panel2);p.setColor(i==0?cyan:Color.rgb(90,98,115));c.drawCircle(x,189,9,p);txt(c,fav[i],x,228,9,muted,Paint.Align.CENTER);}
            bold(c,"All Apps",22,264,12,text,Paint.Align.LEFT);
            int cols=4,shown=Math.min(apps.size(),28);float cell=(w-40)/4f;
            for(int idx=0;idx<shown;idx++){int row=idx/cols,col=idx%cols;float x=20+cell*col+cell/2,y=298+row*76;Drawable d=apps.get(idx).icon;if(d!=null){d.setBounds((int)x-21,(int)y-21,(int)x+21,(int)y+21);d.draw(c);}else box(c,x-21,y-21,x+21,y+21,14,panel2);txt(c,trim(apps.get(idx).label,11),x,y+36,9,text,Paint.Align.CENTER);}
            txt(c,"Swipe down to return",w/2,h-8,9,muted,Paint.Align.CENTER);
        }

        void control(Canvas c){
            float w=getWidth(),h=getHeight(); glow(c,w*.85f,80,240,cyan); header(c,"Control Center"); txt(c,"Quick controls",24,77,10,muted,Paint.Align.LEFT);
            float tw=(w-50)/2f;
            tile(c,20,92,20+tw,158,"Wi‑Fi","Android settings",cyan,true,"wifi"); tile(c,30+tw,92,w-20,158,"Bluetooth","Android settings",violet,true,"bluetooth");
            tile(c,20,168,20+tw,234,"Mobile data","Network settings",green,false,"data"); tile(c,30+tw,168,w-20,234,"Location","Location settings",cyan,false,"location");
            tile(c,20,244,20+tw,310,"Rotation","Display settings",violet,false,"rotation"); tile(c,30+tw,244,w-20,310,"Flashlight","Device settings",Color.rgb(255,198,70),false,"flash");
            box(c,20,330,w-20,405,22,panel); txt(c,"Brightness",41,356,12,text,Paint.Align.LEFT); txt(c,"78%",w-41,356,12,cyan,Paint.Align.RIGHT); box(c,41,373,w-41,381,4,Color.rgb(52,57,70));box(c,41,373,w*.78f,381,4,cyan);
            box(c,20,420,w-20,485,22,panel);bold(c,"Device status",41,447,12,text,Paint.Align.LEFT);txt(c,"Battery 78%   •   5G   •   Secure",41,469,10,muted,Paint.Align.LEFT);
            txt(c,"Tap a tile to open the real Android panel",w/2,h-8,9,muted,Paint.Align.CENTER);
        }
        void tile(Canvas c,float l,float t,float r,float b,String title,String sub,int accent,boolean active,String action){
            box(c,l,t,r,b,20,active?Color.rgb(29,31,45):panel);outline(c,l,t,r,b,20,active?Color.argb(120,Color.red(accent),Color.green(accent),Color.blue(accent)):Color.rgb(34,39,51));p.setColor(accent);p.setStyle(Paint.Style.FILL);c.drawCircle(l+27,t+28,9,p);bold(c,title,l+46,t+29,12,text,Paint.Align.LEFT);txt(c,sub,l+46,t+48,9,muted,Paint.Align.LEFT);
        }

        void hub(Canvas c){
            float w=getWidth(),h=getHeight(); glow(c,w/2,190,180,violet); header(c,"NOVA Hub");p.setColor(violet);p.setStyle(Paint.Style.FILL);c.drawCircle(w/2,190,55,p);stroke.setColor(Color.argb(180,82,215,255));stroke.setStrokeWidth(2);c.drawCircle(w/2,190,70,stroke);bold(c,"N",w/2,202,32,text,Paint.Align.CENTER);txt(c,"Your command center",w/2,282,14,text,Paint.Align.CENTER);
            hubButton(c,20,315,w/2-6,375,"Focus Mode","Deep work",violet);hubButton(c,w/2+6,315,w-20,375,"Student","Study tools",cyan);hubButton(c,20,388,w/2-6,448,"Gaming","Play mode",green);hubButton(c,w/2+6,388,w-20,448,"Night","Low distraction",Color.rgb(190,150,255));
            box(c,20,465,w-20,530,20,panel);txt(c,"NOVA AI",40,492,11,cyan,Paint.Align.LEFT);txt(c,"Ask, search, or control NOVA",40,515,13,text,Paint.Align.LEFT);txt(c,"›",w-40,505,26,muted,Paint.Align.CENTER);txt(c,"Swipe right to Glance",w/2,h-8,9,muted,Paint.Align.CENTER);
        }
        void hubButton(Canvas c,float l,float t,float r,float b,String title,String sub,int accent){box(c,l,t,r,b,19,panel);p.setColor(accent);p.setStyle(Paint.Style.FILL);c.drawCircle(l+25,t+30,8,p);bold(c,title,l+42,t+28,11,text,Paint.Align.LEFT);txt(c,sub,l+42,t+47,9,muted,Paint.Align.LEFT);}

        void widgets(Canvas c){
            float w=getWidth(),h=getHeight(); glow(c,w*.2f,170,220,cyan); header(c,"Glance");txt(c,"Your day at a glance",24,78,11,muted,Paint.Align.LEFT);
            box(c,20,98,w-20,205,24,panel);bold(c,"TODAY",42,128,11,cyan,Paint.Align.LEFT);txt(c,"Thursday",42,155,20,text,Paint.Align.LEFT);txt(c,new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault()).format(new Date()),42,180,11,muted,Paint.Align.LEFT);txt(c,"☀  27°",w-42,154,18,text,Paint.Align.RIGHT);txt(c,"Clear",w-42,178,10,muted,Paint.Align.RIGHT);
            box(c,20,220,w-20,320,22,panel);bold(c,"NEXT",42,248,10,violet,Paint.Align.LEFT);txt(c,"No upcoming events",42,276,17,text,Paint.Align.LEFT);txt(c,"Your calendar is clear",42,298,10,muted,Paint.Align.LEFT);
            box(c,20,335,w-20,440,22,panel);bold(c,"BATTERY",42,364,10,green,Paint.Align.LEFT);txt(c,"78%",42,402,30,text,Paint.Align.LEFT);txt(c,"Good · estimated 9h 20m",w-42,397,11,muted,Paint.Align.RIGHT);
            box(c,20,455,w-20,540,22,panel);bold(c,"NOVA TIP",42,484,10,cyan,Paint.Align.LEFT);txt(c,"Swipe up from Home for Control Center",42,510,12,text,Paint.Align.LEFT);txt(c,"Swipe left for NOVA Hub",42,529,10,muted,Paint.Align.LEFT);
            bottom(c,3);
        }

        void openSetting(String action){
            try { a.startActivity(new Intent(action)); }
            catch(Exception e){ Toast.makeText(a,"Settings panel unavailable",Toast.LENGTH_SHORT).show(); }
        }

        @Override public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}
            if(e.getAction()!=MotionEvent.ACTION_UP)return true;
            float dx=e.getX()-downX,dy=e.getY()-downY;float w=getWidth(),h=getHeight();
            if(Math.abs(dx)>90 && Math.abs(dx)>Math.abs(dy)) { if(dx<0)page=3; else page=4; invalidate(); return true; }
            if(Math.abs(dy)>90 && Math.abs(dy)>Math.abs(dx)) { if(dy<0) page=(page==0?2:1); else page=0; invalidate(); return true; }
            if(page==0 && downY>h-100){ if(downX>w*.25f&&downX<w*.5f)page=1; else if(downX>w*.5f&&downX<w*.75f)page=3; else if(downX>=w*.75f)page=4; invalidate(); return true; }
            if(page==2 && downY>=92 && downY<=310){
                int row=(int)((downY-92)/76), col=downX>w/2?1:0;String action=null;
                if(row==0)action=col==0?Settings.ACTION_WIFI_SETTINGS:Settings.ACTION_BLUETOOTH_SETTINGS;
                else if(row==1)action=col==0?Settings.ACTION_DATA_ROAMING_SETTINGS:Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                else if(row==2)action=col==0?Settings.ACTION_DISPLAY_SETTINGS:Settings.ACTION_SETTINGS;
                if(action!=null)openSetting(action);return true;
            }
            if(page==1 && downY>285){int cols=4;float cell=(w-40)/4f;int col=(int)(downX/cell);int row=(int)((downY-277)/76);int idx=row*cols+col;if(idx>=0&&idx<apps.size())launch(apps.get(idx));}
            return true;
        }
        void launch(AppEntry e){try{Intent i=a.getPackageManager().getLaunchIntentForPackage(e.pkg);if(i!=null)a.startActivity(i);}catch(Exception ex){Toast.makeText(a,"Could not open "+e.label,Toast.LENGTH_SHORT).show();}}
    }
}
