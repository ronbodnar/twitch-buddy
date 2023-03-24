package org.mron.twitch.launcher;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mron.twitch.launcher.util.IOManager;

public class TwitchBuddyLauncher {
	
	private static Updater updater;
	
	private static IOManager ioManager;
	
	public static void main(String[] arguments) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
					JFrame.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				ioManager = new IOManager();
				ioManager.parseNews();
				
				updater = new Updater();
				updater.checkForUpdates();
			}
			
		});
	}
	
	public static IOManager getIOManager() {
		return ioManager;
	}

}