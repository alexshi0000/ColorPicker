import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import javax.imageio.*;

public class ColorPicker {

	static ArrayList<CustomColor[]> all = new ArrayList<CustomColor[]>();
	static CustomColor[] swatch;
	static JFrame window;
	static int curr = 1;
	static boolean showing = false;
		//swatch keeps the Custom Colors

	public static class CustomColor{
		private Color color;
		private String R;
		private String G;
		private String B;
		private boolean locked = false;
			//if the user chooses to lock this color
		private boolean light;
			//indicates if this color is a light color or not
		private static String getHex(int d) {
    		String digits = "0123456789ABCDEF";
    		if (d <= 0) return "0";
    		int base = 16;   // flexible to change in any base under 16
    		String hex = "";
    		while (d > 0) {
       			int digit = d % base;              // rightmost digit
        		hex = digits.charAt(digit) + hex;  // string concatenation
        		d = d / base;
        	}
    		return hex;
		}

		public CustomColor(){
			color = new Color((int)(Math.random()*255-0.0001), (int)(Math.random()*255-0.0001), (int)(Math.random()*255-0.0001));
				//the color is chosen from random
			R = getHex(color.getRed());
			G = getHex(color.getGreen());
			B = getHex(color.getBlue());
			if(color.getRed() + color.getBlue() + color.getGreen() > 375)
				light = true;
			else
				light = false;
		}

		public String getHex(){
			if(R.length() < 2)
				R = "0"+R;
			if(G.length() < 2)
				G = "0"+G;
			if(B.length() < 2)
				B = "0"+B;
			return "#"+R+G+B;
		}

		public boolean isLight(){
			return light;
		}
	}

	public static void init_swatch(){
		for(int i = 0; i < 9; i++){
			if(swatch[i] != null){
				if(swatch[i].locked){
					continue;
				}
			}
			swatch[i] = new CustomColor();
		}
	}

	public static void init_window(){
		window = new JFrame();
		window.setSize(1200,400);
		window.add(new Palette());
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("\t\t\t\t\t\t\t\t\t\t\t\t\t\tPalette Generator");
		window.addMouseListener(new MouseHandler());
		window.addKeyListener(new KeyHandler());
	}

	public static class Palette extends JPanel{
		static String header = "";
		//this will be a Jpanel
		public void paintComponent(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			super.paintComponent(g);
			g.fillRect(0,0, this.getWidth(), this.getHeight());
				//the JFrame calls this panel to be drawn via paintComp
			for(int i = 0; i < 9; i++){
				g.setColor(swatch[i].color);
				g.fillRect( (int)(i * (this.getWidth()/9)), 5, (int)(this.getWidth()/9), this.getHeight());
			}

			g.setFont(new Font("Comic Sans MS",Font.PLAIN,22));

			for(int i = 0; i < 9; i++){
				if(swatch[i].locked){
					g.setColor(new Color(255,255,255));
					if(swatch[i].isLight())
						g.setColor(new Color(0,0,0));
					g.drawString(swatch[i].getHex(), (int)((i * this.getWidth()/9) + this.getWidth()/18 - 50), 350);
				} 
			}
			window_change(g);
		}
	}

	static int seconds = 100;

	public static void window_change(Graphics2D g){
		if(!Palette.header.equals("")){
			Color c = new Color(1f,1f,1f,Math.min(seconds/100.0f+0.1f,1f));
			Color b = new Color(0f,0f,0f,Math.max(seconds/100.0f-0.3f,0f));
			g.setFont(new Font("Comic Sans MS",Font.PLAIN,100));
			g.setColor(b);
			g.drawString(Palette.header,396,231);
			g.setColor(c);
			g.drawString(Palette.header,390,225);
		}
	}

	public static class MouseHandler implements MouseListener{
		public void mouseClicked(MouseEvent event){
			for(int i = 1; i <= 9; i++){
				CustomColor focus = swatch[i-1];
				if(event.getX() <= i*(window.getWidth()/9)){
					swatch[i-1].locked = !swatch[i-1].locked;
					break;
				}
			}
			window.add(new Palette());
			window.setVisible(true);
		}
		public void mousePressed(MouseEvent event){}
		public void mouseReleased(MouseEvent event){}
		public void mouseEntered(MouseEvent event){}
		public void mouseExited(MouseEvent event){}
	}

	public static class KeyHandler implements KeyListener{
		public void keyPressed(KeyEvent event){
			if(event.getKeyCode() == KeyEvent.VK_SPACE){
				init_swatch();
				window.add(new Palette());
				window.setVisible(true);
			}
			if(showing)
				return;
			else if(event.getKeyCode() == KeyEvent.VK_UP && curr > 1){
				curr--;
				swatch = all.get(curr-1);
				Palette.header = "Palette "+curr;
				window.add(new Palette()); 
				window.setVisible(true);
			}
			else if(event.getKeyCode() == KeyEvent.VK_DOWN){
				if(curr == all.size()){
					curr++;
					swatch = new CustomColor[9];
					all.add(swatch);
					init_swatch();
					Palette.header = "Palette "+curr;
					window.add(new Palette());
					window.setVisible(true);
				}
				else{
					curr++;
					swatch = all.get(curr-1);
					Palette.header = "Palette "+curr;
					window.add(new Palette());
					window.setVisible(true);
				}
			}
		}
		public void keyReleased(KeyEvent event){}
		public void keyTyped(KeyEvent event){}
	}

	public static void main(String[] args){
		swatch = new CustomColor[9];
		all.add(swatch);
		init_swatch();
		init_window();
		while(true){
			try{Thread.sleep(5);}catch(Exception e){}
			if(!Palette.header.equals("")){
				showing = true;
				if(seconds != 0 && seconds % 2 == 0){
					window.add(new Palette());
					window.setVisible(true);
				}
				seconds--;
				if(seconds == 0){
					seconds = 100;
					Palette.header = "";
					showing = false;
				}
			}
		}
	}
}