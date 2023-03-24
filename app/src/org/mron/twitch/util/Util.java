package org.mron.twitch.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import org.mron.twitch.Constants;
import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Game;

public class Util {

	private ArrayList<Game> games = new ArrayList<Game>();

	public int count(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		InputStream is = new BufferedInputStream(new FileInputStream(fileName));
		try {
			byte[] buffer = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(buffer)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (buffer[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	public void openBrowser(String url) {
		Desktop desktop = Desktop.getDesktop();
		if (Desktop.isDesktopSupported()) {
			if (desktop.isSupported(Action.BROWSE)) {
				try {
					desktop.browse(new URL(url).toURI());
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean streamOnline(String username) {
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(Constants.LIVE_CHECK_URL_PREFIX + username);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + Constants.HTTP_REQUEST_AUTH);
			connection.setReadTimeout(7500);
			connection.setConnectTimeout(7500);
			connection.connect();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

			String input;
			while ((input = bufferedReader.readLine()) != null) {
				sb.append(input);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Boolean.parseBoolean(sb.toString());
	}

	public String parseTwitchUrl(String url) {
		String newUrl = "";
		for (String[] replace : Constants.TWITCH_URL_REPLACEMENTS) {
			newUrl = url.replaceAll(replace[0], replace[1]);
		}
		if (TwitchBuddy.isDebugMode()) {
			Constants.LOGGER.log(Level.INFO, "URL parsed: " + url + " => " + newUrl);
		}
		return newUrl;
	}

	public void centerWindow(Window frame) {
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = graphicsDevice.getDisplayMode().getWidth();
		int height = graphicsDevice.getDisplayMode().getHeight();

		int x = (int) ((width - frame.getWidth()) / 2);
		int y = (int) ((height - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}

	private static Util instance;

	public static Util getInstance() {
		if (instance == null) {
			instance = new Util();
		}
		return instance;
	}

	public ArrayList<Game> getGames() {
		return games;
	}

}