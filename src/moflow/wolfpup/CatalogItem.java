package moflow.wolfpup;

public class CatalogItem {
	public String name;
	public boolean header;
	
	public CatalogItem( String itemName, boolean isHeader ) {
		this.name = itemName;
		header = isHeader;
	}
}
