package jfox.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import jfox.localization.BundleMessages;


public class DataSourceSingleConnection implements DataSource, AutoCloseable {
	
	
	// Champs

	private String			jndiName;
	private String			driver;
	private String			url;
	
	private String			user;
	private String			password;
	
	private DataSource		dataSourceInitiale;
	
	private IProxyConnection connection;
	
	
	// Constructeurs
	
	public DataSourceSingleConnection() {
	}

	public DataSourceSingleConnection( DataSource dataSource ) {
		setDataSourceInitiale(dataSource);
	}

	public DataSourceSingleConnection( Properties props ) {
		extractProperties(props);
	}
	
	public DataSourceSingleConnection( InputStream in ) {
		loadProperties(in);
	}
	
	public DataSourceSingleConnection( URL url) {
		try {
			loadProperties( url.openStream() );
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public DataSourceSingleConnection( Path path) {
		try {
			loadProperties( Files.newInputStream(path) );
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}

	
	// Getters & Setters
	
	public DataSource getDataSourceInitiale() {
		return dataSourceInitiale;
	}
	
	public void setDataSourceInitiale( DataSource dataSourceInitiale ) {
		if ( connection != null ) {
			throw new IllegalStateException( BundleMessages.getString( "ds.error.cn.open" ) );
		}
		if ( dataSourceInitiale != null ) {
			if ( this == dataSourceInitiale ) {
				throw new IllegalArgumentException( BundleMessages.getString( "ds.error.self.ref" ) );
			}
			setDriver(null);
			setUrl(null);
		}
		this.dataSourceInitiale = dataSourceInitiale;
	}
	
	public String getJndiName() {
		return jndiName;
	}
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		if ( connection != null ) {
			throw new IllegalStateException( BundleMessages.getString( "ds.error.cn.open" ) );
		}
		if( driver != null ) {
			setDataSourceInitiale(null);
		}
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if( url != null ) {
			setDataSourceInitiale(null);
		}
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	// Actions

	@Override
	public Connection getConnection(String user, String password) throws SQLException {
		this.user = user;
		this.password = password;
		return getConnection();
	}

	@Override
	public Connection getConnection() throws SQLException {
		
		if ( connection == null ) {
			if ( dataSourceInitiale != null ) {
				Connection cn;
				if ( user == null  ) {
					cn = dataSourceInitiale.getConnection();
				} else {
					cn = dataSourceInitiale.getConnection(user, password);
				}
				connection = FactoryProxyConnection.createProxy( cn );
			} else {
				if ( driver != null ) {
					try {
						Class.forName( driver );
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
				connection = FactoryProxyConnection.createProxy( DriverManager.getConnection(url, user, password) );
			}
		}
		return connection;
		
	}
	
	
	public void close() {
		if ( connection != null ) {
			connection.closeConnection();
			connection = null;
		}
	}
	
	
	// Méthode auxiliaires
	
	private void loadProperties( InputStream in  ) {
		try {
			Properties props = new Properties();
			props.load(in);
			extractProperties(props);
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void extractProperties( Properties props ) {
		setUser(		props.getProperty( "jdbc.user" ) );
		setPassword(	props.getProperty( "jdbc.password" ) );
		setDriver(		props.getProperty( "jdbc.driver" ) );
		setUrl( 		props.getProperty( "jdbc.url" ) );
	}
	
	
	// Actions non implémentées
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		throw new UnsupportedOperationException();
	}
	

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
