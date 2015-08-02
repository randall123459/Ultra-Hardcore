package com.leontg77.uhc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * Number utilities class.
 * @author LeonTG77
 */
public class NumberUtils {
	
	/**
	 * Convert endless decimal health to a 2 lengthed one.
	 * @param damage the amount of damage to convert.
	 * @return The new damage amount with 2 lenghted decimal.
	 */
	public static String convertDouble(double damage) {
		NumberFormat nf = new DecimalFormat("##.##");
		return nf.format(damage);
	}
	
	/**
	 * Turn the health given into percent.
	 * @param health the health of the player.
	 * @return the percent of the health.
	 */
	public static int makePercent(double health) {
		double hearts = health / 2;
		double precent = hearts * 10;
		return (int) precent;
	}
	
	/**
	 * Get a random integer between two ints.
	 * @param min minimum integer value.
	 * @param max maximum integer value.
	 * @return Random integer between two ints.
	 */
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}