package org.wylyeak.gjjx;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.http.Header;
import org.apache.http.HttpException;
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
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);
	private static HttpClient client = new DefaultHttpClient();
	private static HttpResponse response;
	private static String loginUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=login";
	private static String userCenterUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index";
	private static String codeUrl = "http://www.gjjx.com.cn/api.php?op=checkcode&code_len=4&font_size=14&width=84&height=24&font_color=&background=";
	private static String topIndexUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini";

	private static String host = "http://www.gjjx.com.cn/";

	/**
	 * 预约模拟训练
	 */
	private static String bookSimulateList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=2";
	/**
	 * 预约基础＆道路
	 */
	private static String bookBaseCarList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=1";
	/**
	 * 预约穿桩训练
	 */
	private static String bookSlalomCarList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=0";

	/**
	 * 取消训练
	 */
	private static String bookCancelCarList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=appointment&t=3";

	/**
	 * 禁约图片
	 */
	private static String forbiddenBookGif = "http://www.gjjx.com.cn/statics/images/gjjx/zyy_38.gif";

	private static String canBookGif = "http://www.gjjx.com.cn/statics/images/gjjx/zyy_37.gif";

	public static void init() {
		client.getParams()
				.setParameter(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
	}

	public static Object[][] getBookCarList() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(bookBaseCarList);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		httpGet.abort();
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
		return objs;
	}

	private static BookCar parseBookCar(String date, String weekDay,
			String time, Object oldBookCar, String url)
			throws ClientProtocolException, IOException {
		BookCar bookCar = null;
		if (oldBookCar instanceof BookCar) {
			bookCar = (BookCar) oldBookCar;
		} else {
			bookCar = new BookCar();
			bookCar.setDate(date);
			bookCar.setWeekDay(weekDay);
			bookCar.setBookUrl(url);
		}
		getCarTeacher(bookCar, time, url);
		return bookCar;
	}

	private static void getCarTeacher(BookCar bookCar, String time, String url)
			throws ClientProtocolException, IOException {
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
			bookCarUrl.setBookStatus(EBookStatus.ENABLED);
			HttpGet httpGet = new HttpGet(url);
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
				bookCarUrl.getTeacherCar().put(teacherCar.getTecherNo(),
						teacherCar);
			}
			bookCar.getTimeCar().put(time, bookCarUrl);
		}
	}

	private static Object[][] parseBookBody(String body) {
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
				if (td.html().contains(forbiddenBookGif)) {
					objs[i][j] = "禁约";
				} else if (td.html().contains(canBookGif)) {
					Elements as = td.getElementsByTag("a");
					String url = as.attr("href").trim();
					url = host + url;
					objs[i][j] = url.trim();
				} else if (td.html().contains("无车")) {
					objs[i][j] = "无车";
				} else {
					objs[i][j] = td.html().trim();
				}
				j++;
			}
			i++;
		}
		return objs;
	}

	public static String getRandCode() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(topIndexUrl);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		Document document = Jsoup.parse(body);
		Element element = document.getElementById("randcode");
		String randCode = element.attr("value");
		httpGet.abort();
		return randCode;
	}

	public static void getCode() throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(codeUrl);
		response = client.execute(httpGet);
		InputStream is = response.getEntity().getContent();
		BufferedImage image = ImageIO.read(is);
		FileOutputStream fout = new FileOutputStream("1.jpg");
		ImageIO.write(image, "jpg", fout);
		fout.flush();
		fout.close();
		httpGet.abort();
	}

	public static String getRedirectLocation() {
		Header locationHeader = response.getFirstHeader("Location");
		if (locationHeader == null) {
			return null;
		}
		return locationHeader.getValue();
	}

	public static boolean login(String userName, String pwd, String randCode,
			String code) throws ClientProtocolException, IOException {
		HttpPost httpost = new HttpPost(loginUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username", userName));
		nvps.add(new BasicNameValuePair("password", pwd));
		nvps.add(new BasicNameValuePair("code", code));
		nvps.add(new BasicNameValuePair("dosubmit", ""));
		nvps.add(new BasicNameValuePair("searchmem", "输入身份证号查询考试信息"));
		nvps.add(new BasicNameValuePair("randcode", randCode));
		httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		response = client.execute(httpost);
		String body = getBody();
		httpost.abort();
		if (body.indexOf("登陆成功") > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean valitate() throws ClientProtocolException,
			IOException {
		HttpGet httpGet = new HttpGet(userCenterUrl);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String body = client.execute(httpGet, handler);
		httpGet.releaseConnection();
		if (body.indexOf("网上约车") > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String getBody() throws IllegalStateException, IOException {
		InputStream inputStream = response.getEntity().getContent();
		byte[] bye = new byte[inputStream.available()];
		inputStream.read(bye);
		String body = new String(bye);
		return body;
	}

	public static void main(String[] args) throws HttpException, IOException,
			InterruptedException {
		DOMConfigurator.configure("config/log4j.xml");
		init();
		String randCode = getRandCode();
		getCode();
		Scanner cin = new Scanner(System.in);
		logger.info("输入帐号");
		String userName = cin.next();
		logger.info("输入密码");
		String password = cin.next();
		logger.info("输入验证码");
		String code = cin.next();
		cin.close();
		if (login(userName, password, randCode, code)) {
			if (valitate()) {
				logger.info("登录成功");
				getBookCarList();
			} else {
				logger.error("登录失败");
			}
		} else {
			logger.error("登录失败");
		}
	}
}
