package org.mron.twitch.controller;

import java.io.Serializable;
import java.text.NumberFormat;

public class Channel implements Serializable {

	private static final long serialVersionUID = 4337706589106685984L;

	private String url;

	private String bio = "No bio available";

	private String game;

	private String logo;

	private String name, displayName;

	private String status;

	private int viewerCount;

	private boolean online;

	public Channel(String name) {
		this.name = name;
	}

	public Channel(String url, String bio, String game, String name, String logo, String status, int viewerCount, String displayName) {
		this.url = url;
		if (bio != null) {
			this.bio = bio;
		}
		this.game = game;
		this.name = name;
		this.logo = logo;
		this.status = status;
		this.viewerCount = viewerCount;
		this.displayName = displayName;
	}

	public String getUrl() {
		return url;
	}

	public String getBio() {
		return bio;
	}

	public String getGame() {
		return game;
	}

	public String getName() {
		return name;
	}

	public void setName(String n) {
		this.displayName = n;
	}

	public String getLogo() {
		return logo;
	}

	public String getStatus() {
		return status;
	}

	public long getViewerCount() {
		return viewerCount;
	}

	public String getDisplayName() {
		return displayName.replaceAll("_", " ");
	}

	@Override
	public String toString() {
		return getName() + ": [url: " + getUrl() + ", game: " + getGame() + ", logo: " + getLogo() + ", status: \"" + getStatus() + "\", viewer count: " + NumberFormat.getInstance().format(getViewerCount()) + ", display name: " + getDisplayName() + "]";
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}