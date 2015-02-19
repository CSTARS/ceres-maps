package gov.ca.ceres.maps.client;

import edu.ucdavis.gwt.gis.client.config.GadgetConfig;

public class CeresConfig extends GadgetConfig {
    
    protected CeresConfig() {}
    
    public final native boolean enableCeresAdditions() /*-{
        if( this.enableCeresAdditions != null ) return this.enableCeresAdditions;
        return false;
    }-*/;

}
