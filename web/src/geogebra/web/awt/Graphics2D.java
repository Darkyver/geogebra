package geogebra.web.awt;

import geogebra.common.awt.AffineTransform;
import geogebra.common.awt.AttributedCharacterIterator;
import geogebra.common.awt.BasicStroke;
import geogebra.common.awt.BufferedImageOp;
import geogebra.common.awt.Color;
import geogebra.common.awt.Composite;
import geogebra.common.awt.Dimension;
import geogebra.common.awt.FontRenderContext;
import geogebra.common.awt.GlyphVector;
import geogebra.common.awt.GraphicsConfiguration;
import geogebra.common.awt.Image;
import geogebra.common.awt.ImageObserver;
import geogebra.common.awt.Key;
import geogebra.common.awt.Paint;
import geogebra.common.awt.RenderableImage;
import geogebra.common.awt.RenderedImage;
import geogebra.common.awt.RenderingHints;
import geogebra.common.factories.AwtFactory;
import geogebra.common.main.AbstractApplication;
import geogebra.web.kernel.gawt.BufferedImage;
import geogebra.web.openjdk.awt.geom.PathIterator;
import geogebra.web.openjdk.awt.geom.Polygon;
import geogebra.web.openjdk.awt.geom.Shape;

import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.Repetition;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Element;

public class Graphics2D extends geogebra.common.awt.Graphics2D {
	
	protected final Canvas canvas;
	private final Context2d context;
	protected geogebra.common.awt.Shape clipShape = null;

	private Font currentFont = new Font("normal");
	private Color color, bgColor;
	private AffineTransform savedTransform;
	private float [] dash_array = null;

	Paint currentPaint = new geogebra.web.awt.Color(255,255,255,255);
	private JsArrayNumber jsarrn;

	/**
	 * @param canvas
	 */
	public Graphics2D(Canvas canvas) {
	    this.canvas = canvas;
	    this.context = canvas.getContext2d();
	    savedTransform = new geogebra.web.awt.AffineTransform();
	    preventContextMenu (canvas.getElement());
    }

	
	private native void preventContextMenu (Element canvas) /*-{
	    canvas.addEventListener("contextmenu",function(e) {
	    	e.preventDefault();
	    	e.stopPropagation();
	    	return false;
	    });
    }-*/;


	@Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		AbstractApplication.debug("implementation needed for 3D"); // TODO Auto-generated

	}

	
	@Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		AbstractApplication.debug("implementation needed for 3D"); // TODO Auto-generated

	}

	//tmp
	/**<p>
	 * Draws a shape.
	 * </p>
	 * 
	 * @param shape
	 */
	public void draw(Shape shape) {
		if (shape == null) {
			GWT.log("Error in EuclidianView.draw");
			return;
		}
		doDrawShape(shape);
		context.stroke();
	}

	protected void doDrawShape(Shape shape) {
		context.beginPath();
		PathIterator it = shape.getPathIterator(null);
		double[] coords = new double[6];
		
		// see #1718
		boolean enableDashEmulation = nativeDashUsed || AbstractApplication.isFullAppGui(); 
		
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				context.moveTo(coords[0], coords[1]);
				if (enableDashEmulation) setLastCoords(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				if (dash_array == null || !enableDashEmulation) {
					context.lineTo(coords[0], coords[1]);
				} else {
					if (nativeDashUsed) {
						context.lineTo(coords[0], coords[1]);
					} else {
						drawDashedLine(pathLastX,pathLastY,coords[0], coords[1],jsarrn, context);
					}
				}
				setLastCoords(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				context.bezierCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				if (enableDashEmulation) setLastCoords(coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				context.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				if (enableDashEmulation) setLastCoords(coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				context.closePath();
			default:
				break;
			}
			it.next();
		}
		//this.closePath();
	}

	private double pathLastX;
	private double pathLastY;
	
	private void setLastCoords(double x, double y) {
	    pathLastX = x;
	    pathLastY = y;  
    }


	@Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		AbstractApplication.debug("implementation needed for beauty"); // TODO Auto-generated
		return false;
	}

	//
	@Override
    public void drawImage(geogebra.common.awt.BufferedImage img, BufferedImageOp op, int x,
	        int y) {
		BufferedImage img2 = geogebra.web.awt.BufferedImage.getGawtImage(img);
		if (img2 != null)
			context.drawImage(img2.getImageElement(), x, y);
	}

	
	@Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawString(String str, int x, int y) {
		context.fillText(str, x, y);
	}

	
	@Override
    public void drawString(String str, float x, float y) {
		context.fillText(str, x, y);
	}

	
	@Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawString(AttributedCharacterIterator iterator, float x,
	        float y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}


	/**
	 * @param shape
	 */
	public void fill(Shape shape) {
		if (shape == null) {
			AbstractApplication.printStacktrace("Error in EuclidianView.fill");
			return;
		}
		doDrawShape(shape);
		context.fill();		
	}


	@Override
    public GraphicsConfiguration getDeviceConfiguration() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public void setComposite(Composite comp) {
		context.setGlobalAlpha(((geogebra.web.awt.AlphaComposite)comp).getAlpha());
		
//		if (comp != null) {
//			float alpha  = ((geogebra.web.awt.AlphaComposite) comp).getAlpha();
//			if (alpha >= 0f && alpha < 1f) {
//				context.setGlobalAlpha(alpha);
//			}
//			context.setGlobalAlpha(0.5d);
//			context.restore();
//		}
	}

	
	@Override
    public void setPaint(Paint paint) {
		if(paint  instanceof Color){
			context.setFillStyle(Color.getColorString((Color)paint));	
			context.setStrokeStyle(Color.getColorString((Color)paint));
			currentPaint = new geogebra.web.awt.Color((geogebra.web.awt.Color)paint);
		}
		else if(paint instanceof GradientPaint){
			context.setFillStyle(((GradientPaint)paint).getGradient(context));
			currentPaint = new GradientPaint((GradientPaint)paint);
		} else if (paint instanceof TexturePaint) {
			currentPaint = new TexturePaint((TexturePaint)paint);
			CanvasPattern ptr = context.createPattern(((TexturePaint)paint).getImg(), Repetition.REPEAT);
			context.setFillStyle(ptr);
			//why we get null here sometimes?
		}

	}

	
	@Override
    public void setStroke(BasicStroke stroke) {
		if (stroke != null) {
			context.setLineWidth(((geogebra.web.awt.BasicStroke)stroke).getLineWidth());
			context.setLineCap(((geogebra.web.awt.BasicStroke)stroke).getEndCapString());
			context.setLineJoin(((geogebra.web.awt.BasicStroke)stroke).getLineJoinString());

			float [] dasharr = ((geogebra.web.awt.BasicStroke)stroke).getDashArray();
			if (dasharr != null) {
				jsarrn = JavaScriptObject.createArray().cast();
				jsarrn.setLength(dasharr.length);
				for (int i = 0; i < dasharr.length; i++)
					jsarrn.set(i, dasharr[i]);
				setStrokeDash( context, jsarrn );
			} else {
				setStrokeDash( context, null );
			}
			dash_array = dasharr;
		}
	}
	
	private boolean nativeDashUsed = false;

	public native void setStrokeDash(Context2d ctx, JsArrayNumber dasharray) /*-{
		if (typeof ctx.mozDash != 'undefined') {
			ctx.mozDash = dasharray;
			this.@geogebra.web.awt.Graphics2D::nativeDashUsed = true;
			
		} else if (typeof ctx.webkitLineDash != 'undefined') {
			ctx.webkitLineDash = dasharray;
			this.@geogebra.web.awt.Graphics2D::nativeDashUsed = true;
		}
	}-*/;


	@Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public Object getRenderingHint(Key hintKey) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public void setRenderingHints(Map<?, ?> hints) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public void addRenderingHints(Map<?, ?> hints) {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated

	}

	
	@Override
    public RenderingHints getRenderingHints() {
		AbstractApplication.debug("implementation needed"); // TODO Auto-generated
		return null;
	}

	
	@Override
    public void translate(int x, int y) {
		context.translate(x, y);
		savedTransform.translate(x, y);
	}

	
	@Override
    public void translate(double tx, double ty) {
		context.translate(tx, ty);
		savedTransform.translate(tx, ty);

	}

	
	@Override
    public void rotate(double theta) {
		context.rotate(theta);
		savedTransform.concatenate(
				new geogebra.web.awt.AffineTransform(
						Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta), 0, 0));

	}

	
	@Override
    public void rotate(double theta, double x, double y) {
		context.translate(x, y);
		context.rotate(theta);
		context.translate(-x, -y);
		savedTransform.concatenate(
				new geogebra.web.awt.AffineTransform(
						Math.cos(theta), Math.sin(theta), -Math.sin(theta), Math.cos(theta), x, y));
	}

	
	@Override
    public void scale(double sx, double sy) {
		context.scale(sx, sy);
		savedTransform.scale(sx, sy);
	}

	
	@Override
    public void shear(double shx, double shy) {
		transform(new geogebra.web.awt.AffineTransform(
			1, shy, shx, 1, 0, 0
		));
	}


	@Override
    public void transform(AffineTransform Tx) {
		context.transform(Tx.getScaleX(), Tx.getShearY(),
				Tx.getShearX(), Tx.getScaleY(),
				((geogebra.web.awt.AffineTransform)Tx).getTranslateX(),
				((geogebra.web.awt.AffineTransform)Tx).getTranslateY());
		savedTransform.concatenate(Tx);
	}

	
	@Override
    public void setTransform(AffineTransform Tx) {
		context.setTransform(Tx.getScaleX(), Tx.getShearY(),
		Tx.getShearX(), Tx.getScaleY(),
		((geogebra.web.awt.AffineTransform)Tx).getTranslateX(),
		((geogebra.web.awt.AffineTransform)Tx).getTranslateY());
		savedTransform = Tx;

	}

	
	@Override
    public AffineTransform getTransform() {
		AffineTransform ret = new geogebra.web.awt.AffineTransform();
		ret.setTransform(savedTransform);
		return ret;
	}
	

	@Override
    public Paint getPaint() {
		return currentPaint;
		/* The other possible solution would be:

		// this could be an array as well, according to the documentation, so more difficult
		FillStrokeStyle fs = context.getFillStyle();
		Paint ret;
		if (fs.getType() == FillStrokeStyle.TYPE_CSSCOLOR) {
			// it is difficult to make a color out of csscolor
			ret = new geogebra.web.awt.Color((CssColor)fs);
		} else if (fs.getType() == FillStrokeStyle.TYPE_GRADIENT) {
			
		} else if (fs.getType() == FillStrokeStyle.TYPE_PATTERN) {
			
		}
		*/
	}

	
	@Override
    public Composite getComposite() {
		return new geogebra.web.awt.AlphaComposite(3, (float) context.getGlobalAlpha());
	
//		context.save();
//		//just to not return null;
//		return new geogebra.web.awt.AlphaComposite(0, 0) {
//		};
	}


	@Override
    public void setBackground(Color color) {
		// This method only affects Graphics2D.clearRect (if there will be present)
		// and getBackground calls - currently Drawable.drawLabel
		this.bgColor = new geogebra.web.awt.Color((geogebra.web.awt.Color)color);
	}


	@Override
    public Color getBackground() {
		return bgColor;
	}


	@Override
    public BasicStroke getStroke() {
		AbstractApplication.debug("implementation needed really"); // TODO Auto-generated

		return new geogebra.web.awt.BasicStroke(
			(float) context.getLineWidth(), 
			geogebra.web.awt.BasicStroke.getCap(context.getLineCap()),
			geogebra.web.awt.BasicStroke.getJoin(context.getLineJoin()),
			0,
			dash_array,
			0
		);
	}

	public void clip(Shape shape2) {

		if (shape2 == null) {
			// for simple clip, no null is allowed
			clipShape = null;
			GWT.log("Error in Graphics2D.setClip");
			return;
		}
		clipShape = new geogebra.web.awt.GenericShape(shape2);
		doDrawShape(shape2);
		context.save();
		context.clip();
	}

	
	@Override
    public FontRenderContext getFontRenderContext() {
		return new geogebra.web.awt.FontRenderContext(context);
	}

	
	@Override
    public Color getColor() {
		return color;
	}

	
	@Override
    public geogebra.common.awt.Font getFont() {
		return currentFont;
	}

	
	
	public void setCoordinateSpaceHeight(int height) {
		canvas.setCoordinateSpaceHeight(height);
    }

	public void setCoordinateSpaceWidth(int width) {
	    canvas.setCoordinateSpaceWidth(width);
    }

	public int getOffsetWidth() {
		return canvas.getOffsetWidth();
    }

	public int getOffsetHeight() {
	   return canvas.getOffsetHeight();
    }

	public int getCoordinateSpaceWidth() {
	   return canvas.getCoordinateSpaceWidth();
    }

	public int getCoordinateSpaceHeight() {
	    return canvas.getCoordinateSpaceHeight();
    }

	public int getAbsoluteTop() {
	    return canvas.getAbsoluteTop();
    }

	public int getAbsoluteLeft() {
	    return canvas.getAbsoluteLeft(); 
    }

	
    @Override
    public void setFont(geogebra.common.awt.Font font) {
    	if(font instanceof geogebra.web.awt.Font){
    		currentFont=(geogebra.web.awt.Font)font;
    		//TODO: pass other parameters here as well
    		try{
    		context.setFont(currentFont.getFullFontString());
    		}
    		catch(Throwable t){
    			AbstractApplication.debug(currentFont.getFullFontString());
    			t.printStackTrace();
    		}
    	}
	    
    }

	
    @Override
    public void setColor(Color fillColor) {
    	context.setStrokeStyle("rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+(fillColor.getAlpha()/255d)+")");
    	context.setFillStyle("rgba("+fillColor.getRed()+","+fillColor.getGreen()+","+fillColor.getBlue()+","+(fillColor.getAlpha()/255d)+")");
    	this.color=fillColor;
    	this.currentPaint = new geogebra.web.awt.Color((geogebra.web.awt.Color)fillColor);
    }

	@Override
    public void clip(geogebra.common.awt.Shape shape) {
		if (shape == null) {
			GWT.log("Error in Graphics2D.clip");
			return;
		}
		clipShape = shape;
		Shape shape2 = geogebra.web.awt.GenericShape.getGawtShape(shape);
		if (shape2 == null) {
			GWT.log("Error in Graphics2D.clip");
			return;
		}
		doDrawShape(shape2);
		context.save();
		context.clip();
    }


    @Override
    public void drawImage(geogebra.common.awt.BufferedImage img, int x, int y,
            BufferedImageOp op) {
    	BufferedImage bi = geogebra.web.awt.BufferedImage.getGawtImage(img);
    	if(bi==null)
    		return;
    	try{
    		context.drawImage(bi.getImageElement(), x, y);
    	} catch (Exception e){
    		AbstractApplication.error("error in context.drawImage method");
    	}
    }

    public void drawGraphics(geogebra.web.awt.Graphics2D gother, int x, int y,
            BufferedImageOp op) {

    	if (gother==null)
    		return;

    	context.drawImage(gother.getCanvas().getCanvasElement(), x, y);
    }

    @Override
    public void fillRect(int x, int y, int w, int h) {
    	context.fillRect(x, y, w, h);
    }

	
    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {

    	/* TODO: there is some differences between the result of
    	 * geogebra.awt.Graphics.drawLine(...) function.
    	 * Here is an attempt to make longer the vertical and horizontal lines:  
    	 
    	int x_1 = Math.min(x1,x2);
    	int y_1 = Math.min(y1,y2);
    	int x_2 = Math.max(x1,x2);
    	int y_2 = Math.max(y1,y2);
    	
    	if(x1==x2){
    		y_1--;
    		y_2++;
    	} else if(y1==y2){
    		x_1--;
    		x_2++;
    	}
    	 	
    	context.beginPath();
    	context.moveTo(x_1, y_1);
    	context.lineTo(x_2, y_2);
    	context.closePath();
    	context.stroke();
*/
    	context.beginPath();
    	context.moveTo(x1, y1);
    	context.lineTo(x2, y2);
    	context.closePath();
    	context.stroke();
    	
    }


	@Override
    public void setClip(geogebra.common.awt.Shape shape) {
		clipShape = shape;
		if (shape == null) {
			// this may be an intentional call to restore the context
			context.restore();
			return;
		}
		Shape shape2 = geogebra.web.awt.GenericShape.getGawtShape(shape);
		if (shape2 == null) {
			GWT.log("Error in Graphics2D.setClip");
			return;
		}
		doDrawShape(shape2);
		if (clipShape != null) {
			// we should call this only if no clip was set or just after another clip to overwrite
			// in this case we don't want to double-clip something so let's restore the context
			context.restore();
		}
		context.save();
		context.clip();
    }


	@Override
    public void draw(geogebra.common.awt.Shape s) {
		draw(geogebra.web.awt.GenericShape.getGawtShape(s));
    }


	@Override
    public void fill(geogebra.common.awt.Shape s) {
		fill(geogebra.web.awt.GenericShape.getGawtShape(s));
    }

	@Override
    public geogebra.common.awt.Shape getClip() {
		return clipShape;
    }

	@Override
	public void drawRect(int x, int y, int width, int height) {
	   context.rect(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		/*
		 * old code: breaks grid with emulated dashed lines, see #1718
		 * 
		geogebra.common.awt.Shape sh = AwtFactory.prototype.newRectangle(x, y, width, height);
		setClip(sh);
		*/
		
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x + width, y);
		context.lineTo(x + width, y + height);
		context.lineTo(x , y + height);
		context.lineTo(x , y);
		context.clip();

	}

	public void setWidth(int w) {
	    canvas.setWidth(w+"px");
    }


	public void setHeight(int h) {
	    canvas.setHeight(h+"px");
    }


	public void setPreferredSize(Dimension preferredSize) {
	    setWidth((int) preferredSize.getWidth());
	    setHeight((int) preferredSize.getHeight());
	    setCoordinateSpaceHeight(getOffsetHeight());
	    setCoordinateSpaceWidth(getOffsetWidth());
    }
	
	public Canvas getCanvas() {
		return this.canvas;
	}


	@Override
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
	    roundRect(x,y,width,height,arcHeight);
	    context.stroke();
	    
    }
	
	/**
	 * Using arc, because arc to has buggy implementation in some browsers 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param r
	 */
	private void roundRect(int x,int y,int w,int h,int r){
		context.beginPath();
		int ey = y+h;
		int ex = x+w;
		float r2d = (float)Math.PI/180;
	    context.moveTo(x+r,y);
	    context.lineTo(ex-r,y);
	    context.arc(ex-r,y+r,r,r2d*270,r2d*360,false);
	    context.lineTo(ex,ey-r);
	    context.arc(ex-r,ey-r,r,r2d*0,r2d*90,false);
	    context.lineTo(x+r,ey);
	    context.arc(x+r,ey-r,r,r2d*90,r2d*180,false);
	    context.lineTo(x,y+r);
	    context.arc(x+r,y+r,r,r2d*180,r2d*270,false);
	    
		context.closePath();
	}

	@Override
    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
		roundRect(x,y,width,height,arcHeight);
	    context.fill();
	    
    }


	public void fillPolygon(Polygon p) {
	   fill(p);
    }
	
	private native void drawDashedLine(double fromX, double fromY, double toX, double toY, JsArrayNumber pattern,Context2d ctx) /*-{
		  // Our growth rate for our line can be one of the following:
		  //   (+,+), (+,-), (-,+), (-,-)
		  // Because of this, our algorithm needs to understand if the x-coord and
		  // y-coord should be getting smaller or larger and properly cap the values
		  // based on (x,y).
		  var lt = function (a, b) { return a <= b; };
		  var gt = function (a, b) { return a >= b; };
		  var capmin = function (a, b) { return $wnd.Math.min(a, b); };
		  var capmax = function (a, b) { return $wnd.Math.max(a, b); };
		
		  var checkX = { thereYet: gt, cap: capmin };
		  var checkY = { thereYet: gt, cap: capmin };
		
		  if (fromY - toY > 0) {
		    checkY.thereYet = lt;
		    checkY.cap = capmax;
		  }
		  if (fromX - toX > 0) {
		    checkX.thereYet = lt;
		    checkX.cap = capmax;
		  }
		
		  //ctx.moveTo(fromX, fromY);
		  var offsetX = fromX;
		  var offsetY = fromY;
		  var idx = 0, dash = true;
		  while (!(checkX.thereYet(offsetX, toX) && checkY.thereYet(offsetY, toY))) {
		    var ang = $wnd.Math.atan2(toY - fromY, toX - fromX);
		    var len = pattern[idx];
		
		    offsetX = checkX.cap(toX, offsetX + ($wnd.Math.cos(ang) * len));
		    offsetY = checkY.cap(toY, offsetY + ($wnd.Math.sin(ang) * len));
		
		    if (dash) ctx.lineTo(offsetX, offsetY);
		    else ctx.moveTo(offsetX, offsetY);
		
		    idx = (idx + 1) % pattern.length;
		    dash = !dash;
		  }
		
		
	}-*/;
	
	public ImageData getImageData(int x, int y, int width, int height) {
		return context.getImageData(x, y, width, height);
	}

}
