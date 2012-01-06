package geogebra.common.gui;

import java.util.ArrayList;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.geos.GeoElement;

public abstract class GuiManager {

	public abstract void removeSpreadsheetTrace(GeoElement recordObject);

	public abstract void updateMenubarSelection();

	public abstract DialogManager getDialogManager();

	public abstract void showPopupMenu(ArrayList<GeoElement> selectedGeos,
			EuclidianViewInterfaceCommon euclidianViewInterfaceCommon,
			Point mouseLoc);

}
