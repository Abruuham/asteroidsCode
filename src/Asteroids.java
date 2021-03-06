import java.awt.*;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.Random;
import java.time.LocalTime;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;



public class Asteroids
{
	public Asteroids(){
		setup();
	}


	public static void setup()
	{


		appFrame = new JFrame("Asteroids");
		XOFFSET = 0;
		YOFFSET = 40;
		WINWIDTH = 976;
		WINHEIGHT = 800;
		pi = 3.14159265358979;
		twoPi = 2.0 * 3.14159265358979;
		endgame = false;
		p1Width = 25; //18.5
		p1Height = 25; //25
		p1originalX = 46; // 0 + 488 - 12.5 = 475.5
		p1originalY = (double)YOFFSET + ((double)WINHEIGHT / 2.0) - (p1Height / 2.0) + 100;
		playerBullets = new Vector<ImageObject> ();
		playerBulletsTimes = new Vector<Long> ();
		bulletWidth = 15;
		playerBulletLifetime = new Long(1600);
		enemyBulletLifetime = new Long(1600);
		explosionLifetime = new Long(800);
		playerBulletGap = 1;
		flameCount = 1;
		flameWidth = 12.0;
		expcount = 1;
		level = 1;
		asteroids = new Vector<ImageObject> ();
		asteroidsTypes = new Vector<Integer> ();
		ast1width = 32;
		ast2width = 21;
		ast3width = 26;
		try
		{
			background = ImageIO.read(new File("track.png"));
			player = ImageIO.read(new File("mcqueen.png"));
			flames1 = ImageIO.read(new File("flameleft.png"));
			flames2 = ImageIO.read(new File("flamecenter.png"));
			flames3 = ImageIO.read(new File("flameright.png"));
			flames4 = ImageIO.read(new File("blueflameleft.png"));
			flames5 = ImageIO.read(new File("blueflamecenter.png"));
			flames6 = ImageIO.read(new File("blueflameright.png"));
			ast1 = ImageIO.read(new File("ast1.png"));
			ast2 = ImageIO.read(new File("ast2.png"));
			ast3 = ImageIO.read(new File("ast3.png"));
			playerBullet = ImageIO.read(new File("bullet.png"));
			enemyShip = ImageIO.read(new File("mater.png"));
			enemyBullet = ImageIO.read(new File("bullet.png"));
			exp1 = ImageIO.read(new File("explosion1.png"));
			exp2 = ImageIO.read(new File("explosion2.png"));

		}
		catch(IOException ioe)
		{
			System.out.println("caught" + ioe);
		}



	}

	private static class Animate implements Runnable
	{
		public void run()
		{
			while(endgame == false)
			{
				backgroundDraw();
				explosionsDraw();
				enemyBulletsDraw();
				enemyDraw();//
				playerBulletsDraw();
				playerDraw();

				try
				{
					Thread.sleep(32);
				}
				catch(InterruptedException e)
				{

				}
			}
		}
	}

	private static void insertPlayerBullet()
	{
		ImageObject bullet = new ImageObject(0, 0, bulletWidth, bulletWidth, p1.getAngle());
		lockrotateObjAroundObjtop(bullet, p1, p1Width / 2.0);
		playerBullets.addElement(bullet);
		playerBulletsTimes.addElement(System.currentTimeMillis());
	}

	private static void insertEnemyBullet()
	{
		ImageObject bullet = new ImageObject(0, 0, bulletWidth, bulletWidth, enemy.getAngle());
		lockrotateObjAroundObjtop(bullet, enemy, p1Width / 2.0);
		enemyBullets.addElement(bullet);
		enemyBulletsTimes.addElement(System.currentTimeMillis());
	}


	private static class PlayerMover implements Runnable
	{
		public PlayerMover()
		{
			velocitystep = 0.01;
			rotatestep = 0.01;

		}

		public void run()
		{
			while(endgame == false)
			{
				try
				{
					Thread.sleep(10);
				}
				catch(InterruptedException e)
				{

				}

				if(p1.getX() <= 2 || p1.getX() >= 680 || p1.getY() <= 45 || p1.getY() >= 700){
					p1velocity = 0;
					if (upPressed == true) {
						p1velocity = p1velocity + velocitystep;
					}
					if (downPressed == true) {
						p1velocity = p1velocity - velocitystep;
					}
					if (leftPressed == true) {
						if (p1velocity < 0) {
							p1.rotate(-rotatestep);
						} else {
							p1.rotate(rotatestep);
						}
					}
					if (rightPressed == true) {
						if (p1velocity < 0 || collisionOccurs(p1, enemy)) {
							p1.rotate(rotatestep);
						} else {
							p1.rotate(-rotatestep);
						}

					}

				}

					if (upPressed == true) {
						p1velocity = p1velocity + velocitystep;
					}
					if (downPressed == true) {
						p1velocity = p1velocity - velocitystep;
					}
					if (leftPressed == true) {
						if (p1velocity < 0) {
							p1.rotate(-rotatestep);
						} else {
							p1.rotate(rotatestep);
						}
					}
					if (rightPressed == true) {
						if (p1velocity < 0 ) {
							p1.rotate(rotatestep);
						} else {
							p1.rotate(-rotatestep);
						}

					}

				if(firePressed == true)
				{
					try
					{
						if(playerBullets.size() == 0)
						{
							insertPlayerBullet();
						}
						else if(System.currentTimeMillis() - playerBulletsTimes.elementAt(playerBulletsTimes.size() - 1) >
								playerBulletLifetime / 4.0)
						{
							insertPlayerBullet();
						}
					}
					catch(java.lang.ArrayIndexOutOfBoundsException aioobe)
					{

					}
				}


				p1.move(-p1velocity * Math.cos(p1.getAngle() - pi / 2.0), p1velocity * Math.sin(p1.getAngle() - pi / 2.0));
				p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
			}
		}
		private double velocitystep;
		private double rotatestep;
	}

	private static class PlayerBulletsMover implements Runnable
	{
		public PlayerBulletsMover()
		{
			velocity = 1.0;
		}
		public void run()
		{
			while(endgame == false)
			{
				try
				{
					Thread.sleep(4);
				}
				catch (InterruptedException e)
				{

				}
				try
				{
					for(int i = 0; i < playerBullets.size(); i++)
					{
						playerBullets.elementAt(i).move(-velocity * Math.cos(playerBullets.elementAt(i).getAngle() - pi / 2.0),
								velocity * Math.sin(playerBullets.elementAt(i).getAngle() - pi / 2.0));
						playerBullets.elementAt(i).screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);

						if(System.currentTimeMillis() - playerBulletsTimes.elementAt(i) > playerBulletLifetime)
						{
							playerBullets.remove(i);
							playerBulletsTimes.remove(i);
						}

					}
				}
				catch(java.lang.ArrayIndexOutOfBoundsException aie)
				{
					playerBullets.clear();
					playerBulletsTimes.clear();
				}
			}
		}
		private double velocity;
	}

	private static class EnemyShipMover implements Runnable
	{
		public EnemyShipMover()
		{
			velocitystep = 0.01;
			p2rotateStep = 0.01;
		}

		public void run()
		{
			while(endgame == false)
			{
				try
				{
					Thread.sleep(10);
				}
				catch(InterruptedException e)
				{

				}
				if(enemy.getX() <= 2 || enemy.getX() >= 680 || enemy.getY() <= 45 || enemy.getY() >= 700) {
					p2velocity = 0;
					if(wPressed == true)
					{
						p2velocity = p2velocity + velocitystep;
					}
					if(sPressed == true)
					{
						p2velocity = p2velocity - velocitystep;
					}
					if(aPressed == true)
					{
						if(p2velocity < 0)
						{
							enemy.rotate(-p2rotateStep);
						}
						else
						{
							enemy.rotate(p2rotateStep);
						}
					}
					if(dPressed == true)
					{
						if(p2velocity < 0)
						{
							enemy.rotate(p2rotateStep);
						}
						else
						{
							enemy.rotate(-p2rotateStep);
						}
					}
				}

				if(wPressed == true)
				{
					p2velocity = p2velocity + velocitystep;
				}
				if(sPressed == true)
				{
					p2velocity = p2velocity - velocitystep;
				}
				if(aPressed == true)
				{
					if(p2velocity < 0)
					{
						enemy.rotate(-p2rotateStep);
					}
					else
					{
						enemy.rotate(p2rotateStep);
					}
				}
				if(dPressed == true)
				{
					if(p2velocity < 0)
					{
						enemy.rotate(p2rotateStep);
					}
					else
					{
						enemy.rotate(-p2rotateStep);
					}
				}
				if(enemyFire == true)
				{
					try
					{
						if(enemyBullets.size() == 0)
						{
							insertEnemyBullet();
						}
						else if(System.currentTimeMillis() - enemyBulletsTimes.elementAt(enemyBulletsTimes.size() - 1) >
								enemyBulletLifetime / 4.0)
						{
							insertEnemyBullet();
						}
					}
					catch(java.lang.ArrayIndexOutOfBoundsException aioobe)
					{

					}
				}


				enemy.move(-p2velocity * Math.cos(enemy.getAngle() - pi / 2.0), p2velocity * Math.sin(enemy.getAngle() - pi / 2.0));
				enemy.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
			}
		}
		private double velocitystep;
		private double p2rotateStep;

//		public EnemyShipMover()
//		{
//			velocity = 1.0;
//		}
//		public void run()
//		{
//			while(endgame == false )//&& enemyAlive == true)
//			{
//				try
//				{
//					Thread.sleep(10);
//				}
//				catch(InterruptedException e)
//				{
//
//				}
//				try
//				{
//					enemy.move(-velocity * Math.cos(enemy.getAngle() - pi / 2.0),
//							velocity * Math.sin(enemy.getAngle() - pi / 2.0));
//					enemy.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
//				}
//				catch(java.lang.NullPointerException jlnpe)
//				{
//
//				}
//				try
//				{
//					if(enemyAlive == true)
//					{
//						if(enemyBullets.size() == 0)
//						{
//							//insertEnemyBullet();
//						}
//						else if(System.currentTimeMillis() - enemyBulletsTimes.elementAt(enemyBulletsTimes.size() - 1) > enemyBulletLifetime / 4.0)
//						{
//							//insertEnemyBullet();
//						}
//					}
//				}
//				catch(java.lang.ArrayIndexOutOfBoundsException aioobe)
//				{
//
//				}
//			}
//		}
//		private double velocity;
	}

	private static class EnemyBulletsMover implements Runnable
	{
		public EnemyBulletsMover()
		{
			velocity = 1.2;
		}
		public void run()
		{
			while(endgame == false && enemyAlive == true)
			{
				try
				{
					Thread.sleep(4);

				}
				catch(InterruptedException e)
				{

				}
				try
				{
					for(int i = 0; i < enemyBullets.size(); i++)
					{
						enemyBullets.elementAt(i).move(-velocity * Math.cos(enemyBullets.elementAt(i).getAngle() - pi / 2.0),
								velocity * Math.sin(enemyBullets.elementAt(i).getAngle() - pi / 2.0));
						enemyBullets.elementAt(i).screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
						if(System.currentTimeMillis() - enemyBulletsTimes.elementAt(i) > enemyBulletLifetime)
						{
							enemyBullets.remove(i);
							enemyBulletsTimes.remove(i);
						}
					}
				}
				catch(java.lang.ArrayIndexOutOfBoundsException aie)
				{
					enemyBullets.clear();
					enemyBulletsTimes.clear();
				}
			}
		}
		private double velocity;
	}

	private static class CollisionChecker implements Runnable
	{
		public void run()
		{
			Random randomNumbers = new Random(LocalTime.now().getNano());
			while(endgame == false)
			{
				try
				{
					try
					{
						for(int i = 0; i < walls.size(); i++){
							if(collisionOccurs(walls.elementAt(i), p1) == true){
								p1velocity = 0;
							}
							if(collisionOccurs(walls.elementAt(i), enemy) == true){
								p2velocity = 0;
							}
						}


						for(int i = 0; i < playerBullets.size(); i++)
						{
							if(collisionOccurs(playerBullets.elementAt(i), enemy) == true)
							{
								double posX = enemy.getX();
								double posY = enemy.getY();

								playerBullets.remove(i);
								playerBulletsTimes.remove(i);

							}
						}
//
						if(collisionOccurs(enemy, p1) == true)
						{
							//endgame = true;
							//System.out.println("Collision with enemy ");
						}
						for(int i = 0; i < enemyBullets.size(); i++)
						{

							if(collisionOccurs(enemyBullets.elementAt(i), p1) == true)
							{

								System.out.println("Collision with enemy bullet");
								endgame = true;
								System.out.println("Game Over You Lose 4");
							}
						}
					}
					catch(java.lang.NullPointerException jlnpe)
					{

					}
				}
				catch(java.lang.ArrayIndexOutOfBoundsException jlaioobe)
				{

				}
			}
		}
	}

	private static class WinChecker implements Runnable
	{
		public void run(){
//			while(endgame == false){
//				if(asteroids.size() == 0)
//				{
//					endgame = true;
//					System.out.println("Game Over You Win");
//				}
//			}
		}
	}

	private static void generateAsteroids()
	{
		walls = new Vector<ImageObject>();



		walls.addElement(new ImageObject(48, 312, 112, 135, 0));
		walls.addElement(new ImageObject(210, 312, 180, 150, 0));
		walls.addElement(new ImageObject(437, 312, 250, 97, 0));
		walls.addElement(new ImageObject(93, 495, 100, 107,0));
		walls.addElement(new ImageObject(317, 656, 75, 90,0));
		walls.addElement(new ImageObject(490, 507, 230, 190,0));
		walls.addElement(new ImageObject(317, 560, 128, 49,0));
		walls.addElement(new ImageObject(400, 709, 85, 30,0));
		walls.addElement(new ImageObject(210, 742, 100, 30,0));
		walls.addElement(new ImageObject(234, 646, 36, 50,0));
		walls.addElement(new ImageObject(252, 470, 36, 90,0));
		walls.addElement(new ImageObject(0, 409, 44, 328,0));
		walls.addElement(new ImageObject(692, 400, 55, 100, 0));
		walls.addElement(new ImageObject(370, 455, 276, 6,0));
		walls.addElement(new ImageObject(336, 508, 180, 6,0));
		walls.addElement(new ImageObject(38, 650, 89, 128,0));
		walls.addElement(new ImageObject(152, 304, 297, 161, 0));
		walls.addElement(new ImageObject(300, 161, 451, 307, 0));
		walls.addElement(new ImageObject(210, 317, 300, 215, 0));
		walls.addElement(new ImageObject(300, 215, 387, 310, 0));
		walls.addElement(new ImageObject(123, 649, 205, 739, 0));
		walls.addElement(new ImageObject(123, 590, 234, 694, 0));


//		asteroids = new Vector<ImageObject>();
//		asteroidsTypes = new Vector<Integer>();
//		Random randomNumbers = new Random(LocalTime.now().getNano());
//
//		for(int i = 0; i < level; i++){
//			asteroids.addElement(new ImageObject(XOFFSET + (double)(randomNumbers.nextInt(WINWIDTH)),
//					YOFFSET + (double)(randomNumbers.nextInt(WINHEIGHT)), ast1width, ast1width,
//					(double)(randomNumbers.nextInt(360))));
//			asteroidsTypes.addElement(1);
//		}
	}

	private static void generateEnemy()
	{
		try
		{
//			Random randomNumbers = new Random(LocalTime.now().getNano());
//			enemy = new ImageObject(XOFFSET + (double)(randomNumbers.nextInt(WINWIDTH)),
//					YOFFSET + (double)(randomNumbers.nextInt(WINHEIGHT)), 29.0, 16.0,
//					(double)(randomNumbers.nextInt(360)));
			enemy = new ImageObject(66, p1originalY, p1Width, p1Height, 0.0);
		}
		catch(java.lang.IllegalArgumentException jliae)
		{

		}
	}


	private static void lockrotateObjAroundObjbottom(ImageObject objOuter, ImageObject objInner, double dist)
	{
		objOuter.moveto(objInner.getX() + (dist + objInner.getWidth() / 2.0) *
				Math.cos(-objInner.getAngle() + pi / 2.0) + objOuter.getWidth() / 2.0,
				objInner.getY()
				+ (dist + objInner.getHeight() / 2.0) * Math.sin(-objInner.getAngle() + pi / 2.0) + objOuter.getHeight() / 2.0);
		objOuter.setAngle(objInner.getAngle());
	}

	private static void lockrotateObjAroundObjtop(ImageObject objOuter, ImageObject objInner, double dist)
	{
		objOuter.moveto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 + (dist + objInner.getWidth() / 2.0)
				* Math.cos(objInner.getAngle() + pi / 2.0)) / 2.0, objInner.getY() - objOuter.getHeight() + (dist + objInner.getHeight() / 2.0) *
				Math.sin(objInner.getAngle() / 2.0));
		objOuter.setAngle(objInner.getAngle());
	}

	private static AffineTransformOp rotateImageObject(ImageObject obj)
	{
		AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(),
				obj.getWidth() / 2.0, obj.getHeight() / 2.0);
		AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return atop;
	}

	private static AffineTransformOp spinImageObject(ImageObject obj){
		AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(), obj.getWidth() / 2.0,
				obj.getHeight() / 2.0);
		AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		return atop;
	}

	private static void backgroundDraw()
	{
		Graphics g = appFrame.getGraphics();
		Graphics2D g2D = (Graphics2D)g;
		g2D.drawImage(background, XOFFSET, YOFFSET, null);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		DecimalFormat df = new DecimalFormat("#.##");

		g2D.drawString("M.P.H: " + df.format(Math.abs(p1velocity*10)),175,100);
		g2D.drawString("M.P.H: " + df.format(Math.abs(p2velocity*10)),545,100);

	}

	private static void enemyBulletsDraw()
	{
		Graphics g = appFrame.getGraphics();
		Graphics2D g2D = (Graphics2D)g;
		for(int i = 0; i < enemyBullets.size(); i++)
		{
			g2D.drawImage(enemyBullet, (int)(enemyBullets.elementAt(i).getX() + 0.5),
					(int)(enemyBullets.elementAt(i).getY() + 0.5), null);
		}
	}

	public static void enemyDraw()
	{	Graphics g = appFrame.getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(rotateImageObject(enemy).filter(enemyShip, null), (int)(enemy.getX() + 0.5), (int)(enemy.getY() + 0.5), null);
	}

	private static void playerBulletsDraw()
	{
		Graphics g = appFrame.getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		try
		{
			for(int i = 0;i < playerBullets.size(); i++)
			{
				g2D.drawImage(rotateImageObject(playerBullets.elementAt(i)).filter(playerBullet, null),
						(int)(playerBullets.elementAt(i).getX() + 0.5), (int)(playerBullets.elementAt(i).getY() + 0.5), null);
			}
		}
		catch(java.lang.ArrayIndexOutOfBoundsException aioobe)
		{
			playerBullets.clear();
			playerBulletsTimes.clear();
		}
	}

	public static void playerDraw()
	{
		Graphics g = appFrame.getGraphics();
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(rotateImageObject(p1).filter(player, null), (int)(p1.getX()), (int)(p1.getY()), null);
	}

	private static void explosionsDraw()
	{

		Graphics g = appFrame.getGraphics();
		Graphics2D g2D = (Graphics2D)g;
		for(int i = 0; i < explosions.size(); i++)
		{
			if(System.currentTimeMillis() - explosionsTimes.elementAt(i) > explosionLifetime)
			{
				try
				{
					explosions.remove(i);
					explosionsTimes.remove(i);
				}
				catch(java.lang.NullPointerException jlnpe)
				{
					explosions.clear();
					explosionsTimes.clear();
				}
			}
			else
			{
				if(expcount == 1)
				{
					g2D.drawImage(exp1,(int)(explosions.elementAt(i).getX() + 0.5),
							(int)(explosions.elementAt(i).getY() + 0.5), null);
					expcount = 2;
				}
				else if(expcount == 2)
				{
					g2D.drawImage(exp2,(int)(explosions.elementAt(i).getX() + 0.5),
							(int)(explosions.elementAt(i).getY() + 0.5), null);
					expcount = 1;
				}
			}
		}
	}

	public static class KeyPressed extends AbstractAction
	{
		public KeyPressed()
		{
			action = "";
		}
		public KeyPressed(String input)
		{
			action = input;
		}
		public void actionPerformed(ActionEvent e)
		{
			if(action.equals("UP"))
			{
				upPressed = true;
			}
			if(action.equals("DOWN"))
			{
				downPressed = true;
			}
			if(action.equals("LEFT"))
			{
				leftPressed = true;
			}
			if(action.equals("RIGHT"))
			{
				rightPressed = true;
			}
			if(action.equals("F"))
			{
				firePressed = true;
			}
			if(action.equals("W")){
				wPressed = true;
			}
			if(action.equals("A")){
				aPressed = true;
			}
			if(action.equals("S")){
				sPressed = true;
			}
			if(action.equals("D")){
				dPressed = true;
			}
			if(action.equals("E")){
				enemyFire = true;
			}
		}
		private String action;
	}

	private static class KeyReleased extends AbstractAction
	{
		public KeyReleased()
		{
			action = "";
		}
		public KeyReleased(String input)
		{
			action = input;
		}

		public void actionPerformed(ActionEvent e)
		{
			if(action.equals("UP"))
			{
				upPressed = false;
			}
			if(action.equals("DOWN"))
			{
				downPressed = false;
			}
			if(action.equals("LEFT"))
			{
				leftPressed = false;
			}
			if(action.equals("RIGHT"))
			{
				rightPressed = false;
			}
			if(action.equals("F"))
			{
				firePressed = false;
			}
			if(action.equals("W")){
				wPressed = false;
			}
			if(action.equals("A")){
				aPressed = false;
			}
			if(action.equals("S")){
				sPressed = false;
			}
			if(action.equals("D")){
				dPressed = false;
			}
			if(action.equals("E")){
				enemyFire = false;
			}
		}
		private String action;

	}

	private static class QuitGame implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			endgame = true;
			didQuit = true;
		}
	}

	private static class StartGame implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			didQuit = false;
			endgame = true;
			enemyAlive = true;
			upPressed = false;
			downPressed = false;
			leftPressed = false;
			rightPressed = false;
			firePressed = false;
			aPressed = false;
			sPressed = false;
			dPressed = false;
			wPressed = false;
			enemyFire = false;
			p1 = new ImageObject(p1originalX, p1originalY, p1Width, p1Height, 0.0);
			p1velocity = 0.0;
			p2velocity = 0.0;
			generateEnemy();
			flames = new ImageObject(p1originalX + p1Width / 2.0, p1originalY + p1Height, flameWidth, flameWidth, 0.0);
			flameCount = 1;
			expcount = 1;
			try
			{
				Thread.sleep(50);
			}
			catch(InterruptedException ie)
			{

			}
			try {
				playSound("music.wav");
			} catch (LineUnavailableException lineUnavailableException) {
				lineUnavailableException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (UnsupportedAudioFileException unsupportedAudioFileException) {
				unsupportedAudioFileException.printStackTrace();
			}

			playerBullets = new Vector<ImageObject>();
			playerBulletsTimes = new Vector<Long>();
			enemyBullets = new Vector<ImageObject>();
			enemyBulletsTimes = new Vector<Long>();
			explosions = new Vector<ImageObject>();
			explosionsTimes = new Vector<Long>();
			generateAsteroids();
			endgame = false;
			Thread t1 = new Thread(new Animate());
			Thread t2 = new Thread(new PlayerMover());

			Thread t5 = new Thread(new PlayerBulletsMover());
			Thread t6 = new Thread(new EnemyShipMover());
			Thread t7 = new Thread(new EnemyBulletsMover());
			Thread t8 = new Thread(new CollisionChecker());
			Thread t9 = new Thread(new WinChecker());
			t1.start();
			t2.start();
			t5.start();
			t6.start();
			t7.start();
			t8.start();
			t9.start();
		}
	}

	private static void playSound(String soundFile) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		File f = new File(soundFile);
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
		Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volume.setValue(-1 * 40);
		clip.start();



	}


	private static class GameLevel implements ActionListener
	{
		public int decodeLevel(String input)
		{
			int ret = 3;
			if(input.equals("One"))
			{
				ret = 1;
			}
			else if(input.equals("Two"))
			{
				ret = 2;
			}
			else if(input.equals("Three"))
			{
				ret = 3;
			}
			else if(input.equals("Four"))
			{
				ret = 4;
			}
			else if(input.equals("Five"))
			{
				ret = 5;
			}
			else if(input.equals("Six"))
			{
				ret = 6;
			}
			else if(input.equals("Seven"))
			{
				ret = 7;
			}
			else if(input.equals("Eight"))
			{
				ret = 8;
			}
			else if(input.equals("Nine"))
			{
				ret = 9;
			}
			else if(input.equals("Ten"))
			{
				ret = 10;
			}
			return ret;
		}
		public void actionPerformed(ActionEvent e)
		{
			JComboBox cb = (JComboBox)e.getSource();
			String textLevel = (String)cb.getSelectedItem();
			level = decodeLevel(textLevel);
		}
	}

	private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2)
	{
		Boolean ret = false;
		if(p1x > p2x1 && p1x < p2x2)
		{
			if(p1y > p2y1 && p1y < p2y2)
			{
				ret = true;
			}
			if(p1y > p2y2 && p1y < p2y1)
			{
				ret = true;
			}
		}
		if(p1x > p2x2 && p1x < p2x1)
		{
			if(p1y > p2y1 && p1y < p2y2)
			{
				ret = true;
			}
			if(p1y > p2y2 && p1y < p2y1)
			{
				ret = true;
			}
		}
		return ret;

	}

	private static Boolean collisionOccursCoordinates(double p1x1, double p1y1, double p1x2, double p1y2, double p2x1, double p2y1, double  p2x2, double p2y2)
	{
		Boolean ret = false;
		if(isInside(p1x1, p1y1, p2x1, p2y1, p2x2, p2y2) == true)
		{
			ret = true;
		}
		if(isInside(p1x1, p1y2, p2x1, p2y1, p2x2, p2y2) == true)
		{
			ret = true;
		}
		if(isInside(p1x2, p1y1, p2x1, p2y1, p2x2, p2y2) == true)
		{
			ret = true;
		}
		if(isInside(p1x2, p1y2, p2x1, p2y1, p2x2, p2y2) == true)
		{
			ret = true;
		}
		if(isInside(p2x1, p2y1, p1x1, p1y1, p1x2, p1y2) == true)
		{
			ret = true;
		}
		if(isInside(p2x1, p2y2, p1x1, p1y1, p1x2, p1y2) == true)
		{
			ret = true;
		}
		if(isInside(p2x2, p2y1, p1x1, p1y1, p1x2, p1y2) == true)
		{
			ret = true;
		}
		if(isInside(p2x2, p2y2, p1x1, p1y1, p1x2, p1y2) == true)
		{
			ret = true;
		}
		return ret;

	}

	private static Boolean collisionOccurs(ImageObject obj1, ImageObject obj2)
	{
		Boolean ret = false;
		if(collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
				obj1.getY() + obj1.getHeight(),
				obj2.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
				obj2.getY() + obj2.getHeight()) == true)
		{
			ret = true;
		}
		return ret;
	}

	private static class ImageObject
	{
		public ImageObject()
		{
			bounce = false;
		}

		public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput)
		{
			this();
			x = xinput;
			y = yinput;
			xwidth = xwidthinput;
			yheight = yheightinput;
			lastposx = x;
			lastposy = y;
			angle = angleinput;
			internalangle = 0.0;
			coords = new Vector<Double>();
		}

		public double getX()
		{
			return x;
		}

		public double getY()
		{
			return y;
		}
		public double getWidth()
		{
			return xwidth;
		}
		public double getHeight()
		{
			return yheight;
		}
		public double getAngle()
		{
			return angle;
		}
		public double getInternalAngle()
		{
			return internalangle;
		}
		public void setAngle(double angleinput)
		{
			angle = angleinput;
		}
		public void setInternalAngle(double internalangleinput)
		{
			internalangle = internalangleinput;
		}

		public Vector<Double> getCoords()
		{
			return coords;
		}
		public void setCoords(Vector<Double> coordsinput)
		{
			coords = coordsinput;
			generateTriangles();
		}
		public void generateTriangles()
		{
			triangles = new Vector<Double>();
			comX = getComX();
			comY = getComY();
			for(int i = 0; i < coords.size(); i = i + 2)
			{
				triangles.addElement(coords.elementAt(i));
				triangles.addElement(coords.elementAt(i+1));
				triangles.addElement(coords.elementAt((i+2) % coords.size()));
				triangles.addElement(coords.elementAt((i+3) % coords.size()));
				triangles.addElement(comX);
				triangles.addElement(comY);
			}
		}

		public void printTriangles()
		{
			for(int i = 0; i < triangles.size(); i = i + 6)
			{
				System.out.println("p0x: " + triangles.elementAt(i) + "p0y: " + triangles.elementAt(i + 1));
				System.out.println("p1x: " + triangles.elementAt(i + 2) + "p1y: " + triangles.elementAt(i + 3));
				System.out.println("p2x: " + triangles.elementAt(i + 4) + "p2y: " + triangles.elementAt(i + 5));
			}
		}

		public double getComX()
		{
			double ret = 0;
			if(coords.size() > 0)
			{
				for(int i = 0; i < coords.size(); i = i + 2)
				{
					ret = ret + coords.elementAt(i);
				}
				ret = ret / (coords.size() / 2.0);
			}
			return ret;
		}

		public double getComY()
		{
			double ret = 0;
			if(coords.size() > 0)
			{

				for(int i = 1; i < coords.size(); i = i + 2)
				{
					ret = ret + coords.elementAt(i);
				}
				ret = ret / (coords.size() / 2.0);
			}
			return ret;
		}

		public void move(double xinput, double yinput)
		{

			x = x + xinput;
			y = y + yinput;
		}

		public void moveto(double xinput, double yinput)
		{
			x = xinput;
			y = yinput;
		}


		public void screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge)
		{
			if(x > rightEdge)
			{
				moveto(leftEdge, getY());
			}
			if(x < leftEdge)
			{
				moveto(rightEdge, getY());
			}
			if(y > bottomEdge)
			{
				moveto(getX(), topEdge);
			}
			if(y < topEdge)
			{
				moveto(getX(), bottomEdge);
			}

		}

		public void rotate(double angleinput)
		{
			angle = angle + angleinput;
			while(angle > twoPi)
			{
				angle = angle - twoPi;
			}
			while(angle < 0)
			{
				angle = angle + twoPi;
			}
		}

		public void spin(double internalangleinput)
		{
			internalangle = internalangle + internalangleinput;
			while(internalangle > twoPi)
			{
				internalangle = internalangle - twoPi;
			}
			while(internalangle < 0)
			{
				internalangle = internalangle + twoPi;
			}
		}

		private double x;
		private double y;
		private double xwidth;
		private double yheight;
		private double angle;
		private double internalangle;
		private Vector<Double> coords;
		private Vector<Double> triangles;
		private double comX;
		private double comY;
		private Boolean bounce;
		private double lastposx;
		private double lastposy;

	}

	private static void bindKey(JPanel myPanel, String input)
	{
		myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
		myPanel.getActionMap().put(input + " pressed" , new KeyPressed(input));
		myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
		myPanel.getActionMap().put(input + " released" , new KeyReleased(input));

	}

	public static void main(String[] args)
	{
		setup();
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setSize(735, 755);

		JPanel myPanel = new JPanel();

//		String[] levels = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};
//		JComboBox<String> levelMenu = new JComboBox<String>(levels);
//		levelMenu.setSelectedIndex(0);
//		levelMenu.addActionListener(new GameLevel());
//		myPanel.add(levelMenu);


		JButton newGameButton = new JButton("New Game");
		newGameButton.addActionListener(new StartGame());
		myPanel.add(newGameButton);

		JButton quitButton = new JButton("Quit Game");
		quitButton.addActionListener(new QuitGame());
		myPanel.add(quitButton);




		bindKey(myPanel, "UP");
		bindKey(myPanel, "DOWN");
		bindKey(myPanel, "LEFT");
		bindKey(myPanel, "RIGHT");
		bindKey(myPanel, "F");
		bindKey(myPanel, "W");
		bindKey(myPanel, "A");
		bindKey(myPanel, "S");
		bindKey(myPanel, "D");
		bindKey(myPanel, "E");


		appFrame.getContentPane().add(myPanel, "South");
		appFrame.setVisible(true);

	}

	private static Boolean endgame;
	private static Boolean enemyAlive;
	private static BufferedImage background;
	private static BufferedImage player;

	private static Boolean upPressed;
	private static Boolean downPressed;
	private static Boolean leftPressed;
	private static Boolean rightPressed;
	private static Boolean firePressed;
	private static Boolean aPressed;
	private static Boolean sPressed;
	private static Boolean dPressed;
	private static Boolean wPressed;
	private static Boolean enemyFire;


	private static ImageObject p1;
	private static double p1Width;
	private static double p1Height;
	private static double p1originalX;
	private static double p1originalY;
	private static double p1velocity;
	private static double p2velocity;
	private static double p2rotateStep;

	private static ImageObject enemy;
	private static BufferedImage enemyShip;
	private static BufferedImage enemyBullet;
	private static Vector<ImageObject> enemyBullets;
	private static Vector<Long> enemyBulletsTimes;
	private static Long enemyBulletLifetime;

	private static Vector<ImageObject> playerBullets;
	private static Vector<Long> playerBulletsTimes;
	private static double bulletWidth;
	private static BufferedImage playerBullet;
	private static Long playerBulletLifetime;
	private static double playerBulletGap;

	private static ImageObject flames;
	private static BufferedImage flames1;
	private static BufferedImage flames2;
	private static BufferedImage flames3;
	private static BufferedImage flames4;
	private static BufferedImage flames5;
	private static BufferedImage flames6;
	private static int flameCount;
	private static double flameWidth;

	private static int level;

	private static Vector<ImageObject> walls; //wallObjects
	private static Vector<ImageObject> asteroids;
	private static Vector<Integer> asteroidsTypes;
	private static BufferedImage ast1;
	private static BufferedImage ast2;
	private static BufferedImage ast3;
	private static double ast1width;
	private static double ast2width;
	private static double ast3width;

	private static Vector<ImageObject> explosions;
	private static Vector<Long> explosionsTimes;
	private static Long explosionLifetime;
	private static BufferedImage exp1;
	private static BufferedImage exp2;
	private static Boolean didQuit;

	private static int expcount;

	private static int XOFFSET;
	private static int YOFFSET;
	private static int WINWIDTH;
	private static int WINHEIGHT;

	private static double pi;
	private static double twoPi;

	private static JFrame appFrame;

	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;



}




























