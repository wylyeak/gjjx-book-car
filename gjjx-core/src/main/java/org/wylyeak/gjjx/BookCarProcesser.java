package org.wylyeak.gjjx;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BookCarProcesser {
	private HttpClient client;
	private HttpResponse response;

	private final String userName;
	private final String password;
	private final String fileName;
	private boolean login;
	private String randCode;
	private String code;
	private final Scanner cin = new Scanner(System.in);

	public BookCarProcesser(String userName, String password, String fileName)
			throws ClientProtocolException, IOException {
		this.userName = userName;
		this.password = password;
		this.fileName = fileName;
		initClient();
	}

	private void initClient() {
		client = new DefaultHttpClient();
		client.getParams()
				.setParameter(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
	}

	private void inputCode() {
		System.out.print("输入验证码:");
		code = cin.next();
	}

	private String getTips(String body) {
		Document document = Jsoup.parse(body);
		Elements elements = document.getElementsByClass("guery");
		return elements.get(0).html();
	}

	private boolean isTips(String body) {
		if (body.indexOf("提示信息") > 0) {
			return true;
		}
		return false;
	}

	public boolean login() throws ClientProtocolException, IOException {
		getRandCode();
		getCode();
		inputCode();
		HttpPost httpost = new HttpPost(StaticData.loginUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username", userName));
		nvps.add(new BasicNameValuePair("password", password));
		nvps.add(new BasicNameValuePair("code", code));
		nvps.add(new BasicNameValuePair("dosubmit", ""));
		nvps.add(new BasicNameValuePair("searchmem", "输入身份证号查询考试信息"));
		nvps.add(new BasicNameValuePair("randcode", randCode));
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpost, handler);
		httpost.abort();
		if (body.indexOf("登陆成功") > 0 && valitate()) {
			login = true;
			return true;
		} else {
			System.out.println(getTips(body));
			return false;
		}
	}

	public Object getBookCarList(String url) throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(url);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		httpGet.abort();
		if (isTips(body)) {
			String tips = getTips(body);
			return tips;
		} else {
			Object[][] objs = parseBookBody(body);
			for (int j = 1; j < 8; j++) {
				for (int i = 2; i < 6; i++) {
					if (objs[i][j] == null) {
						continue;
					}
					objs[i][j] = parseBookCar(objs[0][j].toString(),
							objs[1][j].toString(), objs[i][0].toString(),
							objs[2][j], objs[i][j].toString());
				}
			}
			Map<String, BookCar> map = new LinkedHashMap<String, BookCar>();
			for (int i = 1; i < 8; i++) {
				map.put(objs[0][i].toString(), (BookCar) objs[2][i]);
			}
			return map;
		}
	}

	private BookCar parseBookCar(String date, String weekDay, String time,
			Object oldBookCar, String url) throws ClientProtocolException,
			IOException {
		BookCar bookCar = null;
		if (oldBookCar instanceof BookCar) {
			bookCar = (BookCar) oldBookCar;
		} else {
			bookCar = new BookCar();
			bookCar.setDate(date);
			bookCar.setWeekDay(weekDay);
			bookCar.setBookUrl(url);
		}
		getCarTeacher(bookCar, time, url, false);
		return bookCar;
	}

	private boolean valitate() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(StaticData.userCenterUrl);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		httpGet.releaseConnection();
		if (body.indexOf("网上约车") > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean bookTeacher(TeacherCar teacherCar)
			throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(teacherCar.getUrl());
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		httpGet.abort();
		if (isTips(body)) {
			String tip = getTips(body);
			if (tip.contains("约车成功")) {
				return true;
			} else {
				System.out.println(tip);
				return false;
			}
		}
		return false;
	}

	private Object[][] parseBookBody(String body) {
		Document document = Jsoup.parse(body);
		Element element = document.getElementById("u1tab");
		Elements trs = element.getElementsByTag("tr");
		trs.remove(0);
		Object[][] objs = new Object[6][8];
		int i = 0;
		for (Element tr : trs) {
			Elements tds = tr.children();
			int j = 0;
			for (Element td : tds) {
				if (td.html().contains(StaticData.forbiddenBookGif)) {
					objs[i][j] = "禁约";
				} else if (td.html().contains(StaticData.canBookGif)) {
					Elements as = td.getElementsByTag("a");
					String url = as.attr("href").trim();
					url = StaticData.host + url;
					objs[i][j] = url.trim();
				} else if (td.html().contains("无车")) {
					objs[i][j] = "无车";
				} else {
					String str = td.html().trim();
					if (str.indexOf("月") > 0) {
						str = str.replaceAll("月", "-").replaceAll("日", "");
					}
					objs[i][j] = str;
				}
				j++;
			}
			i++;
		}
		return objs;
	}

	public String getRandCode() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(StaticData.topIndexUrl);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		Document document = Jsoup.parse(body);
		Element element = document.getElementById("randcode");
		randCode = element.attr("value");
		httpGet.abort();
		return randCode;
	}

	public void getCode() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(StaticData.codeUrl);
		response = client.execute(httpGet);
		InputStream is = response.getEntity().getContent();
		BufferedImage image = ImageIO.read(is);
		FileOutputStream fout = new FileOutputStream(fileName);
		ImageIO.write(image, "jpg", fout);
		fout.flush();
		fout.close();
		httpGet.abort();
	}

	public String getRedirectLocation() {
		Header locationHeader = response.getFirstHeader("Location");
		if (locationHeader == null) {
			return null;
		}
		return locationHeader.getValue();
	}

	private void getCarTeacher(BookCar bookCar, String time, String url,
			boolean getTeacher) throws ClientProtocolException, IOException {
		if (url.contains("禁约")) {
			BookCarUrl bookCarUrl = new BookCarUrl();
			bookCarUrl.setBookCar(bookCar);
			bookCarUrl.setTime(time);
			bookCarUrl.setBookStatus(EBookStatus.FORBIDDEN);
			bookCar.getTimeCar().put(time, bookCarUrl);
		} else if (url.contains("无车")) {
			BookCarUrl bookCarUrl = new BookCarUrl();
			bookCarUrl.setBookCar(bookCar);
			bookCarUrl.setTime(time);
			bookCarUrl.setBookStatus(EBookStatus.NOCAR);
			bookCar.getTimeCar().put(time, bookCarUrl);
		} else {
			BookCarUrl bookCarUrl = new BookCarUrl();
			bookCarUrl.setBookCar(bookCar);
			bookCarUrl.setTime(time);
			bookCarUrl.setBookUrl(url);
			bookCarUrl.setBookStatus(EBookStatus.ENABLED);
			bookCar.getTimeCar().put(time, bookCarUrl);
		}
	}

	public void getCarTeacher(BookCarUrl bookCarUrl)
			throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(bookCarUrl.getBookUrl());
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		httpGet.abort();
		Document document = Jsoup.parse(body);
		Elements elements = document.getElementsByClass("wyz_specific");
		Elements trs = elements.get(0).getElementsByTag("tr");
		trs.remove(0);
		for (Element tr : trs) {
			TeacherCar teacherCar = new TeacherCar();
			Elements tds = tr.children();
			teacherCar.setDate(tds.get(0).html());
			teacherCar.setTime(tds.get(1).html());
			teacherCar.setStopNo(tds.get(2).html());
			teacherCar.setSiteNo(tds.get(4).html());
			teacherCar.setTecherNo(tds.get(4).html());
			String tmp = tds.get(5).children().get(0).attr("onclick");
			tmp = tmp.replaceAll("bpk_js\\(", "").replaceAll("\\)", "")
					.replaceAll("'", "");
			String[] tmps = tmp.split(",");
			String bookUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=bpk&id="
					+ tmps[0]
					+ "&yyrq="
					+ tmps[1]
					+ "&sd="
					+ tmps[2]
					+ "&cnbh=" + tmps[3] + "&traint=" + tmps[4];
			teacherCar.setUrl(bookUrl);
			bookCarUrl.getTeacherCar()
					.put(teacherCar.getTecherNo(), teacherCar);
		}
	}

	public boolean isLogin() {
		return login;
	}

}
