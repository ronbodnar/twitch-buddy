package org.mron.twitch;

import java.io.File;
import java.util.logging.Logger;

public class Constants {

	/**
	 * The Logger class instance used for accurate print outs.
	 */
	public static final Logger LOGGER = Logger.getLogger("Twitch Buddy");

	/**
	 * The URL for obtaining channel data.
	 */
	public static final String CHANNEL_URL_PREFIX = "http://api.mron.co/twitch/channel.php?user=";

	/**
	 * The URL for obtaining featured game data.
	 */
	public static final String FEATURED_GAMES_URL = "http://api.mron.co/twitch/games.php";

	/**
	 * The URL for obtaining featured video data.
	 */
	public static final String FEATURED_VIDEOS_URL_PREFIX = "http://api.mron.co/twitch/highlights.php?game=";

	/**
	 * The URL for obtaining featured channel data.
	 */
	public static final String FEATURED_CHANNELS_URL_PREFIX = "http://api.mron.co/twitch/info.php?game=";

	/**
	 * The URL for checking whether a channel is live or not.
	 */
	public static final String LIVE_CHECK_URL_PREFIX = "http://api.mron.co/twitch/info.php?liveCheck=true&channel=";

	/**
	 * The URL for obtaining followers for a specified channel.
	 */
	public static final String FOLLOWER_URL = "http://api.mron.co/twitch/followers.php?username=";

	/**
	 * The URL for obtaining followed users for a specified channel.
	 */
	public static final String FOLLOWING_URL = "http://api.mron.co/twitch/following.php?username=";

	/**
	 * The URL replacements for filtering URLs with invalid syntax.
	 */
	public static final String[][] TWITCH_URL_REPLACEMENTS = new String[][] {
	{
	"www.", ""
	}, {
	"http://", ""
	}, {
	"twitch.tv/", ""
	}
	};
	
	public static final byte[] HTTP_REQUEST_AUTH = "root:".getBytes();

	/**
	 * The directory in which settings will be stored.
	 */
	public final static String SETTINGS_DIRECTORY = System.getProperty("user.home") + File.separator + ".mron" + File.separator + "twitch-buddy" + File.separator;

	/**
	 * The location for search history.
	 */
	public final static String RECENT_SEARCHES_PATH = SETTINGS_DIRECTORY + "searches.txt";

	/**
	 * The location for user profile search history.
	 */
	public final static String RECENT_PROFILE_SEARCHES_PATH = SETTINGS_DIRECTORY + "profile_searches.txt";

}