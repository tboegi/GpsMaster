package org.gpsmaster.filehub;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.apache.commons.io.IOUtils;

import org.gpsmaster.Const;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.util.IProgressReporter;
import eu.fuegenstein.util.ProgressInfo;
import eu.fuegenstein.util.LogEntry;

/**
 * Class for distributing GPS data items from a source to one ore more targets,
 * implementing the {@link IItemSource} resp. {@link IItemTarget} interfaces.
 * 
 * i.e. Filesystem, Database, Devices, Web, Slippy Map ...
 * 
 * TODO Perform file format conversion if necessary
 * 
 * TODO handle transfer of a single file differently
 * 		(i.e. no progress bar)
 * 
 * Runs in background and reports progress.
 *  
 * TODO progress report of bytes transferred
 * 		http://docs.oracle.com/javase/6/docs/api/javax/swing/ProgressMonitorInputStream.html
 * 		http://docs.oracle.com/javase/tutorial/uiswing/components/progress.html
 * 		http://usabilityetc.com/articles/size-input-streams/  
 * @author rfu
 *
 */
public class FileHub {

	private String title = "Transfer";
	private MessageCenter msg = null;
	private IProgressReporter progressReporter = null;
	private ProgressInfo totalProgress = null;
		
	private IItemSource itemSource = null;
	private List<IItemTarget> itemTargets = null;
	private List<TransferableItem> processedItems = null;
	private List<GpsLoader> loaders = new ArrayList<GpsLoader>();
	private List<PropertyChangeListener> changeListeners = new ArrayList<PropertyChangeListener>();
	
	private SwingWorker<Void, Void> transferWorker = null;

	private GPXFile currentGpx = null; // for source/targets providing/requiring a GPXFile

	private byte[] streamBuffer = null;
	
	/**
	 * 
	 */
	public FileHub() {
		itemTargets = new ArrayList<IItemTarget>();		
		processedItems = Collections.synchronizedList(new ArrayList<TransferableItem>());
		makeTransferWorker();
	}

	/**
	 * @return the title
	 */
	public String getProgressTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the active item source
	 * @return item source or NULL
	 */
	public IItemSource getItemSource() {
		return itemSource;
	}

	/**
	 * Set the active item source
	 * @param source
	 */
	public void setItemSource(IItemSource source) {
		itemSource = source;
	}
	
	/**
	 * Add an item to the list of items to be transferred
	 * @param item
	 */
	public void addItem(TransferableItem item) {
		itemSource.getItems().add(item);
	}
	
	/**
	 * 
	 * @param target
	 */
	public void addItemTarget(IItemTarget target) {
		itemTargets.add(target);
	}
	
	/**
	 * Removes the given target from the list of receiving targets. 
	 * No exception is thrown if the target was not added previously.
	 * @param target
	 */
	public void removeItemTarget(IItemTarget target) {
		itemTargets.remove(target);
	}

	/**
	 * 
	 * @param targets
	 */
	public void setItemTargets(List<IItemTarget> targets) {
		itemTargets = targets;
	}
	
	/**
	 * Get the list of current item targets
	 * @return
	 */
	public List<IItemTarget> getItemTargets() {
		return itemTargets;
	}
	
	/**
	 * @return the reporter
	 */
	public IProgressReporter getProgressReporter() {
		return progressReporter;
	}

	/**
	 * @param reporter the reporter to set
	 */
	public void setProgressReporter(IProgressReporter reporter) {
		progressReporter = reporter;
		String title = "Loading files";
		progressReporter.setTitle(title);						
		totalProgress = new ProgressInfo();
		totalProgress.setMinValue(0);
		progressReporter.addProgressItem(totalProgress);
	}
	
	/**
	 * @return the msg
	 */
	public MessageCenter getMessageCenter() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMessageCenter(MessageCenter msg) {
		this.msg = msg;
	}

	/**
	 * 
	 * @param changeListener
	 */
	public void addChangeListener(PropertyChangeListener changeListener) {
		if (!changeListeners.contains(changeListener)) {
			changeListeners.add(changeListener);
		}
		// 
	}
	
	/**
	 * 
	 * @param changeListener
	 */
	public void removeChangeListener(PropertyChangeListener changeListener) {
		if (changeListeners.contains(changeListener)) {
			changeListeners.remove(changeListener);
		}
		if (transferWorker.getState() != StateValue.DONE) {
			transferWorker.removePropertyChangeListener(changeListener);
		}
	}
	
	/**
	 * Get the list of items that have been processed so far.
	 * @return
	 */
	public List<TransferableItem> getProcessedItems() {
		return processedItems;
	}
		
	/**
	 * Start the transfer task. Items added via addItem() while 
	 * the task is running will also be processed.
	 * 
	 */
	public void run() {
		if (itemSource == null) {
			throw new  IllegalArgumentException("No item source set");
		}
		// if transferWorker is idle, create a new instance and start it.
		if (transferWorker.getState() == StateValue.DONE) {
			makeTransferWorker();
		}
		// re-add PropertyChangeListeners 
		for (PropertyChangeListener listener : changeListeners) {
			transferWorker.removePropertyChangeListener(listener);
			transferWorker.addPropertyChangeListener(listener);
		}
		
		if (progressReporter != null) {
			progressReporter.setTitle("Transferring ...");
		}
		transferWorker.execute();
	}
	
	/**
	 * Cancel the running transfer at at the next possible occasion.
	 * Ignored if no background transfer is running.
	 */
	public void cancel() {
		if (transferWorker != null) {
			transferWorker.cancel(true);
		}
		if (progressReporter != null) {
			progressReporter.cancel();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isCancelled() {
		return transferWorker.isCancelled();
	}
	
	/**
	 * 
	 */
	public void clear() {
		loaders.clear();
		// items.clear();
		processedItems.clear();
		changeListeners.clear();		
	}
	
	
	private void makeTransferWorker() {
		transferWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				int i = 0;
				if (progressReporter != null) {
					progressReporter.reset();
				}
				
				int size = itemSource.getItems().size();
				firePropertyChange(Const.PCE_TRANSFERSTARTED, null, null);
				
				while ((i < size) && !isCancelled()) {					
															
					TransferableItem item = itemSource.getItems().get(i);
					setProgressTitle("Getting " + item.getName() + " from " + itemSource.getName());
					
					// update progress
					if (progressReporter != null) {
						totalProgress.setMaxValue(processedItems.size() + itemSource.getItems().size());
						totalProgress.incrementValue(); 
						progressReporter.update();
						if (progressReporter.isCancelled()) {
							cancel(true); 
						}
					}

					item.setTransferState(TransferableItem.STATE_PROCESSING);
					firePropertyChange(Const.PCE_TRANSFERITEMSTATECHANGED, null, item);
										
					try {
						if (itemSource.getDataType() == DataType.STREAM) {
							itemSource.open(item);
							streamBuffer = IOUtils.toByteArray(itemSource.getInputStream());
							itemSource.close();
						}
						
						// dispatch to target(s)
						for (IItemTarget target : itemTargets) {
							if (target.isEnabled()) {
								if (target.doShowProgressText()) {
									setProgressTitle("Sending " + item.getName() + " to " + target.getName());
								}
								try {
									dispatch(item, itemSource, target);
								} catch (Exception e) {
									item.log.addEntry(LogEntry.ERROR, "sending to " + target.getName() + " failed", e);
								}
							}
						}						

					}
					catch (Exception e) { // catches only if (DataType.STREAM) above
						item.getLog().addEntry(LogEntry.ERROR, "loading from " + itemSource.getName() + " failed", e);
					}

					processedItems.add(item);
					item.setTransferState(TransferableItem.STATE_FINISHED);
					
					// reset global GPX / stream buffers					
					currentGpx = null;
					streamBuffer = null;
					
					firePropertyChange(Const.PCE_TRANSFERITEMSTATECHANGED, null, item);
					i++;
					size = itemSource.getItems().size();
				}
				
				return null;
			}
			
			@Override
			protected void done() {
				firePropertyChange(Const.PCE_TRANSFERFINISHED, null, null);
				loaders.clear();				
			}		
		};	
	}
		
	/***
	 * Send given item to given target. convert from GPX to stream (and vice versa) if necessary  
	 * @param item item to transfer
	 * @param source source the item came from
	 * @param target target to send to
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private void dispatch(TransferableItem item, IItemSource source, IItemTarget target) throws Exception {
		
		// TODO do not send an item back to its source
		if (target.isEnabled()) { 
			if ((source.getDataType() == DataType.GPXFILE) && (target.getDataType() == DataType.GPXFILE)) {
				target.addGpxFile(source.getGpxFile(item), item);
			} else if ((source.getDataType() == DataType.STREAM) && (target.getDataType() == DataType.GPXFILE)) {				
				streamToGpx(item, streamBuffer);
				target.addGpxFile(currentGpx, item);
			} else if ((source.getDataType() == DataType.GPXFILE) && (target.getDataType() == DataType.STREAM)) {
				gpxToStream(item, source.getGpxFile(item), target);
			} else if ((source.getDataType() == DataType.STREAM) && (target.getDataType() == DataType.STREAM)) {
				streamToStream(item, streamBuffer, target);
			} else if ((source.getDataType() == DataType.STREAM) && (target.getDataType() == DataType.STREAMGPX)) {
				streamToGpx(item, streamBuffer);
				streamToStreamGpx(item, target);
			} else if ((source.getDataType() == DataType.GPXFILE) && (target.getDataType() == DataType.STREAMGPX)){
				gpxToStreamGpx(item, source.getGpxFile(item), target);
			} else {
				// throw exception unsupported datatype combination
				throw new UnsupportedOperationException("Source/Target DataType combination not supported");
			}
		}
	}
		
	/**
	 * extract the currentGpx from the given inputBuffer by using the appropriate {@link GpsLoader} 
	 * @param item used for Metadata 
	 * @param inputBuffer containing the content of the {@link IItemSource}'s {@link InputStream}
	 * @param target to send the item to
	 * @throws Exception
	 */
	private void streamToGpx(TransferableItem item, byte[] inputBuffer) throws Exception {
		GpsLoader loader = null;
		// create the GPXFile only if it hasn't been created for a different target before
		if (currentGpx == null) {
			if (item.getLoaderClassName() != null) {
				loader = GpsLoaderFactory.getLoaderByClassName(item.getLoaderClassName());	
			} else if (item.getSourceFormat() != null) {
				loader = GpsLoaderFactory.getLoaderByExtension(item.getSourceFormat());
			}
			if (loader == null) {
				throw new IllegalArgumentException("unknown file type / loader class");
			}	
			item.setLoaderClassName(loader.getClass().getName());
			if (loader.canValidate()) {
				try {
					loader.validate(new ByteArrayInputStream(inputBuffer));
				} catch (Exception e) {
					item.getLog().addEntry(LogEntry.WARNING, "validation failed", e);
				}
			}
			currentGpx = loader.load(new ByteArrayInputStream(inputBuffer), item.getSourceFormat());
		}		
	}
	
	/**
	 * 
	 * @param item
	 * @param inputBuffer
	 * @param target
	 * @throws Exception
	 */
	private void streamToStream(TransferableItem item, byte[] inputBuffer, IItemTarget target) throws Exception {
		target.open(item);
		target.getOutputStream().write(inputBuffer);
		target.close();
	}
	
	/**
	 * Send item to a target that requires both an {@link OutputStream} AND the corresponding {@link GPXFile}
	 * @param item
	 * @param target
	 * @throws Exception 
	 * @throws IOException 
	 */
	private void streamToStreamGpx(TransferableItem item, IItemTarget target) throws IOException, Exception {
		target.open(item);
		target.addGpxFile(currentGpx, item);
		target.getOutputStream().write(streamBuffer);
		target.close();		
	}
	
	/**
	 * 
	 * @param item
	 * @param gpxFile Source {@link GPXFile}
	 * @param target
	 * @throws IOException
	 * @throws Exception
	 * TODO redundant code with {@link gpxToStream(TransferableItem, GPXFile, IItemTarget)}
	 * TODO avoid calling writer.save() if {@link GPXFile} has already been written to (a) stream before (implement cache)
	 * TODO where to get file format (extension) for conversion to stream?
	 */
	private void gpxToStreamGpx(TransferableItem item, GPXFile gpxFile, IItemTarget target) throws IOException, Exception {
		String ext = target.getRequiredFormat();
		if (ext == null) {
			ext = item.getSourceFormat();  
		}
		GpsLoader writer = GpsLoaderFactory.getLoaderByExtension(ext);
		target.open(item);
		target.addGpxFile(gpxFile, item);
		writer.save(gpxFile, target.getOutputStream());
		target.close();	
	}
	
	/**
	 * 
	 * @param item
	 * @param gpxFile
	 * @param target
	 * @throws ClassNotFoundException 
	 * 
	 * TODO better exception handling/error reporting. let user know what and where it happened
	 */
	private void gpxToStream(TransferableItem item, GPXFile gpxFile, IItemTarget target) throws Exception {
		String ext = target.getRequiredFormat();
		if (ext == null) {
			ext = item.getSourceFormat();  
		}
		GpsLoader writer = GpsLoaderFactory.getLoaderByExtension(ext);
		target.open(item);
		writer.save(gpxFile, target.getOutputStream());
		target.close();			
	}
	
	/**
	 * TODO set title to totalProgress Panel instead
	 * Shortcut to ProgressReporter
	 * @param title
	 */
	private void setProgressTitle(String title) {
		if (progressReporter != null) {
			totalProgress.setName(title);
			// progressReporter.setTitle(title);
		}
	}
	
	@Override
	public void finalize() {
		System.out.println("disposing FileHub");
	}
	
}
