package com.mc.pha.util;

import java.util.Date;

import ti.android.util.Point3D;
/**
 * 
 * @author Jackson
 */
public class CalorieTracker {

	public Date dateSinceReset = new Date();
	public Date dateAtLastExerciseChange = dateSinceReset;
	public int lastActivity = -1;
	public double totalCaloriesFromExercise;
	private AccelerometerDataAnalysis dataAnalysis = new AccelerometerDataAnalysis();
	private int calLastActivity;
	private int calThisActivity;
	
	private final double caloriesBurnedPerSecWalking = (232/60.0/60.0);
	private final double caloriesBurnedPerSecRunning = (704/60.0/60.0);
	
	
/**
from: http://www.nutristrategy.com/caloriesburnedrunning.htm
Exercise (1 hour)	130 lb	155 lb	180 lb	205 lb
Running, 5 mph (12 minute mile)		472	563	654	745	
Running, 5.2 mph(11.5 minute mile)	531	633	735	838
Running, 6 mph (10 min mile)		590	704	817	931 ***
Running, 6.7 mph (9 min mile)		649	774	899	1024
Running, 7 mph (8.5 min mile)		679	809	940	1070
Running, 7.5mph (8 min mile)		738	880	1022	1163
Running, 8 mph (7.5 min mile)		797	950	1103	1256
Running, 8.6 mph (7 min mile)		826	985	1144	1303
Running, 9 mph (6.5 min mile)		885	1056	1226	1396
Running, 10 mph (6 min mile)		944	1126	1308	1489
Running, 10.9 mph (5.5 min mile)	1062	1267	1471	1675

Walking 2.5 mph, easy				177	211	245	279
Walking 3.0 mph (20min mile) 		195	232	270	307 ***
Walking 3.5 mph, brisk pace			224	267	311	354
Walking 4.0 mph, very brisk			295	352	409	465
*/
	public int getTotalCaloriesSoFarToday() {
		return (int) totalCaloriesFromExercise;
	}
	/**
	 * 
	 * @return calories from last reset period
	 */
	public int resetCaloriesAndDay() {
		int i = (int) totalCaloriesFromExercise;
		totalCaloriesFromExercise = 0;
		dateSinceReset = new Date();
		return i;
	}
	/**
	 * 
	 * @param point
	 * @return latest exercise activity code
	 */
	public int updateCaloriesWithAccelerometerData(Point3D point) {
		Date now = new Date();
		int currentActivity = dataAnalysis.newDataPointArrived(point);
		double timeDiff = (now.getTime() - dateAtLastExerciseChange.getTime()) / 1000.0;
		if (timeDiff > 60.0 || currentActivity != lastActivity) {
			//update every 60sec or when activity changes
			double burnRate = 0;
			if (lastActivity == AccelerometerDataAnalysis.RUNNING) 
				burnRate = caloriesBurnedPerSecRunning;
			else if (lastActivity == AccelerometerDataAnalysis.WALKING) 
				burnRate = caloriesBurnedPerSecWalking;
			totalCaloriesFromExercise += timeDiff * burnRate;
			calThisActivity += timeDiff * burnRate;
			if (currentActivity != lastActivity) {
				calLastActivity = calThisActivity;
				calThisActivity = 0;
			}
			dateAtLastExerciseChange = now;
			lastActivity = currentActivity;
		}
		return lastActivity;
	}
	public int getCalFromLastActivity() {
		return calLastActivity;
	}
	public int getCalSoFarCurrentActivity() {
		return calThisActivity;
	}
}
