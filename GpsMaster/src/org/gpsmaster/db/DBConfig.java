package org.gpsmaster.db;

/**
 * Class holding database configuration 
 * 
 * @author rfu
 *
 */
public class DBConfig {

	private String DSN = "";
	private boolean compression = false;
	private String username = "";
	private String password = "";
	/**
	 * @return the dSN
	 */
	public String getDSN() {
		return DSN;
	}
	/**
	 * @param dSN the dSN to set
	 */
	public void setDSN(String dSN) {
		DSN = dSN;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the compression
	 */
	public boolean isCompression() {
		return compression;
	}
	/**
	 * determine if GPS data is compressed before storing
	 * 
	 * @param compression the compression to set
	 */
	public void setCompression(boolean compression) {
		this.compression = compression;
	}
	
	
}
