package org.wylyeak.gjjx;

import java.util.HashMap;
import java.util.Map;

public class BookCarUrl {
	private BookCar bookCar;
	private String bookUrl;
	private String time;
	private EBookStatus bookStatus;
	private Map<String, TeacherCar> teacherCar = new HashMap<String, TeacherCar>();

	public BookCar getBookCar() {
		return bookCar;
	}

	public Map<String, TeacherCar> getTeacherCar() {
		return teacherCar;
	}

	public void setBookCar(BookCar bookCar) {
		this.bookCar = bookCar;
	}

	public void setTeacherCar(Map<String, TeacherCar> teacherCar) {
		this.teacherCar = teacherCar;
	}

	public String getBookUrl() {
		return bookUrl;
	}

	public void setBookUrl(String bookUrl) {
		this.bookUrl = bookUrl;
	}

	public EBookStatus getBookStatus() {
		return bookStatus;
	}

	public void setBookStatus(EBookStatus bookStatus) {
		this.bookStatus = bookStatus;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "BookCarUrl ["
				+ (bookUrl != null ? "bookUrl=" + bookUrl + ", " : "")
				+ (time != null ? "time=" + time + ", " : "")
				+ (bookStatus != null ? "bookStatus=" + bookStatus + ", " : "")
				+ (teacherCar != null ? "teacherCar=" + teacherCar : "") + "]";
	}

}
