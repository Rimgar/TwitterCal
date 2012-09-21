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
		return formatInt(get(YEAR)) + "-" + formatInt(get(MONTH)+1) + "-" + formatInt(get(DAY_OF_MONTH)) + "T" + formatInt(get(HOUR_OF_DAY)) + ":" + formatInt(get(MINUTE)) + ":" + formatInt(get(SECOND)) /*+ "." formatInt(get(MILLISECOND))*/ + "Z";
	}
	
	public String getDate() {
		return formatInt(get(YEAR)) + "-" + formatInt(get(MONTH)+1) + "-" + formatInt(get(DAY_OF_MONTH)) + "T00:00:00Z";
	}
	
	private String formatInt(int i) {
		DecimalFormat df = new DecimalFormat("00");
		return df.format(i);
	}

}
