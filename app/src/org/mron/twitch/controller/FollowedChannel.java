package org.mron.twitch.controller;

public class FollowedChannel {

	private String url, logo, name, displayName, game;

	public FollowedChannel(String url, String logo, String name, String displayName, String game) {
		this.url = url;
		this.logo = logo;
		this.name = name;
		this.displayName = displayName;
		this.game = game;
	}

	public String getUrl() {
		return url;
	}

	public String getLogo() {
		return logo;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName.replaceAll("_", " ");
	}

	public String getGame() {
		return game;
	}

}