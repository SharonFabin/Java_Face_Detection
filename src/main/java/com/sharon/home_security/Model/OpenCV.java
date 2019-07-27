package com.sharon.home_security.Model;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpenCV {
    private volatile boolean active;
    private boolean haarClassifer, lbpClassifier;
    private VideoCapture capture;
    private ScheduledExecutorService timer;
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;
    public static String basePath=System.getProperty("user.dir");
    public static String classifierPath1=basePath+"\\src\\main\\resources\\haarcascades\\" +
            "haarcascade_frontalface_alt2.xml";

    public OpenCV(){
        nu.pattern.OpenCV.loadLocally();
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;
        System.out.println(classifierPath1);
    }

    public void startCamera(ImageUser imageUser) {
        if (!active) {
            this.capture.open(0);
            if (this.capture.isOpened()) {
                active = true;
                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = () -> {
                    // effectively grab and process a single frame
                    Mat frame = grabFrame();
                    // convert and show the frame

                    Image imageToShow = Utils.mat2Image(frame);
                    imageUser.useImage(imageToShow);
                };


                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
            }
            else {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        }
        else {
            active=false;

            // stop the timer
            this.stopAcquisition();
        }
    }

    private Mat grabFrame() {
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    // face detection
                    this.detectAndDisplay(frame);
                }

            }
            catch (Exception e) {
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0)
        {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(
                grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
            Point tl = facesArray[i].tl(),br=facesArray[i].br();
            //System.out.println("detected x: "+((br.x-tl.x)/2+br.x)+" y: "+((br.y-tl.y)/2)+br.y);
            System.out.println("detected x: "+tl.x+" y: "+tl.y);
        }

    }

    public void setClassifier(int choise){
        String haarXml = classifierPath1;
        String lbpXml = "";
        if(choise==1) this.faceCascade.load(haarXml);
        else this.faceCascade.load(lbpXml);
    }

    private void stopAcquisition() {
        if (this.timer!=null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture," +
                        " trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }


}
