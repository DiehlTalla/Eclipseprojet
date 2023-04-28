package contacts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import contacts.data.Personne;
import contacts.data.Telephone;
import jakarta.inject.Inject;
import jfox.jdbc.UtilJdbc;


public class DaoTelephone {

	
	//-------
	// Champs
	//-------

	@Inject
	private DataSource		dataSource;

	
	//-------
	// Actions
	//-------

	public void mettreAJourPourPersonne( Personne personne ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet			rs 		= null;
		String				sql;

		try {

			// Crée une Map des éléments à modifier
			// (ceux qui on un identifiant)
			var map = new HashMap<Integer,Telephone>();
			for ( var item : personne.getTelephones() ) {
				if( item.getId() != null ) {
					map.put( item.getId(), item ); 
				}
			}

			// Crée un ResultSet des éléments présents dans la table
			cn = dataSource.getConnection();
			sql = "SELECT * FROM telephone WHERE idpersonne = ?";
			stmt = cn.prepareStatement( sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE );
			stmt.setObject( 1, personne.getId() );
			rs = stmt.executeQuery();
			
			// Parcourt les éléments du ResultSet
			// Supprime ceux qui ne sont pas dans la Map
			// Modifier ceux qui y sont présents
			while( rs.next() ) {
				var key = rs.getObject( "idtelephone", Integer.class );
				var item = map.get( key );
				if( item == null ) {
					rs.deleteRow();
				} else {
					update( rs, item, personne );
					rs.updateRow();
				}
			}

			// Insère les nouveaux éléments
			// (ceux dont l'identifiant est null)
			for ( var item : personne.getTelephones() ) {
				if( item.getId() == null ) {
					rs.moveToInsertRow();
					update( rs, item, personne );
					rs.insertRow();
				}
			}
			
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public List<Telephone> listerPourPersonne( Personne personne ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT * FROM telephone WHERE idpersonne = ? ORDER BY libelle";
			stmt = cn.prepareStatement(sql);
			stmt.setObject( 1, personne.getId() );
			rs = stmt.executeQuery();

			List<Telephone> liste = new ArrayList<>();
			while (rs.next()) {
				liste.add( construireTelephone( rs, personne ) );
			}
			return liste;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}
	
	
	//-------
	// Méthodes auxiliaires
	//-------
	
	private void update( ResultSet rs, Telephone item, Personne personne ) throws SQLException {
		rs.updateObject( "idpersonne", personne.getId() );
		rs.updateObject( "libelle", item.getLibelle() );
		rs.updateObject( "numero", item.getNumero() );
	}
	
	protected Telephone construireTelephone( ResultSet rs, Personne personne ) throws SQLException {
		Telephone telephone = new Telephone();
		telephone.setId(rs.getObject( "idtelephone", Integer.class ));
		telephone.setLibelle(rs.getObject( "libelle", String.class ));
		telephone.setNumero(rs.getObject( "numero", String.class ));
		return telephone;
	}

}
