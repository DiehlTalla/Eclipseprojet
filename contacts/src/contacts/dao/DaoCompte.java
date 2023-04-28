package contacts.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import contacts.data.Compte;
import jakarta.inject.Inject;
import jfox.jdbc.UtilJdbc;


public class DaoCompte {

	
	// Champs

	@Inject
	private DataSource		dataSource;
	@Inject
	private DaoRole			daoRole;

	
	// Actions

	public void inserer( Compte compte )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			// Insère le compte
			sql = "INSERT INTO compte ( pseudo, motdepasse, email ) VALUES ( ?, ?, ? )";
			stmt = cn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS ); 
			stmt.setObject( 1, compte.getPseudo() );
			stmt.setObject( 2, compte.getMotDePasse() );
			stmt.setObject( 3, compte.getEmail() );
			stmt.executeUpdate();

			// Récupère l'identifiant généré par le SGBD
			rs = stmt.getGeneratedKeys();
			rs.next();
			compte.setId( rs.getObject( 1, Integer.class) );
	
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}

		// Insère les rôles
		daoRole.mettreAJourPourCompte( compte );
	}
	

	public void modifier( Compte compte )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String 				sql;

		try {
			cn = dataSource.getConnection();

			// Modifie le compte
			sql = "UPDATE compte SET pseudo = ?, motdepasse = ?, email = ? WHERE idcompte =  ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, compte.getPseudo() );
			stmt.setObject( 2, compte.getMotDePasse() );
			stmt.setObject( 3, compte.getEmail() );
			stmt.setObject( 4, compte.getId() );
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}

		// Modifie les rôles
		daoRole.mettreAJourPourCompte( compte );

	}
	

	public void supprimer( int idCompte )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		String 				sql;

		try {
			cn = dataSource.getConnection();

			// Supprime le compte
			sql = "DELETE FROM compte WHERE idcompte = ? ";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, idCompte );
			stmt.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( stmt, cn );
		}
	}
	

	public Compte retrouver( int idCompte )  {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT * FROM compte WHERE idcompte = ?";
            stmt = cn.prepareStatement( sql );
            stmt.setObject( 1, idCompte );
            rs = stmt.executeQuery();

            if ( rs.next() ) {
                return construireCompte( rs, true );
            } else {
            	return null;
            }
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}
	

	public List<Compte> listerTout()   {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT * FROM compte ORDER BY pseudo";
			stmt = cn.prepareStatement( sql );
			rs = stmt.executeQuery();

			List<Compte> liste = new ArrayList<>();
			while ( rs.next() ) {
				liste.add( construireCompte( rs, false ) );
			}
			return liste;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public Compte validerAuthentification( String pseudo, String motDePasse )  {
		
		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		try {
			cn = dataSource.getConnection();

			sql = "SELECT * FROM compte WHERE pseudo = ? AND motdepasse = ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject( 1, pseudo );
			stmt.setObject( 2, motDePasse );
			rs = stmt.executeQuery();

			if ( rs.next() ) {
				return construireCompte( rs, true );			
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}


	public boolean verifierUnicitePseudo( String pseudo, Integer idCompte )   {

		Connection			cn		= null;
		PreparedStatement	stmt	= null;
		ResultSet 			rs 		= null;
		String				sql;

		if ( idCompte == null ) idCompte = 0;
		
		try {
			cn = dataSource.getConnection();

			sql = "SELECT COUNT(*) = 0 AS unicite"
					+ " FROM compte WHERE pseudo = ? AND idcompte <> ?";
			stmt = cn.prepareStatement( sql );
			stmt.setObject(	1, pseudo );
			stmt.setObject(	2, idCompte );
			rs = stmt.executeQuery();
			
			rs.next();
			return rs.getBoolean( "unicite" );
	
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			UtilJdbc.close( rs, stmt, cn );
		}
	}
	
	
	// Méthodes auxiliaires
	
	protected Compte construireCompte( ResultSet rs, boolean flagComplet ) throws SQLException {
		Compte compte = new Compte();
		compte.setId( rs.getObject( "idcompte", Integer.class ) );
		compte.setPseudo( rs.getObject( "pseudo", String.class ) );
		compte.setMotDePasse( rs.getObject( "motdepasse", String.class ) );
		compte.setEmail( rs.getObject( "email", String.class ) );
		if ( flagComplet ) {
			compte.getRoles().setAll( daoRole.listerPourCompte( compte ) );
		}
		return compte;
	}
	
}
