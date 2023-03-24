package org.mron.twitch.ui.settings;

import org.mron.twitch.ui.settings.general.GeneralSettings;
import org.mron.twitch.ui.settings.history.ClearHistory;

public enum SettingPanels {
	GENERAL(new GeneralSettings()), HISTORY(new ClearHistory());

	private SettingPanel settingPanel;

	private SettingPanels(SettingPanel settingPanel) {
		this.settingPanel = settingPanel;
	}

	public String getName() {
		String name = capitalizeString(name());
		return name.replaceAll("_", " ");
	}

	public String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'' || chars[i] == '_') {
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	public SettingPanel getSettingPanel() {
		return settingPanel;
	}

	public static SettingPanels forId(int id) {
		for (SettingPanels setting : SettingPanels.values()) {
			if (setting.ordinal() == id) {
				return setting;
			}
		}
		return null;
	}

	public static SettingPanel forString(String string) {
		for (SettingPanels setting : SettingPanels.values()) {
			if (setting.getName().equals(string)) {
				return setting.getSettingPanel();
			}
		}
		return null;
	}

}