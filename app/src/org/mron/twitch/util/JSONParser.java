package org.mron.twitch.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mron.twitch.Constants;
import org.mron.twitch.TwitchBuddy;
import org.mron.twitch.controller.Channel;
import org.mron.twitch.controller.Game;
import org.mron.twitch.controller.Settings;
import org.mron.twitch.controller.Video;
import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.ui.tabs.HighlightsTab;
import org.mron.twitch.ui.tabs.LiveChannelsTab;
import org.mron.twitch.ui.tabs.impl.ProfileTabs;

public class JSONParser {

	private static JSONParser instance;

	public enum JsonElements {
		FEATURED_CHANNELS, FEATURED_VIDEOS, FEATURED_GAMES;
	}

	/**
	 * Gets all of the JSON data for the specified game.
	 * 
	 * @param game
	 *            The name of the game to collect the data for.
	 * @return All of the JSON data for the specified game.
	 * @throws IOException
	 */
	public String getJSONData(JsonElements json) throws IOException {
		StringBuilder sb = new StringBuilder();
		try {
			URL url = null;
			switch (json) {
				case FEATURED_CHANNELS:
					String game = null;
					if (TwitchBuddyUI.getInstance().getFeaturedGames() == null) {
						game = Settings.getInstance().getPreferredGame();
					} else {
						game = (String) TwitchBuddyUI.getInstance().getFeaturedGames().getSelectedItem();
						game = game.replaceAll("\\(.*?\\)", "");
					}
					url = new URL(Constants.FEATURED_CHANNELS_URL_PREFIX + game.trim().replaceAll(" ", "+"));
					break;

				case FEATURED_VIDEOS:
					if (TwitchBuddyUI.getInstance().getFeaturedGames() == null) {
						game = Settings.getInstance().getPreferredGame();
					} else {
						game = (String) TwitchBuddyUI.getInstance().getFeaturedGames().getSelectedItem();
						game = game.replaceAll("\\(.*?\\)", "");
					}
					url = new URL(Constants.FEATURED_VIDEOS_URL_PREFIX + game.trim().replaceAll(" ", "+"));
					break;

				case FEATURED_GAMES:
					url = new URL(Constants.FEATURED_GAMES_URL);
					break;
			}
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
		} catch (SocketTimeoutException ex) {
			System.out.println("Socket timed out");
		}
		return sb.toString();
	}

	/**
	 * Parses the specified data to be used in the features channels tab.
	 * 
	 * @param data
	 *            The data to be parsed.
	 */
	public void parseJSONData(JsonElements json) {
		long startTime = System.currentTimeMillis();

		try {
			switch (json) {
				case FEATURED_CHANNELS:
					JSONArray array = (JSONArray) JSONValue.parse(getJSONData(json));
					if (array == null) {
						if (TwitchBuddy.isDebugMode()) {
							Constants.LOGGER.log(Level.SEVERE, "Critical error while parsing " + json.name() + "!");
							System.out.println(array);
							System.out.println(getJSONData(json));
						}
						return;
					}
					for (int i = 0; i < array.size(); i++) {
						JSONObject result = (JSONObject) array.get(i);

						String url = (String) result.get("url");
						String bio = (String) result.get("bio");
						String game = (String) result.get("game");
						String name = (String) result.get("name");
						String logo = (String) result.get("logo");
						String status = (String) result.get("status");

						long viewers = (long) result.get("viewers");

						String displayName = (String) result.get("display_name");

						LiveChannelsTab.getInstance().addChannel(new Channel(url, bio, game, name, logo, status, (int) viewers, displayName));
					}
					break;

				case FEATURED_VIDEOS:
					array = (JSONArray) JSONValue.parse(getJSONData(json));
					if (array == null) {
						if (TwitchBuddy.isDebugMode()) {
							Constants.LOGGER.log(Level.SEVERE, "Critical error while parsing " + json.name() + "!");
						}
						return;
					}
					for (int i = 0; i < array.size(); i++) {
						JSONObject result = (JSONObject) array.get(i);

						String title = (String) result.get("title");
						String description = (String) result.get("description");
						String preview = (String) result.get("preview");
						String url = (String) result.get("url");
						String timeAgo = (String) result.get("time_ago");

						long time = (long) result.get("time");
						long views = (long) result.get("views");

						String name = (String) result.get("name");
						String displayName = (String) result.get("display_name");

						HighlightsTab.getInstance().addVideo(new Video(title, description, preview, url, timeAgo, time, views, name, displayName));
					}
					break;

				case FEATURED_GAMES:
					array = (JSONArray) JSONValue.parse(getJSONData(json));
					if (array == null) {
						if (TwitchBuddy.isDebugMode()) {
							Constants.LOGGER.log(Level.SEVERE, "Critical error while parsing " + json.name() + "!");
						}
						return;
					}
					Util.getInstance().getGames().clear();
					for (int i = 0; i < array.size(); i++) {
						JSONObject result = (JSONObject) array.get(i);

						String name = (String) result.get("name");
						String logo = (String) result.get("logo");

						long viewers = (long) result.get("viewers");
						long channels = (long) result.get("channels");

						Util.getInstance().getGames().add(new Game(name, logo, viewers, channels));
					}
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (TwitchBuddy.isDebugMode()) {
			Constants.LOGGER.log(Level.INFO, "Parsed " + json.name() + " in " + (System.currentTimeMillis() - startTime) + " ms");
		}
	}

	public Channel parseChannel(String user) {
		Channel channel = null;
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(Constants.CHANNEL_URL_PREFIX + user.trim().replaceAll(" ", "_"));

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

			JSONArray array = (JSONArray) JSONValue.parse(sb.toString());
			if (array == null) {
				if (TwitchBuddy.isDebugMode()) {
					Constants.LOGGER.log(Level.SEVERE, "Critical error while parsing " + user + "'s channel");
				}
				return null;
			}
			for (int i = 0; i < array.size(); i++) {
				JSONObject result = (JSONObject) array.get(i);

				String name = (String) result.get("name");
				String displayName = (String) result.get("display_name");

				String status = (String) result.get("status");
				String logo = (String) result.get("logo");
				String game = (String) result.get("game");
				String url2 = (String) result.get("url");
				String bio = (String) result.get("bio");

				channel = new Channel(url2, bio, game, name, logo, status, -1, displayName);
				channel.setOnline(Util.getInstance().streamOnline(channel.getName()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return channel;
	}

	public void parseHighlights(String user, boolean broadcasts) {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL("http://api.mron.co/twitch/highlights.php?user=" + user.trim().replaceAll(" ", "_") + (broadcasts ? "&broadcasts=true" : ""));

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

			JSONArray array = (JSONArray) JSONValue.parse(sb.toString());
			if (array == null) {
				if (TwitchBuddy.isDebugMode()) {
					Constants.LOGGER.log(Level.SEVERE, "Critical error while parsing " + user + "'s " + (broadcasts ? "past broadcasts" : "highlights"));
				}
				return;
			}
			if (broadcasts) {
				ProfileTabs.getInstance().getPastBroadcasts().clear();
			} else {
				ProfileTabs.getInstance().getHighlights().clear();
			}
			for (int i = 0; i < array.size(); i++) {
				JSONObject result = (JSONObject) array.get(i);

				String title = (String) result.get("title");
				String description = (String) result.get("description");
				String preview = (String) result.get("preview");
				String url2 = (String) result.get("url");
				String timeAgo = (String) result.get("time_ago");

				long time = (long) result.get("time");
				long views = (long) result.get("views");

				String name = (String) result.get("name");
				String displayName = (String) result.get("display_name");

				if (broadcasts) {
					ProfileTabs.getInstance().getPastBroadcasts().add(new Video(title, (description == null ? "No description available" : description), preview, url2, timeAgo, time, views, name, displayName));
				} else {
					ProfileTabs.getInstance().getHighlights().add(new Video(title, description, preview, url2, timeAgo, time, views, name, displayName));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean channelExists(String user) {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(Constants.CHANNEL_URL_PREFIX + user.trim().replaceAll(" ", "_"));

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

			JSONArray array = (JSONArray) JSONValue.parse(sb.toString());
			if (array == null) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static JSONParser getInstance() {
		if (instance == null) {
			instance = new JSONParser();
		}
		return instance;
	}

}