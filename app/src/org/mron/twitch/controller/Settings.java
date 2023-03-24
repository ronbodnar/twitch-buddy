package org.mron.twitch.controller;

public class Settings {

	private boolean unsavedSettings;

	public boolean isUnsavedSettings() {
		return unsavedSettings;
	}

	public void setUnsavedSettings(boolean unsavedSettings) {
		this.unsavedSettings = unsavedSettings;
	}

	private String preferredGame = "N/A";

	public String getPreferredGame() {
		return preferredGame;
	}

	public void setPreferredGame(String preferredGame) {
		this.preferredGame = preferredGame;
	}

	private boolean alwaysOnTop;

	public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public static Settings instance;

}