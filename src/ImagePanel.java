import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Image Panel class exxtending JPanel is a panel which contains the jumbled up image, this class implements mouselistener and the method 
 * for swapping the tiles. It also checks id the game has ended sending a final message
 * @author suyashlohia
 *
 */
public class ImagePanel extends JPanel implements MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame jf;
	private ArrayList<JButton> ImgBtns = new ArrayList<JButton>();
	
	/**
	 * Static public boolean variable to communicate with other files 
	 */
	public static boolean updateflag=false;
	
	private JButton Button1;
	private JButton Button2;
	
	/**
	 * Constructor function initialising the values of the panel
	 * @param f, object of Jframe represeting the entire puzzle 
	 * @param b, arraylist of buttons
	 * @param s, arralyist of points
	 */
	public ImagePanel(JFrame f,ArrayList<JButton> b) {
		this.jf=f;
		this.ImgBtns=b;

		this.setLayout(new GridLayout (10,10));
		this.setPreferredSize(new Dimension(700,700));
		addMouseListener(this);
		
	}
	/**
	 * Method function to change the boolean value of static variable to false
	 */
	public static void changeflag() {
		updateflag=false;
	}
	/**
	 * Method function to change the boolean value of static variable to false
	 */
	public static void changeflagtrue() {
		updateflag=true;
	}

	
	/**
	 *MouseListener to get the index of the second button
	 */
	public void mouseEntered(MouseEvent e) {
		Button2 = (JButton) e.getSource();
	}
	/**
	 *MouseListener to get the index of the first button
	 */
	public void mousePressed(MouseEvent e) {
		Button1 = (JButton) e.getSource();
	}
	
	/**
	 *MouseListener to swap the two buttons once the mouse has been released
	 */
	public void mouseReleased(MouseEvent e) {
		int i2 = ImgBtns.indexOf(Button2);
		int i1 = ImgBtns.indexOf(Button1);
		Collections.swap(ImgBtns, i1, i2);
		
		this.removeAll();
		for(JButton j: ImgBtns) {
			this.add(j);
		}
		this.validate();
//		EndOfGame();
		updateflag=true;
	}
	/**
	 * An empty mouselistener added because of package import and implemention
	 */
	public void mouseClicked(MouseEvent e) {}
	/**
	 * An empty mouselistener added because of package import and implemention
	 */
	public void mouseExited(MouseEvent e) {}
	



}
	

