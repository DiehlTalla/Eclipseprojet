package contacts.dao;


	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;
	import java.util.ArrayList;
	import java.util.List;

	import javax.sql.DataSource;

	import contacts.data.Memo;
import contacts.data.Service;
import jakarta.inject.Inject;
	import jfox.jdbc.UtilJdbc;

	public class DaoService {

		// -------
		// Champs
		// -------

		@Inject
		private DataSource dataSource;

		@Inject
		private DaoCategorie daoCategorie;
		

		// -------
		// Actions
		// -------

		public void inserer(Service service) {

			Connection cn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql;

			try {
				cn = dataSource.getConnection();
				sql = "INSERT INTO memo ( id, nom, annee,siege,idpersonne) VALUES( ?, ?, ?, ?, ? ) ";
				stmt = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setObject(1, service.getId());
				stmt.setObject(2, service.getNom());
				stmt.setObject(3, service.getAnnee());
				
				stmt.setObject(4, service.getFlagsiege());
				stmt.setObject(5, service.getCategorie() == null ? null : service.getCategorie().getId());

				stmt.executeUpdate();

				// Récupère l'identifiant généré par le SGBD
				rs = stmt.getGeneratedKeys();
				rs.next();
				service.setId(rs.getObject(1, Integer.class));

			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				UtilJdbc.close(rs, stmt, cn);
			}
		}

		public void modifier(Service service) {

			Connection cn = null;
			PreparedStatement stmt = null;
			String sql;

			try {
				cn = dataSource.getConnection();
				sql = "UPDATE memo SET Id = ?, nom = ?, flagurgent = ?, idcategorie=? WHERE idmemo =  ?";
				stmt = cn.prepareStatement(sql);
				stmt.setObject(1, service.getId());
				stmt.setObject(2, service.getNom());
				stmt.setObject(3, service.getAnnee());
				stmt.setObject(4, service.getFlagsiege());
				
				stmt.setObject(5, service.getCategorie() == null ? null : service.getCategorie().getId());
				stmt.setObject(6, service.getId());

				stmt.executeUpdate();

			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				UtilJdbc.close(stmt, cn);
			}
		}

		public void supprimer(int idService) {

			Connection cn = null;
			PreparedStatement stmt = null;
			String sql;

			try {
				cn = dataSource.getConnection();
				sql = "DELETE FROM memo WHERE idservice = ? ";
				stmt = cn.prepareStatement(sql);
				stmt.setObject(1, idService);
				stmt.executeUpdate();

			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				UtilJdbc.close(stmt, cn);
			}
		}

		public Service retrouver(int idService) {

			Connection cn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql;

			try {
				cn = dataSource.getConnection();
				sql = "SELECT * FROM memo WHERE idservice = ?";
				stmt = cn.prepareStatement(sql);
				stmt.setObject(1, idService);
				rs = stmt.executeQuery();

				if (rs.next()) {
					return construireService(rs);
				} else {
					return null;
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				UtilJdbc.close(rs, stmt, cn);
			}
		}

		public List<Service> listerTout() {

			Connection cn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			String sql;

			try {
				cn = dataSource.getConnection();
				sql = "SELECT * FROM service ORDER BY nom";
				stmt = cn.prepareStatement(sql);
				rs = stmt.executeQuery();

				List<Service> liste = new ArrayList<>();
				while (rs.next()) {
					liste.add(construireService(rs));
				}
				return liste;

			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				UtilJdbc.close(rs, stmt, cn);
			}
		}

		private Service construireService(ResultSet rs) {
			// TODO Auto-generated method stub
			return null;
		}

		public int compterPourCategorie(int idCategorie) {

			Connection cn = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			try {
				cn = dataSource.getConnection();
				String sql = "SELECT COUNT(*) FROM memo WHERE idcategorie = ?";
				stmt = cn.prepareStatement(sql);
				stmt.setObject(1, idCategorie);
				rs = stmt.executeQuery();

				rs.next();
				return rs.getInt(1);

			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				UtilJdbc.close(rs, stmt, cn);
			}
		}
	public int compterPourCategorie1( int idCategorie ) {
	    	
			Connection			cn		= null;
			PreparedStatement	stmt 	= null;
			ResultSet 			rs		= null;

			try {
				cn = dataSource.getConnection();
	            String sql = "SELECT COUNT(*) FROM service WHERE idcategorie = ?";
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

		// -------
		// Méthodes auxiliaires
		// -------

		protected Memo construireMemo(ResultSet rs) throws SQLException {
			Memo memo = new Memo();
			memo.setId(rs.getObject("idservice", Integer.class));
			memo.setTitre(rs.getObject("nom", String.class));
			memo.setDescription(rs.getObject("annee", String.class));
			memo.seFlagUrgent(rs.getObject("flagsiege", Boolean.class));
			var idCategorie = rs.getObject( "idpersonne", Integer.class );
			if ( idCategorie != null ) {
			 memo.setCategorie( daoCategorie.retrouver( idCategorie ) );
			}
			return memo;
			
		}
		
}