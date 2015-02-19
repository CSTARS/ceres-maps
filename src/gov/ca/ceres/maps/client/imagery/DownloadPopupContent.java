package gov.ca.ceres.maps.client.imagery;

import gov.ca.ceres.maps.client.resources.CmvResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DownloadPopupContent extends Composite {

	private static DownloadPopupContentUiBinder uiBinder = GWT.create(DownloadPopupContentUiBinder.class);
	interface DownloadPopupContentUiBinder extends UiBinder<Widget, DownloadPopupContent> {}

	@UiField SimplePanel outerAttributes;
	@UiField SimplePanel outerFiles;
	@UiField HTML name;
	@UiField HTML title;
	
	private FlexTable attrsTable = null;
	
	public DownloadPopupContent() {
		initWidget(uiBinder.createAndBindUi(this));
		attrsTable = new FlexTable();
		outerAttributes.add(attrsTable);
		attrsTable.setCellSpacing(10);
		setStyleName("DownloadPopupContent");
	}
	
	public void setName(String name) {
		this.name.setHTML(name);
	}
	
	public void setTitle(String title) {
		this.title.setHTML(title);
	}
	
	public void addAttribute(String key, String value) {
		int row = attrsTable.getRowCount();
		attrsTable.setHTML(row, 0, "<span style='color:#444444;font-weight:bold'>"+key+"</span>");
		attrsTable.setHTML(row, 1, "<span style='color:#777777'>"+checkUrl(value)+"</span>");
	}
	
	public String checkUrl(String value) {
		if( value.startsWith("http://") ) {
			if( value.endsWith("png") || value.endsWith("jpg") || value.endsWith("jpeg") || 
					value.endsWith("gif") || value.endsWith("bmp") ) {
				return "<img src='"+value+"' />";
			}
			return "<a href='"+value+"' target='_blank'>"+value+"</a>";
		}
		return value;
	}

	public void show() {
		setStyleName("DownloadPopupContent-show");
	}
	



}
