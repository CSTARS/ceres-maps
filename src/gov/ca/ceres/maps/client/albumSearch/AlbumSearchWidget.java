package gov.ca.ceres.maps.client.albumSearch;

import edu.ucdavis.gwt.gis.client.AppManager;
import edu.ucdavis.gwt.gis.client.GisClient;
import edu.ucdavis.gwt.gis.client.resources.GadgetResources;
import edu.ucdavis.gwt.gis.client.toolbar.Toolbar;
import edu.ucdavis.gwt.gis.client.toolbar.button.ToolbarItem;
import gov.ca.ceres.maps.client.resources.CmvResources;
import gov.ca.ceres.maps.client.search.CeicSearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AlbumSearchWidget extends Composite {

	private static final JsonpRequestBuilder XHR = new JsonpRequestBuilder();
	private static AlbumSearchWidgetUiBinder uiBinder = GWT.create(AlbumSearchWidgetUiBinder.class);
	interface AlbumSearchWidgetUiBinder extends UiBinder<Widget, AlbumSearchWidget> {}

	private MapDisplayPanel[] panels = null;
	private String searchUrl = "http://cbase.casil.ucdavis.edu/cgi-bin/ceicBase.py?bq=[Resource==MapServer]&sortorder=descending&attributes=&querytype=search";
	
	private int resultsPerPage = 7;
	private int currentPage = 1;
	private String currentSearchText = "";
	private int selectedIndex = 0;
	
	private Icon icon = new Icon();
	
	@UiField TextBox searchBox;
	@UiField Button searchButton;
	@UiField Image moveLeft;
	@UiField Image moveRight;
	@UiField HTML textResults;
	@UiField AbsolutePanel results;
	@UiField HTML message;
	@UiField HTML close;
	
	public AlbumSearchWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		
		searchBox.getElement().getStyle().setPadding(8, Unit.PX);
		searchBox.getElement().getStyle().setFontSize(16, Unit.PX);
		searchBox.addKeyUpHandler(new KeyUpHandler(){
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ){
					search();
				}
			}
		});
		
		close.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		moveLeft.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				setSelectedIndex(selectedIndex-1);
			}
		});
		moveLeft.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				moveLeft.setResource(CmvResources.INSTANCE.back_hover());
			}
		});
		moveLeft.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				moveLeft.setResource(CmvResources.INSTANCE.back());
			}
		});
		
		moveRight.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				setSelectedIndex(selectedIndex+1);
			}
		});
		moveRight.addMouseOverHandler(new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				moveRight.setResource(CmvResources.INSTANCE.forward_hover());
			}
		});
		moveRight.addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				moveRight.setResource(CmvResources.INSTANCE.forward());
			}
		});
		
		searchButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				search();
			}
		});
		
		Window.addResizeHandler(new ResizeHandler(){
			@Override
			public void onResize(ResizeEvent event) {
				if( isAttached() ) resize();
			}
		});
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	private void resize() {
		GisClient client = AppManager.INSTANCE.getClient();
		int w = client.getWidth();
		int h = client.getHeight();
		
		results.setHeight((h-163)+"px");
		setSize(w+"px", h+"px");
		getElement().getStyle().setTop(client.getTop(), Unit.PX);
		getElement().getStyle().setLeft(client.getLeft(), Unit.PX);
		setSelectedIndex(selectedIndex);
	}
	
	private String getSearchUrl() {
		String url = searchUrl + "&q=" + currentSearchText;
		url += "&start-index=" + currentPage;
		return url + "&max-results=" + resultsPerPage;
	}
	
	public void search() {
		currentSearchText = searchBox.getText();
		currentPage = 1;
		query();
	}
	
	public void search(String text) {
		currentSearchText = text;
		searchBox.setText(text);
		currentPage = 1;
		query();
	}
	
	private void query() {
		String url = getSearchUrl();
		results.clear();
		panels = null;
		message.setText(" - loading...");
		
		XHR.requestObject(url, new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(Throwable caught) {
				message.setText(" - Search Failed");
			}
			@Override
			public void onSuccess(JavaScriptObject result) {
				try {
					render((SearchResult) result);
				} catch (Exception e) {
					message.setText(" - Search Failed");
				}
			}
		});	
	}
	
	private void render(SearchResult sr) {
		message.setText(" - "+sr.getTotalItems()+" Results Found.");
		
		
		panels = new MapDisplayPanel[sr.getTotalItems()];
		for(int i = 0; i < sr.getItems().length(); i++ ) {
			panels[i] = new MapDisplayPanel(sr.getItems().get(i), i, results, this);
		}
		setSelectedIndex(0);
	}
	
	private void setSelectedIndex(int index) {
		if( panels == null ) return;
		
		if( index < 0 || index > panels.length-1) {
			return;
		}
		
		selectedIndex = index;
		
		int maxloaded = 0;
		// rotate panels
		for( int i = 0; i < panels.length; i++ ) {
			if( panels[i] == null ) {
				maxloaded = i-1;
				break;
			}
			panels[i].setSelectedIndex(index);
		}
		
		// load if needed
		if( index >= maxloaded-3 ) {
			loadPage(maxloaded);
		}
	}

	private void loadPage(int maxloaded) {
		currentPage = (int) Math.floor( (double) (maxloaded+1) / (double) resultsPerPage);
		if( currentPage == 0 ) return;
		
		for( int i = maxloaded+1; i < maxloaded+1+resultsPerPage; i++ ) {
			panels[i] = new MapDisplayPanel(i, results, this);
		}
		
		String url = getSearchUrl();
		
		XHR.requestObject(url, new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(JavaScriptObject result) {
				try {
					renderDynamic((SearchResult) result);
				} catch (Exception e) {}
			}
		});	
	}
	
	private void renderDynamic(SearchResult sr) {
		int start = sr.getStartIndex()*resultsPerPage;
		for(int i = 0; i < sr.getItems().length(); i++ ) {
			panels[start+i].setItem(sr.getItems().get(i));
		}
	}
	
	public void show() {
	    // make sure the main menu goes away
	    AppManager.INSTANCE.getClient().getToolbar().hideMenu();
	    
		RootPanel.get().add(this);
		resize();
	}
	
	public void hide() {
		RootPanel.get().remove(this);
	}

	private class Icon extends ToolbarItem {
		
		public Icon() {}
		
		@Override
		public String getIcon() {
			return "<i class='icon-search'></i>";
		}

		@Override
		public void onAdd(Toolbar toolbar) {}

		public void onClick() {
			AlbumSearchWidget.this.show();
			searchBox.setText("");
			search();
		}

		@Override
		public String getText() {
			return "Search CERES for Services";
		}
		
	}
	
}
