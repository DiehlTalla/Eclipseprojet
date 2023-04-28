package jfox.context;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jfox.javafx.view.ControllerAbstract;


public abstract class ContextAbstract implements IContext {
	
	//-------
	// Champs
	//-------
	
	protected final List<Object>	beans = new ArrayList<>();
	
	protected ContextAbstract 		mainContext = this;
	
	
	//-------
	// Setters
	//-------
	
	@Override
	public void setMainContext(ContextAbstract mainContext) {
		this.mainContext = mainContext;
	}
	
	
	//-------
	// Actions
	//-------
	
	
	@Override
	@SuppressWarnings( "unchecked" )
	public final <T> T getBean( Class<T> type ) {
		
		T bean = null;
		
		var flagSingleton = ! type.isAnnotationPresent( Dependent.class )
				&& ! ControllerAbstract.class.isAssignableFrom(type);
		
		if ( flagSingleton ) {
			// Recherche dans la liste
			for ( Object item : beans ) {
				if ( type.isAssignableFrom( item.getClass() ) ) {
					if ( bean != null ) {
						throw new RuntimeException( "Several beans found for the type : " + type.getName() );
					}
					bean = (T) item;
				}
			}
			if ( bean != null ) {
				return bean;
			}
		}
		
		
		// Si pas trouvé dans la liste
		// détermine le type de l'objet à instancier
		
		try {
			
			// Instancie le bean et l'initialise 
			// (ou le récupère s'il existe déjà dans un autre contexte)
			bean = makeBean(type, flagSingleton);

			if (bean == null && mainContext == this ) {
				throw new RuntimeException( "Bean not found : " + type.getName() );
			}
			return bean;
			
		} catch ( Exception e) {
			if ( bean != null ) {
				beans.remove(bean);
			}
			throw runtimeException(e);
		}
	}
	
	
	@Override
	public final void addBean( Object bean ) {
		if ( mainContext == this ) {
			beans.add(bean);
		} else {
			mainContext.addBean(bean);
		}
	}
	
	
	@Override
	public void close() {
		for( int i = beans.size()-1; i >= 0; --i ) {
			close( beans.get(i), beans.get(i).getClass() );
		}
	}
	
	
	//-------
	// Méthodes auxiliaires
	//-------

	protected abstract <T> T makeBean( Class<T> type, boolean flagSingleton ) throws Exception;

	protected <T> T initBean( Class<T> typeImpl, boolean flagSingleton )  {
		
		if ( mainContext != this ) {
			return mainContext.initBean( typeImpl, flagSingleton );
		}

		T bean = null;
		
		try {
			bean = typeImpl.getConstructor().newInstance() ;
			inject( bean, typeImpl );
			init(bean, typeImpl);
			if ( flagSingleton ) {
				beans.add(bean);
			}
			return bean;
		} catch ( Exception e) {
			if ( bean != null ) {
				beans.remove(bean);
			}
			throw runtimeException(e);
		}
	}
	
	
	private void inject( Object bean, Class<?> type ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		if ( type.getSuperclass() != null ) {
			inject( bean, type.getSuperclass() );
		}

		for( Field field : type.getDeclaredFields() ) {
			boolean flagInject = 
					field.isAnnotationPresent( Inject.class )
					|| field.isAnnotationPresent( Resource.class );
			if ( flagInject )  {
				field.setAccessible(true);
				Object dep = null;
				if ( field.getType().isAssignableFrom( this.getClass()  ) )  {
					dep = this;
				} else {
					dep = getBean( field.getType() );
				}
				if ( dep != null ) {
					field.set( bean, dep );
				} else {
					throw new RuntimeException( "Bean not found : " + field.getType().getName() );
				}
			}
		}

		for ( Method method : type.getDeclaredMethods() ) {
			boolean flagInject = 
					( method.isAnnotationPresent( Inject.class )
						|| method.isAnnotationPresent( Resource.class )
					) && method.getParameterCount() > 0;
			if ( flagInject )  {
				method.setAccessible(true);
				
				Object deps[] = new Object[method.getParameterCount()];
				for ( int i = 0; i < deps.length; ++i  ) {
					deps[i] = null;
					if ( method.getParameterTypes()[i].isAssignableFrom( this.getClass()  ) )  {
						deps[i] = this;
					} else {
						deps[i] = getBean( method.getParameterTypes()[i] );
					}
					if ( deps[i] == null ) {
						throw new RuntimeException( "Bean not found : " + method.getParameterTypes()[i].getName() );
					}
				}
				method.invoke( bean, deps );
			}
		}
		
	}
	
	
	private void init( Object bean, Class<?> type ) throws IllegalAccessException, InvocationTargetException {

		if ( type.getSuperclass() != null ) {
			init( bean, type.getSuperclass() );
		}

		for ( Method method : type.getDeclaredMethods() ) {
			if ( method.isAnnotationPresent( PostConstruct.class ) )  {
				method.setAccessible(true);
				method.invoke( bean );						
			}
		}
		
	}
	
	
	private void close( Object bean, Class<?> type ) {
		
		try {
			// Exécute la méthode de libération des ressources du bean
			if ( AutoCloseable.class.isAssignableFrom(type) ) {
				((AutoCloseable) bean).close();
				return;
			}
			for ( Method method : type.getDeclaredMethods() ) {
				if ( method.isAnnotationPresent( PreDestroy.class ) )  {
					method.setAccessible(true);
					method.invoke( bean );						
				}
			}
		} catch ( Exception e ) {
			throw runtimeException(e);
		}

		if ( type.getSuperclass() != null ) {
			close( bean, type.getSuperclass() );
		}
	}

	
	
	public static final RuntimeException runtimeException( Throwable e ) {
		if ( e instanceof ReflectiveOperationException
				&& e.getCause() != null ) {
			e = e.getCause();
		}
		if ( e instanceof RuntimeException ) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}

}
