package code;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPortException;

import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;

import extra.Utils;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 * 
 * This application handles a video stream and can convert its frames in gray
 * scale or color. Moreover, for each frame, it shows the corresponding
 * histogram and it is possible to add a logo in a corner of the video.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 2.0 (2017-03-10)
 * @since 1.0 (2013-11-20)
 * 
 */
public class Video extends Application implements Runnable {
	public static volatile boolean drillOn;
	public static volatile boolean lightOn;
	public static volatile boolean arduinoOn = true;
	public static volatile String light = "c255";
	public static volatile String lastColor = "c255";

	@Override
	public void start(Stage primaryStage) {
		try {
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Video.fxml"));
			// store the root element so that the controllers can use it
			BorderPane rootElement = (BorderPane) loader.load();
			// create and style a scene
			Scene scene = new Scene(rootElement, 1000, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Rock Analysis Instrument");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();
			primaryStage.getIcons().add(Utils.mat2Image(Imgcodecs.imread("resources/log.jpeg"))); // set logo of app
			// set the proper behavior on closing the application
			VideoController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SerialPortException {

		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Video obj = new Video();
		Thread thread = new Thread(obj);
		thread.start();
		launch(args);

	}

	@Override
	public void stop() {
		System.out.println("Exiting");
		arduinoOn = false;
	}

	@Override
	public void run() {
		try {
			Arduino.main(null); // start Arduino Java Communication
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
