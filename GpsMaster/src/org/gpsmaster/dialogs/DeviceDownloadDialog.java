package org.gpsmaster.dialogs;

import javax.swing.JFrame;

import org.gpsmaster.device.GpsDevice;

import eu.fuegenstein.messagecenter.MessageCenter;

public class DeviceDownloadDialog extends GenericDownloadDialog {


	/**
	 *
	 */
	private static final long serialVersionUID = -1333915128373781885L;
	private GpsDevice device = null;

	public DeviceDownloadDialog(JFrame frame, MessageCenter msg) {
		super(frame, msg);
		// TODO Auto-generated constructor stub
	}

	public GpsDevice getDevice() {
		return device;
	}

	public void setDevice(GpsDevice device) {
		this.device = device;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getColumnKey(int inColNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
