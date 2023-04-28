package jfox.jdbc;

import java.sql.Connection;

public interface IProxyConnection extends Connection {
	
	public void closeConnection();

}
