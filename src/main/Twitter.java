/**
 * 
 */
package main;

import java.util.List;
import java.util.Properties;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusUpdate;
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
	
	public twitter4j.Twitter getTwitter() {
		return twitter;
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
	
	public void sendReply(Status reply, String message) throws TwitterException {
		StatusUpdate update = new StatusUpdate("@" + reply.getUser().getScreenName() + " " + message);
		update.setInReplyToStatusId(reply.getId());
		twitter.updateStatus(update);
	}
	
//	public void sendDm() {
//		
//	}
	
	public void getTimeline() throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		for (Status status : statuses) {
			System.out.println(status.getUser().getScreenName() + ": " + status.getText());
		}
	}
	
	public List<Status> getMentions() throws TwitterException {
		long mentionId = Long.parseLong(prop.getProperty("twitter.readMentionsId", "1"));
		Paging page = new Paging(mentionId);
		List<Status> mentions = twitter.getMentions(page);
		
		return mentions;
	}
	
	public List<DirectMessage> getDMs() throws TwitterException {
		long dmId = Long.parseLong(prop.getProperty("twitter.readDmId", "1"));
		Paging page = new Paging(dmId);
		List<DirectMessage> dms = twitter.getDirectMessages(page);
		
		return dms;
	}
	
}
