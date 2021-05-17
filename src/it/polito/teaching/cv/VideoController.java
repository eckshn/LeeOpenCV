package it.polito.teaching.cv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import it.polito.elite.teaching.cv.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the histogram creation.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 2.0 (2017-03-10)
 * @since 1.0 (2013-11-20)
 * 
 */
public class VideoController
{
	private int l_thresh = 125;
	@FXML
	private Text r;
	@FXML
	private Text g;
	@FXML
	private Text b;
	// the FXML button
	@FXML
	private Button button;
	// the FXML logo checkbox
	// the FXML grayscale checkbox
	// the FXML area for showing the current frame
	@FXML
	private ImageView currentFrame;
	@FXML
	private ImageView logo;
	@FXML
	private CheckBox masked;
	@FXML
	private Text rock;
	
	public static String[] ints = {"", "", ""};
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	public static VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;
	
	/**
	 * Initialize method, automatically called by @{link FXMLLoader}
	 */
	public void initialize()
	{
		this.capture = new VideoCapture();
		this.cameraActive = false;
	}
	
	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	protected void startCamera()
	{
		logo.setFitWidth(100);
		logo.setPreserveRatio(true);
		updateImageView(logo, Utils.mat2Image(Imgcodecs.imread("resources/log.jpeg")));
		// set a fixed width for the frame
		this.currentFrame.setFitWidth(600);
		// preserve image ratio
		this.currentFrame.setPreserveRatio(true);
		
		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(0);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						// convert and show the frame
						if(!masked.isSelected()) {
							Image imageToShow = Utils.mat2Image(frame);
							updateImageView(currentFrame, imageToShow);
							double[] vals = getBGR(frame);
							for(int i = 0; i < 3; i++) {
								ints[i] = ((int) vals[i]) + "";
							}
							b.setText("B = " + ints[0]);
							g.setText("G = " + ints[1]);
							r.setText("R = " + ints[2]);
							
							int total = (int) (vals[0] + vals[1] + vals[2]);
							if(total < 275) {
								rock.setText("Rock Type : Mafic");
							}
							else if (total < 325) {
								rock.setText("Rock Type : Intermediate");
							}
							else {
								rock.setText("Rock Type : Felsic");
							}
							//r.setText("hi");
						}
						else {
							double[] vals = findRock();
							for(int i = 0; i < 3; i++) {
								ints[i] = ((int) vals[i]) + "";
							}
							b.setText("B = " + ints[0]);
							g.setText("G = " + ints[1]);
							r.setText("R = " + ints[2]);
							
							int total = (int) (vals[0] + vals[1] + vals[2]);
							if(total < 275) {
								rock.setText("Rock Type : Mafic");
							}
							else if (total < 325) {
								rock.setText("Rock Type : Intermediate");
							}
							else {
								rock.setText("Rock Type : Felsic");
							}
						}
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.button.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.button.setText("Start Camera");
			
			// stop the timer
			this.stopAcquisition();
		}
	}
	
	
	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Mat grabFrame()
	{
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				

				
			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the frame elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	
	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition()
	{
		if (this.timer != null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	private double[] findRock() {
		Mat copy = grabFrame();
		Mat thresh = new Mat();
		Mat grayscale = new Mat();
		Imgproc.cvtColor(copy, grayscale, Imgproc.COLOR_RGB2GRAY); //sets the value of output of the desired change from image 
		
		Imgproc.threshold(grayscale, thresh, l_thresh, 1, Imgproc.THRESH_BINARY_INV);
		
		Mat kernel = Mat.ones(new int[] {2, 2}, 0);
		Mat opening = new Mat();
		Mat closing = new Mat();
		Imgproc.morphologyEx(thresh, opening, Imgproc.MORPH_OPEN, kernel);
		Imgproc.morphologyEx(opening, closing, Imgproc.MORPH_CLOSE, kernel);
		
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchey = new Mat();
		Imgproc.findContours(closing, contours, hierarchey, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE); //adds value to the contours list
		Scalar color = new Scalar(255, 255, 255); //red
		int index = findLargestArea(contours); //index of largest area
		Imgproc.drawContours(copy, contours, index, color, 2); //draws contours only at the index
		
		Mat mask = Mat.zeros(copy.size(), 0);
		Imgproc.drawContours(mask, contours, index, color, -1);
		Mat temp = grabFrame();
		Mat object = new Mat();
		Core.bitwise_and(temp, temp, object, mask);
		Image imageToShow = Utils.mat2Image(object);
		updateImageView(currentFrame, imageToShow);
		
		MatOfDouble mean = new MatOfDouble();
		MatOfDouble dev = new MatOfDouble();
		Core.meanStdDev(temp, mean, dev, mask);
		return mean.toArray();
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}
	
	public static int findLargestArea(List<MatOfPoint> contours) {
		Mat max = contours.get(0);
		int index = 0;
		for(int i = 0; i < contours.size(); i++) {
			if(Imgproc.contourArea(max) < Imgproc.contourArea(contours.get(i))) {
				max = contours.get(i);
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed()
	{
		this.stopAcquisition();
	}
	
	public static boolean on() {
		return capture.isOpened();
	}
	
	public static double[] getBGR(Mat image, Mat mask) {
		MatOfDouble mean = new MatOfDouble();
		MatOfDouble dev = new MatOfDouble();
		Core.meanStdDev(image, mean, dev, mask);
		return mean.toArray();
	}
	
	public static double[] getBGR(Mat image) {
		MatOfDouble mean = new MatOfDouble();
		MatOfDouble dev = new MatOfDouble();
		Core.meanStdDev(image, mean, dev);
		return mean.toArray();
	}
	
}
