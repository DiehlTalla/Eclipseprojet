package jfox.context;

import java.util.ArrayList;
import java.util.List;


public class ContextGlobal extends ContextAbstract {
	
	
	//-------
	// Champs
	//-------
	
	private final List<IContext>	contexts = new ArrayList<>();
	
	
	//-------
	// Constructeur
	//-------
	
	public ContextGlobal( IContext...contexts  ) {
		for ( IContext item : contexts ) {
			addContext( item );
		}
	}
	
	
	//-------
	// Setters
	//-------
	
	@Override
	public void setMainContext(ContextAbstract mainContext) {
		super.setMainContext(mainContext);
		for ( IContext item : contexts ) {
			item.setMainContext(mainContext);
		}
	}
	
	
	// Actions
	
	public void addContext( IContext context ) {
		if ( context != null ) {
			contexts.add( context );
			context.setMainContext( this.mainContext );
		}
	}
	
	
	@Override
	public void close() {
		for( IContext context : contexts ) {
			context.close();
		}
		super.close();
	}
	
	
	//-------
	// Méthodes auxiliaires
	//-------

	@Override
	protected <T> T makeBean(Class<T> type, boolean flagSingleton) throws Exception {

		
		T bean = null;
		
		// Recherche dans les autres contextes
		for ( IContext context : contexts ) {
			bean = context.getBean( type );
			if ( bean != null ) {
				return bean;
			}
		}

		// Si le type n'est pas une interface
		if ( ! type.isInterface() ) {
			bean = initBean( type, flagSingleton );
		} 
		return bean;
	
	}

}
