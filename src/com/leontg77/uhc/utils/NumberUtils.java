package com.leontg77.uhc.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Number utilities class.
 * <p>
 * Contains number related methods.
 * 
 * @author LeonTG77
 */
public class NumberUtils {
	
	/**
	 * Convert endless decimal health to a 2 lengthed one.
	 * 
	 * @param damage the amount of damage to convert.
	 * @return The new damage amount with 2 lenghted decimal.
	 */
	public static String convertDouble(double damage) {
		NumberFormat nf = new DecimalFormat("##.##");
		return nf.format(damage);
	}
	
	/**
	 * Turn the health given into percent.
	 * 
	 * @param health the health of the player.
	 * @return the percent of the health.
	 */
	public static String makePercent(double health) {
		double hearts = health / 2;
		double percent = hearts * 10;
		
		if (percent >= 66) {
			return "§a" + ((int) percent);
		} else if (percent >= 33) {
			return "§e" + ((int) percent);
		} else if (percent == 0) {
			return "§7" + ((int) percent);
		} else {
			return "§c" + ((int) percent);
		}
	}
	
	/**
	 * Get a random integer between two ints.
	 * 
	 * @param min minimum integer value.
	 * @param max maximum integer value.
	 * @return Random integer between two ints.
	 */
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	/**
	 * This does something with splitting things.
	 * @author EXSolo
	 */
	public static <T> List<List<T>> split(List<T> toSplit, int howOften) {
		List<List<T>> list = new ArrayList<List<T>>(howOften);
		
		for (int i = 0; i < howOften; i++) {
			list.add(new ArrayList<T>());
		}
		
		int i = 0;
		
	    for (T t : toSplit) {
	 	    list.get(i).add(t);
	 	    i = (i + 1) % howOften;
	    }
	    return list;
	}
}