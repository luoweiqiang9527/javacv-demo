package com.allincode;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;

public class Main {
    public static void main(String[] args) throws IOException {
        URL url = Demo.class.getClassLoader().getResource("haarcascade_frontalface_alt.xml");
        assert url != null;
        String classifierName = new File(url.getFile()).getCanonicalPath();

        CascadeClassifier classifier = new CascadeClassifier(classifierName);
        if (classifier == null) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber, OpenKinect2FrameGrabber,
        // RealSenseFrameGrabber, RealSense2FrameGrabber, PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        // todo 使用FFmpegFrameGrabber处理关键帧
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, JavaFX, Tesseract, OpenCV, etc).
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        // FAQ about IplImage and Mat objects from OpenCV:
        // - For custom raw processing of data, createBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
        //   Java2DFrameConverter and OpenCVFrameConverter, one after the other.
        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
        //   data more directly between BufferedImage and IplImage or Mat via Frame objects.
        Mat grabbedImage = converter.convert(grabber.grab());

        int height = grabbedImage.rows();
        int width = grabbedImage.cols();

        // Objects allocated with `new`, clone(), or a create*() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling deallocate().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        Mat grayImage = new Mat(height, width, CV_8UC1);

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        // We can allocate native arrays using constructors taking an integer as argument.
        Point hatPoints = new Point(3);
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {

            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            RectVector faces = new RectVector();
            classifier.detectMultiScale(grayImage, faces);
            long total = faces.size();
            for (long i = 0; i < total; i++) {
                Rect r = faces.get(i);
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);
            }
//            if (total > 0) {
//                // 输出图像
//                opencv_imgcodecs.imwrite(System.currentTimeMillis() + ".jpg", grabbedImage);
//            }
            Frame grabbedFrame = converter.convert(grabbedImage);
            frame.showImage(grabbedFrame);
        }

        frame.dispose();
        grabber.stop();
    }

    private static double compareImage(Mat src1, Mat src2) {
        Mat diff = new Mat();
        opencv_core.absdiff(src1, src2, diff);
        Mat sqr = new Mat();
        opencv_core.pow(diff, 2.0, sqr);
        return opencv_core.mean(sqr).get();
    }
}