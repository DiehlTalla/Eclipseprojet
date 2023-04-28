package jfox.jasperreports;

import java.awt.Desktop;
import java.awt.print.PrinterAbortException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import javax.sql.DataSource;
import javax.swing.SwingUtilities;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfox.javafx.view.IManagerGui;
import jfox.localization.BundleMessages;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.repo.InputStreamResource;
import net.sf.jasperreports.repo.ReportResource;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;
import net.sf.jasperreports.swing.JRViewer;

public abstract class ManagerReportAbstract {

	// -------
	// Constantes
	// -------

	private final String PREFIX_FILE_TEMP = "report-";
	private final String EXTENSION_PDF		= ".pdf";
	private final String EXTENSION_DESIGN	= ".jrxml";
	private final String EXTENSION_REPORT	= ".jasper";

	// -------
	// Champs
	// -------

	private IManagerGui managerGui;
	private DataSource dataSource;

	// -------
	// Getters & Setters
	// -------
	
	public void setManagerGui(IManagerGui managerGui) {
		this.managerGui = managerGui;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// -------
	// Actions de haut niveau
	// -------

	public void openFilePdf(IEnumReport idReport, Map<String, Object> params, String pathPdf) {
		managerGui.execTask(() -> {
			JasperPrint print = fillReport(idReport, params);
			openFilePdf(print, pathPdf);
		});
	}

	public void openFilePdf(IEnumReport idReport, String pathPdf) {
		openFilePdf(idReport, null, pathPdf);
	}

	public void openFilePdf(IEnumReport idReport, Map<String, Object> params) {
		openFilePdf(idReport, params, null);
	}

	public void openFilePdf(IEnumReport idReport) {
		openFilePdf(idReport, null, null);
	}
	

	public void showViewer(IEnumReport idReport, Map<String, Object> params) {
		managerGui.execTask(() -> {
			JasperPrint print = fillReport(idReport, params);
			showViewer(print);
		});
	}

	public void showViewer(IEnumReport idReport) {
		showViewer(idReport, null);
	}

	public void print(IEnumReport idReport, Map<String, Object> params) {
		managerGui.execTask(() -> {
			try {
				JasperPrint print = fillReport(idReport, params);
				JasperPrintManager.printReport(print, true);
			} catch (JRException e) {
				if (!(e.getCause() instanceof PrinterAbortException)) {
					throw e;
				}
			}
		});
	}

	public void print(IEnumReport idReport) {
		print(idReport, null);
	}
	
	// -------
	// Actions de bas niveau
	// -------

	public JasperPrint fillReport(IEnumReport idReport, Map<String, Object> params, Connection cn) {

		JasperReport report = null;

		try {

			if (idReport.getPath().endsWith(EXTENSION_DESIGN)) {
				report = JasperCompileManager.compileReport(getInputStream(idReport));
			} else if (idReport.getPath().endsWith(EXTENSION_REPORT)) {
				report = (JasperReport) JRLoader.loadObject(getInputStream(idReport));
			} else {
				throw new IllegalArgumentException(idReport.getPath());
			}

			// Crée le contexte
			SimpleJasperReportsContext context = new SimpleJasperReportsContext();
			RepositoryService service = new ServiceRepository(idReport);
			context.setExtensions(RepositoryService.class, Collections.singletonList(service));

			// Execution du rapport
			return JasperFillManager.getInstance(context).fill(report, params, cn);

		} catch (JRException | FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public JasperPrint fillReport(IEnumReport idReport, Map<String, Object> params) {
		try (var cn = dataSource.getConnection()) {
			return fillReport(idReport, params, cn );
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void exportToFilePdf(JasperPrint print, String pathPdf) {
		try {
			JasperExportManager.exportReportToPdfFile(print, pathPdf);
		} catch (JRException e) {
			new RuntimeException(e);
		}
	}

	public void openFilePdf(JasperPrint print, String pathPdf) {
		
		if ( print.getPages().size() == 0 ) {
			Platform.runLater( () ->
				managerGui.showDialogMessage( BundleMessages.getString("report.empty") )
			);
			return;
		}
		try {
			if (pathPdf == null || pathPdf.isEmpty()) {
				File filePdf = File.createTempFile(PREFIX_FILE_TEMP, EXTENSION_PDF);
				pathPdf = filePdf.getAbsolutePath();
			}
			exportToFilePdf(print, pathPdf);
			Desktop.getDesktop().open(new File(pathPdf));
		} catch (IOException e) {
			new RuntimeException(e);
		}
	}

	public JRViewer createViewer(IEnumReport idReport, Map<String, Object> params) {
		JasperPrint print = fillReport(idReport, params);
		JRViewer viewer = new JRViewer(print);
		viewer.setOpaque(true);
		viewer.setVisible(true);
		return viewer;
	}

	public void showViewer(JasperPrint print) {
		
		if ( print.getPages().size() == 0 ) {
			Platform.runLater( () ->
				managerGui.showDialogMessage( BundleMessages.getString("report.empty") )
			);
			return;
		}

		JRViewer viewer = new JRViewer(print);
		viewer.setOpaque(true);
		viewer.setVisible(true);

		SwingNode swingNode = new SwingNode();
		SwingUtilities.invokeLater(() -> swingNode.setContent(viewer));
		StackPane pane = new StackPane();
		Scene scene = new Scene(pane);
		pane.getChildren().add(swingNode);
		Platform.runLater(() -> {
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			Stage owner = managerGui.getStage();
			stage.initOwner(owner);
			stage.setTitle(owner.getTitle());
			stage.getIcons().addAll(owner.getIcons());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
		});
	}

	/**
	 * Supprime les fichiers temporaires dont le nom commence par le préfixe indiqué
	 * dans la constante PREFIX_FILE_TEMP, s'ils ont été créés il y a plus de 24
	 * heures.
	 */
	@PostConstruct
	public void cleanDirTemp() {
		File dirTemp = new File(System.getProperty("java.io.tmpdir"));
		String[] paths = dirTemp.list();
		for (String path : paths) {
			File file = new File(dirTemp, path);
			if (path.startsWith(PREFIX_FILE_TEMP) && System.currentTimeMillis() - file.lastModified() > 24 * 3600000) {
				file.delete();
			}
		}
	}

	//-------
	// Méthodes auxiliaires
	//-------

	private InputStream getInputStream(IEnumReport report) throws FileNotFoundException {
		InputStream in = report.getClass().getResourceAsStream(report.getPath());
		if (in == null) {
			throw new FileNotFoundException("The file does not exist : " + report.getPath());
		}
		return in;
	}

	//-------
	// Classe auxiliaire
	//-------

	public static class ServiceRepository implements RepositoryService {

		// Constantes
		public static final String EXCEPTION_MESSAGE_KEY_NOT_IMPLEMENTED = "repo.default.not.implemented";

		// Champs
		private final URI root;

		// Constructeur
		public ServiceRepository(IEnumReport idReport) {
			URI uri = null;
			try {
				uri = idReport.getClass().getResource(idReport.getPath()).toURI();
				uri = uri.resolve(".");
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			} finally {
				root = uri;
			}
		}
		
		// Actions

		public InputStream getInputStream(String path) {
			try {
				URL url = root.resolve(path).toURL();
				return url.openStream();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public <K extends Resource> K getResource(String path, Class<K> resourceType) {

			try {
				InputStream in = getInputStream(path);
				if (in != null) {
					if (resourceType == ReportResource.class) {
						JasperReport report = (JasperReport) JRLoader.loadObject(in);
						if (report != null) {
							ReportResource res = new ReportResource();
							res.setName(path);
							res.setReport(report);
							return (K) res;
						}
					} else if (resourceType == InputStreamResource.class) {
						InputStreamResource res = new InputStreamResource();
						res.setInputStream(in);
						return (K) res;
					}
				}
			} catch (JRException e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		@Override
		public Resource getResource(String path) {
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_NOT_IMPLEMENTED, (Object[]) null);
		}

		@Override
		public void saveResource(String path, Resource resource) {
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_NOT_IMPLEMENTED, (Object[]) null);
		}

	}

}
