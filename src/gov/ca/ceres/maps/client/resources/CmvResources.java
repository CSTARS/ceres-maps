package gov.ca.ceres.maps.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CmvResources  extends ClientBundle {
	
	public static final CmvResources INSTANCE = GWT.create(CmvResources.class);

	@Source("images/back.png")
	public ImageResource back();

	@Source("images/back_hover.png")
	public ImageResource back_hover();
	
	@Source("images/forward.png")
	public ImageResource forward();

	@Source("images/forward_hover.png")
	public ImageResource forward_hover();
	
	@Source("images/googleearth.png")
	public ImageResource googleearth();

	@Source("images/loading.gif")
	public ImageResource loading();
	
	@Source("images/download.png")
	public ImageResource download();
	
	@Source("images/querymap.png")
	public ImageResource querymap();
	
	@Source("images/querymap_inactive.png")
	public ImageResource querymapInactive();

}
