package com.allincode.OpenCV;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

public class VideoCaptureDemo {
    public static void main(String[] args) {
        try (VideoCapture videoCapture = new VideoCapture(0)) {
            if (!videoCapture.isOpened()) {
                System.out.println("Can't open the camera");
                return;
            }
            while (true) {
                Mat image = new Mat();
                videoCapture.read(image);
            }
        }
    }
}
