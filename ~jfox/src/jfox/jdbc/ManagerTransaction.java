package jfox.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import jakarta.inject.Inject;


public class ManagerTransaction {
	
	
	// Logger
	private static final Logger logger = Logger.getLogger(ManagerTransaction.class.getName());
	
	
	// Champs
	
	@Inject
	private DataSource	dataSource;  
	
	
	// Actions

	public void begin() {
		try {
			dataSource.getConnection().setAutoCommit(false);
			logger.finer("Transaction BEGIN");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void commit() {
		try {
			Connection connection = dataSource.getConnection();
			connection.commit();
			connection.setAutoCommit(true);
			logger.finer("Transaction COMMIT");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void rollback() {
		try {
			Connection connecition = dataSource.getConnection();
			connecition.rollback();
			connecition.setAutoCommit(true);
			logger.finer("Transaction ROLLBACK");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void rollbackSiApplicable() {
		try {
			if ( ! dataSource.getConnection().getAutoCommit() ) {
				rollback();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
