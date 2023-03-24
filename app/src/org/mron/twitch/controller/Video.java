package org.mron.twitch.controller;

public class Video {

	private long views, time;

	private String name, displayName;

	private String title, description, preview, url, timeAgo;

	public Video(String title, String description, String preview, String url, String timeAgo, long time, long views, String name, String displayName) {
		this.title = title;
		this.description = description;
		this.preview = preview;
		this.url = url;
		this.timeAgo = timeAgo;
		this.time = time;
		this.views = views;
		this.name = name;
		this.displayName = displayName;
	}

	public long getTime() {
		return time;
	}

	public long getViews() {
		return views;
	}

	public String getTimeAgo() {
		return timeAgo;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName.replaceAll("_", " ");
	}

	public String getTitle() {
		return title == null ? "No title available" : title;
	}

	public String getDescription() {
		return description == null ? "No description available" : description;
	}

	public String getPreview() {
		return preview;
	}

	public String getUrl() {
		return url;
	}

}