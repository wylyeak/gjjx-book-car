package org.wylyeak.gjjx;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws HttpException, IOException, InterruptedException {
		DOMConfigurator.configure("config/log4j.xml");
		HttpClient client = new HttpClient();
		client.getParams()
				.setParameter(HttpMethodParams.USER_AGENT,
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:19.0) Gecko/20100101 Firefox/19.0");
		client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		// 验证码
		GetMethod getMethod = new GetMethod(
				"http://www.gjjx.com.cn/api.php?op=checkcode&code_len=4&font_size=14&width=84&height=24&font_color=&background=");
		int statusCode = client.executeMethod(getMethod);
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
				new NameValuePair("searchmem", "输入身份证号查询考试信息") };
		postMethod.setRequestBody(data);
		postMethod.setRequestHeader("Referer", "http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini");
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.setRequestHeader("Connection", "keep-alive");
		statusCode = client.executeMethod(postMethod);
//		Header header = null;
		if (statusCode == HttpStatus.SC_OK) {
			String body = postMethod.getResponseBodyAsString();
			postMethod.releaseConnection();
			if (body.indexOf("登陆成功") > 0) {
				logger.info("登录成功");
//			    header = postMethod.getResponseHeader("Set-Cookie");
//				logger.error(header.getValue());
				Cookie[] cookies = client.getState().getCookies();// 取出登陆成功后，服务器返回的cookies信息，里面保存了服务器端给的“临时证”
				String tmpcookies = "";
				for (Cookie c : cookies) {
					tmpcookies = tmpcookies + c.toString() + ";";
				}
				logger.error(tmpcookies);
				Document document = Jsoup.parse(body);
				Elements elemets = document
						.getElementsByAttributeValueContaining("src",
								"api/uc.php");
				for (Element element : elemets) {
					String src = element.attr("src");
					getMethod = new GetMethod(src);
//					getMethod.setRequestHeader("Cookie", tmpcookies);
					getMethod.setRequestHeader("Referer", "http://www.gjjx.com.cn/index.php?m=member&c=index&a=login");
//					if(header!=null){
////						getMethod.addRequestHeader(header);
//					}
					statusCode = client.executeMethod(getMethod);
					cookies = client.getState().getCookies();// 取出登陆成功后，服务器返回的cookies信息，里面保存了服务器端给的“临时证”
					tmpcookies = "";
					for (Cookie c : cookies) {
						tmpcookies = tmpcookies + c.toString() + ";";
					}
					logger.error(tmpcookies);
					body = getMethod.getResponseBodyAsString();
					getMethod.releaseConnection();
					logger.info(body);
//					header = getMethod.getResponseHeader("Set-Cookie");
//					if(header != null){
//						logger.error(header.getValue());
//					}
					if(!body.isEmpty()){
						String tmp = "document.write(\"<script type=\\\"text/javascript\\\" src=\\\"";
						String tmp1 = "\\\" reload=\\\"1\\\"></script>\");";
						int start = body.indexOf(tmp);
						int end = body.indexOf(tmp1);
						String url = body.substring(start + tmp.length(), end);
//						url = url.replaceAll("%3D%3D", "==");
						logger.info(url);
						getMethod = new GetMethod(url);
//						getMethod.setRequestHeader("Cookie", tmpcookies);
						getMethod.setRequestHeader("Referer", "http://www.gjjx.com.cn/index.php?m=member&c=index&a=login");
//						if(header!=null){
////							getMethod.addRequestHeader(header);
//						}
						statusCode = client.executeMethod(getMethod);
						cookies = client.getState().getCookies();// 取出登陆成功后，服务器返回的cookies信息，里面保存了服务器端给的“临时证”
						tmpcookies = "";
						for (Cookie c : cookies) {
							tmpcookies = tmpcookies + c.toString() + ";";
						}
						logger.error(tmpcookies);
						body = getMethod.getResponseBodyAsString();
						getMethod.releaseConnection();
						logger.info(body);
//						header = getMethod.getResponseHeader("Set-Cookie");
//						if(header != null){
//							logger.error(header.getValue());
//						}
					}
				}
				Thread.sleep(3000);
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index");
				statusCode = client.executeMethod(getMethod);
				body = getMethod.getResponseBodyAsString();
				getMethod.releaseConnection();
				logger.info(body);
				getMethod = new GetMethod("http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini");
//				getMethod.addRequestHeader(header);
				client.executeMethod(getMethod);
				body = getMethod.getResponseBodyAsString();
				logger.info(body);
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=1");
				client.executeMethod(getMethod);
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
}
