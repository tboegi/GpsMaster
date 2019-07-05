package org.gpsmaster.elevation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.gpsmaster.Const;
import org.gpsmaster.gpxpanel.Waypoint;
import org.gpsmaster.gpxpanel.WaypointGroup;

import eu.fuegenstein.util.ProgressItem;
import eu.fuegenstein.util.ProgressReporter;

/**
 * Class providing elevation correction functionality
 * through elevation data provider
 *
 * @author rfu
 *
 */
public class Corrector {

	private ProgressReporter reporter = null;
	private ProgressItem groupProgress = null;
	private ProgressItem totalProgress = null;

	private ElevationProvider provider = null;
	private List<WaypointGroup> groups = null;
	private boolean inBackground = false;

	private CorrectionTask task = null;
	private PropertyChangeListener changeListener = null;

	/**
	 * Constructor
	 *
	 * @param provider Elevation Provider to use
	 */
	public Corrector(ElevationProvider elevationProvider) {
		provider = elevationProvider;
		provider.setInterpolation(true);
		groups = new ArrayList<WaypointGroup>();
	}

	/**
	 * @return the reporter
	 */
	public ProgressReporter getProgressReporter() {
		return reporter;
	}

	/**
	 * @param reporter the reporter to set
	 */
	public void setProgressReporter(ProgressReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the provider
	 */
	public ElevationProvider getElevationProvider() {
		return provider;
	}

	/**
	 *
	 * @param listener
	 */
	public void setChangeListener(PropertyChangeListener listener) {
		changeListener = listener;
	}

	/**
	 * @return the inBackground
	 */
	public boolean isRunInBackground() {
		return inBackground;
	}

	/**
	 * set if the correction is to be executed as background task
	 * @param inBackground true or false
	 */
	public void setRunInBackground(boolean runInBackground) {
		inBackground = runInBackground;
	}

	/**
	 *
	 * @param groups {@link WaypointGroup}s to correct
	 */
	public void setWaypointGroups(List<WaypointGroup> waypointGroups) {
		groups.clear();
		for(WaypointGroup group : waypointGroups) {
			groups.add(group);
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isCancelled() {
		if (task != null) {
			return task.isCancelled();
		}
		return false;
	}

	/**
	 *
	 */
	public void correct() {
		setupReporter();
		if (inBackground) {
			task = new CorrectionTask();
			if (changeListener != null) {
				task.addPropertyChangeListener(changeListener);
			}
			task.execute();
		} else {

		}
	}

	/**
	 *
	 */
	public void clear() {
		groups.clear();
		reporter.removeProgressItem(groupProgress);
		reporter.removeProgressItem(totalProgress);
		groupProgress = null;
		totalProgress = null;
	}

	/**
	 *
	 */
	private void doCorrection() {

	}

	/**
	 * private helper class for {@link SwingWorker} publish/process
	 * @author rfu
	 *
	 */
	private class Progress {
		public Progress(int groups, int total) {
			this.groups = groups;
			this.total = total;
		}
		public int groups = 0;
		public int total = 0;
	}

	/**
	 *
	 * @author rfu
	 *
	 */
	private class CorrectionTask extends SwingWorker<Void, Progress> {

		int groupCtr = 0;

		@Override
		protected Void doInBackground() throws Exception {

			for (WaypointGroup group : groups) {
				groupCtr++;
				List<Waypoint> waypoints = group.getWaypoints(); // shortcut
				totalProgress.setMaxValue(waypoints.size());

				int s = 0; // start index
				int e = 0; // end index
				while (e < waypoints.size()) {

					e = Math.min(s + provider.getChunkSize(), waypoints.size());

					// publish progress
					if (reporter != null) {
						publish(new Progress(groupCtr, e));
						if(reporter.isCancelled()) {
							cancel(true);
						}
					}
					provider.correctElevation(waypoints.subList(s, e));
					s = e + 1;
				}
			}
			return null;
		}

		/**
		 * transfer local progress status to {@link ProgressReporter}
		 */
		@Override
		protected void process(List<Progress> progressList) {
			if (reporter != null) {
				// just report last entry
				Progress progress = progressList.get(progressList.size() - 1);
				groupProgress.setValue(progress.groups);
				totalProgress.setValue(progress.total);
				reporter.update();
			}
		}

		@Override
		protected void done() {
			System.out.println("done");
			firePropertyChange(Const.PCE_ELEFINISHED, null, null);
		}
	}

	/**
	 *
	 */
	private void setupReporter() {

		if (reporter != null) {

			int total = 0;
			for (int i = 0; i < groups.size(); i++) {
				total += groups.get(i).getWaypoints().size();
			}

			String title = String.format("Correcting elevation of %d trackpoints in %d segments", total, groups.size());
			reporter.setTitle(title);
			reporter.setFooter(provider.getAttribution());

			groupProgress = new ProgressItem();
			groupProgress.setMinValue(0);
			groupProgress.setMaxValue(groups.size());
			reporter.addProgressItem(groupProgress);

			totalProgress = new ProgressItem();
			totalProgress.setMinValue(0);
			// totalProgress.setMaxValue(total);
			reporter.addProgressItem(totalProgress);
		}
	}
}
