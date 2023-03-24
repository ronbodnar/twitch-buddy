package org.mron.twitch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.mron.twitch.Constants;
import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.controller.Settings;
import org.mron.twitch.ui.tabs.HomeTab;

public class IOHandler {

	private int count;

	private String[] channelSearches, profileSearches;

	private File settingsFile, favoritesFile;

	private Settings settings;

	private FileOutputStream fileOutputStream;

	private DataOutputStream dataOutputStream;

	private FileInputStream fileInputStream;

	private DataInputStream dataInputStream;

	private static IOHandler instance;

	public IOHandler() {
		try {
			File file2 = new File(Constants.SETTINGS_DIRECTORY);
			if (!file2.isDirectory()) {
				file2.mkdir();
			}
			settingsFile = new File(Constants.SETTINGS_DIRECTORY + "settings.bin");
			if (!settingsFile.exists()) {
				settingsFile.createNewFile();
			}
			favoritesFile = new File(Constants.SETTINGS_DIRECTORY + "favorites.bin");
			if (!favoritesFile.exists()) {
				favoritesFile.createNewFile();
			}
			settings = Settings.getInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveSettings() {
		try {
			fileOutputStream = new FileOutputStream(settingsFile);
			dataOutputStream = new DataOutputStream(fileOutputStream);

			putBoolean(settings.isAlwaysOnTop());
			putUTF(settings.getPreferredGame());

			dataOutputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readSettings() {
		try {
			long time = System.currentTimeMillis();
			if (settingsFile.length() <= 0) {
				saveSettings();
			}

			fileInputStream = new FileInputStream(settingsFile);
			dataInputStream = new DataInputStream(fileInputStream);

			settings.setAlwaysOnTop(dataInputStream.readBoolean());
			settings.setPreferredGame(dataInputStream.readUTF());

			dataInputStream.close();
			fileInputStream.close();
			if (TwitchBuddy.isDebugMode()) {
				Constants.LOGGER.log(Level.INFO, "Loaded user settings in " + (System.currentTimeMillis() - time) + " ms");
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void loadFavorites() {
		try {
			long time = System.currentTimeMillis();
			if (favoritesFile.length() <= 0) {
				writeFavorites();
				return;
			}

			FileInputStream fis = new FileInputStream(favoritesFile);
			DataInputStream ois = new DataInputStream(fis);

			int size = ois.readInt();
			for (int i = 0; i < size; i++) {
				String name = ois.readUTF();
				HomeTab.getInstance().addChannelToQueue(name);
			}

			ois.close();
			fis.close();

			if (TwitchBuddy.isDebugMode()) {
				Constants.LOGGER.log(Level.INFO, "Loaded favorites in " + (System.currentTimeMillis() - time) + " ms");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void writeFavorites() {
		try {
			FileOutputStream fis = new FileOutputStream(favoritesFile);
			DataOutputStream oos = new DataOutputStream(fis);

			int size = HomeTab.getInstance().getHomeChannels().size();
			oos.writeInt(size);

			for (Channel channel : HomeTab.getInstance().getHomeChannels()) {
				oos.writeUTF(channel.getName());
			}

			oos.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String[] getSearches(boolean profile) {
		try {
			if (profile) {
				profileSearches = new String[Util.getInstance().count(Constants.RECENT_PROFILE_SEARCHES_PATH)];
				BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.RECENT_PROFILE_SEARCHES_PATH));
				String line;
				count = 0;
				while ((line = bufferedReader.readLine()) != null && count < profileSearches.length) {
					profileSearches[count] = line;
					count++;
				}
				bufferedReader.close();
			} else {
				channelSearches = new String[Util.getInstance().count(Constants.RECENT_SEARCHES_PATH)];
				BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.RECENT_SEARCHES_PATH));
				String line;
				count = 0;
				while ((line = bufferedReader.readLine()) != null && count < channelSearches.length) {
					channelSearches[count] = line;
					count++;
				}
				bufferedReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return profile ? profileSearches : channelSearches;
	}

	public void writeSearch(boolean profile, String text) throws IOException {
		try {
			File file = new File(profile ? Constants.RECENT_PROFILE_SEARCHES_PATH : Constants.RECENT_SEARCHES_PATH);
			if (!file.exists()) {
				file.createNewFile();
			}
			if (inFile(profile, text)) {
				if (TwitchBuddy.isDebugMode()) {
					Constants.LOGGER.log(Level.INFO, text + " already exists in file.");
				}
				return;
			}
			if (!profile) {
				text = text.replaceAll("http://", "").replaceAll("www.", "");
			}
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(profile ? Constants.RECENT_PROFILE_SEARCHES_PATH : Constants.RECENT_SEARCHES_PATH, true)))) {
				out.println(text);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void clearSearches(boolean profile) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(profile ? Constants.RECENT_PROFILE_SEARCHES_PATH : Constants.RECENT_SEARCHES_PATH);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean inFile(boolean profile, String text) {
		boolean found = false;

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(profile ? Constants.RECENT_PROFILE_SEARCHES_PATH : Constants.RECENT_SEARCHES_PATH));
			String line;
			count = 0;
			while ((line = bufferedReader.readLine()) != null && count < (profile ? profileSearches.length : channelSearches.length)) {
				if (line.equalsIgnoreCase(text) || line.contains(text)) {
					found = true;
				}
				count++;
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return found;
	}

	public void putInt(int contents) throws IOException {
		dataOutputStream.writeInt(contents);
	}

	public void putUTF(String contents) throws IOException {
		dataOutputStream.writeUTF(contents);
	}

	public void putBoolean(boolean contents) throws IOException {
		dataOutputStream.writeBoolean(contents);
	}

	public static IOHandler getInstance() {
		if (instance == null) {
			instance = new IOHandler();
		}
		return instance;
	}

}