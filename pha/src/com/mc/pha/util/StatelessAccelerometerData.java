package com.mc.pha.util;

import ti.android.util.Point3D;

/**
 * 
 * @author Jackson
 */
public class StatelessAccelerometerData {
	
	private static int MAX_SIZE_ACCEL_ARRAY = 16;
	private static double RUNNING_MODEL_THRESHOLD = 0.92;
	private static double WALKING_MODEL_THRESHOLD = 0.18;
	
	private static double[] magnitudes = new double[MAX_SIZE_ACCEL_ARRAY];
	private static int size;
	
	public static final int STANDING = 0;
	public static final int WALKING = 1;
	public static final int RUNNING = 2;
	public static final int UNKNOWN = -1;
	
	/**
	 * Using a size 16 array was driven by desire to quickly categorize running, etc.
	 * This allows 16 seconds to be the max time elapsed when first determining exercise
	 * The last 16 Points are kept in a circular buffer array, which overwrites old data
	 * Call newDataPointArrived() to update the internal array and get the category
	 * 
	 * @param  points
	 */
	
	public static int classify(Point3D[] points) {
		if (points == null || points.length < 2) return UNKNOWN;
		size = 0;
		for (int i = 0; i < points.length && i < magnitudes.length; i++) {
			magnitudes[i] = convertToMagnitude(points[i]);
			size = i + 1;
		}
		return classificationModel(extractFeaturePeakToPeak());
	}
	
	//model training is done outside this code, using existing datasets
	//could add training in here for future work
	public static void trainModel(Point3D[] running, Point3D[] walking, Point3D[] standing) {
		//use classifier to set thresholds here
	}
	
	private static int classificationModel(double peak) {
		if (peak <= 0) return UNKNOWN;
		if (peak > RUNNING_MODEL_THRESHOLD) return RUNNING;
		else if (peak > WALKING_MODEL_THRESHOLD) return WALKING;
		else return STANDING;
	}

	private static double convertToMagnitude(Point3D point) {
		return Math.sqrt(point.x*point.x + point.y*point.y + point.z*point.z);
	}
	public static double extractFeaturePeakToPeak() {
		double max = extractFeatureMaximum();
		double min = extractFeatureMinimum();
		if (min > max) return -1;
		return max - min;
	}
	private static double extractFeatureMinimum() {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < size; i++) 
			if (min > magnitudes[i]) min = magnitudes[i];
		return min;
	}
	private static double extractFeatureMaximum() {
		double max = 0;
		for (int i = 0; i < size; i++) 
			if (max < magnitudes[i]) max = magnitudes[i];
		return max;
	}
	public static double extractFeatureStdDeviation() {
		double avg = 0;
		for (int i = 0; i < size; i++) 
			avg += magnitudes[i];
		avg /= size;
		
		double variance = 0;
		for (int i = 0; i < size; i++) 
			variance += (avg - magnitudes[i])*(avg - magnitudes[i]);
		variance /= size;
		return Math.sqrt(variance);
	}
}
