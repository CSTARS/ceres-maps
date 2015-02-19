package gov.ca.ceres.maps.client.search;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;

/**
 * Overlay for the result returned from a google commerce search.  This has accessor of the base attributes which
 * should be extended based on your datatype.  Or you can use the generic getters/setters
 * 
 * @author jrmerz
 */
public class Item extends JavaScriptObject {
	
	protected Item() {}

	public final native String getId() /*-{
		if( this.id ) return this.id;
		return "";
	}-*/;
	
	public final native String getTitle() /*-{
		if( this.title ) return this.title;
		return "";
	}-*/;
	
	public final native String getLink() /*-{
		if( this.link ) return this.link;
		return "";
	}-*/;
	
	public final native String getDescription() /*-{
		if( this.description ) return this.description;
		return "";
	}-*/;
	
	public final native String getLastUpdated() /*-{
		if( this.updated ) return this.update;
		return "";
	}-*/;
	
	public final native boolean hasValue(String key, String type)/*-{
		if( this.attributes[key+"("+type+")"] != null ) {
			 if( this.attributes[key+"("+type+")"].length > 0 ) return true;
		}
		return false;
	}-*/;
	
	public final native JsArrayString getStringKeys() /*-{
		var keys = [];
		var pat=/\(text\)$/g;
		for( key in this.attributes ) {
			var arr = this.attributes[key];
			if( arr.length > 0 ) {
				if( key.match(pat) ) keys.push(key.replace("(text)", ""));
			}
		}
		return keys;
	}-*/;
	
	public final native JsArrayString getIntKeys() /*-{
			var keys = [];
			var pat=/\(int\)$/g;
			for( key in this.attributes ) {
				var arr = this.attributes[key];
				if( arr.length > 0 ) {
					if( key.match(pat) ) {
						keys.push(key.replace("(int)", ""));
					}
				}
			}
			return keys;
		}-*/;
	
	public final native JsArrayString getValueAsString(String key) /*-{
		if( this.attributes[key+"(text)"] ) return this.attributes[key+"(text)"];
		return [];
	}-*/;
	
	public final native JsArrayString getValueAsDate(String key) /*-{
		if( this.attributes[key+"(date)"] ) return this.attributes[key+"(date)"];
		return [];
	}-*/;
	
	public final native JsArrayInteger getValueAsInt(String key) /*-{
		if ( this.attributes[key+"(int)"] ) return this.attributes[key+"(int)"];
		return [];
	}-*/;
	
	public final native JsArrayNumber getValueAsFloat(String key) /*-{
		if ( this.attributes[key+"(float)"] ) return this.attributes[key+"(float)"];
		return [];
	}-*/;
	
}
