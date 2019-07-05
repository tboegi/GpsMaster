package org.gpsmaster.filehub;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gpsmaster.GpsMaster;
import org.gpsmaster.gpxpanel.GPXFile;
import org.gpsmaster.tree.GPXTree;

/**
 * Class implementing an {@link IItemSource} for {@link GPXFile}s selected in the {@link GPXTree}
 * @author rfu
 *
 */
public class MapSource implements IItemSource {


	GPXTree gpxTree = null;
	List<TransferableItem> items = new ArrayList<TransferableItem>();

	/**
	 * Constructor
	 * @param tree
	 */
	public MapSource(GPXTree tree) {
		this.gpxTree = tree;
	}

	@Override
	public String getName() {

		return "Map";
	}

	@Override
	public DataType getDataType() {
		return DataType.GPXFILE;
	}

	@Override
	public boolean doShowProgressText() {
		return false;
	}

	/**
	 *
	 */
	@Override
	public List<TransferableItem> getItems() {
		// for now, only the currently selected GPXFile is returned.
		// later, add all items selected in the GPXTree (multiselect)
		items.clear();
		GpxFileItem item = new GpxFileItem(GpsMaster.active.getGpxFile());
		items.add(item);
		return items;
	}

	@Override
	public GPXFile getGpxFile(TransferableItem item) {

		return GpsMaster.active.getGpxFile();
	}

	@Override
	public void open(TransferableItem transferableItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

}
