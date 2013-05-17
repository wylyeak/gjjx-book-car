package org.wylyeak.gjjx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.wylyeak.gjjx.util.TimeUtils;

@SuppressWarnings("unchecked")
public class AutoBookCarHandler implements Runnable {
	private final BookCarProcesser processer;
	private String date;
	private String time;
	private final Scanner cin = new Scanner(System.in);
	private Integer index;

	public AutoBookCarHandler(String userName, String password, String fileName)
			throws ClientProtocolException, IOException {
		processer = new BookCarProcesser(userName, password, fileName);
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (processer.isLogin()) {
					try {
						if (index == null) {
							System.out.println("1 预约基础道路训练   \n2 预约穿桩训练");
							index = cin.nextInt();
						}
						Object obj = processer.getBookCarList(StaticData
								.getBookUrl(index));
						Map<String, BookCar> map = null;
						if (obj instanceof String) {
							System.out.println(obj);
							if (obj.equals("您的登陆已超时请返回登陆")) {
								processer.loginOut();
								continue;
							} else {
								Thread.sleep(StaticData.SleepTime);
								continue;
							}
						} else {
							map = (Map<String, BookCar>) obj;
						}

						if (date == null && time == null) {
							for (BookCar bookCar : map.values()) {
								System.out.print(bookCar.getDate() + "("
										+ bookCar.getWeekDay() + ")  ");
							}
							System.out.println("\n"
									+ map.values().iterator().next()
											.getTimeCar().keySet());
							date = cin.next();
							time = cin.next();
							time = StaticData.encodeTime(time);
						}
						BookCar bookCar = map.get(date);
						if (bookCar != null) {
							BookCarUrl bookCarUrl = bookCar.getTimeCar().get(
									time);
							if (bookCarUrl != null) {
								if (bookCarUrl.getBookStatus() == EBookStatus.NOCAR) {
									System.out.println("date = " + date
											+ "  time = " + time
											+ "无车   睡眠6s继续刷");
									Thread.sleep(StaticData.SleepTime);
								} else if (bookCarUrl.getBookStatus() == EBookStatus.FORBIDDEN) {
									System.out.println("date = " + date
											+ "  time = " + time + "禁约");
									break;
								} else {
									processer.getCarTeacher(bookCarUrl);
									List<TeacherCar> list = new ArrayList<TeacherCar>(
											bookCarUrl.getTeacherCar().values());
									int index = new Random().nextInt(list
											.size());
									TeacherCar teacherCar = list.get(index);
									if (processer.bookTeacher(teacherCar)) {
										System.out.println(teacherCar
												+ "\t约车成功");
										break;
									}
									break;
								}
							} else {
								System.out.println("未找到时间,睡眠6s重试");
								Thread.sleep(StaticData.SleepTime);
							}
						} else {
							Date now = new Date();
							Date sleepDate = TimeUtils.parseStr(
									TimeUtils.getDayAfter(now, 0) + " 06:00",
									TimeUtils.YYYY_MM_DD_HH_MM);
							if (now.after(sleepDate)) {
								System.out.println("超过 06 点  依然没有时间  继续刷");
								Thread.sleep(StaticData.SleepTime);
							} else {
								System.out
										.println("未找到日期,睡眠到06:00 开始刷  "
												+ (sleepDate.getTime() - now
														.getTime()));
								Thread.sleep(sleepDate.getTime()
										- now.getTime());
							}
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						try {
							Thread.sleep(StaticData.SleepTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							Thread.sleep(StaticData.SleepTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						try {
							Thread.sleep(StaticData.SleepTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					try {
						processer.login();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						try {
							Thread.sleep(StaticData.SleepTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							Thread.sleep(StaticData.SleepTime);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
