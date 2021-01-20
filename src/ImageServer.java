import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.PixelGrabber;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;





/**
 * This class represents the server of the P2P Hybrid Image Sharing Application. It contains server sockets for peers to connect and transfer image data accordingly. 
 * The class has implemented an authentication module system where it reads logiin credentials from User.tct file and then only allowd those peers to connect who 
 * already have acess. 
 * 
 * @author Suyash Lohia
 * @version 1.0
 */
public class ImageServer {
	
	//Server Variables

	private ArrayList<ActivePeer> peerlist;
	private ArrayList<ObjectOutputStream> writers = new ArrayList<>();
	private ArrayList<ObjectOutputStream> newwriters = new ArrayList<>();
	private ServerSocket ss,us;
	private ArrayList<String> usernames= new ArrayList<String>();
	private ArrayList<String> passwords= new ArrayList<String>();
	//GUI Variables 
	private JFrame jf;
	private JPanel jpbase = new JPanel();
	private JPanel jptop = new JPanel();
	private ArrayList<JButton> ImgBtns = new ArrayList<JButton>();
	private JButton LoadImageBtn;
	private Image OrgImage;	
	private ImageIcon OrgImageIcon;
	private boolean flag=true;
//	private String fpath="/Users/suyashlohia/Desktop/afakeimage.jpg";	
	private ImagePanel MyImagePanel = new ImagePanel(jf,ImgBtns);
	private String fpath="afakeimage.jpg";
//	public static boolean updateimageflag=false;
//	public static boolean addpeerflag=false;
//	public static void changeuiflag() {
//		updateimageflag=false;
//	}
//	public static void changeapflag() {
//		addpeerflag=false;
//	}
	
	/**
	 * Main method for server class, which initiates all the process
	 * @param args, striing array which is empty for this program
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ImageServer serv= new ImageServer();
		serv.go();
	}
	/**
	 * public method function go which is responsible for the main flow of the server program. 
	 * @throws IOException
	 */
	public void go() throws IOException {

		String filepath = "User.txt";
		FileInputStream FInput = new FileInputStream(new File(filepath));
		DataInputStream DIn = new DataInputStream(FInput);
		String line;
		String[] obj;
		while ((line =DIn.readLine())!= null) {
			obj= line.split(";");
			usernames.add(obj[0].substring(9));
			passwords.add(obj[1].substring(13));
		}
		System.out.println(usernames.size());
		
		try {
			ss= new ServerSocket(9000);
			us= new ServerSocket(9999);
			System.out.println("serversocket created");
			peerlist = new ArrayList<>();
			buildGUI();
			ActivePeer a = new ActivePeer("localhost",9000);
			peerlist.add(a);
			Thread newthread = new Thread(new DiffSS());
			newthread.start();
			Thread ut = new Thread(new ClientUpdateFiles());
			ut.start();
			while (true) {
				Socket s= ss.accept();
				Thread t = new Thread(new ClientHandler(s));
				t.start();
				System.out.println("connection established");
//				if(ImagePanel.updateflag==true) {
//					// update all peers
//					System.out.println("updating file for all peers");
//					ImagePanel.changeflag();
//				}
//				else {
//					System.out.print("");
//				}
			}
		}
		catch (Exception e){
			
		}
		
	}
	private class DiffSS implements Runnable{
		public void run() {
			while(true) {
				try {
					Socket snew=us.accept();
					ObjectOutputStream updateout = new ObjectOutputStream(snew.getOutputStream());
					newwriters.add(updateout);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	private class ClientUpdateFiles implements Runnable{
		public void run() {
			while(true) {
				if(ImagePanel.updateflag==true) {
//					updateimageflag=true;
					try {
						updateeverything();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("updating file for all peers");
					ImagePanel.changeflag();
				}
				else {
					System.out.print("");
				}
			}
		}

		private void updateeverything() throws IOException {
			// TODO Auto-generated method stub
			for (ObjectOutputStream ooo :newwriters) {
				
//				ooo.writeObject(activelist.get(0));
				String te="update";
				ooo.writeObject(te);
//				ooo.flush();
				for(JComponent bb : ImgBtns) {
					ooo.writeObject(bb);
				}
				ooo.flush();
//				staticflag=true;
			}
		}
		
	}
	private class ClientHandler implements Runnable{
		private Socket s;
		private ObjectOutputStream objoutstream;
		private ObjectInputStream objinstream;
		private ClientHandler(Socket soc) throws IOException {
			this.s = soc;
			objoutstream = new ObjectOutputStream(s.getOutputStream());
			objinstream = new ObjectInputStream(s.getInputStream());
			
		}
		@Override
		public void run() {
			try {
				System.out.println("reading objects");
				String IP= (String) objinstream.readObject();
				String uname = (String)objinstream.readObject();
				String upass = (String)objinstream.readObject();
				System.out.println("done");
				String ver = authenticateUser(uname, upass, IP);
//				String ver="successxxx";
				objoutstream.writeObject(ver);
				objoutstream.flush();
				System.out.println("written objects");
//				
			
				Thread.sleep(1000);
				if(ver.equals("Success")) {
					System.out.println("reaching here?");
					addActPeer();
					sendImg();
					
				}
//				}
				else {
					newwriters.remove(newwriters.size()-1);
					Thread.currentThread().interrupt();
					return;
				}
//				
				
			} catch (Exception e) {
				
				System.out.println("unable to write object");
				e.printStackTrace();
				
			}
			
		}
		private synchronized void sendImg() throws IOException {
			// TODO Auto-generated method stub
			int noofbuttons = (100/(peerlist.size()-1));
			int count=0;
			for (JComponent b : ImgBtns) {
				if(count!=noofbuttons) {
				objoutstream.writeObject(b);
				count+=1;
			   }
				else {
					break;
				}
			}
			objoutstream.flush();
		}
		
		private synchronized void addActPeer() throws IOException {
			int port_id = peerlist.size() + 9000;
			ActivePeer newa = new ActivePeer(s.getInetAddress().getHostAddress(), port_id);
			objoutstream.writeObject(port_id);
			objoutstream.writeObject(peerlist);
			peerlist.add(newa);
			updateEveryPeer(newa);
			writers.add(objoutstream);			
			System.out.println("Added peer to activepeer list " +port_id);
			
		}
		private void updateEveryPeer(ActivePeer a) throws IOException {
			
			for (ObjectOutputStream ooo :writers) {
//				addpeerflag=true;
				ooo.writeObject(a);
				
				ooo.flush();
			}
//			Iterator<ObjectOutputStream> oi = writers.iterator();
//			while(oi.hasNext()) {
//				ObjectOutputStream oos = oi.next();
////				oos.reset();
//				oos.writeObject(a);
//				oos.flush();
//			}
		}
		
	}
	
	/**
	 *Public method to authenticate the peer by checking his login credentials. 
	 * @param uname, username of peer
	 * @param upass, hashed password of user 
	 * @param IP, IP address of peer
	 * @return string, representing if user is authenticated or not
	 */
	public String authenticateUser(String uname, String upass, String IP) {
		// TODO Auto-generated method stub
		if (!IP.equals("localhost")) {
			return "Fail";
		}
		for (int i=0; i<usernames.size();i++) {
			if(uname.contains(usernames.get(i)) && upass.contains(passwords.get(i)))
				return "Success";
		}
		return "Fail";
	}
	
	/**
	 * Method functino to build the GUI of the Server app;iication
	 */
	public void buildGUI(){
		jf = new JFrame ("Puzzle Game");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LoadImageBtn = new JButton("Load New Image");
		LoadImageBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {		
					
				GetImageFile();	
					if (flag) {
						ClearData();
						BuildPuzzle();
						for (JButton b: ImgBtns) {
							MyImagePanel.add(b);
						}
						MyImagePanel.validate();
						jf.repaint();
					}	
				ImagePanel.changeflagtrue();
			}
		});
		jpbase.setLayout(new GridLayout(1,1));
		jpbase.add(LoadImageBtn);
		jf.getContentPane().add(BorderLayout.SOUTH, jpbase);		

		GetImageFile();
		if(flag==false) {
			System.exit(0);
		}
		BuildPuzzle();
		for (JButton b: ImgBtns) {
			MyImagePanel.add(b);
		}
		

		jptop.setLayout(new GridBagLayout());
		jptop.add(MyImagePanel);
		jf.add(jptop);

		jf.setSize(770, 770);
		jf.setVisible(true);
		jf.pack();
	}
	
	
	/**
	 * Method function to build the image bocks 
	 */
	public void BuildPuzzle() {
		
		for(int rows=0;rows<10;++rows) {
			for(int cols=0; cols<10; ++cols) {
				Image croppedImg = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(OrgImage.getSource(), new CropImageFilter(cols*70,rows*70,100,100)));
				JButton b = new JButton(new ImageIcon(croppedImg));
				ImgBtns.add(b);
//				Point temp= new Point(rows,cols);
//				b.putClientProperty("position", temp);
//				BtnCoords.add(temp);
				b.addMouseListener(MyImagePanel);
			}
		}
	}
	
	/**Method function to clear existing images 
	 */
	public void ClearData(){
		MyImagePanel.removeAll();
		ImgBtns.clear();
//		BtnCoords.clear();
	}
	
	/**
	 * Method function to get source or filepath of the image 
	 */
	public void GetImageFile() {
		FileNameExtensionFilter AcceptableFormat = new FileNameExtensionFilter("Image Files", "jpg", "gif", "png","tif");
		JFileChooser ChooseFile = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		ChooseFile.setFileSelectionMode(JFileChooser.FILES_ONLY);		
		ChooseFile.addChoosableFileFilter(AcceptableFormat);
		ChooseFile.setAcceptAllFileFilterUsed(false);
		int check = ChooseFile.showOpenDialog(null);		
		String filepath = returnFilePath(check,ChooseFile);
		
		String temp= isvalidImage(filepath);
        if(temp.equals("Pixels equal: false")) {
        	ImageIcon tempImageIcon = new ImageIcon(new ImageIcon(filepath).getImage().getScaledInstance(700,700, Image.SCALE_SMOOTH));
    		System.out.println(tempImageIcon);
    		OrgImage= tempImageIcon.getImage();
    		flag = true;
        }
        else {
        	JOptionPane.showMessageDialog(jf,"Invalid File");
        	flag= false;
        }
	}
	/**
	 * Method functino to return the fiile path 
	 * @param check, integer use to check condition
	 * @param cf, JfileChooser for which option to be checked 
	 * @return striing, representing the fiilepath 
	 */
	public String returnFilePath(int check, JFileChooser cf) {
		if (JFileChooser.CANCEL_OPTION== check) {
			System.exit(1);
		}
		else if (JFileChooser.APPROVE_OPTION==check) {
			File ActualFile = cf.getSelectedFile();
			return  ActualFile.getAbsolutePath().toString();
		}
		return "";	
	}
	/**
	 * Method function to check if image is valid or not
	 * @param filepath, string parametere representing filepath of image to be checked 
	 * @return striing, message representing if its valid or not. 
	 */
	public String isvalidImage(String filepath) {
		
		Image image1 = Toolkit.getDefaultToolkit().getImage(fpath);
		Image image2 = Toolkit.getDefaultToolkit().getImage(filepath);

		try {

			PixelGrabber grabImage1Pixels = new PixelGrabber(image1, 0, 0, -1,
					-1, false);
			PixelGrabber grabImage2Pixels = new PixelGrabber(image2, 0, 0, -1,
					-1, false);

			int[] image1Data = null;

			if (grabImage1Pixels.grabPixels()) {
				int width = grabImage1Pixels.getWidth();
				int height = grabImage1Pixels.getHeight();
				image1Data = new int[width * height];
				image1Data = (int[]) grabImage1Pixels.getPixels();
			}

			int[] image2Data = null;

			if (grabImage2Pixels.grabPixels()) {
				int width = grabImage2Pixels.getWidth();
				int height = grabImage2Pixels.getHeight();
				image2Data = new int[width * height];
				image2Data = (int[]) grabImage2Pixels.getPixels();
			}

			String s=("Pixels equal: "
					+ java.util.Arrays.equals(image1Data, image2Data));
			return s;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return "" ;
	}
}
