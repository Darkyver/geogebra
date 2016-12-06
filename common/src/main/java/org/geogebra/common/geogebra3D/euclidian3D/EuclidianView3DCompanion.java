package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.EuclidianSettings3D;

/**
 * Companion for EuclidianView3D
 * 
 */
public class EuclidianView3DCompanion extends EuclidianViewCompanion {

	public EuclidianView3DCompanion(EuclidianView view) {
		super(view);
	}

	@Override
	public EuclidianView3D getView() {
		return (EuclidianView3D) super.getView();
	}

	/**
	 * draws the mouse cursor (for glasses)
	 *
	 * @param renderer1
	 *            renderer
	 */
	public void drawMouseCursor(Renderer renderer1) {
		if (!getView().hasMouse()) {
			return;
		}

		if (getView().getProjection() != EuclidianView3D.PROJECTION_GLASSES) {
			return;
		}

		GPoint mouseLoc = getView().getEuclidianController().getMouseLoc();
		if (mouseLoc == null) {
			return;
		}

		Coords v;

		if (getView().getCursor3DType() == EuclidianView3D.CURSOR_DEFAULT) {
			// if mouse is over nothing, use mouse coords and screen for depth
			v = new Coords(mouseLoc.x + renderer1.getLeft(),
					-mouseLoc.y + renderer1.getTop(), 0, 1);
		} else {
			// if mouse is over an object, use its depth and mouse coords
			Coords eye = renderer1.getPerspEye();
			double z = getView().getToScreenMatrix()
					.mul(getView().getCursor3D().getCoords()).getZ() + 20; // to
																			// be
																			// over
			double eyeSep = renderer1.getEyeSep(); // TODO eye lateralization

			double x = mouseLoc.x + renderer1.getLeft() + eyeSep - eye.getX();
			double y = -mouseLoc.y + renderer1.getTop() - eye.getY();
			double dz = eye.getZ() - z;
			double coeff = dz / eye.getZ();

			v = new Coords(x * coeff - eyeSep + eye.getX(),
					y * coeff + eye.getY(), z, 1);
		}

		getView().drawMouseCursor(renderer1, v);

	}

	public void drawFreeCursor(Renderer renderer1) {
		// free point on xOy plane
		renderer1.drawCursor(PlotterCursor.TYPE_CROSS2D);
	}

	public GeoElement getLabelHit(GPoint p, PointerEventType type) {
		if (type == PointerEventType.TOUCH) {
			return null;
		}
		return getView().getRenderer().getLabelHit(p);
	}

	/**
	 * @return mouse pick width for openGL picking
	 */
	public int getMousePickWidth() {
		return 3;
	}

	@Override
	public boolean isMoveable(GeoElement geo) {
		return geo.isMoveable();
	}

	public int getCapturingThreshold(PointerEventType type) {
		return getView().getApplication().getCapturingThreshold(type);
	}

	public void setZNearest(double zNear) {
		// used for some input3D
	}

	public void resetAllVisualStyles() {
		// used for some input3D
	}

	public void resetOwnDrawables() {
		// used for some input3D
	}

	public void update() {
		// used for some input3D
	}

	public void draw(Renderer renderer1) {
		// used for some input3D
	}

	public void drawHidden(Renderer renderer1) {
		// used for some input3D
	}

	public void drawTransp(Renderer renderer1) {
		// used for some input3D
	}

	public void drawHiding(Renderer renderer1) {
		// used for some input3D
	}

	public double getScreenZOffset() {
		return 0;
	}

	public void drawPointAlready(GeoPoint3D point) {
		getView().drawPointAlready(point.getMoveMode());
	}

	/**
	 * rotate to default
	 */
	public void setDefaultRotAnimation() {
		getView().setRotAnimation(EuclidianView3D.ANGLE_ROT_OZ,
				EuclidianView3D.ANGLE_ROT_XOY, false);
	}

	public void getXMLForStereo(StringBuilder sb, int eyeDistance, int sep) {
		if (eyeDistance != EuclidianSettings3D.PROJECTION_PERSPECTIVE_EYE_DISTANCE_DEFAULT) {
			sb.append("\" distance=\"");
			sb.append(eyeDistance);
		}
		if (sep != EuclidianSettings3D.EYE_SEP_DEFAULT) {
			sb.append("\" separation=\"");
			sb.append(sep);
		}
	}

	public void setBackground(GColor color) {
		if (color != null) {
			getView().setBackground(color, color);
		}

	}

	/**
	 *
	 * @return true if consumes space key hitted
	 */
	public boolean handleSpaceKey() {
		return false;
	}

	protected boolean moveCursorIsVisible() {
		return getView().cursorIsTranslateViewCursor()
				|| getView().getEuclidianController()
						.getMode() == EuclidianConstants.MODE_TRANSLATEVIEW;
	}

	protected void drawTranslateViewCursor(Renderer renderer1,
			EuclidianCursor cursor, GeoPoint3D cursorOnXOYPlane,
			CoordMatrix4x4 cursorMatrix) {

		if (getView().getApplication().has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			switch (cursor) {
			default:
			case MOVE:
				renderer1.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
				getView().drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
				renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
				break;
			case RESIZE_X:
			case RESIZE_Y:
			case RESIZE_Z:
				renderer1.setMatrix(cursorMatrix);
				getView().getRenderer()
						.drawCursor(PlotterCursor.TYPE_ALREADY_Z);
				renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
				break;
			}
		} else {
			renderer1.setMatrix(cursorOnXOYPlane.getDrawingMatrix());
			getView().drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
			renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
		}

	}

	public void updateStylusBeamForMovedGeo() {
		// used for some 3D inputs
	}

}
