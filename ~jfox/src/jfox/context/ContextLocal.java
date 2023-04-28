package jfox.context;


public class ContextLocal extends ContextAbstract {
	
	
	//-------
	// Méthodes auxiliaires
	//-------

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T makeBean( Class<T> type, boolean flagSingleton )  {
	
		Class<T> typeImpl;
		
		// Si c'est une interface
		//  - recherche un type compatible dans le package courant le contexte
		//  - si pas trouvé, recherche dans les autres contextes
		if ( type.isInterface() ) {
			StringBuilder sbNomImpl = new StringBuilder();
			sbNomImpl.append( type.getSimpleName() );
			if ( sbNomImpl.charAt(0) == 'I' ) {
				sbNomImpl.deleteCharAt(0);
			}
			sbNomImpl.insert( 0, '.' );
			sbNomImpl.insert( 0, this.getClass().getPackage().getName() );
			try {
				typeImpl = (Class<T>) Class.forName( sbNomImpl.toString() );
			} catch ( ClassNotFoundException e) {
				return null;
			}
		} else {
			// Le type n'est pas une interface
			typeImpl = type;
		}
		
		// Initialise le bean
		return initBean( typeImpl, flagSingleton );
	}

}
