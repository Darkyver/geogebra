package geogebra3D.gui.dialogs.options;

import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.dialog.options.AxisPanel;
import geogebra.gui.dialog.options.OptionsEuclidianD;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.LayoutUtil;
import geogebra.main.AppD;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoClippingCube3D;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * options for 3D view
 * @author mathieu
 *
 */
public class OptionsEuclidian3D extends OptionsEuclidianD {
	
	private AxisPanel3D zAxisPanel;
	
	private JCheckBox cbUseClipping, cbShowClipping;

	private JRadioButton radioClippingSmall, radioClippingMedium, radioClippingLarge;

	private JPanel clippingOptionsPanel, boxSizePanel;

	/**
	 * constructor
	 * @param app application
	 * @param view 3D view
	 */
	public OptionsEuclidian3D(AppD app, EuclidianViewND view) {
		super(app, view);
		

		enableStuff(false);
		
		updateGUI();
	}
	
	
	
	
	@Override
	protected JPanel buildBasicPanel() {

		//-------------------------------------
		// clipping options panel
		clippingOptionsPanel = new JPanel();
		clippingOptionsPanel.setLayout(new BoxLayout(clippingOptionsPanel, BoxLayout.Y_AXIS));

		// clipping
		cbUseClipping = new JCheckBox(app.getPlain("UseClipping")); 						
		clippingOptionsPanel.add(cbUseClipping);  
		clippingOptionsPanel.add(Box.createRigidArea(new Dimension(10,0)));
		cbShowClipping = new JCheckBox(app.getPlain("ShowClipping")); 						
		clippingOptionsPanel.add(cbShowClipping);  
		
		boxSizePanel = new JPanel();
		boxSizePanel.setLayout(new BoxLayout(boxSizePanel, BoxLayout.Y_AXIS));
		radioClippingSmall = new JRadioButton(app.getPlain("small"));
		radioClippingMedium = new JRadioButton(app.getPlain("medium"));
		radioClippingLarge = new JRadioButton(app.getPlain("large"));
		boxSizePanel.add(radioClippingSmall);
		boxSizePanel.add(radioClippingMedium);
		boxSizePanel.add(radioClippingLarge);
		radioClippingSmall.addActionListener(this);
		radioClippingMedium.addActionListener(this);
		radioClippingLarge.addActionListener(this);
		ButtonGroup boxSizeGroup = new ButtonGroup();
		boxSizeGroup.add(radioClippingSmall);
		boxSizeGroup.add(radioClippingMedium);
		boxSizeGroup.add(radioClippingLarge);

		
		//-------------------------------------

		JPanel basicPanel = super.buildBasicPanel();
        basicPanel.add(clippingOptionsPanel);
        basicPanel.add(boxSizePanel);

		return basicPanel;
	}

	private void enableStuff(boolean flag){
		//	TODO remove when implemented
		

		//btBackgroundColor.setEnabled(flag);

		tfMinX.setEnabled(flag);
		tfMaxX.setEnabled(flag);
		tfMinY.setEnabled(flag);
		tfMaxY.setEnabled(flag);

		btAxesColor.setEnabled(flag);
		cbAxesStyle.setEnabled(flag);
		cbShowMouseCoords.setEnabled(flag);
		cbTooltips.setEnabled(flag);
		
		((AxisPanel3D) xAxisPanel).enableStuff(flag);
		((AxisPanel3D) yAxisPanel).enableStuff(flag);
		zAxisPanel.enableStuff(flag);
				

		cbGridManualTick.setEnabled(flag);  
        ncbGridTickX.setEnabled(flag); 
		ncbGridTickY.setEnabled(flag);
		cbGridTickAngle.setEnabled(flag);  
		cbGridStyle.setEnabled(flag);  
		cbGridType.setEnabled(flag);  
		cbBoldGrid.setEnabled(flag); 
		btGridColor.setEnabled(flag); 
		
	}

	
	
	@Override
	public void updateGUI() {
		super.updateGUI();

		//basic panel
		cbUseClipping.removeActionListener(this);
		cbUseClipping.setSelected(((EuclidianView3D) view).useClippingCube());
		cbUseClipping.addActionListener(this);
		
		cbShowClipping.removeActionListener(this);
		cbShowClipping.setSelected(((EuclidianView3D) view).showClippingCube());
		cbShowClipping.addActionListener(this);  
		
		/*
		radioClippingSmall.removeActionListener(this);
		radioClippingMedium.removeActionListener(this);
		radioClippingLarge.removeActionListener(this);
		*/
		int flag = ((EuclidianView3D) view).getClippingReduction();
		radioClippingSmall.setSelected(flag==GeoClippingCube3D.REDUCTION_SMALL);
		radioClippingMedium.setSelected(flag==GeoClippingCube3D.REDUCTION_MEDIUM);
		radioClippingLarge.setSelected(flag==GeoClippingCube3D.REDUCTION_LARGE);
		/*
		radioClippingSmall.addActionListener(this);
		radioClippingMedium.addActionListener(this);
		radioClippingLarge.addActionListener(this);
	*/

		//z axis panel
		zAxisPanel.updatePanel();
		
		//projection
		//tfPersp.removeActionListener(this);
		tfPersp.setText(""+((EuclidianView3D) view).getProjectionPerspectiveValue());
		//tfPersp.addActionListener(this);
		
		tfAnaglyph.setText(""+((EuclidianView3D) view).getEyeSepFactor()*1000);
		cbAnaglyphGray.setSelected(((EuclidianView3D) view).isAnaglyphGrayScaled());
		cbAnaglyphShutDownGreen.setSelected(((EuclidianView3D) view).isAnaglyphShutDownGreen());
		
		
		tfObliqueAngle.setText(""+((EuclidianView3D) view).getProjectionObliqueAngle());
		tfObliqueFactor.setText(""+((EuclidianView3D) view).getProjectionObliqueFactor());		
	}
	
	
	
	@Override
	protected void initAxisPanels(){

        xAxisPanel = new AxisPanel3D(app, view, 0);
        yAxisPanel = new AxisPanel3D(app, view, 1);
        zAxisPanel = new AxisPanel3D(app, view, 2);       
	}
	
	@Override
	protected void addTabs(){
		super.addTabs();
		tabbedPane.addTab("" , buildProjectionPanel() );
	}
	
	@Override
	protected void addAxisTabs(){
		super.addAxisTabs();
		tabbedPane.addTab("", new JScrollPane(zAxisPanel));		

	}
	
	
	private JLabel[] projectionLabel;
	private JTextField tfPersp, tfAnaglyph, tfObliqueAngle, tfObliqueFactor;
	private JLabel tfPerspLabel, tfAnaglyphLabel, tfObliqueAngleLabel, tfObliqueFactorLabel;
	private ProjectionButtons projectionButtons;
	private JCheckBox cbAnaglyphGray;
	private JLabel cbAnaglyphGrayLabel;
	private JCheckBox cbAnaglyphShutDownGreen;
	private JLabel cbAnaglyphShutDownGreenLabel;
	
	
	private class ProjectionButtons {
		
		private JButton[] buttons;
		
		private int buttonSelected;
		
		private EuclidianView3D view;
		
		private ProjectionButtons(OptionsEuclidian3D options){
			
			view = (EuclidianView3D) options.view;
			
			buttons = new JButton[4];
			
			buttons[EuclidianView3D.PROJECTION_ORTHOGRAPHIC] =  new JButton(app.getImageIcon("stylebar_vieworthographic.gif"));
			buttons[EuclidianView3D.PROJECTION_PERSPECTIVE] =  new JButton(app.getImageIcon("stylebar_viewperspective.gif"));
			buttons[EuclidianView3D.PROJECTION_ANAGLYPH] =  new JButton(app.getImageIcon("stylebar_viewanaglyph.gif"));
			buttons[EuclidianView3D.PROJECTION_OBLIQUE] =  new JButton(app.getImageIcon("stylebar_viewcav.gif"));
			
			for (int i=0; i<4; i++)
				buttons[i].addActionListener(options);			
			
			buttonSelected = view.getProjection();
			buttons[buttonSelected].setSelected(true);
		}
		
		private JButton getButton(int i){
			return buttons[i];
		}
		
		void setSelected(int i){
			buttons[buttonSelected].setSelected(false);
			buttonSelected = i;
			buttons[buttonSelected].setSelected(true);
			
		}
		
		
	}
	
	private JPanel buildProjectionPanel() {
		
		//JLabel label;		
			
		
		projectionLabel = new JLabel[4]; // "orthographic", "perspective", "anaglyph" etc.
		for(int i=0;i<4;i++)
			projectionLabel[i] = new JLabel("");
		
		projectionButtons = new ProjectionButtons(this);
		
		
        JPanel orthoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        orthoPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC));
        orthoPanel.add(projectionLabel[0]);
            
 		
        JPanel perspPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        perspPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_PERSPECTIVE));
        perspPanel.add(projectionLabel[1]);
        tfPerspLabel = new JLabel("");
        perspPanel.add(tfPerspLabel);
        tfPersp = new MyTextField(app,5);
        tfPersp.addActionListener(this);
        tfPersp.addFocusListener(this);
        perspPanel.add(tfPersp);
        
        
        JPanel anaglyphPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        anaglyphPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_ANAGLYPH));
        anaglyphPanel.add(projectionLabel[2]);
        tfAnaglyphLabel = new JLabel("");
        anaglyphPanel.add(tfAnaglyphLabel);
        tfAnaglyph = new MyTextField(app,3);
        tfAnaglyph.addActionListener(this);
        tfAnaglyph.addFocusListener(this);
        anaglyphPanel.add(tfAnaglyph);
        cbAnaglyphGray = new JCheckBox();
        cbAnaglyphGray.addActionListener(this);
        cbAnaglyphGrayLabel = new JLabel("");
        anaglyphPanel.add(cbAnaglyphGray);
        anaglyphPanel.add(cbAnaglyphGrayLabel);
        cbAnaglyphShutDownGreen = new JCheckBox();
        cbAnaglyphShutDownGreen.addActionListener(this);
        cbAnaglyphShutDownGreenLabel = new JLabel("");
        anaglyphPanel.add(cbAnaglyphShutDownGreen);
        anaglyphPanel.add(cbAnaglyphShutDownGreenLabel);
           
        

		
        JPanel cavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        cavPanel.add(projectionButtons.getButton(EuclidianView3D.PROJECTION_OBLIQUE));
        cavPanel.add(projectionLabel[EuclidianView3D.PROJECTION_OBLIQUE]);
        tfObliqueAngleLabel = new JLabel("");
        cavPanel.add(tfObliqueAngleLabel);
        tfObliqueAngle = new MyTextField(app,4);
        tfObliqueAngle.addActionListener(this);
        tfObliqueAngle.addFocusListener(this);
        cavPanel.add(tfObliqueAngle);
        tfObliqueFactorLabel = new JLabel("");
        cavPanel.add(tfObliqueFactorLabel);
        tfObliqueFactor = new MyTextField(app,4);
        tfObliqueFactor.addActionListener(this);
        tfObliqueFactor.addFocusListener(this);
        cavPanel.add(tfObliqueFactor);
 		
        

		//==========================================
		// create basic panel and add all sub panels
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setBorder(BorderFactory.createEmptyBorder(5,5,2,5));
        
		/*
        northPanel.add(dimPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0,16)));
        northPanel.add(axesOptionsPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0,16)));
        northPanel.add(bgPanel);
        */
		northPanel.add(orthoPanel);
		northPanel.add(perspPanel);
		northPanel.add(anaglyphPanel);
		northPanel.add(cavPanel);

        // use a BorderLayout to keep sub panels together
        JPanel ret = new JPanel(new BorderLayout());
        ret.add(northPanel, BorderLayout.NORTH);
        
        return ret;
		
	}
	
	@Override
	protected void setTabLabels(){
		tabbedPane.setTitleAt(0,app.getMenu("Properties.Basic"));
        tabbedPane.setTitleAt(1, app.getPlain("xAxis"));
        tabbedPane.setTitleAt(2, app.getPlain("yAxis"));   
        tabbedPane.setTitleAt(3, app.getPlain("zAxis"));   
        tabbedPane.setTitleAt(4, app.getMenu("Grid"));	
        tabbedPane.setTitleAt(5, app.getMenu("Projection"));	
	}
	
	@Override
	public void setLabels(){
		super.setLabels();
		
		zAxisPanel.setLabels();
		
		//basic tab
		clippingOptionsPanel.setBorder(LayoutUtil.titleBorder(app
				.getPlain("Clipping")));
		cbUseClipping.setText(app.getPlain("UseClipping"));
		cbShowClipping.setText(app.getPlain("ShowClipping"));

		boxSizePanel.setBorder(LayoutUtil.titleBorder(app
				.getPlain("BoxSize")));
		radioClippingSmall.setText(app.getPlain("small"));
		radioClippingMedium.setText(app.getPlain("medium"));
		radioClippingLarge.setText(app.getPlain("large"));
		
		//perspective tab
		projectionLabel[0].setText(app.getPlain("orthographic"));
		
		projectionLabel[1].setText(app.getPlain("perspective")+":");
		tfPerspLabel.setText(app.getPlain("eyeDistance")+":");
		
		projectionLabel[2].setText(app.getPlain("anaglyph")+":");
		tfAnaglyphLabel.setText(app.getPlain("eyesSeparation")+":");
		cbAnaglyphGrayLabel.setText(app.getPlain("grayScale"));
		cbAnaglyphShutDownGreenLabel.setText(app.getPlain("shutDownGreen"));
		
		projectionLabel[3].setText(app.getPlain("oblique")+":");
		tfObliqueAngleLabel.setText(app.getPlain("angle")+":");
		tfObliqueFactorLabel.setText(app.getPlain("factor")+":");
		
		projectionButtons.setSelected(((EuclidianView3D) view).getProjection());
	}
	
	@Override
	protected void doActionPerformed(Object source) {	
			
		if (source == cbUseClipping) {
			((EuclidianView3D) view).setUseClippingCube(cbUseClipping.isSelected());			
		}else if (source == cbShowClipping) {
				((EuclidianView3D) view).setShowClippingCube(cbShowClipping.isSelected());		
		}else if (source == radioClippingSmall) {		
			((EuclidianView3D) view).setClippingReduction(GeoClippingCube3D.REDUCTION_SMALL);
		}else if (source == radioClippingMedium) {		
			((EuclidianView3D) view).setClippingReduction(GeoClippingCube3D.REDUCTION_MEDIUM);
		}else if (source == radioClippingLarge) {		
			((EuclidianView3D) view).setClippingReduction(GeoClippingCube3D.REDUCTION_LARGE);
		}else if (source == tfPersp) {
			try{
				double val = Double.parseDouble(tfPersp.getText());
				if (! Double.isNaN(val)) {
					double min =1;
					if (val<min){
						val=min;
						tfPersp.setText(""+val);
					}

					((EuclidianView3D) view).setProjectionPerspectiveValue(val);	
				}
			}catch(NumberFormatException e){
				tfPersp.setText(""+((EuclidianView3D) view).getProjectionPerspectiveValue());
			}
		}else if (source == tfAnaglyph) {
			try{
				double val = Double.parseDouble(tfAnaglyph.getText());
				if (! Double.isNaN(val)) {
					if (val<0){
						val=0;
						tfAnaglyph.setText(""+val);
					}

					((EuclidianView3D) view).setEyeSepFactor(val/1000);	
				}
			}catch(NumberFormatException e){
				tfAnaglyph.setText(""+((EuclidianView3D) view).getEyeSepFactor()*1000);
			}
		}else if (source == tfObliqueAngle) {
			try{
				double val = Double.parseDouble(tfObliqueAngle.getText());
				if (! Double.isNaN(val)) {

					((EuclidianView3D) view).setProjectionObliqueAngle(val);
				}
			}catch(NumberFormatException e){
				tfObliqueAngle.setText(""+((EuclidianView3D) view).getProjectionObliqueAngle());
			}
		}else if (source == tfObliqueFactor) {
			try{
				double val = Double.parseDouble(tfObliqueFactor.getText());
				if (! Double.isNaN(val)) {
					if (val<0){
						val=0;
						tfObliqueFactor.setText(""+val);
					}
					((EuclidianView3D) view).setProjectionObliqueFactor(val);
				}
			}catch(NumberFormatException e){
				tfObliqueFactor.setText(""+((EuclidianView3D) view).getProjectionObliqueFactor());
			}
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_ORTHOGRAPHIC)) {
			((EuclidianView3D) view).setProjectionOrthographic();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_ORTHOGRAPHIC);
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_PERSPECTIVE)) {
			((EuclidianView3D) view).setProjectionPerspective();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_PERSPECTIVE);
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_ANAGLYPH)) {
			((EuclidianView3D) view).setProjectionAnaglyph();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_ANAGLYPH);
		}else if (source == projectionButtons.getButton(EuclidianView3D.PROJECTION_OBLIQUE)) {
			((EuclidianView3D) view).setProjectionOblique();
			projectionButtons.setSelected(EuclidianView3D.PROJECTION_OBLIQUE);
		}else if (source == cbAnaglyphGray) {
			((EuclidianView3D) view).setAnaglyphGrayScaled(cbAnaglyphGray.isSelected());
		/*
		}else if (source == cbAnaglyphGrayLabel) {
			boolean flag = !cbAnaglyphGray.isSelected();
			cbAnaglyphGray.setSelected(flag);
			((EuclidianView3D) view).setAnaglyphGrayScaled(flag);
			*/
		} else if (source == cbAnaglyphShutDownGreen) {
			((EuclidianView3D) view).setAnaglyphShutDownGreen(cbAnaglyphShutDownGreen.isSelected());
			
		} else {
			super.doActionPerformed(source);		
		}
	}
	
	
	@Override
	protected void updateFont(Font font) {

		super.updateFont(font);
		
		zAxisPanel.updateFont();
	}
	
	

	private class AxisPanel3D extends AxisPanel{
		private static final long serialVersionUID = 1L;
		final static protected int AXIS_Z = 2;

		public AxisPanel3D(AppD app, EuclidianViewND view, int axis) {
			super(app, view, axis);
		}
		
		protected void enableStuff(boolean flag){
//			TODO remove when implemented
			
			cbAxisNumber.setEnabled(flag);
			cbManualTicks.setEnabled(flag);
			cbPositiveAxis.setEnabled(flag);
			cbDrawAtBorder.setEnabled(flag);
			ncbTickDist.setEnabled(flag);	
		    cbTickStyle.setEnabled(flag); 
		    cbAxisLabel.setEnabled(flag);
		    cbUnitLabel.setEnabled(flag);
			tfCross.setEnabled(flag);		
		}
		
		@Override
		protected String getString(){
			if (axis==AXIS_Z) {
				return "zAxis";
			}
			return super.getString();
		}	
	}	

}
