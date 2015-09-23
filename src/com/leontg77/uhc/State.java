package com.leontg77.uhc;

/**
 * The game state class.
 * 
 * @author LeonTG77
 */
public enum State {
	LOBBY, SCATTER, INGAME;

	private static State currentState;
	
	/**
	 * Sets the current state to be #.
	 * @param state the state setting it to.
	 */
	public static void setState(State state) {
		Settings.getInstance().getData().set("state", state.name().toUpperCase());
		Settings.getInstance().saveData();
		
		currentState = state;
	}
	
	/**
	 * Checks if the state is #.
	 * @param state The state checking.
	 * @return True if it's the given state.
	 */
	public static boolean isState(State state) {
		return currentState == state;
	}
	
	/**
	 * Gets the current state.
	 * @return The state
	 */
	public static State getState() {
		return currentState;
	}
}