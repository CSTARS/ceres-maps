package gov.ca.ceres.maps.client.search;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class SearchResult extends JavaScriptObject {

	protected SearchResult() {}
	
	public final native String getKind() /*-{
		return this.kind;
	}-*/;
	
	public final native String getETag() /*-{
		return this.etag;
	}-*/;
	
	public final native String getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getSelfLink() /*-{
		return this.selfLink;
	}-*/;
	
	public final native String getNextLink() /*-{
		return this.nextLink;
	}-*/;
	
	public final native int getTotalItems() /*-{
		return this.totalResults;
	}-*/;
	
	public final native int getStartIndex() /*-{
		return this.startIndex;
	}-*/;
	
	public final native int getItemsPerPage() /*-{
		return this.itemsPerPage;
	}-*/;
	
	public final native int getCurrentItemCount() /*-{
		return this.currentItemCount;
	}-*/;
	
	public final native JsArray<Item> getItems() /*-{
		return this.entries;
	}-*/;
	
	
	
}
