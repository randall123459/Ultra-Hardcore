package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {
	private static Fireworks instance = new Fireworks();
	
	public static Fireworks getRandomizer() {
		return instance;
	}
 
	private Random ran = new Random();
	
	private ArrayList<Color> colors = new ArrayList<Color>();
	private ArrayList<Color> fade = new ArrayList<Color>();
	private ArrayList<Type> types = new ArrayList<Type>();
 
	public void setup() {
		colors.add(Color.WHITE);
		colors.add(Color.PURPLE);
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.AQUA);
		colors.add(Color.BLUE);
		colors.add(Color.FUCHSIA);
		colors.add(Color.GRAY);
		colors.add(Color.LIME);
		colors.add(Color.MAROON);
		colors.add(Color.YELLOW);
		colors.add(Color.SILVER);
		colors.add(Color.TEAL);
		colors.add(Color.ORANGE);
		colors.add(Color.OLIVE);
		colors.add(Color.NAVY);
		colors.add(Color.BLACK);
		types.add(Type.BURST);
		types.add(Type.BALL);
		types.add(Type.BALL_LARGE);
		types.add(Type.CREEPER);
		types.add(Type.STAR);
		fade.add(Color.WHITE);
		fade.add(Color.PURPLE);
		fade.add(Color.RED);
		fade.add(Color.GREEN);
		fade.add(Color.AQUA);
		fade.add(Color.BLUE);
		fade.add(Color.FUCHSIA);
		fade.add(Color.GRAY);
		fade.add(Color.LIME);
		fade.add(Color.MAROON);
		fade.add(Color.YELLOW);
		fade.add(Color.SILVER);
		fade.add(Color.TEAL);
		fade.add(Color.ORANGE);
		fade.add(Color.OLIVE);
		fade.add(Color.NAVY);
		fade.add(Color.BLACK);
	}
 
	public Type getRandomType() {
		return types.get(ran.nextInt(types.size()));
	}
 
	public Color getRandomColor() {
		return colors.get(ran.nextInt(colors.size()));
	}
	 
	public Color getRandomFade() {		
		return fade.get(ran.nextInt(fade.size()));
	}
	
	public boolean getRandomFlicker() {
		return ran.nextBoolean();
	}
	
	public boolean getRandomTrail() {
		return ran.nextBoolean();
	}
	
	public boolean getFade() {
		return ran.nextBoolean();
	}
 
	public void launchRandomFirework(Location loc){
		Firework item = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = item.getFireworkMeta();
		meta.setPower(1);
		if (ran.nextBoolean()) {
			meta.addEffect(FireworkEffect.builder().flicker(getRandomFlicker()).trail(getRandomTrail()).with(getRandomType()).withColor(getRandomColor()).build());
		} else {
			meta.addEffect(FireworkEffect.builder().flicker(getRandomFlicker()).trail(getRandomTrail()).with(getRandomType()).withColor(getRandomColor()).withFade(getRandomFade()).build());
		}
		item.setFireworkMeta(meta);
	}
}