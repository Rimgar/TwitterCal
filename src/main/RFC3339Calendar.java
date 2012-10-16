/**
 * 
 */
package main;

import java.text.DecimalFormat;
import java.util.GregorianCalendar;

/**
 * @author Julian Kuerby
 *
 */
@SuppressWarnings("serial")
public class RFC3339Calendar extends GregorianCalendar {
	
	public RFC3339Calendar() {
		super();
	}
	
	public RFC3339Calendar(int year, int month, int dayOfMonth) {
		super(year, month, dayOfMonth);
	}
	
	public RFC3339Calendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		super(year, month, dayOfMonth, hourOfDay, minute);
	}
	
	public RFC3339Calendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		super(year, month, dayOfMonth, hourOfDay, minute, second);
	}
	
	public String toString() {
		return get(YEAR) + "-" + formatInt(get(MONTH)+1) + "-" + formatInt(get(DAY_OF_MONTH)) + "T" + formatInt(get(HOUR_OF_DAY)) + ":" + formatInt(get(MINUTE)) + ":" + formatInt(get(SECOND)) /*+ "." formatInt(get(MILLISECOND))*/ + "Z";
	}
	
	public String getRFC3339Date() {
		return get(YEAR) + "-" + formatInt(get(MONTH)+1) + "-" + formatInt(get(DAY_OF_MONTH)) + "T00:00:00Z";
	}
	
	public String getDate() {
		return get(YEAR) + "-" + formatInt(get(MONTH)+1) + "-" + formatInt(get(DAY_OF_MONTH));
	}
	
	public String getTimeOfDay() {
		return formatInt(get(HOUR_OF_DAY)) + ":" + formatInt(get(MINUTE)) + ":" + formatInt(get(SECOND));
	}
	
	public static RFC3339Calendar parseDate(String date) {
		if(! date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new IllegalArgumentException("Illegal Date-Format");
		}
		int year = Integer.parseInt(date.substring(0, 4)); // 2012-09-12
		int month = Integer.parseInt(date.substring(5, 7))-1;
		int dayOfMonth = Integer.parseInt(date.substring(8));
		return new RFC3339Calendar(year, month, dayOfMonth);
	}
	
	private String formatInt(int i) {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(i);
	}

	@Override
	public RFC3339Calendar clone() {
		return (RFC3339Calendar) super.clone();
	}
	
	

}
