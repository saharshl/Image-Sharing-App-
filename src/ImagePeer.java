import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;





/**
 * This class models the peers of the P2P Hybrid Image Sharing Application. The user/peer needs to provide valid login credentials to access the application. 
 * This class also implements a hashing interface called Hash, which is there to hash the password before sending it to the server. 
 * @author Suyash Lohiia
 * @version 1.0
 *
 */
public class ImagePeer implements Hash{
	
	//Server Variables
		private Integer port;
		private boolean flag = false;
		private boolean flag2= false;
		private String serv;
		private String uname;
		private String upass;
		private String IP;
		private ServerSocket ss;
		private Socket s;
		private ObjectOutputStream objoutstream,newout;
		private ObjectInputStream objinstream,newin;
		private ArrayList<ActivePeer> activepeers;
		private ArrayList<ObjectOutputStream> writers = new ArrayList<>();
		private boolean gotIP=false;
	
	private JFrame jf,jf1,jf2,jf3;
	private ArrayList<JButton> ImgBtns = new ArrayList<JButton>();
	private JPanel jptop = new JPanel();
	private ImagePanel PeerImagePanel = new ImagePanel(jf,ImgBtns);
	
	/**
	 * Main method for ImagePeeer class responsible for general flow of program. 
	 * @param args, string array which is empty for this program
	 * @throws ClassCastException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassCastException, UnknownHostException, IOException {
		ImagePeer im= new ImagePeer();
		im.go();
	}

	private synchronized void go() throws ClassCastException, UnknownHostException, IOException {

		inputip_gui();
		initial_serverconnect();
	
		inputusername_gui();
		inputpassword_gui();
		
		bGui();
		Socket sne = new Socket("localhost", 9999);
		newout = new ObjectOutputStream(sne.getOutputStream());
		newin = new ObjectInputStream(sne.getInputStream());
		setupNetworking();
	}
	private void initial_serverconnect() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		
		s = new Socket("localhost", 9000);
		objoutstream = new ObjectOutputStream(s.getOutputStream());
		objinstream = new ObjectInputStream(s.getInputStream());
		
	}

	private void inputip_gui() {
		// TODO Auto-generated method stub
		jf1=new JFrame("Input IP Address");
		jf1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp = new JPanel();
		JLabel j_user = new JLabel("Connect to Server");


		JTextField user_field = new JTextField(10);

	
		JButton j_login = new JButton("Ok");
		j_login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IP=user_field.getText();
				jf1.setVisible(false);
				jf2.setVisible(true);
			}
		})
		;
		JButton j_cancel = new JButton("Cancel");

		j_cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					objoutstream.close();
					objinstream.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
			}
		});
		
		jp1.add(user_field);
		jp2.add(j_login);
		jp2.add(j_cancel);
		
		jp.setLayout(new GridLayout(2,1));
		jp.add(j_user);
		jp.add(jp1);

	
		System.out.println("workingh here");
		jf1.getContentPane().add(BorderLayout.CENTER,jp);
		jf1.getContentPane().add(BorderLayout.SOUTH,jp2);
		jf1.setLocation(300,300);
        jf1.setSize(250,150);
        jf1.setVisible(true);
		
		
	}
	private void inputusername_gui() {
		// TODO Auto-generated method stub
		jf2=new JFrame("Input Username");
		jf2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp = new JPanel();		
		JLabel j_user = new JLabel("Username");


		JTextField user_field = new JTextField(10);

	
		JButton j_login = new JButton("Ok");
		j_login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				uname=user_field.getText();
				jf2.setVisible(false);
				jf3.setVisible(true);
			}
		})
		;
		JButton j_cancel = new JButton("Cancel");

		j_cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					objoutstream.close();
					objinstream.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
			}
		});
		
		jp1.add(user_field);
		jp2.add(j_login);
		jp2.add(j_cancel);
		
		jp.setLayout(new GridLayout(2,1));
		jp.add(j_user);
		jp.add(jp1);

	
		System.out.println("workingh here");
		jf2.getContentPane().add(BorderLayout.CENTER,jp);
		jf2.getContentPane().add(BorderLayout.SOUTH,jp2);
		jf2.setLocation(300,300);
        jf2.setSize(250,150);
        jf2.setVisible(false);
	}

	private void inputpassword_gui() {
		// TODO Auto-generated method stub
		jf3=new JFrame("Input Password");
		jf3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel jp1 = new JPanel();
		JPanel jp2 = new JPanel();
		JPanel jp = new JPanel();
		
		JLabel j_user = new JLabel("Password");
	
		JTextField user_field = new JTextField(10);

	
		JButton j_login = new JButton("Ok");
		j_login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				upass=user_field.getText();
				String reply = null;
				String hashedupass=hashing(upass);
				
				try {
					objoutstream.writeObject(IP);
					objoutstream.writeObject(uname);
					objoutstream.writeObject(hashedupass);
					System.out.println("written objects");
//					Thread.sleep(2000);
//					System.out.println("thread working");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					 reply = (String) objinstream.readObject();
					 System.out.println("reading objects");
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (reply.equals("Success")){
					jf3.setVisible(false);
					jf.setVisible(true);
					flag2=true;
//					setupNetworking();
				}
				else {
					JOptionPane.showMessageDialog(null,"Login Failed");
					System.exit(0);
				}
			}
		})
		;
		JButton j_cancel = new JButton("Cancel");

		j_cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					objoutstream.close();
					objinstream.close();
					System.exit(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
			}
		});
		
		jp1.add(user_field);
		jp2.add(j_login);
		jp2.add(j_cancel);
		
		jp.setLayout(new GridLayout(2,1));
		jp.add(j_user);
		jp.add(jp1);

	
		System.out.println("workingh here");
		jf3.getContentPane().add(BorderLayout.CENTER,jp);
		jf3.getContentPane().add(BorderLayout.SOUTH,jp2);
		jf3.setLocation(300,300);
        jf3.setSize(250,150);
        jf3.setVisible(false);
	}

	

	/**
	 * Method function to build the main GUI of the peer
	 */
	public synchronized void bGui() {
		jf = new JFrame ("Image Peer");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		for (int i=0;i<100;i++) {
			JButton b = new JButton();
			ImgBtns.add(b);
			b.addMouseListener(PeerImagePanel);
			
		}
		for (JButton b: ImgBtns) {
			PeerImagePanel.add(b);
		}

		jptop.setLayout(new GridBagLayout());
		jptop.add(PeerImagePanel);
		jf.add(jptop);	
		jf.setSize(700, 700);
		jf.setVisible(false);
		jf.pack();
	}

	@SuppressWarnings("unchecked")
	private void setupNetworking() {
		// TODO Auto-generated method stub
		try {
//			setupNetworking2();
			while(true) {
				if(flag2) {
					if(!flag) {
						Thread.sleep(1000);
						writers.add(objoutstream);
						System.out.println("reached setup networking or not?");
						port = (Integer)objinstream.readObject();

						activepeers= (ArrayList<ActivePeer>) objinstream.readObject();

						ss = new ServerSocket(port);
						Thread t = new Thread(new ClientAccepter(ss));
						t.start();
						for(ActivePeer ea: activepeers){
							if(ea.getPort() != 9000 && ea.getPort() != port) {
								try {
									Thread tt = new Thread(new PeerConnector(ea.getIP(), ea.getPort()));
					 				tt.start();
								} catch (Exception e) {
								}
							}
						}
						receiveimg(objinstream);
						PeerImagePanel.removeAll();
						for (JButton b: ImgBtns) {
							PeerImagePanel.add(b);
						}
						jf.repaint();
						Thread ut = new Thread(new ServerTalker());
						ut.start();
						flag = true;
					}
					else {
//						if(ImageServer.addpeerflag) {
							Object obj= (Object) objinstream.readObject();
							if(obj instanceof ActivePeer) {	
								ActivePeer objactive = (ActivePeer) obj;
								if(!activepeers.contains(objactive)) {
									activepeers.add(objactive);
								}
							}
							Thread.sleep(2000);
//							ImageServer.changeapflag();
//						}else {
//							System.out.print("");
//						}
					}
				}
				else {
					System.out.print("");
				}

			}
		}
		catch(Exception e){
			System.out.println("unable to estabilish connection");
		}
	}

//	private void setupNetworking2() throws UnknownHostException, IOException, ClassNotFoundException {
//		serv= "localhost";
//		s = new Socket(serv, 9000);
//		objoutstream = new ObjectOutputStream(s.getOutputStream());
//		objinstream = new ObjectInputStream(s.getInputStream());
		
//		receiveimg(objinstream);
//		PeerImagePanel.removeAll();
//		for (JButton b: ImgBtns) {
//		PeerImagePanel.add(b);
//		}
//		jf.repaint();
//	}
	private synchronized void receiveimg(ObjectInputStream objinsteam) throws ClassNotFoundException, IOException {
		int ilimit= (100/activepeers.size());
		System.out.println(activepeers.size());
		for (int i=0;i<ilimit;i++) {
		ImgBtns.set(i,(JButton) objinstream.readObject());
		}

	}
	private class ClientAccepter extends Thread{
		private ServerSocket ss;
		private ClientAccepter(ServerSocket servsocket) throws IOException {
			ss = servsocket;
		}
		@Override
		public void run() {
			while(true) {
				try {
					Socket s = ss.accept();
					Thread t = new Thread(new ClientHandler(s));
					t.start();
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}

			}
		}
	}

		private class ClientHandler extends Thread{
			private Socket s;
			private ObjectOutputStream objoutstream;
			private ObjectInputStream objinstream1;
			private ClientHandler(Socket soc) throws IOException {
				this.s = soc;
			}
			@Override
			public synchronized void run() {
				try {
					setupNetworking();
					System.out.println(activepeers.size());
					if(activepeers.size()==2) {
						for (int i=50;i<100;i++) {
							objoutstream.writeObject(ImgBtns.get(i));
						}
					}
					else {
						if(port==9001) {
							for (int i=33;i<66;i++) {
								objoutstream.writeObject(ImgBtns.get(i));
							}
						}
						else {
							for (int i=66;i<100;i++) {
								objoutstream.writeObject(ImgBtns.get(i));
							}
						}
						
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			private void setupNetworking() throws IOException {
				objoutstream = new ObjectOutputStream(s.getOutputStream());
				writers.add(objoutstream);
				objinstream1 = new ObjectInputStream(s.getInputStream());
			}
		}

	private class PeerConnector extends Thread{
		private Socket s;
		private int portid;
		private ObjectOutputStream objoutstream;
		private ObjectInputStream objinstream2;
		private PeerConnector(String stringip,int n)  {
			InetAddress inetAddress;
			try {
				inetAddress = InetAddress.getByName(stringip);
				System.out.println(inetAddress);
				s = new Socket(inetAddress, n);
				portid=n;
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		@Override
		public synchronized void run() {
			try {
				setupNetworking();
				System.out.println("peer connector working");
//				int peer_no= port -9000;
//				int peersandservers= active.size();
				if(activepeers.size()==2) {
				for(int i=50;i<100;i++) {
					ImgBtns.set(i,(JButton) objinstream2.readObject());
				}
				}
				else if(activepeers.size()==3) {
					if(portid==9001) {
					for(int i=33;i<66;i++) {
						ImgBtns.set(i,(JButton) objinstream2.readObject());
					}
					}
					else {
						for(int i=66;i<100;i++) {
							ImgBtns.set(i,(JButton) objinstream2.readObject());
						}
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private void setupNetworking() throws IOException {
			objoutstream = new ObjectOutputStream(s.getOutputStream());
			writers.add(objoutstream);
			objinstream2 = new ObjectInputStream(s.getInputStream());
		}
	}
	
	private class ServerTalker implements Runnable{

//		private ObjectInputStream istream;
//		private ServerTalker(ObjectInputStream i) {
//			istream=i;
//		}
		@Override
		public synchronized void run() {

			while(true) {
//					if(ImageServer.updateimageflag) {
						String temp;
						try {
							System.out.print("");
							System.out.println("ondition reaching");
							temp = (String)newin.readObject();
							System.out.print(temp);
//							Thread.sleep(500);
							if(temp.equals("update")) {
								System.out.println("true condition reaching");
								for(int i=0;i<100;i++) {
										ImgBtns.set(i,(JButton) newin.readObject());
								}
								System.out.println("all buttons read");
//								ImageServer.changeuiflag();
								PeerImagePanel.removeAll();
								for (JButton b: ImgBtns) {
									PeerImagePanel.add(b);
								}
								jf.repaint();
								jf.setVisible(true);
								System.out.println("frame repainted");
								jf.repaint();
							}
							else {
								System.out.print("");
							}
						}catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							System.out.print("");
						}
//					}
//					else {
//						System.out.print("");
//					}
			}
		
		}
	}
	/**
	 * Method function overriding abstract method from hash interface to hash the passwords. 
	 */
	@Override
	public String hashing(String password) {
		
		String passwordToHash = password;
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return generatedPassword;
		
	}

}
