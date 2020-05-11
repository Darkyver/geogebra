package org.geogebra.web.full.euclidian.inline;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.richtext.impl.Carota;
import org.geogebra.web.richtext.impl.CarotaDocument;
import org.geogebra.web.richtext.impl.CarotaRange;
import org.geogebra.web.richtext.impl.CarotaUtil;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

import jsinterop.annotations.JsFunction;

public class InlineTableControllerW implements InlineTableController {

	private static final int CELL_HEIGHT = 36;
	private static final int CELL_WIDTH = 100;

	private static boolean hypergridLoaded;

	private GeoInlineTable table;
	private final EuclidianView view;

	private Element tableElement;
	private Style style;

	private Element elemR;
	private Element elemE;
	private CarotaDocument exampleRenderer;
	private CarotaDocument editor;

	/**
	 *
	 * @param view view
	 * @param table editable table
	 */
	public InlineTableControllerW(GeoInlineTable table, EuclidianView view, Element parent) {
		this.table = table;
		this.view = view;
		CarotaUtil.ensureInitialized(view.getFontSize());
		prepareCarota();
		if (hypergridLoaded) {
			initTable(parent);
		} else {
			load(parent);
		}
	}

	public void update() {
		GPoint2D location = table.getLocation();

		setLocation(view.toScreenCoordX(location.x),
				view.toScreenCoordY(location.y));

		setWidth(2 * CELL_WIDTH);
		setHeight(2 * CELL_HEIGHT);

		setAngle(0);
	}

	@Override
	public void format(String key, Object val) {
		CarotaRange selection = editor.selectedRange();
		CarotaRange range = selection.getStart() == selection.getEnd() ? editor.documentRange()
				: selection;
		range.setFormatting(key, val);
	}

	@Override
	public void setLocation(int x, int y) {
		style.setLeft(x, Style.Unit.PX);
		style.setTop(y, Style.Unit.PX);
	}

	@Override
	public void setWidth(int width) {
		style.setWidth(width, Style.Unit.PX);
	}

	@Override
	public void setHeight(int height) {
		style.setHeight(height, Style.Unit.PX);
	}

	@Override
	public void setAngle(double angle) {
		style.setProperty("transform", "rotate(" + angle + "rad)");
	}

	@Override
	public void removeFromDom() {
		if (tableElement != null) {
			tableElement.removeFromParent();
		}
	}

	private void prepareCarota() {
		this.elemR = DOM.createDiv();
		RootPanel.getBodyElement().appendChild(elemR);
		elemR.getStyle().setWidth(CELL_WIDTH, Style.Unit.PX);
		elemR.getStyle().setHeight(CELL_HEIGHT, Style.Unit.PX);
		elemR.getStyle().setVisibility(Style.Visibility.HIDDEN);
		this.exampleRenderer = Carota.get().getEditor().create(elemR);

		elemE = DOM.createDiv();
		elemE.getStyle().setWidth(CELL_WIDTH, Style.Unit.PX);
		elemE.getStyle().setHeight(CELL_HEIGHT, Style.Unit.PX);
		this.editor = Carota.get().getEditor().create(elemE);
	}

	private void load(Element parent) {
		ScriptElement gmInject = Document.get().createScriptElement();
		// TODO add as dependency
		gmInject.setSrc("https://fin-hypergrid.github.io/core/demo/build/fin-hypergrid.js");
		ResourcesInjector.loadJS(gmInject, new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				hypergridLoaded = true;
				initTable(parent);
			}

			@Override
			public void onError() {
				Log.warn("Could not load Hypergrid");
			}

			@Override
			public void cancel() {
				// no need to cancel
			}
		});
	}

	private void initTable(Element parent) {
		JsArrayMixed dataJ = JsArrayMixed.createArray().cast();
		for (int i = 0; i < table.getRows(); i++) {
			JsArrayString rowJ = JsArrayString.createArray().cast();
			for (int j = 0; j < table.getColumns(); j++) {
				rowJ.push(table.getContents(i, j));
			}
			dataJ.push(rowJ);
		}

		tableElement = DOM.createDiv();
		tableElement.addClassName("mowWidget");
		parent.appendChild(tableElement);

		initTable(tableElement, dataJ, elemR, elemE,
				editor, exampleRenderer, new EditCallback() {
			@Override
			public void onEdit(int x, int y, String value) {
				table.setContents(y, x, value);
				view.getApplication().storeUndoInfo();
			}
		});

		style = tableElement.getStyle();
		style.setPosition(Style.Position.ABSOLUTE);

		update();
	}

	private native void initTable(Element parent, JsArrayMixed data,
			Element elemR, Element elemE,
			CarotaDocument exampleEditor, CarotaDocument exampleRenderer,
			EditCallback callback) /*-{
		var grid = new $wnd.fin.Hypergrid(parent);
		grid.properties.showHeaderRow = false;
		grid.properties.rowHeaderNumbers = false;
		grid.properties.rowHeaderCheckboxes = false;
		grid.setData(data);
		for (var row = 0; row < data.length; row++) {
			grid.setRowHeight(row, 36)
		}
		for (var row = 0; row < data[0].length; row++) {
			grid.setColumnWidth(row, 100)
		}

		var richtextRenderer = grid.cellRenderers.BaseClass.extend({
			paint: function(gc, config) {
				var x = config.bounds.x,
					y = config.bounds.y;
				if (exampleRenderer && exampleRenderer.load) {
					exampleRenderer.width(config.bounds.width);
					elemR.style.width = config.bounds.width+"px";
					exampleRenderer.load(JSON.parse(config.value || "[]"), false);
					var canvas = elemR.querySelector("canvas");
					canvas && gc.drawImage(canvas, x, y);
				}
			}
		});
		var richtextEditor = grid.cellEditors.BaseClass.extend({
			template: '<div style="position:absolute;background-color:white"><div/></div>',
			showEditor: function() {
				this.el.style.display = 'inline';
				this.el.firstChild.appendChild(elemE);
			},
			saveEditorValue: function(value) {
				var x = this.event.gridCell.x;
				var y = this.event.gridCell.y;
				callback(x, y, value);
				this["super"].saveEditorValue.call(this, value);
			},
			setEditorValue: function(value) {
				exampleEditor.load(JSON.parse(value || "[]"), true);
			},
			getEditorValue: function(value) {
				return JSON.stringify(exampleEditor.save());
			},
			setBounds: function(bounds){
				this["super"].setBounds.call(this, bounds);
				exampleEditor.width(bounds.width - 2);
				elemE.style.width = bounds.width+"px";
				elemE.style.height = bounds.height+"px";
			},
			takeFocus: function() {}
		})
		grid.cellRenderers.add('Carota', richtextRenderer);
		grid.cellEditors.add('CarotaEditor', richtextEditor);
		grid.properties.renderer = ['Carota'];
		grid.properties.editor = 'CarotaEditor';
		grid.repaint();
	}-*/;

	@JsFunction
	private interface EditCallback {
		void onEdit(int x, int y, String value);
	}
}
