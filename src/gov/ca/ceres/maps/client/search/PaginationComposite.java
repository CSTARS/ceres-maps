package gov.ca.ceres.maps.client.search;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class PaginationComposite extends Composite {

	FlowPanel panel = new FlowPanel();
	private String style;
    
	private final static String DEFAULT_STYLE_NAME	= "pagination";
	private static final String PAGE_TITLE_PREVIOUS 	= "<< Prev";
	private static final String PAGE_TITLE_NEXT 	= "Next >>";
	private static final String PAGE_STYLE_PREVIOUS = "pagination-value-previous";
	private static final String PAGE_STYLE_NEXT 	= "pagination-value-next";
	private static final String PAGE_STYLE_CURRENT	= "pagination-value-current";
	private static final String PAGE_STYLE	= "pagination-value";

	private CeicSearch search = null;
	
	public PaginationComposite() {
    	try {
    		this.style	= DEFAULT_STYLE_NAME;
    		initWidget(panel);
    		setStyleName(this.style);
    	} catch (Exception e) {}
    }
	
	public void setSearch(CeicSearch search) {
		this.search = search;
	}
	
	public void clear() {
		try {
			panel.clear();
		} catch (Exception e) {}
	}

	public void update(int totalCount, int currentPage, int itemsPerPage) {
		try {

			int startPoint;
			int endPoint;

			panel.clear();

			if (currentPage == 1) {
				startPoint = 1;
				endPoint = itemsPerPage;
			} else {
				startPoint = 1 + ((currentPage - 1) * itemsPerPage);
				endPoint = (startPoint - 1) + itemsPerPage;
			}
			 
			if (endPoint > totalCount) {
				endPoint = totalCount;
			}
			
			//how many pages are there?
			int numPages = 1 + (totalCount / itemsPerPage);

			//if (!( (totalCount % 10) > 0) ) {
			//	numPages--;
			//}
			
			if (numPages > 1) {
				if (currentPage > 1) {
					int previousPage = currentPage - 1;
					addPage(PAGE_TITLE_PREVIOUS, previousPage, PAGE_STYLE_PREVIOUS);
				}
				
				int pageRangeStart = 1;
				int pageRangeEnd = 10;
				
				if (currentPage > (pageRangeEnd / 2)) {
					pageRangeStart = currentPage - 5;
					pageRangeEnd = pageRangeStart + 10;
				}
				
				if (pageRangeEnd > numPages)	{
					pageRangeEnd = numPages;
					pageRangeStart = numPages - 10;
					if (pageRangeStart < 1) {
						pageRangeStart = 1;
					}
				}
						
				for (int x = pageRangeStart; x <= pageRangeEnd; x++) {
					if (currentPage == x) {
						addPage(String.valueOf(x), x, PAGE_STYLE_CURRENT);
					} else {
						addPage(String.valueOf(x), x, PAGE_STYLE);
					}
				}
				
				if (currentPage < numPages) {	
					int nextPage = currentPage;
					nextPage++; //not sure why, but currentPage + 1 was NOT giving correct results??
					addPage(PAGE_TITLE_NEXT, nextPage, PAGE_STYLE_NEXT);
				}
				setVisible(true);
			}
		} catch (Exception e) {}
	}
		
	private void addPage(String title, int page, String style) {
		try {
			Anchor aLink = new Anchor(title);
			aLink.getElement().setAttribute("page", page+"");
			aLink.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					int page = Integer.parseInt(((Anchor) event.getSource()).getElement().getAttribute("page"));
					search.showPage(page);
				}
			});
			aLink.setStyleName(style);
			panel.add(aLink);
		} catch (Exception e) {}
	}

}

