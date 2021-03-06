package org.wylyeak.gjjx;

public class StaticData {
	/**
	 * 登录
	 */
	public static String loginUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=login";

	public static String logoutUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=logout";
	/**
	 * 用户中心
	 */
	public static String userCenterUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index";
	/**
	 * 验证码
	 */
	public static String codeUrl = "http://www.gjjx.com.cn/api.php?op=checkcode&code_len=2&font_size=100&width=300&height=300&font_color=&background=";
	/**
	 * 迷你首页
	 */
	public static String topIndexUrl = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=mini";
	/**
	 * host
	 */
	public static String host = "http://www.gjjx.com.cn/";
	/**
	 * 预约模拟训练
	 */
	@Deprecated
	public static String bookSimulateList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=2";
	/**
	 * 预约基础＆道路
	 */
	public static String bookBaseCarList = "http://www.gjjx.com.cn/index.php?m=member&c=indexdeg&a=orderlist&traint=1&datatime=1";
	/**
	 * 预约穿桩训练
	 */
	public static String bookSlalomCarList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=orderlist&traint=0";

	public static String getBookUrl(int index) {
		switch (index) {
		case 0:
			return bookSimulateList;
		case 1:
			return bookBaseCarList;
		case 2:
			return bookSlalomCarList;
		}
		throw new IllegalArgumentException("选择的不存在");
	}

	public static String encodeTime(String time) {
		if (time.equals("上午") || time.equals("1")) {
			time = "1(上午)";
		} else if (time.equals("下午") || time.equals("2")) {
			time = "2(下午)";
		} else if (time.equals("晚间") || time.equals("3")) {
			time = "3(晚间)";
		}
		return time;
	}

	/**
	 * 取消训练
	 */
	public static String bookCancelCarList = "http://www.gjjx.com.cn/index.php?m=member&c=index&a=appointment&t=3";

	/**
	 * 禁约图片
	 */
	public static String forbiddenBookGif = "http://www.gjjx.com.cn/statics/images/gjjx/zyy_38";

	/**
	 * 可约图片
	 */
	public static String canBookGif = "http://www.gjjx.com.cn/statics/images/gjjx/zyy_37.gif";

	public static final int SleepTime = 6000;

	public static char[] chs = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9' };

}
