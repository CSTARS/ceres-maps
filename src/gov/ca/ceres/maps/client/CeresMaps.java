package gov.ca.ceres.maps.client;

import edu.ucdavis.gwt.gis.client.AppManager;
import edu.ucdavis.gwt.gis.client.Debugger;
import edu.ucdavis.gwt.gis.client.GisClient;
import edu.ucdavis.gwt.gis.client.GisClient.GisClientLoadHandler;
import edu.ucdavis.gwt.gis.client.help.MainHelpMenu;
import edu.ucdavis.gwt.gis.client.toolbar.button.DrawToolButton;
import edu.ucdavis.gwt.gis.client.toolbar.menu.BasemapMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.ShareDownloadMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.HelpMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.IdentifyMenu;
import edu.ucdavis.gwt.gis.client.toolbar.menu.SaveLoadMenu;
import gov.ca.ceres.maps.client.albumSearch.AlbumSearchWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;


public class CeresMaps implements EntryPoint {

	private GisClient mapClient = null;
	private AlbumSearchWidget search = new AlbumSearchWidget();
	private CeresConfig ceresConfig = null;

	public void onModuleLoad() {
		mapClient = new GisClient();
		
		Debugger.INSTANCE.log("Gis Client Created");
		
		ceresConfig = (CeresConfig) AppManager.INSTANCE.getConfig();
		
		if( ceresConfig.enableCeresAdditions() ) {
    		String searchText = Window.Location.getParameter("search");
    		if( searchText != null ) {
    		    Debugger.INSTANCE.log("Running ceres search from url");
    			search.show();
    			search.search(searchText);
    		}
		}
		
		mapClient.load(new GisClientLoadHandler(){
			@Override
			public void onLoad() {
				onClientReady();
			}
		});
	}

		
	public void onClientReady() {
	    Debugger.INSTANCE.log("Creating basemap menu");

		mapClient.getToolbar().addToolbarMenu(new BasemapMenu());
		
		if( ceresConfig.enableIdentifyTool() ) {
		    Debugger.INSTANCE.log("Adding identify tool");
		    mapClient.getToolbar().addToolbarMenu(new IdentifyMenu());
		}
		
		/* TODO: reimplement if we get a new search service up for this
		if( ceresConfig.enableCeresAdditions() ) {
		    Debugger.INSTANCE.log("Adding ceres additions");
		    mapClient.getAddLayerMenu().addItem(search.getIcon());	
		}
		*/
		
		Debugger.INSTANCE.log("Adding draw button");
		mapClient.getAddLayerMenu().addItem(new DrawToolButton());
		
		Debugger.INSTANCE.log("Creating save menu");
		mapClient.getToolbar().addToolbarMenu(new SaveLoadMenu());
		
		Debugger.INSTANCE.log("Creating share/download menu");
		mapClient.getToolbar().addToolbarMenu(new ShareDownloadMenu());
		
		Debugger.INSTANCE.log("Creating help menu");
        mapClient.getToolbar().addToolbarMenu(new HelpMenu());
		
		mapClient.expandLegends(true);
		
		if( GisClient.isIE7() || GisClient.isIE8() ) {
			mapClient.getRootPanel().getElement().getStyle().setProperty("border", "1px solid #aaaaaa");
		}
		
	}
	
	

}
