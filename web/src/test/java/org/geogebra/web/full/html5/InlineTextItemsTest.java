package org.geogebra.web.full.html5;

import static junit.framework.TestCase.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.AppMocker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ TextAreaElement.class, InlineTextToolbar.class})
public class InlineTextItemsTest {

	private ContextMenuGeoElementW contextMenu;
	private Construction construction;
	private AppW app;
	private GPoint2D point;
	private BaseWidgetFactory factory;

	@Before
	public void setUp() {
		app = AppMocker.mockNotes(getClass());
		construction = app.getKernel().getConstruction();
		point = new GPoint2D(0, 0);
		factory = new MenuWidgetFactory(app);
		enableSettingsItem();
	}

	private void enableSettingsItem() {
		app.setShowMenuBar(true);
		app.setRightClickEnabled(true);
	}

	@Test
	public void testPolygon() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createPolygon());
		contextMenu = new ContextMenuGeoElementW(app, geos, factory);
		contextMenu.addOtherItems();
		GMenuBarMock menu = (GMenuBarMock) contextMenu.getWrappedPopup().getPopupMenu();
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste", "SEPARATOR", "General.Order", "SEPARATOR",
				"FixObject", "ShowTrace", "Settings"
		);

		assertEquals(expected, menu.getTitles());
	}

	private GeoElement createPolygon() {
		GeoPolygon poly = new GeoPolygon(construction);
		poly.setLabel("poly1");
		return poly;
	}

	private GeoElement createTextInline() {
		GeoInlineText text = new GeoInlineText(construction, point);
		text.setLabel("text1");
		return text;
	}
}