/**
 * 
 */
package main;

import java.sql.Time;
import java.util.List;
import java.util.Properties;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * @author Julian Kuerby
 *
 */
public class Twitter {
	
	private Properties prop;
	private twitter4j.Twitter twitter;
	
	public Twitter(Properties prop) {
		this.prop = prop;
		AccessToken accessToken = new AccessToken(prop.getProperty("twitter.accessToken"), prop.getProperty("twitter.accessTokenSecret"));
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(prop.getProperty("twitter.consumerKey"), prop.getProperty("twitter.consumerSecret"));
		twitter.setOAuthAccessToken(accessToken);
	}
	
	public void sendTweet(String message) throws TwitterException {
		twitter.updateStatus(message);
	}
	
	public void sendMention(String message) throws NumberFormatException, TwitterException {
		sendMention(Long.parseLong(prop.getProperty("twitter.user")), message);
	}
	
	public void sendMention(long userID, String message) throws TwitterException {
		sendTweet("@" + twitter.showUser(userID).getScreenName() + " " + message);
	}
	
	public void sendTimeTweet() throws TwitterException {
		twitter.updateStatus("@Rimgar_ Time: " + new Time(System.currentTimeMillis()).toGMTString());
	}
	
	public void sendTimeDM() throws TwitterException {
		twitter.sendDirectMessage("rimgar_", "Time: " + new Time(System.currentTimeMillis()).toGMTString());
	}
	
	public void getTimeline() throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		for (Status status : statuses) {
			System.out.println(status.getUser().getScreenName() + ": " + status.getText());
		}
	}
	
	public void getDMs() throws TwitterException {
		List<DirectMessage> dms = twitter.getDirectMessages();
		for (DirectMessage dm : dms) {
			System.out.println(dm.getSender().getScreenName() + ": " + dm.getText());
		}
	}

}
