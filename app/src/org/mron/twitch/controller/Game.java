package org.mron.twitch.controller;

public class Game {

	private String name;

	private String logo;

	private long viewerCount;

	private long channelCount;

	public Game(String name, String logo, long viewerCount, long channelCount) {
		this.name = name;
		this.logo = logo;
		this.viewerCount = viewerCount;
	}

	public String getName() {
		return name;
	}

	public String getLogo() {
		return logo;
	}

	public long getViewerCount() {
		return viewerCount;
	}

	public long getChannelCount() {
		return channelCount;
	}

}