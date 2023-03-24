/*
 * Copyright (c) 2014 Ron Bodnar.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mron.twitch.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import javax.swing.JOptionPane;

import org.mron.twitch.launcher.ui.UpdaterUI;

public class Updater {

	private UpdaterUI updaterUI;
	
	private boolean updateAvailable;

	private long downloadStartTime;

	private String operatingSystem;

	private Queue<String> queue = new ArrayDeque<String>();

	private Map<String, String> md5 = new HashMap<String, String>();

	public String getAssetsDirectory() {
		return "assets" + File.separator;
	}
	
	public String getIconDirectory() {
		return getAssetsDirectory() + "images" + File.separator + "icons" + File.separator;
	}
	
	public String getImageDirectory() {
		return getAssetsDirectory() + "images" + File.separator;
	}

	public String getDependencyDirectory() {
		return getAssetsDirectory() + "dependencies" + File.separator;
	}

	public void checkForUpdates() {
		updaterUI = new UpdaterUI();

		getLocalMD5(new File(".").listFiles());

		fetchUpdateList();

		if (!queue.isEmpty()) {
			updateAvailable = true;
		} else {
			updaterUI.setMessage(0, "Twitch Buddy is currently up to date!");
		}
		updaterUI.constructFrame(this);
	}

	public void downloadUpdates() {
		updaterUI.setMessage(0, "Preparing to download " + queue.size() + " file" + (queue.size() == 1 ? "" : "s") + "...");
		try {
			Thread.sleep(750);
			int downloadedFiles = 0;
			int filesToDownload = queue.size();
			for (Iterator<String> ite$ = queue.iterator(); ite$.hasNext();) {
				String[] args = ite$.next().split(",");
				String fileName = args[0];
				String downloadLink = args[1];
				URL url = new URL(downloadLink);
				
				if (fileName.contains("dependencies")) {
					File file = new File(getDependencyDirectory());
					if (!file.exists()) {
						file.mkdirs();
					}
				} else if (fileName.contains("icons")) {
					File file = new File(getIconDirectory());
					if (!file.exists()) {
						file.mkdirs();
					}
				} else if (fileName.contains("images")) {
					File file = new File(getImageDirectory());
					if (!file.exists()) {
						file.mkdirs();
					}
				}

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				byte[] buffer = new byte[2048];

				File file = new File(fileName);
				file.delete();
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				int read;
				long progress = 0L;
				long percentage = 0L;
				downloadStartTime = System.currentTimeMillis();
				while ((read = conn.getInputStream().read(buffer)) != -1) {
					bos.write(buffer, 0, read);
					progress += read;
					percentage = ((file.length() * 100 / conn.getContentLength()));
					updaterUI.setMessage(percentage, "Downloading file " + (downloadedFiles + 1) + "/" + filesToDownload + " - " + percentage + "% @ " + formatBytes((long) getDownloadSpeed(progress)) + "/s");
				}
				updaterUI.setMessage(100, "Downloading file " + (downloadedFiles + 1) + "/" + filesToDownload + " - 100% @ " + formatBytes((long) getDownloadSpeed(progress)) + "/s");
				bos.flush();
				bos.close();
				conn.getInputStream().close();
				downloadedFiles++;
				Thread.sleep(750);
			}
			updateAvailable = false;
			updaterUI.setMessage(0, "Twitch Buddy is currently up to date!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the current download speed by the user for the client updater.
	 * 
	 * @param variable
	 *            The data used to find the download speed.
	 * @return The current download speed.
	 */
	public float getDownloadSpeed(long variable) {
		long elapsedTime = System.currentTimeMillis() - downloadStartTime;
		return 1000f * variable / elapsedTime;
	}

	/**
	 * Formats the given bytes as a human readable byte count. (Example: 230 KB)
	 * 
	 * @param bytes
	 *            The bytes to convert to String.
	 * @return The String representation of the amount of bytes in the given data member.
	 */
	public static String formatBytes(long bytes) {
		final int unit = 1024;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "" + "KMGTPEZY".charAt(exp - 1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public void write(InputStream inputStream, File fileToWrite) throws IOException {
		BufferedInputStream buffInputStream = new BufferedInputStream(inputStream);
		FileOutputStream fos = new FileOutputStream(fileToWrite);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		int byteData;
		while ((byteData = buffInputStream.read()) != -1) {
			bos.write((byte) byteData);
		}
		bos.close();
		fos.close();
		buffInputStream.close();
	}

	public void fetchUpdateList() {
		try {
			URL url = new URL("http://cdn.mron.co/buddy.upd");
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Authorization", "Basic " + "root:".getBytes());
			connection.setReadTimeout(7500);
			connection.setConnectTimeout(7500);
			connection.connect();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				String[] parts = line.split(",");
				String fileName = parts[0];
				String md5hash = parts[1];
				String downloadLink = parts[2];
				File file = new File(fileName);
				if (!file.exists() || !md5hash.equals(md5.get(file.getName()))) {
					queue.add(fileName + "," + downloadLink);
				}
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getLocalMD5(File[] files) {
		if (files == null) {
			System.out.println("No files specified for local MD5");
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				getLocalMD5(file.listFiles());
			} else {
				md5.put(file.getName(), getMD5(file.getPath()));
			}
		}
	}

	public void start() {
		try {
			File jar = new File(getDependencyDirectory() + "twitch-buddy.jar");
			if (jar.exists()) {
				if (isWindows()) {
					Runtime.getRuntime().exec("java -cp " + getDependencyDirectory() + "*;" + jar.getAbsolutePath() + " org.mron.buddy.TwitchBuddy");
				} else if (isMac() || isUnix()) {
					Runtime.getRuntime().exec("java -cp " + getDependencyDirectory() + "*:" + jar.getAbsolutePath() + " org.mron.buddy.TwitchBuddy");
				}
			} else {
				Object[] options = {
					"OK"
				};
				JOptionPane.showOptionDialog(null, "You are missing required core files and must update the program before first use.\n\nThe program will now exit.", "Fatal Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMD5(String file) {
		try {
			InputStream in = new FileInputStream(file);
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			byte[] fileBytes = new byte[in.available()];
			in.read(fileBytes);
			algorithm.update(fileBytes, 0, fileBytes.length);
			String md5 = new BigInteger(1, algorithm.digest()).toString(16);
			in.close();
			return md5;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isWindows() {
		return getOperatingSystem().indexOf("win") >= 0;
	}
 
	public boolean isMac() {
		return getOperatingSystem().indexOf("mac") >= 0;
	}
 
	public boolean isUnix() {
		return getOperatingSystem().indexOf("nix") >= 0 || getOperatingSystem().indexOf("nux") >= 0 || getOperatingSystem().indexOf("aix") > 0;
	}
	
	public String getOperatingSystem() {
		if (operatingSystem == null) {
			operatingSystem = System.getProperty("os.name").toLowerCase();
		}
		return operatingSystem;
	}
	
	public boolean isUpdateAvailable() {
		return updateAvailable;
	}

}