package com.argos.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Class defining an object used to find target zones based on the k-means algorithm
 * @author MSI
 */
public class KmeansTargetZoneFinder {

    private Integer attemptNumber = 5;
    private Integer clustersNumber = 3;
    private Integer threshold = 120;
    private Integer min_area_contour = 500;

    private final boolean debug = true;

    /**
     * Gets a list of detected TargetZone from an image
     *
     * @param src_image Mat representing an image
     * @return list of TargetZone
     */
    public List<TargetZone> getDetectedTargetZones(Mat src_image) {

        Log.d("Arnaud", String.valueOf(src_image.channels()));

        List<TargetZone> detectedZones = new ArrayList<>();

        Mat kmean_mask = new Mat();
        Mat kmean_mask_inverted = new Mat();
        Mat source_bitwised_and = new Mat();
        Mat hierarchy = new Mat();
        Mat srcclone = src_image.clone();

        Mat bestLabels = new Mat();
        TermCriteria criteria = new TermCriteria();
        int flags = Core.KMEANS_PP_CENTERS;
        Mat centers = new Mat();

        Mat data = srcclone.reshape(1, srcclone.rows() * srcclone.cols());
        data.convertTo(data, CvType.CV_32F);

        Core.kmeans(data, clustersNumber, bestLabels, criteria, attemptNumber, flags, centers);

        Mat draw = new Mat((int) src_image.total(), 1, CvType.CV_32FC3);
        Mat colors = centers.reshape(4, clustersNumber);
        for (int i = 0; i < clustersNumber; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
            Mat col = colors.row(i); // can't use the Mat directly with setTo() (see #19100)  
            double d[] = col.get(0, 0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0], d[1], d[2]), mask);
        }

        draw = draw.reshape(3, src_image.rows());
        draw.convertTo(draw, CvType.CV_8U);

        Imgproc.cvtColor(draw, kmean_mask, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(kmean_mask, kmean_mask_inverted, threshold, 200, Imgproc.THRESH_BINARY);//ou Inv
        Core.bitwise_and(src_image, src_image, source_bitwised_and, kmean_mask_inverted);

        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(kmean_mask_inverted, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];
        
        for (int i = 0; i < contours.size(); i++) {

            contoursPoly[i] = new MatOfPoint2f(contours.get(i).toArray());
            double peri = Imgproc.arcLength(contoursPoly[i], true);
            Imgproc.approxPolyDP(contoursPoly[i], contoursPoly[i], 0.02 * peri, true);

            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));

            TargetZone newZone = new TargetZone(boundRect[i].x, boundRect[i].y, boundRect[i].x + boundRect[i].width,
                    boundRect[i].y + boundRect[i].height, boundRect[i].width, boundRect[i].height);

            if (newZone.getArea() >= min_area_contour && !newZone.existsInArray(detectedZones)) {
                detectedZones.add(newZone);
            }

        }

        return detectedZones;

    }

    /**
     * Gets a list of Contours from an image
     *
     * @param src_image Mat representing an image
     * @return list of MatOfPoint
     */
    public List<MatOfPoint> getDetectedContours(Mat src_image){
        Mat kmean_mask = new Mat();
        Mat kmean_mask_inverted = new Mat();
        Mat source_bitwised_and = new Mat();
        Mat hierarchy = new Mat();
        Mat srcclone = src_image.clone();

        Mat bestLabels = new Mat();
        TermCriteria criteria = new TermCriteria();
        int flags = Core.KMEANS_PP_CENTERS;
        Mat centers = new Mat();

        Mat data = srcclone.reshape(1, srcclone.rows() * srcclone.cols());
        data.convertTo(data, CvType.CV_32F);

        Core.kmeans(data, clustersNumber, bestLabels, criteria, attemptNumber, flags, centers);
        System.out.println("k" +centers);
        Mat draw = new Mat((int) src_image.total(), 1, CvType.CV_32FC3);
        Mat colors = centers.reshape(3, clustersNumber);
        for (int i = 0; i < clustersNumber; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
            Mat col = colors.row(i); // can't use the Mat directly with setTo() (see #19100)  
            double d[] = col.get(0, 0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0], d[1], d[2]), mask);
        }

        draw = draw.reshape(3, src_image.rows());
        draw.convertTo(draw, CvType.CV_8U);

        Imgproc.cvtColor(draw, kmean_mask, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(kmean_mask, kmean_mask_inverted, threshold, 200, Imgproc.THRESH_BINARY);//ou Inv
        Core.bitwise_and(src_image, src_image, source_bitwised_and, kmean_mask_inverted);

        List<MatOfPoint> contours = new ArrayList<>();

        Imgproc.findContours(kmean_mask_inverted, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        return contours;
    }

    public Mat getHsv_kmean_mask(Mat src_image) {

        Mat kmean_mask = new Mat();
        Mat kmean_mask_inverted = new Mat();
        Mat source_bitwised_and = new Mat();
        Mat hierarchy = new Mat();
        Mat srcclone = src_image.clone();

        Mat bestLabels = new Mat();
        TermCriteria criteria = new TermCriteria();
        int flags = Core.KMEANS_PP_CENTERS;
        Mat centers = new Mat();

        Mat data = srcclone.reshape(1, srcclone.rows() * srcclone.cols());
        data.convertTo(data, CvType.CV_32F);

        Core.kmeans(data, clustersNumber, bestLabels, criteria, attemptNumber, flags, centers);
        System.out.println("k" +centers);
        Mat draw = new Mat((int) src_image.total(), 1, CvType.CV_32FC3);
        Mat colors = centers.reshape(3, clustersNumber);
        for (int i = 0; i < clustersNumber; i++) {
            Mat mask = new Mat(); // a mask for each cluster label
            Core.compare(bestLabels, new Scalar(i), mask, Core.CMP_EQ);
            Mat col = colors.row(i); // can't use the Mat directly with setTo() (see #19100)
            double d[] = col.get(0, 0); // can't create Scalar directly from get(), 3 vs 4 elements
            draw.setTo(new Scalar(d[0], d[1], d[2]), mask);
        }

        draw = draw.reshape(3, src_image.rows());
        draw.convertTo(draw, CvType.CV_8U);
        return draw;

    }

    public Mat getBitwised_img(Mat src_image) {

        Mat source_bitwised_and = new Mat();

        Core.bitwise_and(src_image, src_image, source_bitwised_and, getHsv_kmean_mask(src_image));

        return source_bitwised_and;

    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Integer getClustersNumber() {
        return clustersNumber;
    }

    public void setClustersNumber(Integer clustersNumber) {
        this.clustersNumber = clustersNumber;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getMin_area_contour() {
        return min_area_contour;
    }

    public void setMin_area_contour(Integer min_area_contour) {
        this.min_area_contour = min_area_contour;
    }
            


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat img = Imgcodecs.imread("C:\\Users\\MSI\\Desktop\\master\\Gestion-de-projet\\Argos\\src\\main\\java\\com\\argos\\utils\\test.png");
        KmeansTargetZoneFinder cal = new KmeansTargetZoneFinder();
        cal.getDetectedTargetZones(img);
    }

}
