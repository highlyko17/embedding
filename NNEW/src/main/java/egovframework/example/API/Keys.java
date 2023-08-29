package egovframework.example.API;

public class Keys {
	public static String OPENAPI_KEY = "";
	public static String OPENAPI_ORGKEY = "";
	public static String PINECONE_KEY = "";
	
	public static Keys keys = new Keys();
	public Keys getInstance() {
		return keys;
	}
}
