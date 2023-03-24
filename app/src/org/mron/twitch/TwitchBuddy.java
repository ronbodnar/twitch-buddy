package org.mron.twitch;

import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mron.twitch.ui.TwitchBuddyUI;
import org.mron.twitch.util.IOHandler;
import org.mron.twitch.util.ImageConstants;
import org.mron.twitch.util.JSONParser;
import org.mron.twitch.util.JSONParser.JsonElements;

public class TwitchBuddy {

	/**
	 * Used for displaying logs while the program is being run.
	 */
	private static boolean debugMode = false;

	private static ImageConstants imageConstants;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (!debugMode) {
					debugMode = args.length == 1;
				}
				long time = System.currentTimeMillis();

				try {
					UIManager.setLookAndFeel(debugMode ? UIManager.getSystemLookAndFeelClassName() : "com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}

				setUtils(new ImageConstants());

				IOHandler.getInstance().readSettings();

				JSONParser.getInstance().parseJSONData(JsonElements.FEATURED_GAMES);

				IOHandler.getInstance().loadFavorites();

				TwitchBuddyUI.getInstance().constructFrame();
				TwitchBuddyUI.getInstance().getHomeTab().buildChannelList(true);
				TwitchBuddyUI.getInstance().getHighlightsTab().buildVideoList(true);
				TwitchBuddyUI.getInstance().getLiveChannelsTab().buildChannelList(true);

				if (isDebugMode()) {
					Constants.LOGGER.log(Level.INFO, "TwitchBuddy has loaded in " + ((System.currentTimeMillis() - time) / 1000L) + " seconds");
				}
			}

		});
	}

	/**
	 * Checks if the program is currently running in debug mode.
	 * 
	 * @return True if the program is in debug mode.
	 */
	public static boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Sets the program to the specified debug mode.
	 * 
	 * @param debugMode
	 *            The state to set debug mode.
	 */
	public static void setDebugMode(boolean debugMode) {
		TwitchBuddy.debugMode = debugMode;
	}

	public static ImageConstants getImageConstants() {
		return imageConstants;
	}

	public static void setUtils(ImageConstants imageConstants) {
		TwitchBuddy.imageConstants = imageConstants;
	}

}