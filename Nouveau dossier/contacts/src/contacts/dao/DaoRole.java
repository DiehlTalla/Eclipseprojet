package contacts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import contacts.data.Compte;
import jakarta.inject.Inject;
import jfox.jdbc.UtilJdbc;


public class DaoRole {

	
	//-------
	// Champs
	//-------

	@Inject
	private DataSource		dataSource;

	
	//-------
	// Actions
	//-------

	public void mettreAJourPourCompte( Compte compte ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet			rs 		= null;
		String				sql;

		try {

			// Crée le Set des éléments à conserver ou à insérer
			var set = new HashSet<>();
			for ( var item : compte.getRoles() ) {
				set.add( item ); 
			}

			// Crée le RésultSet des éléments présents dans la base 
			cn = dataSource.getConnection();
			sql = "SELECT * FROM role WHERE idCompte = ?";
			stmt = cn.prepareStatement( sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			stmt.setObject( 1, compte.getId() );
			rs = stmt.executeQuery();
			
			// Parcourt les éléments du ResultSet
			// Supprime ceux qui ne sont pas dans la Map
			// Retire du Set ceux qui y sont trouvés
			// Ainsi, à la fin, le Set ne contient plus que les éléments à insérer
			while( rs.next() ) {
				var item = rs.getObject( "role", String.class );
				if( ! set.contains( item ) ) {
					rs.deleteRow();
				} else {
					set.remove( item );
				}
			}
			
			// Insère les éléments encore présents dans le Set
			for ( var item : set ) {
				rs.moveToInsertRow();
				rs.updateObject( "idcompte", compte.getId() );
				rs.updateObject( "role", item );
				rs.insertRow();
			}
			
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public List<String> listerPourCompte( Compte compte ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT * FROM role WHERE idcompte = ? ORDER BY role";
			stmt = cn.prepareStatement(sql);
			stmt.setObject( 1, compte.getId() );
			rs = stmt.executeQuery();

			List<String> liste = new ArrayList<>();
			while (rs.next()) {
				liste.add( rs.getObject("role", String.class) );
			}
			return liste;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}

}
