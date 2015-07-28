package com.leontg77.uhc;

/**
 * The game state class.
 * @author LeonTG77
 */
public enum GameState {
	LOBBY, WAITING, INGAME;

	private static GameState currentState;
	
	/**
	 * Sets the current state to be #.
	 * @param state the state setting it to.
	 */
	public static void setState(GameState state) {
		GameState.currentState = state;
		Settings.getInstance().getData().set("game.currentstate", state.name().toUpperCase());
		Settings.getInstance().saveData();
	}
	
	/**
	 * Checks if the state is #.
	 * @param state The state checking.
	 * @return True if it's the given state.
	 */
	public static boolean isState(GameState state) {
		return GameState.currentState == state;
	}
	
	/**
	 * Gets the current state.
	 * @return The state
	 */
	public static GameState getState() {
		return currentState;
	}
}