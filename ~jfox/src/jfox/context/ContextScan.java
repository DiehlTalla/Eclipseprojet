package jfox.context;

import java.util.List;


public class ContextScan extends ContextAbstract {
	
	
	//-------
	// Champs
	//-------
	
	private final List< Class<?> >	components;
	
	
	//-------
	// Constructeurs
	//-------
	
	public ContextScan( String[] includes, String[] excludes  ) {
		components = new Scanner() .getClasses( includes, excludes );
	}
	
	public ContextScan( String[] includes )  {
		this( includes, null );
	}
	
	public ContextScan( String include, String[] excludes ) {
		this( new String[] { include }, excludes );
	}
	
	public ContextScan( String include, String exclude ) {
		this( include, new String[] { exclude } );
	}
	
	public ContextScan( String include ) {
		this( include, (String[]) null );
	}
	
	public ContextScan() {
		this( getPackageName() );
	}
	
	private static String getPackageName() {
		try {
			return Class.forName(  new Throwable().getStackTrace()[2].getClassName() ).getPackageName();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException( e );
		}
	}

	
	//-------
	// Méthodes auxiliaires
	//-------

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T makeBean(Class<T> type, boolean flagSingleton) throws Exception {

		// Détermine le type à instancier
		Class<T> typeImpl = null;
		for ( Class<?> component : components ) {
			if ( type.isAssignableFrom( component ) ) {
				if ( typeImpl != null ) {
					throw new RuntimeException( "Several beans found for the type : " + type.getName() );
				}
				typeImpl = (Class<T>) component;
			}
		}
		if ( typeImpl == null ) {
			if ( type.isInterface() ) {
				return null;
			} else {
				typeImpl = type;
			}
		}
		
		// Initialise le bean
		return initBean( typeImpl, flagSingleton );
	}

}
