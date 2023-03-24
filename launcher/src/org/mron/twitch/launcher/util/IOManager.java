package org.mron.twitch.launcher.util;

import java.util.ArrayList;

import org.mron.twitch.launcher.NewsItem;

public class IOManager {

	private ArrayList<NewsItem> newsItems;

	public void parseNews() {
		for (int i = 0; i < 10; i++) {
			getNewsItems().add(new NewsItem("3/8/2014", (i % 2) == 0 ? "Extremely long news title" : "News title",
					"Ron",
					"Lorepsum dolor sit amet, consectetur adipiscing elit. Cras condimentum, lorem sit amet tristique tincidunt, ante est tempus orci, in imperdiet metus nibh accumsan nisl. Cras condimentum nec leo eu varius. Nunc a ipsum nibh. Phasellus augue elit, egestas sit amet dui a, feugiat molestie lacus. Etiam eu est ac ipsum faucibus rhoncus."));
		}
	}

	public ArrayList<NewsItem> getNewsItems() {
		if (newsItems == null) {
			newsItems = new ArrayList<NewsItem>();
		}
		return newsItems;
	}

}