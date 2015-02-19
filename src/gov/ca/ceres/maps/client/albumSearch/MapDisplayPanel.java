package gov.ca.ceres.maps.client.albumSearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdavis.gwt.gis.client.AppManager;
import edu.ucdavis.gwt.gis.client.Debugger;
import edu.ucdavis.gwt.gis.client.GisClient;
import edu.ucdavis.gwt.gis.client.extras.EsriPreview;

public class MapDisplayPanel extends Composite {

	private static MapDisplayPanelUiBinder uiBinder = GWT.create(MapDisplayPanelUiBinder.class);
	interface MapDisplayPanelUiBinder extends UiBinder<Widget, MapDisplayPanel> {}
	
	private static int IMAGE_SIZE = 450;
	
	@UiField HTML title;
	@UiField SimplePanel outerPreview;
	@UiField FocusPanel eventPanel;
	@UiField SimplePanel outerDescription;
	@UiField HTML description;
	
	private int index = 0;
	private AbsolutePanel parent = null;
	private String url = "";
	private AlbumSearchWidget widget;
	private boolean showingBorder = false;
	private int selectedIndex = -1;
	private EsriPreview preview = null;
	private int newSize = IMAGE_SIZE;
	
	public MapDisplayPanel(Item item, int index, AbsolutePanel parent, AlbumSearchWidget widget) {
		initWidget(uiBinder.createAndBindUi(this));
		this.index = index;
		this.parent = parent;
		this.widget = widget;
		setItem(item);
	}
	
	public MapDisplayPanel(int index, AbsolutePanel parent, AlbumSearchWidget widget) {
		initWidget(uiBinder.createAndBindUi(this));
		this.index = index;
		this.parent = parent;
		this.widget = widget;
		showingBorder = true;
		getElement().getStyle().setProperty("border", "3px solid blue");
		//setTransform(getElement(), 0);
		outerPreview.setSize(IMAGE_SIZE+"px", IMAGE_SIZE+"px");
	}
	
	public void setItem(Item item) {
		if( showingBorder ) {
			getElement().getStyle().setProperty("border", "none");
			outerPreview.setSize("auto", "auto");
		}
		
		description.setHTML(trunc(item.getDescription()));
		title.setText(item.getTitle().replaceAll("_", " "));
		preview = new EsriPreview(item.getLink(), IMAGE_SIZE, IMAGE_SIZE);
		outerPreview.add(preview);
		url = item.getLink();

		eventPanel.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				AppManager.INSTANCE.getClient().addLayer(url, title.getText(), 80);
				widget.hide();
			}
		});
		
		if( GisClient.isIE7() || GisClient.isIE8() ) {
			description.getElement().getStyle().setColor("#2278da");
			title.getElement().getStyle().setColor("#2278da");
		}
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		int diff = index - selectedIndex;
		int top = 50;
	
		int sectionMiddle = (int) Math.floor((double) parent.getOffsetWidth() / (double) 2);
		int sectionWidth = (int) Math.floor((double) sectionMiddle / (double) 3);
	
		int offset = calcOffset(1);
		sectionMiddle = sectionMiddle - offset;
		
		switch(diff) {
			case -3:
				offset = calcOffset(.4);
				getElement().getStyle().setZIndex(1);
				//setTransform(getElement(), 0.4);
				getElement().getStyle().setOpacity(.2);
				setPosition(-1*offset, top - 50 );
				break;
			case -2: 
				getElement().getStyle().setZIndex(2);
				offset = calcOffset(.5);
				//setTransform(getElement(), 0.6);
				getElement().getStyle().setOpacity(.4);
				setPosition(sectionWidth-offset, top - 25);
				break;
			case -1: 
				getElement().getStyle().setZIndex(3);
				offset = calcOffset(.8);
				//setTransform(getElement(), 0.8);
				getElement().getStyle().setOpacity(.7);
				setPosition(sectionWidth*2-offset, top - 12);
				break;
			case 0: 
				getElement().getStyle().setZIndex(4);
				getElement().getStyle().setOpacity(1);
				setPosition(sectionMiddle, top);
				//setTransform(getElement(), 1);
				break;
			case 1: 
				getElement().getStyle().setZIndex(3);
				offset = calcOffset(.8);
				getElement().getStyle().setOpacity(.7);
				//setTransform(getElement(), 0.8);
				setPosition(sectionMiddle+offset, top - 12);
				break;
			case 2:
				getElement().getStyle().setZIndex(2);
				offset = calcOffset(.5);
				//setTransform(getElement(), 0.6);
				getElement().getStyle().setOpacity(.4);
				setPosition(sectionMiddle+sectionWidth+offset, top - 25);
				break;
			case 3:
				getElement().getStyle().setZIndex(1);
				offset = calcOffset(.3);
				//setTransform(getElement(), 0.4);
				getElement().getStyle().setOpacity(.2);
				setPosition(sectionMiddle+(2*sectionWidth)+offset, top- 50 );
				break;
			default: 
				if( isAttached() ) removeFromParent();
		}
		
		showDescription.cancel();
		if( index == selectedIndex ) {
			showDescription.schedule(500);
		} else {
			outerDescription.getElement().getStyle().setTop(newSize, Unit.PX);
			outerDescription.setHeight("0px");
		}
	}
	
	private Timer showDescription = new Timer() {
		@Override
		public void run() {
			if( index == selectedIndex ) {
				outerDescription.getElement().getStyle().setTop(newSize-description.getOffsetHeight(), Unit.PX);
				outerDescription.setHeight(description.getOffsetHeight()+"px");
			}
		}
	};
	
	private int calcOffset(double ratio) {
		if( parent.getOffsetHeight() < 550 ) {
			double r = (double) parent.getOffsetHeight() / (double) 550;
			newSize = (int) Math.floor(r * (double) IMAGE_SIZE);
			if( preview != null ) preview.scaleImage(r);
			title.setWidth(newSize+"px");
			eventPanel.setWidth(newSize+"px");
			return (int) Math.floor( (ratio * (double) newSize) / 2 );
		}
		
		return (int) Math.floor( (ratio * (double) IMAGE_SIZE) / 2 );
	}
	
	private void setPosition(int left, int top) {
		if( isAttached() ) {
			parent.setWidgetPosition(this, left, top);
		} else {
			parent.add(this, left, top);
		}
	}
	
	private void setTransform(Element ele, double scale) {		
		setTransform(ele, "scale("+scale+")");
	}
	
	private native void setTransform(Element ele, String style) /*-{
		ele.style['WebkitTransform'] = style;
		ele.style['MozTransform'] = style;
		ele.style['transform'] = style;
		ele.style['msTransform'] = style;
		ele.style['OTransform'] = style;
	}-*/;


	private String trunc(String text) {
		if( text.length() > 400 ) return text.substring(0, 400)+"...";
		return text;
	}

}
