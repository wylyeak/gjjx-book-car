package org.wylyeak.gjjx;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) throws HttpException, IOException {
		DOMConfigurator.configure("config/log4j.xml");
		HttpClient client = new HttpClient();
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
		PostMethod method = new PostMethod(
				"http://www.gjjx.com.cn/index.php?m=member&c=index&a=login");
		NameValuePair[] data = { new NameValuePair("username", userName),
				new NameValuePair("password", password),
				new NameValuePair("code", code),
				new NameValuePair("dosubmit", ""),
				new NameValuePair("searchmem", "输入身份证号查询考试信息") };
		method.setRequestBody(data);
		statusCode = client.executeMethod(method);
		if (statusCode == HttpStatus.SC_OK) {
			String body = method.getResponseBodyAsString();
			if (body.indexOf("登陆成功") > 0) {
				logger.info("登录成功");
				logger.error(body);
				Header header = method.getResponseHeader("Set-Cookie");
				System.out.println(header.getValue());
				Cookie[] cookies = client.getState().getCookies();// 取出登陆成功后，服务器返回的cookies信息，里面保存了服务器端给的“临时证”
				String tmpcookies = "";
				for (Cookie c : cookies) {
					tmpcookies = tmpcookies + c.toString() + ";";
				}
				Document document = Jsoup.parse(body);
				Elements elemets = document
						.getElementsByAttributeValueContaining("src",
								"api/uc.php");
				for (Element element : elemets) {
					String src = element.attr("src");
					getMethod = new GetMethod(src);
					getMethod.setRequestHeader("Set-Cookie",header.getValue());//将“临时证
					statusCode = client.executeMethod(getMethod);
					body = method.getResponseBodyAsString();
					logger.info(body);
				}
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index");
				getMethod.setRequestHeader("Set-Cookie",header.getValue());//将“临时证
				statusCode = client.executeMethod(getMethod);
				body = method.getResponseBodyAsString();
				logger.info(body);
				getMethod = new GetMethod(
						"http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=1");
				client.executeMethod(getMethod);
				body = method.getResponseBodyAsString();
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
