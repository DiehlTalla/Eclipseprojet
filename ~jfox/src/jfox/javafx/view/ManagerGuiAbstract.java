package jfox.javafx.view;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.SEVERE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import jfox.exception.ExceptionAnomaly;
import jfox.exception.ExceptionPermission;
import jfox.exception.ExceptionValidation;
import jfox.javafx.util.UtilFX;
import jfox.localization.BundleMessages;


public abstract class ManagerGuiAbstract implements IManagerGui, IFactoryScene {

	//-------
	// Logger
	//-------
	private static final Logger logger = Logger.getLogger(ManagerGuiAbstract.class.getName());

	//-------
	// Constantes
	//-------
	private static final KeyCodeCombination KCC_ENTER = new KeyCodeCombination(KeyCode.ENTER);
	private static final KeyCodeCombination KCC_ESCAPE = new KeyCodeCombination(KeyCode.ESCAPE);

	//-------
	// Champs
	//-------
	
	private final Map<Class<?>, View>	views = new HashMap<>();

	private Application	application;
	
	protected Stage stage;
	private IFactoryScene factoryScene = this;
	private View viewPrevious;
	private View viewActive;

	private boolean flagNewStage = true;
	private boolean flagShowErrorInProgress = false;;

	protected Callback<Class<?>, Object> factoryController;
	protected Class<? extends IConfigDialog> typeConfigDialogDefault = ConfigDialogDefault.class;

	protected List<Class<? extends Exception>> listExceptionValidation = new ArrayList<>();
	protected List<Class<? extends Exception>> listExceptionPermission = new ArrayList<>();
	protected List<Class<? extends Exception>> listExceptionAnomaly = new ArrayList<>();


	//-------
	// Constructeur
	//-------

	public ManagerGuiAbstract() {

		addExceptionValidation(ExceptionValidation.class);
		addExceptionPermission(ExceptionPermission.class);
		addExceptionAnomaly(ExceptionAnomaly.class);

		// Traitement par défaut des exceptions
		Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
		Thread.currentThread().setUncaughtExceptionHandler(this::handleUncaughtException);
	}

	//-------
	// Getters & setters
	//-------

	@Override
	public final Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setFactoryController(Callback<Class<?>, Object> factoryController) {
		this.factoryController = factoryController;
	}
	
	public void setApplication(Application application) {
		this.application = application;
	}
	

	//-------
	// Actions
	//-------

	// Affichage des vues

	private View getView(Class<?> typeView) {
		var view = views.get( typeView );
		if( view == null ) {
			var name = typeView.getSimpleName() + ".fxml";
			var location = typeView.getResource( name );
			if( location == null ) {
				throw UtilFX.runtimeException(
						new FileNotFoundException( name ) );
			}
			
			boolean flagTransient = false;
			Class<? extends IConfigDialog> typeConfigDialog = ConfigDialogDefault.class;
			if( typeView.isAnnotationPresent( ConfigView.class )) {
				var configView = typeView.getAnnotation( ConfigView.class );
				flagTransient = configView.flagTransient();
				typeConfigDialog = configView.typeConfigDialog();
			}
			view = new View( location, flagTransient, typeConfigDialog );
			views.put( typeView, view );
		}
		return view;
	}
	
	@Override
	public void showView(Class<?> typeView) {
		showView( getView( typeView ) );
	}

	@Override
	public void showView(IEnumView idView) {
		showView(idView.getView());
	}
	
	private void showView( View view ) {

		var scene = view.getScene();
		var controller = view.getController();
		var flagNewScene = false;

		// Charge le panneau si nécessaire
		if ( scene == null ) {

			flagNewScene = true;

			try {
				// Charge le panneau
				FXMLLoader loader = new FXMLLoader( view.getLocation() );
				loader.setControllerFactory(factoryController);
				view.setRoot( loader.load() );

				// Enregistre le controller dans la vue
				scene = factoryScene.createScene( view );
				controller = loader.getController();
				view.setScene(scene);
				view.setController(controller);
				if( controller != null ) {
					controller.getValidation().validate();
				}

			} catch (IOException e) {
				throw UtilFX.runtimeException(e);
			}
		}

		// Exécute la méthode refresh() du controlleur
		if (controller != null) {
			controller.refresh();
		}

		stage.setScene(scene);
		viewPrevious = viewActive;
		viewActive = view;
		boolean flagClosePrviousView = !flagNewStage;
		flagNewStage = false;

		// Réinitialise la forme du pointeur si besoin
		scene.setCursor(null);
		scene.setCursor(Cursor.DEFAULT);
		scene.getRoot().setCursor(Cursor.DISAPPEAR);

		if (flagNewScene) {
			delayAccelerators( view );
		}

		if (flagClosePrviousView) {
			closeViewObjects( viewPrevious );
		}

		// Force le réaffichage en cas d'utilisation de la mise à l'échelle
		refreshStage();
		
	}
	
	private void delayAccelerators( View view ) {
		
		final Scene scene = view.getScene();
		
		// Retarde les actions sur les boutons par défaut pour éviter
		// la propagation d'évènements à la vue suivante
		Platform.runLater( () -> {
			Runnable accEnter = scene.getAccelerators().get(KCC_ENTER);
			if (accEnter != null) {
				scene.getAccelerators().put(KCC_ENTER, () -> {
					Platform.runLater( () -> {
						accEnter.run();
					});
				});
			}
			Runnable accEscape = scene.getAccelerators().get(KCC_ESCAPE);
			if (accEscape != null) {
				scene.getAccelerators().put(KCC_ESCAPE, () -> {
					Platform.runLater( () -> {
						accEscape.run();
					});
				});
			}
		});
	}
	
	
	private void closeViewObjects( View view ) {
		
		// Libère les ressource de la vue si flagTransient == true
		if ( view.isFlagTransient()  ) {
			for ( Object o : view.getObjectsToClose() ) {
				try {
					Method method = o.getClass().getMethod("close");
					method.invoke( o );
				} catch (NoSuchMethodException e) {
				} catch (Exception e) {
					showDialogError(e);
				}
			}
			view.setScene(null);
			view.setController(null);
			view.getObjectsToClose().clear();
		}
	}
	
	private void refreshStage() {

		double height = stage.getHeight();

		if ( Screen.getPrimary().getOutputScaleX() > 1. && ! Double.isNaN( height ) ) {
			
			if ( stage.isMaximized() ) {

				stage.setFullScreen(true);
				stage.setFullScreen(false);
				
			} else {
				
		    	WritableValue<Double> writableHeight = new WritableValue<Double>() {
		    	    @Override
		    	    public Double getValue() {
		    	        return stage.getHeight();
		    	    }
		    	    @Override
		    	    public void setValue(Double value) {
		    	        stage.setHeight(value);
		    	    }
		    	};	
		    	
		    	Timeline timeline = new Timeline();
			    timeline.getKeyFrames().addAll(
		    	        new KeyFrame( new Duration(20), 
	        	            new KeyValue( writableHeight, height )
		    	        )
		    	    );
		    	stage.setHeight( height + 0.001 );
		    	timeline.play();    	
				
			}
		}
	}

	
	@Override
	public void showDialog(Class<?> typeView) {
		showDialog( getView( typeView ) );
	}
	
	@Override
	public void showDialog(IEnumView idView) {
		showDialog( idView.getView() );
	}

	private void showDialog( View view ) {
		IConfigDialog configDialog;
		IFactoryScene factoryPrevious;
		View viewPrevious;
		try {
			var typeConfigDialog = view.getTypeConfigDialog();
			if( this.typeConfigDialogDefault != null
					&& ( 
							typeConfigDialog == ConfigDialogDefault.class
							|| typeConfigDialog == null
						)
					) 
			{
				typeConfigDialog = this.typeConfigDialogDefault;
			}
			configDialog = (IConfigDialog) factoryController.call( typeConfigDialog );
			factoryPrevious = factoryScene;
			viewPrevious = viewActive;
			factoryScene = configDialog;
		} catch (Exception e) {
			throw UtilFX.runtimeException(e);
		}

		flagNewStage = true;
		Stage stagePrevious = stage;
		stage = new Stage();

		stage.initOwner(stagePrevious);
		stage.setTitle(stagePrevious.getTitle());
		stage.getIcons().addAll(stagePrevious.getIcons());
		stage.initModality(Modality.APPLICATION_MODAL);

		try {
			showView(view);
			configDialog.configureStage(stage);
		} catch (Exception e) {
			returnToPreviousStage(factoryPrevious, viewPrevious);
			throw UtilFX.runtimeException(e);
		}

		EventHandler<WindowEvent> handlerOnShown = (event) -> {
			delayAccelerators( view );
			stage.setOnShowing(null);
		};
		stage.setOnShown(handlerOnShown);
		
		stage.showAndWait();
		returnToPreviousStage(factoryPrevious, viewPrevious);
	}

	private void returnToPreviousStage(IFactoryScene factoryPrevious, View viewPrevious) {
		closeViewObjects( viewActive );
		factoryScene = factoryPrevious;
		viewActive = viewPrevious;
		stage = (Stage) stage.getOwner();
	}

	
	@Override
	public void closeDialog() {
		if ( stage.getOwner() != null ) {
			stage.close();
		}
	}

	@Override
	public void exit() {
		Platform.exit();
	}

	
	//-------
	// Actions générales
	//-------

	@Override
	public void execTask(RunnableWithException runnable) {

		final EventHandler<InputEvent> inputEventConsumer = (event) -> event.consume();
		stage.addEventFilter(InputEvent.ANY, inputEventConsumer);

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				stage.getScene().setCursor(Cursor.WAIT);
				stage.getScene().getRoot().setCursor(null);
				runnable.run();
				return null;
			}
		};

		EventHandler<WorkerStateEvent> handlerEndOfTask = (event) -> {
			stage.removeEventFilter(InputEvent.ANY, inputEventConsumer);
			stage.getScene().setCursor(Cursor.DEFAULT);
			if (task.getException() != null) {
				showDialogError(task.getException());
			}
		};
		task.setOnCancelled(handlerEndOfTask);
		task.setOnFailed(handlerEndOfTask);
		task.setOnSucceeded(handlerEndOfTask);

		Thread thread = new Thread(task);
		thread.start();
	}

	@Override
	public void showDialogMessage(String message) {
		final Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.initOwner(stage);
		alert.setHeaderText(message);
		alert.showAndWait();
	}

	@Override
	public boolean showDialogConfirm(String message) {
		final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.initOwner(stage);
		alert.setHeaderText(message);
		final Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}

	@Override
	public void showDialogError( Throwable e ) {
		
		var logger = ManagerGuiAbstract.logger;

		if (flagShowErrorInProgress) {
			return;
		}

		var message = BundleMessages.getString( "error.failure" );

		if (e != null) {
			
//			var i = 0;
//			var st = e.getStackTrace();
//			for( var item : st ) {
//				System.out.println( i + " " + item.getClassName() );
//				++i;
//				if( i > 5 ) break;
//			}
			logger = Logger.getLogger( e.getStackTrace()[0].getClassName() );

			e = UtilFX.unwrapException(e);

			boolean flagKnownType = false;

			if (!flagKnownType) {
				for (var type : listExceptionValidation) {
					if (type.isAssignableFrom(e.getClass())) {
						message = e.getMessage();
						flagKnownType = true;
						break;
					}
				}
			}
			if (!flagKnownType) {
				for (var type : listExceptionPermission) {
					if (type.isAssignableFrom(e.getClass())) {
						message = BundleMessages.getString( "error.permission" );
						logger.log(FINEST, message, e);
						flagKnownType = true;
						break;
					}
				}
			}
			if (!flagKnownType) {
				for (var type : listExceptionAnomaly) {
					if (type.isAssignableFrom(e.getClass())) {
						if ( e.getCause() == null ) {
							logger.log(SEVERE, e.getMessage() == null ? "" : e.getMessage() , e);
						} else {
							logger.log(FINE, e.getMessage() == null ? "" : e.getMessage() , e);
						}
						flagKnownType = true;
						break;
					}
				}
			}
			if (!flagKnownType) {
				logger.log(SEVERE, e.getMessage() == null ? "" : e.getMessage() , e);
			}
		}
		showDialogError(message);
	}

	@Override
	public void showDialogError(String message) {
		final Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.initOwner(stage);
		alert.setHeaderText(message);
		flagShowErrorInProgress = true;
		alert.showAndWait();
		flagShowErrorInProgress = false;
	}
	
	@Override
	public void showDocument(String uri) {
		application.getHostServices().showDocument(uri);
	} 

	
	//-------
	// Méthodes de configuraiton
	//-------

	public abstract void configureStage() throws IOException;

	protected void addExceptionAnomaly(Class<? extends Exception> type) {
		listExceptionAnomaly.add(type);
	}

	protected void addExceptionPermission(Class<? extends Exception> type) {
		listExceptionPermission.add(type);
	}

	protected void addExceptionValidation(Class<? extends Exception> type) {
		listExceptionValidation.add(type);
	}

	
	//-------
	// Méthodes auxiliaires
	//-------

	
	private void handleUncaughtException(Thread t, Throwable e) {
		showDialogError(e);
	}

}
