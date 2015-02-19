package gov.ca.ceres.maps.client.search;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class CountComposite extends Composite {
	
	private FlowPanel panel 	= new FlowPanel();
	private HTML countDisplay	= new HTML();
	
	private String style;
	//private String query;
	//private int currentPage;
	//private int itemsPerPage;
	private int totalCount;
	private String formatWithResults;
	private String formatNoResults;
	//private SearchRequest searchRequest;
	
	//private static final String COUNT_RESULTS_DEFAULT_FORMAT 	= 
	private static final String COUNT_NO_RESULTS_DEFAULT_FORMAT = "0 Results";
	private static final String DEFAULT_STYLE	= "search-results-count";

    public CountComposite() {
    	try {
    		this.style = DEFAULT_STYLE;
	    	//formatWithResults 	= COUNT_RESULTS_DEFAULT_FORMAT;
	    	formatNoResults 	= COUNT_NO_RESULTS_DEFAULT_FORMAT;
	    	panel.add(countDisplay);
	    	initWidget(panel);
	    	setStyleName(this.style);
    	} catch (Exception e) {}
    }
    
	public void clear() {
		try {
			countDisplay.setHTML("");
		} catch (Exception e) {}
	}

	public void update(int totalCount, int currentPage, int itemsPerPage) {
		try {
			this.totalCount 	= totalCount;
			String countMessage = getCountMessage(currentPage, itemsPerPage);
			countDisplay.setHTML(countMessage);
			panel.setVisible(true);
		} catch (Exception e) {}
	}
	
	private String getCountMessage(int currentPage, int itemsPerPage) {
		int startPoint 		= 0;
		int endPoint 		= 0;
		try {

	    	if (this.totalCount == 0) {
	    		return formatNoResults;
	    	} else {
				if (currentPage == 1) {
					startPoint = 1;
					endPoint = itemsPerPage;
				} else {
					startPoint = 1 + ((currentPage - 1) * itemsPerPage);
					endPoint = (startPoint - 1) + itemsPerPage;
				}
				if (endPoint > this.totalCount) {
					endPoint = this.totalCount;
				}
	    	}
		} catch (Exception e) {}
    	return "<span style='color:#333333;font-size: 18px'>Search Results:</span>  <span style='color: #888888; font-size: 18px'>" + String.valueOf(startPoint) + " to " + 
    			String.valueOf(endPoint) + " of " + String.valueOf(this.totalCount)+"</span>";
	}



}
