package org.wylyeak.gjjx;

import java.util.LinkedHashMap;
import java.util.Map;

public class BookCar {
	private String date;
	private String weekDay;
	private String bookUrl;
	private Map<String, BookCarUrl> timeCar = new LinkedHashMap<String, BookCarUrl>();

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBookUrl() {
		return bookUrl;
	}

	public void setBookUrl(String bookUrl) {
		this.bookUrl = bookUrl;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public Map<String, BookCarUrl> getTimeCar() {
		return timeCar;
	}

	public void setTimeCar(Map<String, BookCarUrl> timeCar) {
		this.timeCar = timeCar;
	}

	@Override
	public String toString() {
		return "BookCar [" + (date != null ? "date=" + date + ", " : "")
				+ (weekDay != null ? "weekDay=" + weekDay + ", " : "")
				+ (bookUrl != null ? "bookUrl=" + bookUrl + ", " : "")
				+ (timeCar != null ? "timeCar=" + timeCar : "") + "]";
	}

}
