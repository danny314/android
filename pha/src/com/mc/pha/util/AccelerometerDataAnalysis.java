package com.mc.pha.util;

import java.util.Arrays;

import ti.android.util.Point3D;

/**
 * 
 * @author Jackson
 */
public class AccelerometerDataAnalysis {

	private Point3D[] mRecentAccelerometerReadings;
	private int mLatestAccelPoint;
	private int size;
	private double[] mMagnitudes;
	private double mExponentiallyWeightedMagnitude;
	private double mExpFactor = 0.94;
	
	private int MAX_SIZE_ACCEL_ARRAY = 16;
	private double RUNNING_MODEL_THRESHOLD = 0.92;
	private double WALKING_MODEL_THRESHOLD = 0.18;
	
	public static int STANDING = 0;
	public static int WALKING = 1;
	public static int RUNNING = 2;
	public static int UNKNOWN = -1;
	
	public AccelerometerDataAnalysis() {
		mMagnitudes = new double[MAX_SIZE_ACCEL_ARRAY];
	}
	/**
	 * Using a size 16 array was driven by desire to quickly categorize running, etc.
	 * This allows 16 seconds to be the max time elapsed when first determining exercise
	 * The last 16 Points are kept in a circular buffer array, which overwrites old data
	 * Call newDataPointArrived() to update the internal array and get the category
	 * 
	 * @param recent 
	 */
	public AccelerometerDataAnalysis(Point3D[] recent) {
		//can be null, which makes this as the default constructor
		mMagnitudes = new double[MAX_SIZE_ACCEL_ARRAY];
		setRecentAccelerometerReadings(recent);
	}
	
	private void setRecentAccelerometerReadings(Point3D[] recent) {
		if (recent == null) {
			mRecentAccelerometerReadings = null; size = 0;
		} else if (recent.length == MAX_SIZE_ACCEL_ARRAY) {
			mRecentAccelerometerReadings = Arrays.copyOf(recent, MAX_SIZE_ACCEL_ARRAY);
			size = MAX_SIZE_ACCEL_ARRAY;
			mLatestAccelPoint = size - 1;
		} else {
			mRecentAccelerometerReadings = new Point3D[MAX_SIZE_ACCEL_ARRAY];
			int j = recent.length < MAX_SIZE_ACCEL_ARRAY ? 0 : recent.length - MAX_SIZE_ACCEL_ARRAY;
			size = 0;
			for (int i = 0; j < recent.length; i++) {
				mRecentAccelerometerReadings[i] = recent[j];
				j++;
				size = i + 1;
				mLatestAccelPoint = size - 1;
			}
		}
		for (int i = 0; i < size; i++) {
			mMagnitudes[i] = convertToMagnitude(mRecentAccelerometerReadings[i]);
			if (i == 0) mExponentiallyWeightedMagnitude = mMagnitudes[i];
			else { 
				mExponentiallyWeightedMagnitude = mExponentiallyWeightedMagnitude * mExpFactor  + 
					(1 - mExpFactor) * mMagnitudes[i];
			}
		}
	}
	/**
	 * @param point
	 * @return the category determined by the accelerometer data model
	 * RUNNING, WALKING, or STANDING, but UNKNOWN if can't be determined  
	 */
	public int newDataPointArrived(Point3D point) {
		//boot the oldest data point, or null if array is not full
		if (mRecentAccelerometerReadings != null) {
			mLatestAccelPoint += 1;
			if (mLatestAccelPoint == MAX_SIZE_ACCEL_ARRAY) mLatestAccelPoint = 0;
		} else {
			mRecentAccelerometerReadings = new Point3D[MAX_SIZE_ACCEL_ARRAY];
			mLatestAccelPoint = 0; size = 0;
		}
		//add the newest data point
		mRecentAccelerometerReadings[mLatestAccelPoint] = point;
		mMagnitudes[mLatestAccelPoint] = convertToMagnitude(point);
		if (size == 0) mExponentiallyWeightedMagnitude = mMagnitudes[mLatestAccelPoint];
		else {
			mExponentiallyWeightedMagnitude = mExponentiallyWeightedMagnitude * mExpFactor + 
				(1 - mExpFactor) * mMagnitudes[mLatestAccelPoint];
		}
		if (size < MAX_SIZE_ACCEL_ARRAY) size++; 
		
		
		//calculate the new features extracted
		extractFeatureFFT();
		extractFeatureStdDeviation();
//		extractFeatureMaximum();
//		extractFeatureMinimum();
	
		double peak = extractFeaturePeakToPeak();
		//use parametric model to determine status
		return classificationModel(peak);
	}
	
	//model training is done outside this code, using existing datasets
	//could add training in here for future work
	public void trainModel(Point3D[] running, Point3D[] walking, Point3D[] standing) {
		
	}
	
	private int classificationModel(double peak) {
		if (peak <= 0) return UNKNOWN;
		if (peak > RUNNING_MODEL_THRESHOLD) return RUNNING;
		else if (peak > WALKING_MODEL_THRESHOLD) return WALKING;
		else return STANDING;
	}
	private double[] extractFeatureFFT() {
		// Using 16 data points makes this operation faster	
		// use JTransform library if this is needed: edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D
//		if (size == 16) {
//			DoubleFFT_1D fft = new DoubleFFT_1D(16);
//			double[] data = magnitudes.clone();
//			fft.realForward(data);
//			//use the magnitudes of the FFT in the classification model...
//			return data;
//		}
		return null;
	}
	private double convertToMagnitude(Point3D point) {
		return Math.sqrt(point.x*point.x + point.y*point.y + point.z*point.z);
	}
	private double extractFeaturePeakToPeak() {
		double max = extractFeatureMaximum();
		double min = extractFeatureMinimum();
		if (min > max) return -1;
		return max - min;
	}
	private double extractFeatureMinimum() {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < size; i++) 
			if (min > mMagnitudes[i]) min = mMagnitudes[i];
		return min;
	}
	private double extractFeatureMaximum() {
		double max = 0;
		for (int i = 0; i < size; i++) 
			if (max < mMagnitudes[i]) max = mMagnitudes[i];
		return max;
	}
	private double extractFeatureStdDeviation() {
		double avg = 0;
		for (int i = 0; i < size; i++) 
			avg += mMagnitudes[i];
		avg /= size;
		
		double variance = 0;
		for (int i = 0; i < size; i++) 
			variance += (avg - mMagnitudes[i])*(avg - mMagnitudes[i]);
		variance /= size;
		return Math.sqrt(variance);
	}
	public static void main(String[] args) {
		//tests
		Point3D[] p = {new Point3D(1, 1, 1)};
		AccelerometerDataAnalysis a = new AccelerometerDataAnalysis(p);
		
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 1)) + " should equal -1");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 1)) + " should equal -1");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 1.285)) + " should be STANDING 0");
		System.out.println(a.newDataPointArrived(new Point3D(1, .9, 1.359)) + " should be WALKING 1");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be 1");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.2436)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be RUNNING 2");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be 1");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be 1");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be 0");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be 0");
		System.out.println(a.newDataPointArrived(new Point3D(1, 1, 2.243)) + " should be -1");
	}
}
