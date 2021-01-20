import java.io.Serializable;

/**
 * ActivePeer class which models a peer/client in the image sharing application. 
 * It contains the Ip Adress anfd the port of the peer connection. 
 * @author suyashlohia
 *
 */
public class ActivePeer implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private String IP;
	private int port;
	
	/**
	 * Constructor function which is doing nothing
	 */
	public ActivePeer() {
		super();
	}
	
	/**
	 * Constructor function initialising the activepeer by setting the IP adress and the port
	 * @param i
	 * @param p
	 */
	public ActivePeer(String i, int p) {
		this.IP = i;
		this.port = p;
	}
	/**
	 * Method function to get the IP address
	 * @return IP, string respresnting the IP address
	 */
	public String getIP() {
		return IP;
	}
	/**
	 * Method Function to set the IP address
	 * @param IP, string respresnting the IP address
	 */
	public void setIP(String i) {
		IP = i;
	}
	/**
	 * Method function to get the Port
	 * @return port, int representing the port of the peer 
	 */
	public int getPort() {
		return port;
	}
	/**
	 * Method function to set the Port
	 * @param port, int representing the port of the peer 
	 */
	public void setPort(int p) {
		this.port = p;
	}
}
