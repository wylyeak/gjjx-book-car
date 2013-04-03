package org.wylyeak.gjjx;

public class TeacherCar {
	private String date;
	private String time;
	private String stopNo;
	private String siteNo;
	private String techerNo;
	private String url;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStopNo() {
		return stopNo;
	}

	public void setStopNo(String stopNo) {
		this.stopNo = stopNo;
	}

	public String getSiteNo() {
		return siteNo;
	}

	public void setSiteNo(String siteNo) {
		this.siteNo = siteNo;
	}

	public String getTecherNo() {
		return techerNo;
	}

	public void setTecherNo(String techerNo) {
		this.techerNo = techerNo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "TeacherCar [" + (date != null ? "date=" + date + ", " : "")
				+ (time != null ? "time=" + time + ", " : "")
				+ (stopNo != null ? "stopNo=" + stopNo + ", " : "")
				+ (siteNo != null ? "siteNo=" + siteNo + ", " : "")
				+ (techerNo != null ? "techerNo=" + techerNo + ", " : "")
				+ (url != null ? "url=" + url : "") + "]";
	}

}
