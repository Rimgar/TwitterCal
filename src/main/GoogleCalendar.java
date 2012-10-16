/**
 * 
 */
package main;

import java.util.Calendar;
import java.util.Properties;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Julian Kuerby
 *
 */
public class GoogleCalendar {
//	private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
	private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/calendar";
	private static final String CALENDAR_ID = "1frkkepse5l79agfi5vg8q2te0@group.calendar.google.com"; // Geburtstage
//	private static final String CALENDAR_ID = "82mh530edinflthpb0suvuen74@group.calendar.google.com"; // Test-Kalender
	private static final String CALENDAR_API_URL = "https://www.googleapis.com/calendar/v3/calendars/";
	
//	private Properties prop;
	private OAuthService service;
	private Token accessToken;
	
	public GoogleCalendar(Properties prop) {
//		this.prop = prop;
		service = new ServiceBuilder().provider(GoogleApi.class)
				.apiKey(prop.getProperty("google.apiKey"))
				.apiSecret(prop.getProperty("google.apiSecret"))
				.scope(GOOGLE_SCOPE)
				.build();
		accessToken = new Token(prop.getProperty("google.accessToken"), prop.getProperty("google.accessTokenSecret"));
	}
	
	public JSONArray getEventsOnDate(RFC3339Calendar cal) {
		OAuthRequest request = new OAuthRequest(Verb.GET, CALENDAR_API_URL + CALENDAR_ID + "/events");
		request.addQuerystringParameter("orderBy", "startTime");
		request.addQuerystringParameter("singleEvents", "true");
		request.addQuerystringParameter("timeMin", cal.getRFC3339Date());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		request.addQuerystringParameter("timeMax", cal.getRFC3339Date());
		service.signRequest(accessToken, request);
		request.addHeader("GData-Version", "3.0");
		Response response = request.send();
		
		if(response.isSuccessful()) {
			JSONArray arr = parseJsonString(response.getBody());
			return arr;
		}
		return null;
	}
	
	public JSONArray getTodaysEvents() {
		return getEventsOnDate((RFC3339Calendar) TwitterCal.now.clone());
	}
	
	public JSONArray getEventsAfter(RFC3339Calendar cal) {
		JSONArray arr = getNEventsAfter(1, cal);
		String date = arr.getJSONObject(0).getJSONObject("start").getString("date");
		return getEventsOnDate(RFC3339Calendar.parseDate(date));
	}
	
	public JSONArray getNEventsAfter(int n, RFC3339Calendar cal) {
		OAuthRequest request = new OAuthRequest(Verb.GET, CALENDAR_API_URL + CALENDAR_ID + "/events");
		request.addQuerystringParameter("orderBy", "startTime");
		request.addQuerystringParameter("singleEvents", "true");
		cal.add(Calendar.DAY_OF_MONTH, 1);
		request.addQuerystringParameter("timeMin", cal.getRFC3339Date());
		request.addQuerystringParameter("maxResults", "" + n);
		service.signRequest(accessToken, request);
		request.addHeader("GData-Version", "3.0");
		Response response = request.send();
		
		if(response.isSuccessful()) {
			JSONArray arr = parseJsonString(response.getBody());
			return arr;
		}
		return null;
	}
	
	public JSONArray getEventsBetween(RFC3339Calendar start, RFC3339Calendar end) {
		OAuthRequest request = new OAuthRequest(Verb.GET, CALENDAR_API_URL + CALENDAR_ID + "/events");
		request.addQuerystringParameter("orderBy", "startTime");
		request.addQuerystringParameter("singleEvents", "true");
		request.addQuerystringParameter("timeMin", start.getRFC3339Date());
		request.addQuerystringParameter("timeMax", end.getRFC3339Date());
		service.signRequest(accessToken, request);
		request.addHeader("GData-Version", "3.0");
		Response response = request.send();
		
		if(response.isSuccessful()) {
			JSONArray arr = parseJsonString(response.getBody());
			return arr;
		}
		return null;
	}
	
	public JSONArray parseJsonString(String json) {
		JSONObject obj = JSON.parseObject(json);
		JSONArray arr = obj.getJSONArray("items");
		return arr;
	}
	
	public void insertEvent(RFC3339Calendar cal, String summary) {
		OAuthRequest request = new OAuthRequest(Verb.POST, CALENDAR_API_URL + CALENDAR_ID + "/events");
		JSONObject event = new JSONObject();
		JSONObject start = new JSONObject();
		start.put("date", cal.getDate());
		event.put("start", start);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		JSONObject end = new JSONObject();
		end.put("date", cal.getDate());
		event.put("end", end);
		event.put("summary", summary);
		String[] recurrence = new String[1];
		recurrence[0] = "RRULE:FREQ=YEARLY";
		event.put("recurrence", recurrence);
		request.addPayload(event.toJSONString());
		request.addHeader("Content-Type", "application/json");
		service.signRequest(accessToken, request);
		Response response = request.send();
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
