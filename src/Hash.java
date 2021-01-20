
/**
 * An interface which contains an abstract method hashing whose role is to conver the unhashed password to 
 * a hashed password. This interface is implemented by the User class which uses the method to hash the passwords.
 * @author Suyash Lohia 
 * @version 1.0
 */
public interface Hash {
	
	/**
	 * An abstract method which is overridden by the method definition in the classes which implement this 
	 * interface. 
	 * @param password, a string containing the unshashed passowrd to be hashed 
	 * @return , nothing since this is an abstract method. However returning the hashed password in overridden definitions. 
	 */
	public abstract String hashing(String password);
	
}