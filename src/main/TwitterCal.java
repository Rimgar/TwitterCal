/**
 * 
 */
package main;

import java.io.FileInputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * @author Julian Kuerby
 *
 */
public class TwitterCal {
	
	private static Twitter twitter;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Properties prop = new Properties();
//		prop.setProperty("TConsumerKey", "1234");
//		prop.setProperty("TConsumerSecret", "3456");
//		prop.setProperty("TAccessToken", "5678");
//		prop.setProperty("TAccessTokenSecret", "7890");
//		prop.list(System.out);
//		prop.store(new FileOutputStream("settings.properties"), "");
		prop.load(new FileInputStream("settings.properties"));
//		AccessToken accessToken = new AccessToken(prop.getProperty("twitter.accessToken"), prop.getProperty("twitter.accessTokenSecret"));
//		twitter = new TwitterFactory().getInstance();
//		twitter.setOAuthConsumer(prop.getProperty("twitter.consumerKey"), prop.getProperty("twitter.consumerSecret"));
//		twitter.setOAuthAccessToken(accessToken);
//		sendTimeTweet();
//		getTimeline();
//		getDMs();
//		sendTimeDM();
		
		GoogleCalendar cal = new GoogleCalendar(prop);
		System.out.println(cal.getTodaysEvents());
//		System.out.println(cal.getNextNEvents(5));
	}

}
