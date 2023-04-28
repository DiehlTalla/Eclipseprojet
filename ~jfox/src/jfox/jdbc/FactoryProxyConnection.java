package jfox.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.logging.Logger;

import jfox.localization.BundleMessages;


public class FactoryProxyConnection {

	
	// Actions
	
	public static IProxyConnection createProxy( Connection connection ) {

		return (IProxyConnection) Proxy.newProxyInstance(
				IProxyConnection.class.getClassLoader(),
				new Class[] {IProxyConnection.class},
				new Handler( connection )
				);		
	}
	
	
	
	private static class Handler implements InvocationHandler {
		
		// Logger

		private static final Logger logger = Logger.getLogger( Handler.class.getName() );
	
		
		// Champs
		
		private final Connection connection;
		
		
		// Constructeur
		
		public Handler( Connection connection ) {
			super();

			if ( connection == null ) {
				throw new NullPointerException();
			}
			this.connection = connection;
		}

		
		// Actions
		
	    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	    	
	    	switch ( method.getName() ) {
	    		
	    	case "closeConnection" :
//	    		System.out.println( BundleMessages.getString( "cn.info.closing" ) );
				logger.config( BundleMessages.getString( "cn.info.closing" ) + " @" + connection.hashCode() );
	    		connection.close();
	    		return null ;
	    		
	    	case "close" :
	    		return null;
	    		
	    	default :
				try {
					return method.invoke( connection, args);
				} catch ( InvocationTargetException e ) {
					throw e.getCause();
				}
	    	}
		}
	}
	

}
