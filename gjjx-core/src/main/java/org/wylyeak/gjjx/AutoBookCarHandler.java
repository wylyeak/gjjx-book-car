package org.wylyeak.gjjx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;

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
			if (processer.isLogin()) {
				try {
					if (index == null) {
						System.out.println("0 预约模拟训练\n1 预约基础道路训练   \n2 预约穿桩训练");
						index = cin.nextInt();
					}
					Object obj = processer.getBookCarList(StaticData
							.getBookUrl(index));
					Map<String, BookCar> map = null;
					if (obj instanceof String) {
						System.out.println(obj);
						Thread.sleep(5000);
						continue;
					} else {
						map = (Map<String, BookCar>) obj;
					}

					if (date == null) {
						for (BookCar bookCar : map.values()) {
							System.out.print(bookCar.getDate()
									+ bookCar.getWeekDay() + "\t");
						}
						date = cin.next();
					}
					BookCar bookCar = map.get(date);
					if (bookCar != null) {
						if (time == null) {
							System.out.println(bookCar.getTimeCar().keySet()
									+ ":");
							time = cin.next();
						}
						BookCarUrl bookCarUrl = bookCar.getTimeCar().get(time);
						if (bookCarUrl != null) {
							if (bookCarUrl.getBookStatus() == EBookStatus.NOCAR) {
								System.out
										.println("date = " + date + "  time = "
												+ time + "无车   睡眠10分钟继续刷");

							} else if (bookCarUrl.getBookStatus() == EBookStatus.FORBIDDEN) {
								System.out.println("date = " + date
										+ "  time = " + time + "禁约");
								break;
							} else {
								processer.getCarTeacher(bookCarUrl);
								List<TeacherCar> list = new ArrayList<TeacherCar>(
										bookCarUrl.getTeacherCar().values());
								int index = new Random().nextInt(list.size());
								TeacherCar teacherCar = list.get(index);
								if (processer.bookTeacher(teacherCar)) {
									System.out.println(teacherCar + "\t约车成功");
									break;
								}
								break;
							}
						} else {
							System.out.println("未找到日期,睡眠30分钟重试");
							Thread.sleep(1000);
						}
					} else {
						System.out.println("未找到日期,睡眠30分钟重试");
						Thread.sleep(1000);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					processer.login();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
