package geogebra.web.euclidian;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.Font;
import geogebra.common.awt.GeneralPath;
import geogebra.common.awt.Graphics2D;
import geogebra.common.awt.Point;
import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.AbstractEuclidianController;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.DrawableND;
import geogebra.common.euclidian.EuclidianStyleConstants;
import geogebra.common.euclidian.AbstractEuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrix;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.main.AbstractApplication;
import geogebra.web.awt.BasicStroke;
import geogebra.web.awt.Color;
import geogebra.web.kernel.gawt.Ellipse2D;
import geogebra.web.kernel.gawt.Line2D;



public class EuclidianView extends AbstractEuclidianView {
	
	geogebra.web.awt.Graphics2D g2 = null;
	
	protected static final long serialVersionUID = 1L;

	public EuclidianView(Canvas canvas,
            AbstractEuclidianController euclidiancontroller, boolean[] showAxes,
            boolean showGrid) {
		
		super(euclidiancontroller, null);
	    // TODO Auto-generated constructor stub
		this.g2 = new geogebra.web.awt.Graphics2D(canvas);
    }

	// zoom rectangle colors
	protected static final Color colZoomRectangle = new Color(200, 200, 230);
	protected static final Color colZoomRectangleFill = new Color(200, 200, 230, 50);

	// STROKES
	protected static MyBasicStroke standardStroke = new MyBasicStroke(1.0f);

	protected static MyBasicStroke selStroke = new MyBasicStroke(
			1.0f + EuclidianStyleConstants.SELECTION_ADD);

	// protected static MyBasicStroke thinStroke = new MyBasicStroke(1.0f);

	// axes strokes
	protected static BasicStroke defAxesStroke = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	protected static BasicStroke boldAxesStroke = new BasicStroke(2.0f, // changed from 1.8f (same as bold grid) Michael Borcherds 2008-04-12
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	// axes and grid stroke
	protected BasicStroke axesStroke, tickStroke, gridStroke;

	protected Line2D.Double tempLine = new Line2D.Double();
	protected Ellipse2D.Double circle = new Ellipse2D.Double(); //polar grid circles
	protected boolean unitAxesRatio;

	static public MyBasicStroke getDefaultStroke() {
		return standardStroke;
	}

	static public MyBasicStroke getDefaultSelectionStroke() {
		return selStroke;
	}
	
	private boolean disableRepaint;
	public void setDisableRepaint(boolean disableRepaint) {
		this.disableRepaint = disableRepaint;
	}
	
	
	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 * @param width 
	 * @param type 
	 * @return stroke
	 */
	public static BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianStyleConstants.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT : standardStroke.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	public static void setAntialiasing(Graphics2D g2d) {
	    // TODO Auto-generated method stub
	    
    }
	
	public void setCoordinateSpaceSize(int width, int height) {
		g2.setCoordinateSpaceWidth(width);
		g2.setCoordinateSpaceHeight(height);
	}
	
	public void synCanvasSize() {
		setCoordinateSpaceSize(g2.getOffsetWidth(), g2.getOffsetHeight());
	}
	
	/**
	 * Gets the coordinate space width of the &lt;canvas&gt;.
	 * 
	 * @return the logical width
	 */
	public int getWidth() {
		return g2.getCoordinateSpaceWidth();
	}

	/**
	 * Gets the coordinate space height of the &lt;canvas&gt;.
	 * 
	 * @return the logical height
	 */
	public int getHeight() {
		return g2.getCoordinateSpaceHeight();
	}
	
	/**
	 * Gets pixel width of the &lt;canvas&gt;.
	 * 
	 * @return the physical width in pixels
	 */
	public int getPhysicalWidth() {
		return g2.getOffsetWidth();
	}
	
	/**
	 * Gets pixel height of the &lt;canvas&gt;.
	 * 
	 * @return the physical height in pixels
	 */
	public int getPhysicalHeight() {
		return g2.getOffsetHeight();
	}
	
	public int getAbsoluteTop() {
		return g2.getAbsoluteTop();
	}
	
	public int getAbsoluteLeft() {
		return g2.getAbsoluteLeft();
	}

	public EuclidianController getEuclidianController() {
	    // TODO Auto-generated method stub
	    return null;
    }

	
	public void clearView() {
	    // TODO Auto-generated method stub
	    
    }

    public AbstractApplication getApplication() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void updateBackgroundImage() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public Graphics2D getBackgroundGraphics() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Graphics2D getTempGraphics2D(Font plainFontCommon) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public AffineTransform getCoordTransform() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public GeneralPath getBoundingPath() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Font getFont() {
	    // TODO Auto-generated method stub
	    return null;
    }

    public geogebra.common.awt.Color getBackgroundCommon() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected void setHeight(int h) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setWidth(int h) {
	    // TODO Auto-generated method stub
	    
    }

    public void repaint() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void initCursor() {
	    // TODO Auto-generated method stub
	    
    }

    public void setSelectionRectangle(Rectangle r) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected void setStyleBarMode(int mode) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    protected Drawable newDrawable(GeoElement ge) {
	    // TODO Auto-generated method stub
	    return null;
    }

    public void zoom(double px, double py, double factor, int i, boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void zoomAxesRatio(double d, boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	public int getPointCapturingMode() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	public void setPointCapturing(int pointCapturingStickyPoints) {
	    // TODO Auto-generated method stub
	    
    }

	public boolean hitAnimationButton(AbstractEvent e) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void setAntialiasing(boolean antialiasing2) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void updateFonts() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void updateSize() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public boolean requestFocusInWindow() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public Hits getHits() {
	    // TODO Auto-generated method stub
	    return null;
    }

	public void setDefaultCursor() {
	    // TODO Auto-generated method stub
	    
    }

	public void setHitCursor() {
	    // TODO Auto-generated method stub
	    
    }


}
