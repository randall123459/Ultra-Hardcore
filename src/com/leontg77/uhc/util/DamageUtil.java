package com.leontg77.uhc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DamageUtil {
	
	/**
	 * Convert endless decimal health to a 2 lengthed one.
	 * @param damage the amount of damage to convert.
	 * @return The new damage amount with 2 lenghted decimal.
	 */
	public static String convertHealth(double damage) {
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
}