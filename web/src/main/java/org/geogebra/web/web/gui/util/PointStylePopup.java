package org.geogebra.web.web.gui.util;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.IComboListener;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

public class PointStylePopup extends PopupMenuButtonW implements IComboListener {

	private static final int DEFAULT_SIZE = 4;
	static HashMap<Integer, Integer> pointStyleMap;
	static int mode;
	private PointStyleModel model;
	private boolean euclidian3D;

	public static PointStylePopup create(AppW app, int mode, boolean hasSlider,
			PointStyleModel model) {
		
		PointStylePopup.mode = mode;
		
		pointStyleMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleMap.put(EuclidianView.getPointStyle(i), i);
		}

		ImageOrText[] pointStyleIcons = new ImageOrText[EuclidianView
				.getPointStyleLength()];
		for (int i = 0; i < EuclidianView.getPointStyleLength(); i++) {
			pointStyleIcons[i] = GeoGebraIconW
					.createPointStyleIcon(EuclidianView.getPointStyle(i));
		}

		return new PointStylePopup(app, pointStyleIcons, 2, -1,
				SelectionTable.MODE_ICON, true,
		        hasSlider, model);
	}

	public static PointStylePopup create(AppW app, int mode,
	        PointStyleModel model) {
		return new PointStylePopup(app, null, 1, -1, 
				SelectionTable.MODE_ICON, false, true, model);
	}


	public PointStylePopup(AppW app, ImageOrText[] data, Integer rows,
			Integer columns, SelectionTable mode,
            boolean hasTable, boolean hasSlider, PointStyleModel model) {
		super(app, data, rows, columns, mode, hasTable, hasSlider, null);
	    this.model = model;
		euclidian3D = false;
    }

	public void setModel(PointStyleModel model) {
		this.model = model;
	}
	
	@Override
	public void update(Object[] geos) {
		updatePanel(geos);
	}

	@Override
	public Object updatePanel(Object[] geos) {
		model.setGeos(geos);
		
		if (!model.hasGeos()) {
			this.setVisible(false);
			return null;
		}
		
		boolean geosOK = model.checkGeos(); 
		
		this.setVisible(geosOK);

		if (geosOK) {
			getMyTable().setVisible(!euclidian3D);

			model.updateProperties();

			PointProperties geo0 = (PointProperties) model.getGeoAt(0);
			if (hasSlider()) {
				setSliderValue(geo0.getPointSize());
			}
			
			
			setSelectedIndex(pointStyleMap.get(euclidian3D ? 0 : geo0
			        .getPointStyle()));

			this.setKeepVisible(EuclidianConstants.isMoveOrSelectionMode(mode));
		}
		return this;
	}
	

	//			setSliderValue(((PointProperties) geo).getPointSize());
	@Override
	public void handlePopupActionEvent(){
		super.handlePopupActionEvent();
 		model.applyChanges(getSelectedIndex());
	}
	
	@Override
	public ImageOrText getButtonIcon() {
		if (getSelectedIndex() > -1) {
			return GeoGebraIconW
					.createPointStyleIcon(EuclidianView
							.getPointStyle(this.getSelectedIndex()));
		}
		return new ImageOrText();
	}
	
	@Override 
	public int getSliderValue() {
		int val = super.getSliderValue();
		return val == -1 ? DEFAULT_SIZE : val;
	}

	@Override
	public void setSelectedIndex(int index) {
	    super.setSelectedIndex(index);		
    }

	@Override
	public void addItem(String item) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	public void setSelectedItem(String item) {
	    // TODO Auto-generated method stub
	    
    }

	public boolean isEuclidian3D() {
		return euclidian3D;
	}

	public void setEuclidian3D(boolean euclidian3d) {
		euclidian3D = euclidian3d;
	}

	@Override
	public void clearItems() {
		// TODO Auto-generated method stub

	}

	public void addItem(GeoElement item) {
		// TODO Auto-generated method stub

	}

}
