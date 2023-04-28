package jfox.localization;

import java.util.ResourceBundle;

public class BundleMessages {

	/* In WildFly, an error occurs if the class and the files ahve the
	 * same name. So, if the file's name is messages.properties, the class
	 * class name must not be Messages.
	 */
	
	
	// Champs
	
	private static ResourceBundle bundle = ResourceBundle.getBundle(
			BundleMessages.class.getPackageName() + "/messages" );

	
	// Actions
	
	public static String getString( String key ) {
		try {
			return bundle.getString( key );
		} catch (Exception e) {
			return e.getClass().getSimpleName() + " : " + key;
		}
	}

	
	public static String[] getStringArray( String key ) {
		try {
			return bundle.getStringArray( key );
		} catch (Exception e) {
			return new String[] { e.getClass().getSimpleName() + " : " + key };
		}
	}
	
	
}
