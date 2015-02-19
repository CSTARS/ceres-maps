package gov.ca.ceres.maps.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdavis.gwt.gis.client.GisClient;
import edu.ucdavis.gwt.gis.client.extras.EsriPreview;
import edu.ucdavis.gwt.gis.client.layers.DataLayer;
import edu.ucdavis.gwt.gis.client.layers.MapServerDataLayer;
import edu.ucdavis.gwt.gis.client.layers.DataLayer.DataLayerLoadHandler;
import edu.ucdavis.gwt.gis.client.resources.GadgetResources;
import edu.ucdavis.gwt.gis.client.toolbar.Toolbar;
import edu.ucdavis.gwt.gis.client.toolbar.button.ToolbarItem;

public class CeicSearch extends PopupPanel {

	private static final JsonpRequestBuilder XHR = new JsonpRequestBuilder();
	private static CeicSearchUiBinder uiBinder = GWT.create(CeicSearchUiBinder.class);
	interface CeicSearchUiBinder extends UiBinder<Widget, CeicSearch> {}
	
	static {
		XHR.setTimeout(30000);
	}	
	
	@UiField TextBox searchBox;
	@UiField HTML searchButton;
	@UiField Image loadingIcon;
	@UiField HorizontalPanel pagingPanel;
	@UiField FlowPanel resultsPanel;
	@UiField HTML closeButton;
	@UiField ScrollPanel scrollPanel;
	@UiField CountComposite count;
	@UiField PaginationComposite page;
	
	private Icon icon = new Icon();
	private String searchUrl = "?bq=[Resource==MapServer]&sortorder=descending&attributes=&querytype=search";
	
	private int resultsPerPage = 6;
	private int currentPage = 1;
	private String currentSearchText = "";
	//private Element glassElement = null;
	//private HideAnimation hideAnimation = new HideAnimation();
	//private ShowAnimation showAnimation = new ShowAnimation();
	private GisClient client;
	
	public CeicSearch(GisClient client, String baseUrl) {
		searchUrl = baseUrl+searchUrl;
		this.client = client;
		setWidget(uiBinder.createAndBindUi(this));
		setGlassEnabled(true);
		
		setStyleName("AltasServiceSearch");
		setGlassStyleName("AltasServiceSearch-glasspanel");
		
		loadingIcon.setVisible(false);
		initHandlers();
		//glassElement = getGlassElement();
		page.setSearch(this);
	}
	
	public void animateShow() {
		setStyleName("AltasServiceSearch-detached");
		setGlassStyleName("AltasServiceSearch-glasspanel-detached");
		show();
		
		Timer t = new Timer() {
			@Override
			public void run() {
				setStyleName("AltasServiceSearch");
				setGlassStyleName("AltasServiceSearch-glasspanel");
			}
		};
		t.schedule(50);
	}
	
	private void animateHide() {
		setStyleName("AltasServiceSearch-detached");
		setGlassStyleName("AltasServiceSearch-glasspanel-detached");
		
		Timer t = new Timer() {
			@Override
			public void run() {
				hide();
			}
		};
		t.schedule(500);
	}
	
	private void initHandlers() {
		searchBox.addKeyUpHandler(new KeyUpHandler(){
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ){
					search();
				}
			}
		});
		
		searchButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				search();
			}
		});
		
		closeButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				//hideAnimation.run(500);
				animateHide();
			}
		});
		
		addAttachHandler(new Handler(){
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if( event.isAttached() ) {
					searchBox.setFocus(true);
					updateSize();
					new Timer() {
						@Override
						public void run() {
							center();
						}
					}.schedule(100);
					
				}
			}
		});
		
		Window.addResizeHandler(new ResizeHandler(){
			@Override
			public void onResize(ResizeEvent event) {
				if( isShowing() ) {
					updateSize();
					center();
				}
			}
		});
	}
	
	private void updateSize() {
		setWidth((Window.getClientWidth()*.8)+"px");
		scrollPanel.setHeight(((Window.getClientHeight()*.8)-152)+"px");
	}
	
	public Icon getMenuItem() {
		return icon;
	}

	private String getSearchUrl() {
		String url = searchUrl + "&q=" + currentSearchText;
		url += "&start-index=" + currentPage;
		return url + "&max-results=" + resultsPerPage;
	}
	
	
	public void search(String text) {
		searchBox.setText(text);
		search();
	}
	
	public void search() {
		currentSearchText = searchBox.getText();
		currentPage = 1;
		query();
	}
	
	public void showPage(int page) {
		currentPage = page;
		query();
	}
	
	private void query() {
		String url = getSearchUrl();
		loadingIcon.setVisible(true);
		resultsPanel.clear();
		
		XHR.requestObject(url, new AsyncCallback<JavaScriptObject>() {
			@Override
			public void onFailure(Throwable caught) {
				setError("Search Failed");
				loadingIcon.setVisible(false);
			}
			@Override
			public void onSuccess(JavaScriptObject result) {
				try {
					render((SearchResult) result);
				} catch (Exception e) {
					setError("Search Failed");
				}
				loadingIcon.setVisible(false);
				
			}
		});	
	}
	

	
	private void render(SearchResult results) {
		page.update(results.getTotalItems(), results.getStartIndex(), results.getItemsPerPage());
		count.update(results.getTotalItems(), results.getStartIndex(), results.getItemsPerPage());
		for(int i = 0; i < results.getItems().length(); i++ ) {
			Item item = results.getItems().get(i);
			Result r = new Result(item.getLink(), item.getTitle(), item.getDescription());
			resultsPanel.add(r);
		}
	}
	
	private void setError(String msg) {
		resultsPanel.clear();
		resultsPanel.add(new HTML(msg));
	}
	
	private class Result extends Composite {
		
		private FocusPanel panel = new FocusPanel();
		private HorizontalPanel hp = new HorizontalPanel();
		private HTML description = new HTML();
		private HTML cover = new HTML("<div style='margin-top:80px'>Add to Map</div>");
		private String url = "";
		private String name = "";
		
		public Result(String url, String n, String desc) {
			initWidget(panel);
			this.url = url;
			this.name = n;
			EsriPreview ep = new EsriPreview(url, 200, 200);

			description.setHTML("<div style='color:#2278DA;font-weight:bold;font-size:16px'>"+name+"</div>" +
					"<div style='color: #093;font-size:14px;margin-bottom:5px;'>"+Result.this.url+"</div>" +
							"<div style='font-size:12px;color:#555555'>"+desc+"</div>");
				
			hp.add(ep);
			description.setSize("280px", "200px");
			description.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			hp.add(description);
			hp.setWidth("500px");
			panel.setSize("500px", "200px");
			
			FlowPanel fp = new FlowPanel();
			fp.getElement().getStyle().setPosition(Position.RELATIVE);
			cover.setStyleName("AltasServiceSearch-cover");
			setStyleName("AltasServiceSearch-item-detached");
			//showCover(false);
			fp.add(cover);
			fp.add(hp);
			
			panel.add(fp);
			
			description.getElement().getStyle().setPaddingLeft(10, Unit.PX);
			
			initHandlers();
		}
		
		//private native String getDescription(JavaScriptObject object) /*-{
		//	if( object.serviceDescription ) return object.serviceDescription;
		//	else if( object.description ) return object.description;
		//	return "";
		//}-*/;
		
		private void initHandlers() {
			
			cover.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					cover.setHTML("<div style='margin-top:80px'>Adding...</div>");
					animateHide();
					MapServerDataLayer msdl = (MapServerDataLayer) client.addLayer(url, name, 80);
					/*msdl.addLoadHandler(new DataLayerLoadHandler(){
						@Override
						public void onDataLoaded(DataLayer dataLayer) {
							((MapServerDataLayer) dataLayer).zoomToLayerExtent();
						}
					});*/
				}
			});	
			
			addAttachHandler(new Handler(){
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					if( event.isAttached() ) {
						new Timer() {
							@Override
							public void run() {
								setStyleName("AltasServiceSearch-item");
							}
						}.schedule(500);
					}
				}
			});
		}
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
			CeicSearch.this.animateShow();
			searchBox.setText("");
			search();
		}

		@Override
		public String getText() {
			return "Search CEIC for Services";
		}
		
	}
	
/*	private class ShowAnimation extends Animation {
		@Override
		protected void onStart() {
			getElement().getStyle().setOpacity(0);
			glassElement.getStyle().setOpacity(0);
			show();
		}
		@Override
		protected void onUpdate(double progress) {
			getElement().getStyle().setOpacity(progress);
			glassElement.getStyle().setOpacity(.85*progress);
		}
		@Override
		protected void onComplete() {
			getElement().getStyle().setOpacity(1);
			glassElement.getStyle().setOpacity(.85);
		}	
	}
	
	private class HideAnimation extends Animation {
		@Override
		protected void onStart() {
			getElement().getStyle().setOpacity(1);
			glassElement.getStyle().setOpacity(.85);
		}
		@Override
		protected void onUpdate(double progress) {
			getElement().getStyle().setOpacity(1-progress);
			glassElement.getStyle().setOpacity(.85*(1-progress));
		}
		@Override
		protected void onComplete() {
			getElement().getStyle().setOpacity(0);
			glassElement.getStyle().setOpacity(0);
			hide();
		}
	}
*/


}
