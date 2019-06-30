package org.gpsmaster.gpsloader;

/**
 * 
 * @author rfu
 *
 */
public class IgcExtension {

	private int start = -1;
	private int end = -1;
	private String code = "";
	
	/**
	 * Default constructor
	 */
	public IgcExtension() {
		
	}

	/**
	 * 
	 */
	public IgcExtension(String block) {
		parse(block);
	}

	/**
	 * Get start position within B record
	 * @return
	 */
	public int getStart() {
		return start;
	}
	/**
	 * Set start position within B record
	 * @param start
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	/**
	 * Get end position within B record
	 * @return
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * Set end position within B record
	 * @param end
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	/**
	 * Get 3 letter sourceFmt code
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Set 3 letter sourceFmt code
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * Parse an sourceFmt block (defining the position of 
	 * sourceFmt data within the B record)
	 * @param block format "SSEECCC" (start end code)
	 */
	public void parse(String block) {
		start = Integer.parseInt(block.substring(0, 2)) - 1;
		end = Integer.parseInt(block.substring(2, 4));
		code = block.substring(4);
	}
}
