package org.wylyeak.gjjx;

import java.util.Map;

public class BookCar {
	private String date;
	private String day;
	private Map<String, BookCarUrl> timeCar;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Map<String, BookCarUrl> getTimeCar() {
		return timeCar;
	}

	public void setTimeCar(Map<String, BookCarUrl> timeCar) {
		this.timeCar = timeCar;
	}
}
