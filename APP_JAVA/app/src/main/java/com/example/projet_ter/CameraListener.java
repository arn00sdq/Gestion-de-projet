package com.example.projet_ter;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "ProjectTER::Camera";

    private final JavaCameraView camera_view;

    private Mat rgba_matrix;
    private Mat gray_matrix;

    public CameraListener(JavaCameraView camera_view) {
        this.camera_view = camera_view;
        this.camera_view.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this.rgba_matrix = new Mat();
        this.gray_matrix = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        this.rgba_matrix.release();
        this.gray_matrix.release();
    }

    /**
     * This function is called each time a frame is send by the camera
     *
     * @param inputFrame The frame
     * @return the matrix to display
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Size original_size;

        this.rgba_matrix = inputFrame.rgba();
        this.gray_matrix = inputFrame.gray();

        original_size = this.rgba_matrix.size();

        // Rotate the image
        Core.rotate(this.rgba_matrix, this.rgba_matrix, Core.ROTATE_90_CLOCKWISE);
        // Getting the new ratio
        double img_ratio = (double) (original_size.width / this.rgba_matrix.size().width);
        // Resizing the mat with the new ratio
        Size new_size = new Size(this.rgba_matrix.size().width * img_ratio,
                Math.floor((int) this.rgba_matrix.size().height * img_ratio));
        Imgproc.resize(this.rgba_matrix, this.rgba_matrix, new_size, Imgproc.INTER_CUBIC);
        // getting the sub mat to match the mat size needed
        this.rgba_matrix = this.rgba_matrix.submat((int) ((new_size.height / 2) - (original_size.height / 2)),
                (int) ((new_size.height / 2) + (original_size.height / 2)),
                (int) ((new_size.width / 2) - (original_size.width / 2)),
                (int) ((new_size.width / 2) + (original_size.width / 2)));
        /*


            Ajouter fonction analyse d'image ICI


         */
        Imgproc.rectangle(this.rgba_matrix, new Point(500, 50), new Point(1000, 500), new Scalar(0, 0, 255), 5);
        Imgproc.putText(this.rgba_matrix, "Voici du texte", new Point(500, 550), 2, 2,
                new Scalar(0, 0, 255), 2, Imgproc.LINE_8, false);
        return this.rgba_matrix;
    }

    public void enable() {
        this.camera_view.setCameraPermissionGranted();
        this.camera_view.enableView();
    }

    public void disable() {
        this.camera_view.disableView();
    }

}

