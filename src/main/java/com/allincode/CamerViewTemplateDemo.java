package com.allincode;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.FaceDetectorYN;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class CamerViewTemplateDemo {
    public static void main(String[] args) throws IOException {
//        URL url = Demo.class.getClassLoader().getResource("face_detection_yunet/face_detection_yunet_2023mar.onnx");
//        assert url != null;
//        String modelPath = new File(url.getFile()).getCanonicalPath();

        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        Mat grabbedImage = converter.convert(grabber.grab());

        int height = grabbedImage.rows();
        int width = grabbedImage.cols();

//        FaceDetectorYN faceDetectorYN = FaceDetectorYN.create(modelPath, "cpu", new Size(width, height));

        Mat grayImage = new Mat(height, width, CV_8UC1);

        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {

            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            Mat result = grabbedImage.clone();
//            faceDetectorYN.detect(grabbedImage, result);
            Frame grabbedFrame = converter.convert(result);
            frame.showImage(grabbedFrame);

        }

        frame.dispose();
        grabber.stop();
    }
}
