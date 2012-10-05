/**
 * 
 */
package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONArray;

import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * @author Julian Kuerby
 *
 */
public class TwitterCal {
	
	private static final String propertiesPath = "settings.properties";
	
	private static Twitter twitter;
	private static GoogleCalendar cal;
	private static Properties prop;
	public static RFC3339Calendar now;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		now = new RFC3339Calendar();
		prop = new Properties();
//		prop.setProperty("TConsumerKey", "1234");
//		prop.setProperty("TConsumerSecret", "3456");
//		prop.setProperty("TAccessToken", "5678");
//		prop.setProperty("TAccessTokenSecret", "7890");
//		prop.list(System.out);
//		prop.store(new FileOutputStream("settings.properties"), "");
		prop.load(new FileInputStream(propertiesPath));

		
		
//		JSONArray arr = cal.getTodaysEvents();
//		if(arr != null) {
//			Twitter twitter = new Twitter(prop);
//			for(int i = 0; i < arr.size(); i++) {
//				try {
//					twitter.sendMention("Today: " + arr.getJSONObject(i).getString("summary"));
//				} catch (NumberFormatException | TwitterException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		twitter = new Twitter(prop);
		cal = new GoogleCalendar(prop);
		
		processMentions();
		
		if(! now.getDate().equals(prop.getProperty("now"))) {
			today(null);
		}
		prop.store(new FileOutputStream(propertiesPath), "");
	}
	
	public static void processMentions() throws TwitterException, IOException {
		List<Status> mentions = twitter.getMentions();
//		prop.setProperty("twitter.readMentionsId", mentions.get(0).getId() + "");
//		prop.store(new FileOutputStream(propertiesPath), "");
		
		for (int i = mentions.size() - 1; i >= 0; i--) {
			Status status = mentions.get(i);
			long authUserId = Long.parseLong(prop.getProperty("twitter.user"));
			if(status.getUser().getId() == authUserId && status.getText().startsWith("@" + twitter.getTwitter().getScreenName() + " ")) {
				String[] words = status.getText().split(" ");
				if(words.length > 1) {
					if(words[1].equals("today")) {
						today(status);
					} else if(words[1].equals("next")) {
						
					} else if(words[1].equals("on")) {
						JSONArray arr = cal.getEventsOnDate(RFC3339Calendar.parseDate(words[2]));
						buildTweets(words[2] + ":", arr, status);
					} else if(words[1].equals("after")) {
						
					} else if(words[1].equals("when")) {
						
					}
				}
			}
		}
		prop.store(new FileOutputStream(propertiesPath), "");
	}
	
	public static void today(Status inReplyTo) throws NumberFormatException, TwitterException {
		JSONArray arr = cal.getTodaysEvents();
		buildTweets("Today:", arr, inReplyTo);
		prop.setProperty("now", now.getDate());
	}
	
	public static void buildTweets(String prefix, JSONArray arr, Status inReplyTo) throws NumberFormatException, TwitterException {
		int fixSize = twitter.getTwitter().showUser(Long.parseLong(prop.getProperty("twitter.user"))).getScreenName().length() + 2
		            + prefix.length() + 13;
		int remaining = 140 - fixSize;
		String text = "";
		for(int i = 0; i < arr.size(); i++) {
			String act = arr.getJSONObject(i).getString("summary");
			if(remaining - act.length() - 1 < 0) {
				if(inReplyTo == null) {
					twitter.sendMention(prefix + text + "\n(" + now.getTimeOfDay() + ")");
				} else {
					twitter.sendReply(inReplyTo, prefix + text + "\n(" + now.getTimeOfDay() + ")");
				}
				text = "";
				remaining = 140 - fixSize;
			}
			text += " " + act;
			remaining -= act.length() + 1;
		}
		
		if(! text.equals("")) {
			if(inReplyTo == null) {
				twitter.sendMention(prefix + text + "\n(" + now.getTimeOfDay() + ")");
			} else {
				twitter.sendReply(inReplyTo, prefix + text + "\n(" + now.getTimeOfDay() + ")");
			}
		}
	}

}
