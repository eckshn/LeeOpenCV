package it.polito.teaching.cv;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;

//import com.fazecast.jSerialComm.SerialPort;

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
public class Video extends Application implements Runnable
{
	@FXML
	private CheckBox masked;
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Video.fxml"));
			// store the root element so that the controllers can use it
			BorderPane rootElement = (BorderPane) loader.load();
			// create and style a scene
			Scene scene = new Scene(rootElement, 800, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("Video processing");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();
			
			// set the proper behavior on closing the application
			VideoController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we)
				{
					controller.setClosed();
				}
			}));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		// load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Video obj = new Video();
		Thread thread = new Thread(obj);
		thread.start();
		launch(args);
		
		
	}

	@Override
	public void run() {
		/*SerialPort port = SerialPort.getCommPort("COM4");
		port.setComPortParameters(9600, 8, 1, 0);
		port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
		
		port.openPort();
		if(port.isOpen()) {
			System.out.println("Port initialized!");
		}
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} */
		/*
		try {
			while(VideoController.on()) {
				//String red = VideoController.r.getText();
				//String green = VideoController.g.getText();
				//String blue = VideoController.b.getText();
				System.out.println("grabbing");
				String red = VideoController.ints[0].substring(VideoController.ints[0].indexOf("= ") + 2);
				String green = VideoController.ints[1].substring(VideoController.ints[1].indexOf("= ") + 2);
				String blue = VideoController.ints[2].substring(VideoController.ints[2].indexOf("= ") + 2);
				System.out.println("converting");
				byte[][] flush = new byte[3][1];
				flush[0] = red.getBytes();
				flush[1] = green.getBytes();
				flush[2] = blue.getBytes();
				System.out.println("printing");
				System.out.println(flush[0].toString());
				port.getOutputStream().write(1);
				//port.getOutputStream().write(red.getBytes());
				//port.getOutputStream().flush();
				//port.getOutputStream().write(green.getBytes());
				//port.getOutputStream().flush();
				//port.getOutputStream().write(blue.getBytes());
				//port.getOutputStream().flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
	}
}
