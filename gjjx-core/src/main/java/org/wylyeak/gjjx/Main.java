package org.wylyeak.gjjx;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws HttpException, IOException,
			InterruptedException {
		DOMConfigurator.configure("config/log4j.xml");
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams()
				.setParameter(
						HttpMethodParams.USER_AGENT,
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
		// client.getParams().setCookiePolicy(CookiePolicy.DEFAULT);
		GetMethod getMethod = new GetMethod(
				"http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini");
		client.executeMethod(getMethod);
		showSetCookie(client, getMethod);
		String body = getMethod.getResponseBodyAsString();
		Document document = Jsoup.parse(body);
		Element element = document.getElementById("randcode");
		String randCode = element.attr("value");
		logger.info(randCode);
		// 验证码
		getMethod = new GetMethod(
				"http://www.gjjx.com.cn/api.php?op=checkcode&code_len=4&font_size=14&width=84&height=24&font_color=&background=");
		getMethod.setRequestHeader("Referer",
				"http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini");
		int statusCode = client.executeMethod(getMethod);
		showSetCookie(client, getMethod);
		if (statusCode == HttpStatus.SC_OK) {
			InputStream is = getMethod.getResponseBodyAsStream();
			BufferedImage image = ImageIO.read(is);
			FileOutputStream fout = new FileOutputStream("1.jpg");
			ImageIO.write(image, "jpg", fout);
			fout.flush();
			fout.close();
			getMethod.releaseConnection();
		} else {
			logger.error("状态不对");
		}
		Scanner cin = new Scanner(System.in);
		logger.info("输入帐号");
		String userName = cin.next();
		logger.info("输入密码");
		String password = cin.next();
		logger.info("输入验证码");
		String code = cin.next();
		cin.close();
		PostMethod postMethod = new PostMethod(
				"http://www.gjjx.com.cn/index.php?m=member&c=index&a=login");
		NameValuePair[] data = { new NameValuePair("username", userName),
				new NameValuePair("password", password),
				new NameValuePair("code", code),
				new NameValuePair("dosubmit", ""),
				new NameValuePair("searchmem", "输入身份证号查询考试信息"),
				new NameValuePair("randcode", randCode) };
		postMethod.setRequestBody(data);
		postMethod.setRequestHeader("Referer",
				"http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini");
		postMethod.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		postMethod.setRequestHeader("Connection", "keep-alive");
		postMethod.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");
		statusCode = client.executeMethod(postMethod);
		showSetCookie(client, postMethod);
		if (statusCode == HttpStatus.SC_OK) {
			body = postMethod.getResponseBodyAsString();
			postMethod.releaseConnection();
			if (body.indexOf("登陆成功") > 0) {
				logger.info("登录成功");
				// logger.info(body);
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index");
				Thread.sleep(3000);
				getMethod
						.setRequestHeader("Referer",
								"http://www.gjjx.com.cn/index.php?m=member&c=index&a=login");
				statusCode = client.executeMethod(getMethod);
				showSetCookie(client, getMethod);
				body = getMethod.getResponseBodyAsString();
				getMethod.releaseConnection();
				logger.info(body);
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini");
				client.executeMethod(getMethod);
				showSetCookie(client, getMethod);
				body = getMethod.getResponseBodyAsString();
				logger.info(body);
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=1");
				client.executeMethod(getMethod);
				showSetCookie(client, getMethod);
				body = getMethod.getResponseBodyAsString();
				getMethod.releaseConnection();
				logger.info(body);

			} else {
				if (body.indexOf("验证码输入错误") > 0) {
					logger.error("验证码输入错误");
				} else {
					logger.error("登录失败");
					logger.error(body);
				}
			}
		} else {
			logger.error("状态不对");
		}

	}

	public static void showCookie(HttpClient client) {
		Object obj = client.getParams().getParameter("Cookie");
		logger.warn("\nclient cookie:" + obj);
	}

	public static void showSetCookie(HttpClient client, HttpMethod method)
			throws URIException {
		Object obj = method.getResponseHeader("Set-Cookie");
		logger.warn("\nresponse " + method.getURI() + " ： \n" + obj);
		obj = method.getRequestHeader("Cookie");
		logger.warn("\nrequest " + method.getURI() + " ：\n " + obj);
		showCookie(client);
	}
}
