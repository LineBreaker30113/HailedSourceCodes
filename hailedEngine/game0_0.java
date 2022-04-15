package hailedEngine;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import hailedEngine.HailedEngine.*;

@SuppressWarnings("serial")
public class game0_0 extends JFrame implements KeyListener, MouseListener {
	public boolean isWd=false, isAd=false, isSd=false, isDd=false, isMd=false;
	public boolean ipml=false, ipmr=false, ipmu=false, ipmd=false, issa=true;
	public short enmiesIndex = 0, xplsionsIndex = 1, bultsIndex = 2;
	public float bultsped = 6.74f, plyrsped = 3.f, enmysped = 2.8f, xplsionsped = 1.4f;
	public float enmydamge = 100.f, xplsiondamge = 35.f, xplsionhealth = 260.f;
	public short shotDestate=36, shotdelay=shotDestate, xplsionLspan = 162;
	public short enmyspawdelay=55;
	
	public HailedEngine mengine; public TimerTask gameloop;
	@Override
	public void mousePressed(MouseEvent e) {
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			if(!issa) { break; }
			Bullets.Bullet nbullet = new Bullets.Bullet();
			nbullet.p=new vec2f(mengine.player.p);
			nbullet.v=new vec2f(mengine.mapS2M(new vec2f(e.getX(), e.getY())));
			mengine.entitysets[bultsIndex].add(nbullet);
			onAction(); shotdelay=shotDestate; issa = false; break;
		}

	}
	@Override
	public void mouseReleased(MouseEvent e) {

	}
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W: isWd = true; break;
		case KeyEvent.VK_A: isAd = true; break;
		case KeyEvent.VK_S: isSd = true; break;
		case KeyEvent.VK_D: isDd = true; break;
		}
		onAction();
	}
	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W: isWd = false; break;
		case KeyEvent.VK_A: isAd = false; break;
		case KeyEvent.VK_S: isSd = false; break;
		case KeyEvent.VK_D: isDd = false; break;
		}
		onAction();
	}
	public void onAction() {
		ipml=isAd&&!isDd;ipmr=isDd&&!isAd; ipmu=isWd&&!isSd;ipmd=isSd&&!isWd;
		if(ipml) { mengine.player.v.x=-1.f;
		} else if(ipmr) { mengine.player.v.x=+1.f;
		} else { mengine.player.v.x=0.f; }
		if(ipmu) { mengine.player.v.y=-1.f; 
		} else if(ipmd) { mengine.player.v.y=+1.f;
		} else { mengine.player.v.y=0.f; }
		if(ipmd||ipmu||ipmr||ipml) {mengine.player.v.setamplitude(plyrsped*mengine.unitsize/mengine.frameRate); }
	}
	
	public void initialize() {
		mengine.addKeyListener(this);mengine.addMouseListener(this);
		this.addKeyListener(this);this.addMouseListener(this);
		super.add(mengine); super.pack();
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setLocationRelativeTo(null);
		super.setVisible(true);
		mengine.entitysets[enmiesIndex] = new Enemies(mengine, this);
		mengine.entitysets[xplsionsIndex] = new Explosions(mengine, this);
		mengine.entitysets[bultsIndex] = new Bullets(mengine, this);
		try {
			mengine.entitysets[enmiesIndex].asset = ImageIO.read(new File("Enemy.png"));
			mengine.entitysets[xplsionsIndex].asset = ImageIO.read(new File("Explosion.png"));
			mengine.entitysets[bultsIndex].asset = ImageIO.read(new File("Bullet.png"));
			mengine.plyrskin = ImageIO.read(new File("Player.png"));
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void Gupdate() {
		if(shotdelay!=0) { shotdelay--; } else { issa=true; }
	}
	public void start() {
		game0_0 owg = this;
		gameloop = new TimerTask() {
			public void run() {
				owg.Gupdate();
				mengine.HEupdate();
			}
		};
		mengine.GLtimer.scheduleAtFixedRate(gameloop, 0, mengine.frameDelay);
	}
	public static void main(String[] args) {
		game0_0 mw = new game0_0(); mw.mengine = new HailedEngine(); mw.mengine.initialize(); mw.initialize();
		mw.start();
		
		
	}
	
	public static class Enemies extends HailedEngine.Entities {
		public game0_0 owg;
		public short spawncounter, current=0;

		Enemies(HailedEngine owner, game0_0 ownergame) { super(owner); owg=ownergame; spawncounter=owg.enmyspawdelay; }

		@Override
		public void update() {
			if(spawncounter==0) {
				spawncounter=owg.enmyspawdelay;
				Entity ne = new Entity(ow.player.p.sumwith(300.f, 400.f));
				instances.add(ne);
			} else { spawncounter--; }
			if(current<instances.size()) {
				instances.get(current).target2(ow.player.p, owg.enmysped*ow.unitsize/ow.frameRate); current++;
			} else { current = 0; }
			for(int i=0;i!=instances.size();i++) { Entity ce = instances.get(i);
				ce.p.increase(ce.v);
				if(ce.h<0.f) { instances.remove(i); }
				if(ce.iscollidedC2C(ow, ow.player)) {
					ow.player.h-= owg.enmydamge/((ce.p.distanceto(ow.player.p)+1)*ow.frameRate);
					System.out.print("player's health: ");System.out.println(ow.player.h);
				}
			}
		}
		
	}
	public static class Explosions extends HailedEngine.Entities {
		public game0_0 owg;
		public static class Explosion extends Entity { public short r; Explosion(game0_0 owg) { r=owg.xplsionLspan; } }
		Explosions(HailedEngine owner, game0_0 ownergame) { super(owner); owg=ownergame; }

		public void add(Explosion ninstance) { instances.add(ninstance); ninstance.h=owg.xplsionhealth; }
		@Override
		public void update() {
			
			for(int i =0;i!=instances.size();i++) { Explosion ce = (Explosion) instances.get(i);
				if(ce.r==0) { instances.remove(i); i--; } else { ce.r--; }
				ce.s=(float) Math.sin((((float)ce.r)/(float)owg.xplsionLspan)*Math.PI)+0.5f;
				for(int ti =0;ti!=ow.entitysets[owg.enmiesIndex].instances.size();ti++) {
					Entity tar = ow.entitysets[owg.enmiesIndex].instances.get(ti);
					if(tar.iscollidedC2C(ow, ce)) {
						 float damge = owg.xplsiondamge/((ce.p.distanceto(ce.p)+1)*ow.frameRate);
						 ce.h-=damge;tar.h-=damge;
					}
				}
			}
			
		}
		
	}
	public static class Bullets extends HailedEngine.Entities {
		public static class Bullet extends Entity { public float t=0.f, d; }
		public game0_0 owg;
		Bullets(HailedEngine owner, game0_0 ownergame) { super(owner); owg=ownergame; }
		@Override
		public void add(Entity ninstance) {
			Bullet current = (Bullet) ninstance;
			current.d=ninstance.v.sumwith(ninstance.p.minus()).getamplitude();
			ninstance.target2(ninstance.v, owg.bultsped * ow.getUsize() / ow.frameRate);
			instances.add(ninstance);
		}
		@Override
		public void update() {
			for(int i =0;i!=instances.size();i++) {
				Bullet cb = (Bullet) instances.get(i);
				cb.p.increase(cb.v); cb.t += owg.bultsped * ow.unitsize / ow.frameRate;
				cb.s=(float) Math.sin((cb.t/cb.d)*Math.PI*0.9f+0.05f)+0.5f;
				if(cb.d<=cb.t) {
					Entity nex = new Explosions.Explosion(owg);
					nex.p=cb.p;nex.v=cb.v;
					cb.v.setamplitude(owg.xplsionsped*ow.getUsize()/ow.frameRate);
					ow.entitysets[owg.xplsionsIndex].add(nex);
					instances.remove(cb); i--;
				}
			}
		}
		/*@Override
		public void drawto(Graphics2D target) {
			
		}*/
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	@Override
	public void keyTyped(KeyEvent e) {

	}
	@Override
	public void mouseEntered(MouseEvent e) {

	}
	@Override
	public void mouseExited(MouseEvent e) {

	}
}
