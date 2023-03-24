package org.mron.twitch.launcher;

public class NewsItem {

	private String date, title, author, content;
	
	public NewsItem(String date, String title, String author, String content) {
		this.date = date;
		this.title = title;
		this.author = author;
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getContent() {
		return content;
	}
	
}