package fr.istic.androidrisk.client;

/**
 * Class to be customized with app-specific data. The Eclipse plugin will set
 * these values when the project is created.
 */
public class Setup {

	/**
	 * The AppEngine app name, used to construct the production service URL
	 * below.
	 */
	private static final String APP_NAME = "androidrisk";

	/**
	 * The URL of the production service.
	 */
	public static final String PROD_URL = "https://" + APP_NAME + ".appspot.com";

}
