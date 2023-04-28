package contacts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import contacts.data.Personne;
import jakarta.inject.Inject;
import jfox.jdbc.UtilJdbc;


public class DaoPersonne {

	
	// Champs

	@Inject
	private DataSource		dataSource;
	@Inject
	private DaoTelephone	daoTelephone;
	@Inject
	private DaoCategorie	daoCategorie;

	
	// Actions

	public void inserer( Personne personne )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			// Insère le personne
			sql = "INSERT INTO personne ( idcategorie, nom, prenom ) VALUES ( ?, ?, ? )";
			stmt = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS  );
			stmt.setObject(	1, personne.getCategorie().getId() );
			stmt.setObject(	2, personne.getNom() );
			stmt.setObject(	3, personne.getPrenom() );
			stmt.executeUpdate();

			// Récupère l'identifiant généré par le SGBD
			rs = stmt.getGeneratedKeys();
			rs.next();
			personne.setId( rs.getObject( 1, Integer.class ) );
	
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}

		// Insère les telephones
		daoTelephone.mettreAJourPourPersonne( personne );
	}

	
	public void modifier( Personne personne )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String 				sql;

		try {
			cn = dataSource.getConnection();

			// Modifie le personne
			sql = "UPDATE personne SET idcategorie = ?, nom = ?, prenom = ? WHERE idpersonne =  ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, personne.getCategorie().getId() );
			stmt.setObject( 2, personne.getNom() );
			stmt.setObject( 3, personne.getPrenom() );
			stmt.setObject( 4, personne.getId() );
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}

		// Modifie les telephones
		daoTelephone.mettreAJourPourPersonne( personne );
	}

	
	public void supprimer( int idPersonne )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String 				sql;

		try {
			cn = dataSource.getConnection();

			// Supprime le personne
			sql = "DELETE FROM personne WHERE idpersonne = ? ";
			stmt = cn.prepareStatement(sql);
			stmt.setObject( 1, idPersonne );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}

	
	public Personne retrouver( int idPersonne )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT p.*, c.* FROM personne p JOIN categorie c ON p.idcategorie = c.idcategorie WHERE p.idpersonne = ?";
            stmt = cn.prepareStatement(sql);
            stmt.setObject( 1, idPersonne);
            rs = stmt.executeQuery();

            if ( rs.next() ) {
                return construirePersonne(rs, true, true );
            } else {
            	return null;
            }
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}

	
	public List<Personne> listerTout()   {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT p.*, c.* FROM personne p JOIN categorie c ON p.idcategorie = c.idcategorie ORDER BY p.nom, p.prenom";
			stmt = cn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Personne> liste = new ArrayList<>();
			while (rs.next()) {
				liste.add( construirePersonne(rs, true, false) );
			}
			return liste;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}

    
    public int compterPourCategorie( int idCategorie ) {
    	
		Connection			cn		= null;
		PreparedStatement	stmt 	= null;
		ResultSet 			rs		= null;

		try {
			cn = dataSource.getConnection();
            String sql = "SELECT COUNT(*) FROM personne WHERE idcategorie = ?";
            stmt = cn.prepareStatement( sql );
            stmt.setObject( 1, idCategorie );
            rs = stmt.executeQuery();

            rs.next();
            return rs.getInt( 1 );

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
    }
	
	
	// Méthodes auxiliaires
	
    protected Personne construirePersonne( ResultSet rs, boolean flagCategorie, boolean flagTelephones ) throws SQLException {

		Personne personne = new Personne();
		personne.setId(rs.getObject( "idpersonne", Integer.class ));
		personne.setNom(rs.getObject( "nom", String.class ));
		personne.setPrenom(rs.getObject( "prenom", String.class ));
		
		if( flagCategorie ) {
			personne.setCategorie( daoCategorie.construireCategorie( rs ) );
		}

		if ( flagTelephones ) {
			personne.getTelephones().addAll( daoTelephone.listerPourPersonne( personne ) );
		}
		
		return personne;
	}
	
}
