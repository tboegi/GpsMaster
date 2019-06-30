package org.gpsmaster.undo;

/**
 * see tim.prune.undo
 * 		UndoManager
 * 		UndoStack
 * 		UndoOperation
 * @author rfu
 *
 * It is assumed that each class implementing this interface
 * is instanciated and called only once.
 * 
 */
public interface IUndoable {
	
	/**
	 * Get a short description of this undo operation
	 * 
	 * @return
	 */
	public String getUndoDescription();
	
	/**
	 * perform the actual undo operation
	 *  
	 */
	public void undo() throws Exception; 
	
}
