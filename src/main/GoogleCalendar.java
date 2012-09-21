/**
 * 
 */
package main;

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

/**
 * @author Julian Kuerby
 *
 */
public class GoogleCalendar {
	private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
	private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/calendar";
	private static final String CALENDAR_ID = "1frkkepse5l79agfi5vg8q2te0@group.calendar.google.com"; // Geburtstage
	private static final String CALENDAR_API_URL = "https://www.googleapis.com/calendar/v3/calendars/";
	
	private Properties prop;
	private OAuthService service;
	private Token accessToken;
	
	public GoogleCalendar(Properties prop) {
		this.prop = prop;
		service = new ServiceBuilder().provider(GoogleApi.class)
				.apiKey(prop.getProperty("google.apiKey"))
				.apiSecret(prop.getProperty("google.apiSecret"))
				.scope(GOOGLE_SCOPE)
				.build();
		accessToken = new Token(prop.getProperty("google.accessToken"), prop.getProperty("google.accessTokenSecret"));
	}
	
	public String getTodaysBirthdays() {
		OAuthRequest request = new OAuthRequest(Verb.GET, CALENDAR_API_URL + CALENDAR_ID + "/events");
		request.addQuerystringParameter("orderBy", "startTime");
		request.addQuerystringParameter("singleEvents", "true");
		request.addQuerystringParameter("timeMin", "2012-09-21T00:00:00Z");
		request.addQuerystringParameter("timeMax", "2012-09-22T00:00:00Z");
		service.signRequest(accessToken, request);
		request.addHeader("GData-Version", "3.0");
		Response response = request.send();
		
		return response.getCode() + "\n" + response.getBody();
	}
	
	public String getNextNBirthdays(int n) {
		OAuthRequest request = new OAuthRequest(Verb.GET, CALENDAR_API_URL + CALENDAR_ID + "/events");
		request.addQuerystringParameter("orderBy", "startTime");
		request.addQuerystringParameter("singleEvents", "true");
		request.addQuerystringParameter("timeMin", "2012-09-21T00:00:00Z");
		request.addQuerystringParameter("maxResults", "" + n);
		service.signRequest(accessToken, request);
		request.addHeader("GData-Version", "3.0");
		Response response = request.send();
		
		return response.getCode() + "\n" + response.getBody();
	}
	
	public void scribeGoogleInstalledExistingAccessToken() {
		
		System.out.println("Now we're going to access a protected resource...");
		OAuthRequest request = new OAuthRequest(Verb.GET, CALENDAR_API_URL + CALENDAR_ID);
		service.signRequest(accessToken, request);
		request.addHeader("GData-Version", "3.0");
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getCode());
		System.out.println(response.getBody());
	}
	
//	public static void scribeGoogleInstalled() {
//		OAuthService service = new ServiceBuilder().provider(GoogleApi.class)
//				.apiKey("288549326303.apps.googleusercontent.com")
//				.apiSecret("orJafxzGsw7gwyFc_SND7jfS")
//				.scope(GOOGLE_SCOPE)
//				.build();
//		
//		Scanner in = new Scanner(System.in);
//
//		System.out.println("=== "  + "'s OAuth Workflow ===");
//		System.out.println();
//
//		// Obtain the Request Token
//		System.out.println("Fetching the Request Token...");
//		Token requestToken = service.getRequestToken();
//		System.out.println("Got the Request Token!");
//		System.out.println("(if your curious it looks like this: " + requestToken + " )");
//		System.out.println();
//
//		System.out.println("Now go and authorize Scribe here:");
//		System.out.println(AUTHORIZE_URL +  requestToken.getToken());
//		System.out.println("And paste the verifier here");
//		System.out.print(">>");
//		Verifier verifier = new Verifier(in.nextLine());
//		System.out.println();
//
//		// Trade the Request Token and Verfier for the Access Token
//		System.out.println("Trading the Request Token for an Access Token...");
//		Token accessToken = service.getAccessToken(requestToken, verifier);
//		System.out.println("Got the Access Token!");
//		System.out.println("(if your curious it looks like this: " + accessToken + " )");
//		System.out.println();
//		
//		System.out.println("Now we're going to access a protected resource...");
//		OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/calendar/v3/calendars/" + "1frkkepse5l79agfi5vg8q2te0@group.calendar.google.com");
//		service.signRequest(accessToken, request);
//		request.addHeader("GData-Version", "3.0");
//		Response response = request.send();
//		System.out.println("Got it! Lets see what we found...");
//		System.out.println();
//		System.out.println(response.getCode());
//		System.out.println(response.getBody());
//	}
}