package com.nova.m13;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    NovaLauncherView nova;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(5,6,9));
        getWindow().setNavigationBarColor(Color.rgb(5,6,9));
        nova = new NovaLauncherView(this);
        setContentView(nova);
    }
    @Override public void onBackPressed() {
        if (nova.drawer) { nova.drawer=false; nova.invalidate(); }
        else super.onBackPressed();
    }

    static class AppEntry { String label, pkg; Drawable icon; AppEntry(String l,String p,Drawable i){label=l;pkg=p;icon=i;} }

    static class NovaLauncherView extends View {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        MainActivity a; ArrayList<AppEntry> apps = new ArrayList<>(); boolean drawer=false;
        float downX, downY; Handler handler=new Handler(); String time="";
        Runnable tick= new Runnable(){ public void run(){ time=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()); invalidate(); handler.postDelayed(this,1000); }};
        int violet=Color.rgb(139,92,255), cyan=Color.rgb(82,215,255), text=Color.rgb(245,247,255), muted=Color.rgb(137,145,167), bg=Color.rgb(5,6,9), surface=Color.rgb(13,16,22);
        NovaLauncherView(MainActivity x){ super(x); a=x; p.setTypeface(Typeface.create("sans",Typeface.NORMAL)); stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(2); setFocusable(true); loadApps(); tick.run(); }
        void loadApps(){
            PackageManager pm=a.getPackageManager(); Intent i=new Intent(Intent.ACTION_MAIN,null); i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list=pm.queryIntentActivities(i,0); apps.clear();
            for(ResolveInfo r:list){ if(r.activityInfo.packageName.equals(a.getPackageName())) continue; String l=r.loadLabel(pm).toString(); apps.add(new AppEntry(l,r.activityInfo.packageName,r.loadIcon(pm))); }
            Collections.sort(apps,(x,y)->x.label.compareToIgnoreCase(y.label));
        }
        protected void onDraw(Canvas c){ super.onDraw(c); c.drawColor(bg); if(drawer) drawDrawer(c); else drawHome(c); }
        void txt(Canvas c,String s,float x,float y,float size,int color,Paint.Align align){ p.setStyle(Paint.Style.FILL); p.setTextSize(size); p.setColor(color); p.setTextAlign(align); p.setTypeface(Typeface.create("sans",Typeface.NORMAL)); c.drawText(s,x,y,p); }
        void round(Canvas c,float l,float t,float r,float b,float rad,int color){ p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(l,t,r,b,rad,rad,p); }
        void drawHome(Canvas c){
            float w=getWidth(), h=getHeight();
            p.setStyle(Paint.Style.FILL); p.setShader(new RadialGradient(w*.72f,h*.34f,Math.min(w,h)*.45f,new int[]{Color.argb(90,139,92,255),Color.TRANSPARENT},null,Shader.TileMode.CLAMP)); c.drawCircle(w*.72f,h*.34f,Math.min(w,h)*.45f,p); p.setShader(null);
            txt(c,new SimpleDateFormat("EEEE",Locale.getDefault()).format(new Date()).toUpperCase(Locale.getDefault()),w/2,92,14,muted,Paint.Align.CENTER);
            txt(c,new SimpleDateFormat("dd MMMM",Locale.getDefault()).format(new Date()),w/2,116,13,muted,Paint.Align.CENTER);
            txt(c,time,w/2,205,72,text,Paint.Align.CENTER);
            stroke.setColor(Color.argb(190,82,215,255)); stroke.setStrokeWidth(2); c.drawCircle(w/2,185,96,stroke); stroke.setColor(Color.argb(150,139,92,255)); c.drawCircle(w/2,185,102,stroke);
            txt(c,"NOVA OS",w/2,245,16,text,Paint.Align.CENTER); txt(c,"Designed for focus. Built for you.",w/2,270,12,muted,Paint.Align.CENTER);
            round(c,28,310,w-28,425,28,surface); txt(c,"TODAY",52,344,12,cyan,Paint.Align.LEFT); txt(c,"Ready when you are.",52,375,20,text,Paint.Align.LEFT); txt(c,"Tap NOVA for shortcuts",52,400,12,muted,Paint.Align.LEFT);
            String[] names={"Phone","NOVA AI","Apps","Music"}; float[] xs={w*.15f,w*.38f,w*.62f,w*.85f};
            for(int j=0;j<4;j++){ float yy=h-76; round(c,xs[j]-27,yy-27,xs[j]+27,yy+27,18,Color.rgb(17,21,30)); txt(c,names[j],xs[j],yy+50,11,muted,Paint.Align.CENTER); if(j==1){ p.setStyle(Paint.Style.FILL);p.setColor(violet);c.drawCircle(xs[j],yy,12,p); } else { stroke.setColor(Color.rgb(90,105,130));stroke.setStrokeWidth(2);c.drawCircle(xs[j],yy,11,stroke); } }
            txt(c,"Swipe up for all apps  •  Swipe down to return",w/2,h-8,10,Color.rgb(105,112,130),Paint.Align.CENTER);
        }
        void drawDrawer(Canvas c){
            float w=getWidth(), h=getHeight(); txt(c,"NOVA",28,52,18,text,Paint.Align.LEFT); txt(c,"ALL APPS",28,76,11,muted,Paint.Align.LEFT);
            round(c,24,94,w-24,142,18,surface); txt(c,"⌕  Search apps…",45,125,14,muted,Paint.Align.LEFT);
            int cols=4; float cellW=(w-48)/4f; float startY=185; int shown=Math.min(apps.size(),40);
            for(int idx=0;idx<shown;idx++){ int row=idx/cols,col=idx%cols; float x=24+cellW*col+cellW/2; float y=startY+row*92; Drawable d=apps.get(idx).icon; if(d!=null){d.setBounds((int)x-24,(int)y-24,(int)x+24,(int)y+24);d.draw(c);} txt(c,apps.get(idx).label,x,y+43,10,text,Paint.Align.CENTER); }
            txt(c,"Swipe down to return",w/2,h-14,10,muted,Paint.Align.CENTER);
        }
        public boolean onTouchEvent(MotionEvent e){
            if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();return true;}
            if(e.getAction()==MotionEvent.ACTION_UP){float dx=e.getX()-downX,dy=e.getY()-downY;
                if(!drawer && dy < -90 && Math.abs(dy)>Math.abs(dx)){drawer=true;invalidate();return true;}
                if(drawer && dy > 90 && Math.abs(dy)>Math.abs(dx)){drawer=false;invalidate();return true;}
                if(!drawer && Math.abs(dx)<50 && Math.abs(dy)<50 && downY>getHeight()-150){float w=getWidth(); if(Math.abs(downX-w*.62f)<70){drawer=true;invalidate();return true;} if(Math.abs(downX-w*.38f)<70){Toast.makeText(a,"NOVA AI — Phase 2",Toast.LENGTH_SHORT).show();return true;}}
                if(drawer && Math.abs(dx)<40 && Math.abs(dy)<40 && downY>150){int col=(int)((downX-24)/((getWidth()-48)/4f));int row=(int)((downY-150)/92);int idx=row*4+col;if(idx>=0&&idx<apps.size()){launch(apps.get(idx));return true;}}
                return true;
            } return true;
        }
        void launch(AppEntry e){try{Intent i=a.getPackageManager().getLaunchIntentForPackage(e.pkg);if(i!=null){a.startActivity(i);}}catch(Exception ex){Toast.makeText(a,"Could not open "+e.label,Toast.LENGTH_SHORT).show();}}
    }
}
