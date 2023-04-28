package jfox.javafx.util;

import java.lang.reflect.Method;

import javafx.util.Callback;


public class Extractor<S, T> implements Callback<T, String >  {
	
	
	// Fields
	
	private String propertyName;
	private String methodName;
	private Method method;
	private boolean flagError;
	
	
	// Contrructor
	
	public Extractor( String propertyName ) {
		this.propertyName = propertyName;
	}
	
	
	
	@Override
	public String call(T param) {
		
		if ( flagError ) {
			return null;
		}
		
		if ( method == null ) {
			try {
				methodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
				method = param.getClass().getMethod( methodName );
			} catch (Exception e) {
			}
			
			if ( method == null && methodName != null ) {
				methodName = methodName.replace( "get", "is" );
				try {
					method = param.getClass().getMethod( methodName );
				} catch (NoSuchMethodException | SecurityException e) {
				}
			}
		}
		
		if ( method == null ) {
			flagError = true;
			Exception e = new java.lang.IllegalStateException ( "Cannot read from unreadable property '" + propertyName + "' in type " + param.getClass() );
			e.printStackTrace();
			return null;
		} else {
			try {
				return  String.valueOf( method.invoke( param ) );
			} catch (Exception e) {
				throw  UtilFX.runtimeException(e);
			}
		}
		
	}

}
