package contacts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import contacts.data.Categorie;
import jakarta.inject.Inject;
import jfox.jdbc.UtilJdbc;


public class DaoCategorie {

	
	//-------
	// Champs
	//-------

	@Inject
	private DataSource		dataSource;

	
	//-------
	// Actions
	//-------

	public void inserer( Categorie categorie ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "INSERT INTO categorie ( libelle, debut ) VALUES( ?, ? ) ";
			stmt = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			stmt.setObject( 1, categorie.getLibelle() );
			stmt.setObject( 2, categorie.getDebut() );
			stmt.executeUpdate();

			// Récupère l'identifiant généré par le SGBD
			rs = stmt.getGeneratedKeys();
			rs.next();
			categorie.setId( rs.getObject( 1, Integer.class) );
	
		} catch ( SQLException e ) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public void modifier( Categorie categorie ) {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "UPDATE categorie SET libelle = ?, debut = ? WHERE idcategorie =  ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, categorie.getLibelle() );
			stmt.setObject( 2, categorie.getDebut() );
			stmt.setObject( 3, categorie.getId() );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}


	public void supprimer( int idCategorie ) {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "DELETE FROM categorie WHERE idcategorie = ? ";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, idCategorie );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}

	
	public Categorie retrouver( int idCategorie ) {

		Connection			cn 		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "SELECT * FROM categorie WHERE idcategorie = ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject(1, idCategorie);
			rs = stmt.executeQuery();

			if ( rs.next() ) {
				return construireCategorie( rs );
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public List<Categorie> listerTout() {

		Connection			cn 		= null;
		PreparedStatement	stmt 	= null;
		ResultSet 			rs		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();
			sql = "SELECT * FROM categorie ORDER BY libelle";
			stmt = cn.prepareStatement( sql );
			rs = stmt.executeQuery();

			List<Categorie> liste = new ArrayList<>();
			while (rs.next()) {
				liste.add( construireCategorie( rs ) );
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
	
	protected Categorie construireCategorie( ResultSet rs ) throws SQLException {
		Categorie categorie = new Categorie();
		categorie.setId( rs.getObject( "idcategorie", Integer.class ) );
		categorie.setLibelle( rs.getObject( "libelle", String.class ) );
		categorie.setDebut( rs.getObject( "debut", LocalDate.class ) );
		return categorie;
	}

}
