package gov.ca.ceres.maps.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class TransformPanel extends Composite {
	
	private HTML panel = new HTML("This is a test");
	
	public TransformPanel() {
		initWidget(panel);
		panel.setSize("400px", "400px");
		panel.getElement().getStyle().setZIndex(9000);
		panel.getElement().getStyle().setTop(0, Unit.PX);
		panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		panel.getElement().getStyle().setBackgroundColor("red");
		panel.getElement().getStyle().setProperty("border", "4px solid blue");
		setTransition(panel.getElement());
		
		new Timer() {
			@Override
			public void run() {
				updateTransform();
				updateColor();
			}
		}.scheduleRepeating(2000);
	}
	
	private double generateValue(int max, boolean canBeNeg) {
		double num = Random.nextDouble() * (double) max;
		if( canBeNeg ) {
			if( Random.nextBoolean() ) num = num * -1;
		}
		return num;
	}
	
	private void updateTransform() {
		double a = generateValue(1, true);
		double b = generateValue(1, true);
		double c = generateValue(1, true);
		double d = generateValue(1, true);
		int tx = (int) Math.floor(generateValue(250, false));
		int ty = (int) Math.floor(generateValue(250, false));
		setTransformMoz(panel.getElement(), "matrix("+a+","+b+","+c+","+d+","+tx+"px,"+ty+"px)");
		setTransform(panel.getElement(), "matrix("+a+","+b+","+c+","+d+","+tx+","+ty+")");
	}
	
	private void updateColor() {
		int cR = (int) Math.floor(generateValue(255, false));
		int cG = (int) Math.floor(generateValue(255, false));
		int cB = (int) Math.floor(generateValue(255, false));
		
		int bR = (int) Math.floor(generateValue(255, false));
		int bG = (int) Math.floor(generateValue(255, false));
		int bB = (int) Math.floor(generateValue(255, false));
		
		getElement().getStyle().setProperty("border", "4px solid rgb("+cR+","+cG+","+cB+")");
		getElement().getStyle().setBackgroundColor("rgb("+bR+","+bG+","+bB+")");
	}
	
	private native void setTransform(Element ele, String style) /*-{
		ele.style['WebkitTransform'] = style;
		ele.style['transform'] = style;
		ele.style['msTransform'] = style;
		ele.style['OTransform'] = style;
	}-*/;
	
	private native void setTransformMoz(Element ele, String style) /*-{
		ele.style['MozTransform'] = style;
	}-*/;
	
	private native void setTransition(Element ele) /*-{
		ele.style['MozTransition'] = "all 2100ms linear";
		ele.style['WebkitTransition'] = "all 2100ms linear";
		ele.style['transition'] = "all 2100ms linear";
		ele.style['msTransition'] = "all 2100ms linear";
		ele.style['OTransition'] = "all 2100ms linear";
	}-*/;

}
