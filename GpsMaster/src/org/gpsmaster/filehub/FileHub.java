package org.gpsmaster.filehub;

import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.gpsmaster.Const;
import org.gpsmaster.gpsloader.GpsLoader;
import org.gpsmaster.gpsloader.GpsLoaderFactory;
import org.gpsmaster.gpxpanel.GPXFile;

import eu.fuegenstein.messagecenter.MessageCenter;
import eu.fuegenstein.util.IProgressReporter;
import eu.fuegenstein.util.ProgressItem;

/**
 * Class for distributing GPS data items from a source to one ore more targets,
 * implementing the {@link IItemSource} resp. {@link IItemTarget} interfaces.
 *
 * i.e. Filesystem, Database, Devices, Web, Slippy Map ...
 *
 * TODO Perform file format conversion if necessary
 * TODO Directly copy InputStream to target if supported / requested by target
 *
 * Runs in background and reports progress.
 *
 *  TODO allow dynamic addition of files to load during execution.
 *
 * @author rfu
 *
 */
public class FileHub {

	private MessageCenter msg = null;
	private IProgressReporter progressReporter = null;
	private ProgressItem totalProgress = null;
	private int totalItems = 0;

	private IItemSource itemSource = null;
	private List<IItemTarget> itemTargets = null;
	private List<ITransferableItem> items = null;
	private List<ITransferableItem> processedItems = null;
	private Hashtable<String, GpsLoader> loaders = new Hashtable<String, GpsLoader>();

	private SwingWorker<Void, Void> transferWorker = null;

	/**
	 *
	 */
	public FileHub() {
		itemTargets = new ArrayList<IItemTarget>();
		items = Collections.synchronizedList(new ArrayList<ITransferableItem>());
		processedItems = Collections.synchronizedList(new ArrayList<ITransferableItem>());
		makeTransferWorker();
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
		totalProgress = new ProgressItem();
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
	public void setChangeListener(PropertyChangeListener changeListener) {
		transferWorker.addPropertyChangeListener(changeListener);
	}

	/**
	 *
	 * @param item
	 */
	public void addItem(ITransferableItem item) {
		item.setState(ITransferableItem.STATE_PENDING);
		items.add(item);
		totalItems++;
		if (progressReporter != null) {
			totalProgress.setMaxValue(totalItems);
		}
	}

	/**
	 * Get the list of items that have been processed so far.
	 * @return
	 */
	public List<ITransferableItem> getProcessedItems() {
		return processedItems;
	}

	/**
	 * Get the list of items waiting to be processed.
	 * @return
	 */
	public List<ITransferableItem> getItems() {
		return items;
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
		if (progressReporter != null) {
			String name = "Transferring files from " + itemSource.getName() + " to";
			for (IItemTarget target : itemTargets) {
				name += " " + target.getName() + ",";
			}
			totalProgress.setName(name);
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
		items.clear();
		processedItems.clear();
		totalItems = 0;
	}

	/**
	 * This worker allows items to be added while it is running.
	 */
	private void makeTransferWorker() {

		transferWorker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {

				GPXFile gpx = null;
				InputStream inputStream = null;
				List<String> badExt = new ArrayList<String>();
				loaders.clear();

				int size = items.size();

				while ((size > 0) && !isCancelled()) {
					ITransferableItem item = items.get(0);
					String ext = item.getExtension();
					// cache loader
					if ((loaders.containsKey(ext) == false) && (badExt.contains(ext) == false)) {
						try {
							GpsLoader loader = GpsLoaderFactory.getLoader(ext);
							loaders.put(ext, loader);
						} catch (ClassNotFoundException e) {
							error("No loader for filetype ." + ext, e);
							badExt.add(ext);
						}
					}
					GpsLoader loader = loaders.get(ext);
					if (loader != null) {
						try {
							setProgressTitle("Getting " + item.getName() + " from " + itemSource.getName());

							// load from source
							itemSource.open(item);
							// validation!!
							inputStream = itemSource.getInputStream();
							if (loader.isCumulative()) {
								loader.load(inputStream);
							} else {
								gpx = loader.load(inputStream);
						    	if (gpx.getMetadata().getName().isEmpty() ) {
						    		gpx.getMetadata().setName(item.getName());
						    	}
								// send to targets
						    	sendToTargets(item, gpx);
							}
							itemSource.close();
							item.setState(ITransferableItem.STATE_FINISHED);
						}
						catch (Exception e) {
							item.setException(e);
							item.setState(ITransferableItem.STATE_ERROR);
						}
					} else {
						// loader not found - set error & exception
						item.setException(new ClassNotFoundException("No loader for format " + ext.toUpperCase()));
						item.setState(ITransferableItem.STATE_ERROR);
					}

					// update progress
					if (progressReporter != null) {
						totalProgress.incrementValue();
						progressReporter.update();
						if (progressReporter.isCancelled()) {
							cancel(true); // does not work!
						}
					}

					gpx = null;
					items.remove(item);
					processedItems.add(item);
					size = items.size();
				}
				// TODO send cumulated GPX
				return null;
			}

			@Override
			protected void done() {
				firePropertyChange(Const.PCE_TRANSFERFINISHED, null, null);
			}

		};
	}

	/**
	 *
	 * @param gpx
	 */
	private void sendToTargets(ITransferableItem item, GPXFile gpx) {
    	// TODO consider transferring in background for some slow targets
				for (IItemTarget target : itemTargets) {
					// TODO show only for targets where it makes sense:
					setProgressTitle("Sending " + item.getName() + " to " + target.getName());
					target.addGpxFile(gpx);
				}
	}

	/**
	 * Shortcut to ProgressReporter
	 * @param title
	 */
	private void setProgressTitle(String title) {
		if (progressReporter != null) {
			progressReporter.setTitle(title);
		}
	}

	/**
	 * Shortcut to message center
	 * @param text
	 * @param e
	 */
	private void error(String text, Exception e) {
		if (msg != null) {
		 	msg.error(text, e);
		}
	}


}
