package hailedEngine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class HailedEngine extends JPanel {
	Entity player = new Entity();
	public short unitsize = 32;
	/**for zooming closer get zoom closer to 0*/public float zoomratio = 1.f;
	public float getUsize() { return ((float)unitsize)/zoomratio; }
	public short panelWidth = 880, panelHeight = 660;
	public short entttypecnt = 3;
	public float defoultHealth = 100.f;
	public short frameDelay = 20;
	public float frictEstte = 0.5f, frictiater = 0.9f, frameRate = 50.f;
	public boolean isrunning = false;
	public Entities[] entitysets;
	public Timer GLtimer = new Timer();
	public Color backgroundcolor;
	public BufferedImage plyrskin;
	
	
	public void initialize() {
		super.setPreferredSize(new Dimension(panelWidth, panelHeight));
		entitysets = new Entities[entttypecnt];
		frameRate = 1000.f/(float)frameDelay;
		frictiater = (float) Math.pow(frictEstte, 1/frameRate);
		player.h=defoultHealth;
		backgroundcolor = new Color(0,0,0);
	}
	public void HEupdate() {
		for(int i=0;i!=entttypecnt;i++) { entitysets[i].update(); }
		player.p.increase(player.v); if(player.h<0.f) { System.exit(0); }
		repaint();
	}
	public void paintComponent(Graphics g) {
		Graphics2D target = (Graphics2D) g;
		target.fillRect(0, 0, getWidth(), getHeight());
		target.setColor(new Color(255,0,255)); entitysets[0].drawto(target);
		target.setColor(new Color(0,255,0));
		int x=(int)(getWidth()/2.f-player.s*getUsize()/2.f), y=(int)(getHeight()/2.f-player.s*getUsize()/2.f);
		target.drawImage(plyrskin, x, y, (int)(x+player.s*getUsize()), (int)(y+player.s*getUsize()), 0, 0, 32, 32 , this);
		target.setColor(new Color(255,0,0)); entitysets[1].drawto(target);
		target.setColor(new Color(255,255,0)); entitysets[2].drawto(target);
	}
	
	static public class vec2f {
		public float x, y; public vec2f() { x=0.f;y=0.f; }
		vec2f(float x, float y) { this.x=x;this.y=y; } vec2f(vec2f o) { x=o.x;y=o.y; }
		public vec2f(float o) { x=o;y=o; } public vec2f(vec2i o) { x=o.x;y=o.y; }
		public float getamplitude() { return (float) Math.sqrt(x*x+y*y); }
		public vec2f setamplitude(float amplitude) { amplifie(amplitude/getamplitude()); return this; }
		public vec2f disamplitude() { float a = getamplitude(); x/=a;y/=a; return this; }
		public vec2f minus() { return new vec2f(-x,-y); }
		public vec2f oneover() { return new vec2f(1/x,1/y); }
		
		public vec2f mirrorto(vec2f o) { x=o.x*2-x;y=o.y*2-y; return this; }
		public vec2f mirrorto(float ox, float oy) { x=ox*2-x;y=oy*2-y; return this; }
		public vec2f mirroredto(vec2f o) { return new vec2f(x=o.x*2-x,y=o.y*2-y); }
		public vec2f mirroredto(float ox, float oy) { return new vec2f(x=ox*2-x,y=oy*2-y); }
		public float distanceto(vec2f o) { float dx=x-o.x, dy=y-o.y; return (float) Math.sqrt(dx*dx+dy*dy); }
		public float distanceto(float ox, float oy) { float dx=x-ox, dy=y-oy; return (float) Math.sqrt(dx*dx+dy*dy); }
		
		public vec2f copy(vec2f o) { x=o.x;y=o.y; return this; }
		public vec2f copy(float ox, float oy) { x=ox;y=oy; return this; }
		public vec2f copy(float o) { x=o;y=o; return this; }
		
		public vec2f increase(vec2f o) { x+=o.x;y+=o.y; return this; }
		public vec2f sumwith(vec2f o) { return new vec2f(x+o.x,y+o.y); }
		public vec2f amplifie(vec2f o) { x*=o.x;y*=o.y; return this; }
		public vec2f multiplie(vec2f o) { return new vec2f(x*o.x,y*o.y); }
		public vec2f increase(float ox, float oy) { x+=ox;y+=oy; return this; }
		public vec2f sumwith(float ox, float oy) { return new vec2f(x+ox,y+oy); }
		public vec2f amplifie(float ox, float oy) { x*=ox;y*=oy; return this; }
		public vec2f multiplie(float ox, float oy) { return new vec2f(x*ox,y*oy); }
		public vec2f increase(float o) { x+=o;y+=o; return this; }
		public vec2f sumwith(float o) { return new vec2f(x+o,y+o); }
		public vec2f amplifie(float o) { x*=o;y*=o; return this; }
		public vec2f multiplie(float o) { return new vec2f(x*o,y*o); }
	}
	public static class BoundingBox {
		public BoundingBox(float l, float t, float r, float b) { this.l=l;this.r=r;this.t=t;this.b=b; }
		public BoundingBox(vec2f c, vec2f s) { l=c.x-s.x/2;r=c.x+s.x/2; t=c.y-s.y;b=c.y+s.y; }
		public BoundingBox(vec2f c, float s) { l=c.x-s/2;r=c.x+s/2; t=c.y-s;b=c.y+s; }
		public BoundingBox(BoundingBox o) { l=o.l;r=o.r;t=o.t;b=o.b; }
		public float l,r, t,b;
		public boolean iscollided(BoundingBox o) {
			int ls, rs, ts, bs;
			if(l<=o.l) { ls=-1; } else if(l<o.r) { ls=0; } else { ls=1; }
			if(r<=o.r) { rs=-1; } else if(r<o.l) { rs=0; } else { rs=1; }
			if(t<=o.t) { ts=-1; } else if(t<o.b) { ts=0; } else { ts=1; }
			if(b<=o.b) { bs=-1; } else if(b<o.t) { bs=0; } else { bs=1; }
			return (ls==0||rs==0||ls+rs==0)&&(ts==0||bs==0||bs+ts==0);
		}
	}
	
	public static class vec2i {	public vec2i(vec2f o) {	x=(int)o.x;y=(int)o.y; } public int x,y; }
	public vec2f mapS2M(vec2f p) { return p.sumwith(getWidth()*-0.5f, getHeight()*-0.5f).amplifie(zoomratio).increase(player.p); }
	public vec2f mapM2S(vec2f p)
	{ return p.sumwith(player.p.minus()).amplifie(1.f/zoomratio).increase(getWidth()*+0.5f, getHeight()*+0.5f); }
	
	
	static public class Entity{
		public vec2f p, v; public float h, s;
		public vec2f getDS(HailedEngine ow) {return ow.mapM2S(p).increase(-ow.getUsize()*s/2.f);}
		public vec2f getDE(HailedEngine ow) {return ow.mapM2S(p).increase(ow.getUsize()*s/2.f);}
		
		public void target2(vec2f target, float ampl) { v=target.sumwith(p.minus()).setamplitude(ampl); }
		public void accelerate(vec2f target, float ampl) { v.increase(target.sumwith(p.minus()).setamplitude(ampl)); }
		public void ficriate(HailedEngine ow) { v.amplifie(ow.frictiater*ow.frictiater); }
		
		public float distance2(HailedEngine ow, Entity o) { return p.distanceto(o.p)-(s*ow.unitsize/2.f)-(o.s*ow.unitsize/2.f); }
		public boolean iscollidedC2C(HailedEngine ow, Entity o) { return p.sumwith(o.p.minus()).getamplitude()<(s+o.s)*ow.unitsize/2.f; }
		
		public vec2f getColtionMidleC2C(Entity o) {
			vec2f midle = o.p.sumwith(p.minus()); midle.amplifie(0.5f);
			float os = (float)Math.sqrt(o.s*o.s), cs = (float)Math.sqrt(s*s);
			midle.setamplitude(os/cs);
			return midle;
		}
		//public boolean iscollidedS2S(Entity o) { }
		public boolean isBoundsCollided(BoundingBox obb) {
			BoundingBox bb = new BoundingBox(p,s*2.f); return bb.iscollided(obb);
		}
		Entity() { p=new vec2f(); v=new vec2f(); s=1.f;}
		Entity(vec2f position) { p=position; v=new vec2f(); s=1.f; }
	}
	
	static public abstract class Entities{
		ArrayList<Entity> instances = new ArrayList<Entity>();
		public HailedEngine ow; public BufferedImage asset;
		Entities(HailedEngine owner) { ow=owner; }
		
		public abstract void update(/*float dtime*/);
		public void drawto(Graphics2D target) {
			for(int i=0;i!=instances.size();i++) { Entity c = instances.get(i);
				vec2f s = c.getDS(ow);vec2f e = c.getDE(ow);
				target.drawImage(asset, (int)s.x, (int)s.y, (int)e.x, (int)e.y, 0, 0, ow.unitsize, ow.unitsize, ow);
			}// place holder
		}
		public void add(Entity ninstance) { instances.add(ninstance); }
	}
	
	
}
