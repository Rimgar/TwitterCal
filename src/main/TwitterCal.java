/**
 * 
 */
package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 * @author Julian Kuerby
 *
 */
public class TwitterCal {
	
	private static final String propertiesPath = "settings.properties";
	public static final String loggerPath = "TwitterCal.log";
	
	private static Twitter twitter;
	private static GoogleCalendar cal;
	private static Properties prop;
	public static RFC3339Calendar now;
	public static Logger log;

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
//		System.out.println(new File(propertiesPath).getAbsolutePath());
		prop.load(new FileInputStream(propertiesPath));

		log = Logger.getLogger("TwitterCal");
//		log.addHandler(new FileHandler(loggerPath, true));
		
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
		processDms();
		
		if(! now.getDate().equals(prop.getProperty("now"))) {
			today(null);
		}
		prop.store(new FileOutputStream(propertiesPath), "");
	}
	
	public static void processMentions() throws TwitterException, IOException {
		List<Status> mentions = twitter.getMentions();
		long authUserId = Long.parseLong(prop.getProperty("twitter.user"));
		
		for (int i = mentions.size() - 1; i >= 0; i--) {
			Status status = mentions.get(i);
			try {
				if(status.getUser().getId() == authUserId && status.getText().startsWith("@" + twitter.getTwitter().getScreenName() + " ")) {
					String[] words = status.getText().split(" ");
					if(words.length > 1) {
						if(words[1].equals("today")) {
							today(status);
						} else if(words[1].equals("next")) {
							JSONArray arr = cal.getEventsAfter(now.clone());
							buildTweets("", arr, status);
						} else if(words[1].equals("on")) {
							JSONArray arr = cal.getEventsOnDate(RFC3339Calendar.parseDate(words[2]));
							buildTweets(words[2] + ":", arr, status);
						} else if(words[1].equals("after")) {
							JSONArray arr = cal.getEventsAfter(RFC3339Calendar.parseDate(words[2]));
							buildTweets("", arr, status);
						} else if(words[1].equals("when")) {
							String name = status.getText().replaceFirst("@\\S* when\\s+", "").toLowerCase();
							RFC3339Calendar end = now.clone();
							end.add(Calendar.YEAR, 1);
							JSONArray arr = cal.getEventsBetween(now, end);
							for(int j = 0; j < arr.size(); j++) {
								JSONObject obj = arr.getJSONObject(j);
								if(obj.getString("summary").toLowerCase().contains(name)) {
									JSONArray tweet = new JSONArray();
									tweet.add(obj);
									buildTweets("", tweet, status);
								}
							}
						}
					}
				}
			} catch(Exception e) {
				log.log(Level.SEVERE, status.getId() + ": " + status.getText(), e);
			}
			prop.setProperty("twitter.readMentionsId", status.getId() + "");
		}
		prop.store(new FileOutputStream(propertiesPath), "");
	}
	
	public static void processDms() throws TwitterException, IOException {
		List<DirectMessage> dms = twitter.getDMs();
		long authUserId = Long.parseLong(prop.getProperty("twitter.user"));
		
		for (int i = dms.size() - 1; i >= 0; i--) {
			DirectMessage dm = dms.get(i);
			if(dm.getSenderId() == authUserId) {
				String[] words = dm.getText().split(" ");
				if(words.length > 2) {
					if(words[0].equals("add")) {
						if(words[1].matches("\\d{4}-\\d{2}-\\d{2}")) {
							String summary = dm.getText().replaceFirst("^\\s*add\\s*\\d{4}-\\d{2}-\\d{2}\\s*", "");
							summary += " ('" + words[1].substring(2, 4) + ")";
							cal.insertEvent(RFC3339Calendar.parseDate(words[1]), summary);
						}
					}
				}
			}
			prop.setProperty("twitter.readDmId", dm.getId() + "");
		}
		prop.store(new FileOutputStream(propertiesPath), "");
	}
	
	public static void today(Status inReplyTo) throws NumberFormatException, TwitterException {
		JSONArray arr = cal.getTodaysEvents();
		buildTweets("Today:", arr, inReplyTo);
		prop.setProperty("now", now.getDate());
	}
	
	public static void buildTweets(String prefix, JSONArray arr, Status inReplyTo) throws NumberFormatException, TwitterException {
		if(arr != null) {
			int fixSize = twitter.getTwitter().showUser(Long.parseLong(prop.getProperty("twitter.user"))).getScreenName().length() + 2
					    + prefix.length() + 13;
			int remaining = 140 - fixSize;
			String text = "";
			for(int i = 0; i < arr.size(); i++) {
				if(prefix.length() == 0) {
					prefix = arr.getJSONObject(i).getJSONObject("start").getString("date") + ":";
					fixSize += prefix.length();
					remaining -= prefix.length();
				}
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

}
