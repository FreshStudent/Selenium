
/**
 * 这是java+selenium的一个helloworld
 * 功能：打开浏览器，进入百度首页，输入相关内容并搜索
 * @author Hey_boom
 */

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import com.gargoylesoftware.htmlunit.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class OrderCoin {

	// 汕头-外马支行
	private static final String SHANTOU_WEB_URL = "https://jnb.icbc.com.cn/ICBCCOIN/outer/iepa_area05/pages/coinorder?brzo=00202-02003";

	// 广州-番禺营业室
	private static final String PANYU_WEB_URL = "https://jnb.icbc.com.cn/ICBCCOIN/outer/iepa_area05/pages/coinorder?brzo=00243-03602";

	private static final String TXT_URL = "./src/data.txt";

    // 兑换日期 格式：2018-09-07
	private static final String EXCHANGE_DATE = "2018-09-07";

	public static void main(String[] args) throws Exception {

		List<Map<String, String>> dataList = readFile(TXT_URL);//读取txt文件
		
		for (Map<String, String> map : dataList) {
			String name = replaceBlank(map.get("name"));
			String ppNum = replaceBlank(map.get("ppNum"));
			int wantBuyNum = Integer.parseInt(map.get("wantBuyNum"));
			String tel = map.get("tel");
			
//			String name = "鲁怡和";
//			String ppNum = "140825197503173178";
//			String tel = "13202079887";
			
			inputData2Browser(name, ppNum, tel, wantBuyNum);
		}

		// driver.close();
		// driver.quit();//quit()方法关闭浏览器


	}

	/**
	 * 打开浏览器并且输入数据
	 * 
	 * @param name
	 *            用户名
	 * @param ppNum
	 *            身份证号
	 * @param tel
	 *            联系电话
	 * @param wantBuyNum
	 *            欲购数量
	 */
	public static void inputData2Browser(String name, String ppNum, String tel, int wantBuyNum) {

		// 设置chromedriver的环境变量路径,加载本地的Chrome浏览器的驱动文件
		System.setProperty("webdriver.chrome.driver", "/Users/liquanliang/eclipseJeeworkspace/Selenium/chromedriver");

		// 创建chromedriver对象
		WebDriver driver = new ChromeDriver();

		// 使窗体最大化
		driver.manage().window().maximize();

		// 获取url
		driver.get(SHANTOU_WEB_URL);
		
		// By id获取元素 文本输入框
		WebElement txtName = driver.findElement(By.id("txtName")); // 用户名
		WebElement txtPpNum = driver.findElement(By.id("txtPpNum")); // 证件号码
		WebElement txtTel = driver.findElement(By.id("txtTel")); // 手机号码

		txtName.sendKeys(name);
		txtPpNum.sendKeys(ppNum);
		txtTel.sendKeys(tel);

		// 点击跳转下一页
		WebElement search_setting = driver.findElement(By.id("nextstep3"));
		Actions action = new Actions(driver);
		action.click(search_setting).perform();

		// -----填写个人信息 end

		// -----填写预约数量 begin
		// String numsStr = "5"; //购买数量
		// String numsStrJs =
		// "document.getElementById(\"nums\").value=\""+numsStr+"\";"; //执行js复制input框
		// ((JavascriptExecutor) driver).executeScript(numsStrJs);
		// 点击减号 根据每人需要兑换数量来控制点击减号次数，默认是买入最大值
		WebElement canBuyMaxNums = driver.findElement(By.id("nums")); // 最大购买数量
		String inputVal = canBuyMaxNums.getAttribute("value");//  获取input文本框的值
		int buyMaxNums = Integer.parseInt(inputVal);

		// -----填写预约数量 end

		// 点击减号
		WebElement subtract_Setting = driver.findElement(By.id("subtract"));
		Actions subtractAction = new Actions(driver);
		for (int i = 0; i < (buyMaxNums - wantBuyNum); i++) {
			subtractAction.click(subtract_Setting).perform();
		}

		// 回填校验码
		WebElement txtverify = driver.findElement(By.id("txtverify")); // 校验码
		String txtverifyStr = "校验码";
		txtverify.sendKeys(txtverifyStr);

		/**
		 * 核心提示： 面额10元，直径27毫米，发行量2亿枚。 
		 * 7 月 19 日至 7 月 22 日预约。 每人预约、兑换限额为20 枚。 
		 * 9 月  3 日至 9 月 12 日预约兑换。 9 月 18 日至 9 月 25 日现场兑换。 根据每次币种不一，可以搜索所在银行的营业时间，直接把兑换日期赋值了。
		 * 例如，知道市桥营业室是周一到周五，那么就在兑换日期（9 月 3 日至 9 月 12 日预约兑换。 ）中选择 合适的时间，直接赋值即可。
		 * 
		 **/

		// 点击兑换日期
		// WebElement txtexchangedate_setting =
		// driver.findElement(By.id("txtexchangedate"));
		// Actions changedateAction = new Actions(driver);
		// changedateAction.click(txtexchangedate_setting).perform();

		// 赋值兑换日期
		String exchangeDateJs = "document.getElementById(\"txtexchangedate\").value=\"" + EXCHANGE_DATE + "\";"; // 执行js,赋值input框
		((JavascriptExecutor) driver).executeScript(exchangeDateJs);

	}

	/**
	 * 获取验证码图片 TO_DO下一步需要做的是破解验证码，进行回填！！！！！
	 * 
	 * @param driver
	 * @param ppNum
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void getCodePic(WebDriver driver, String ppNum) throws IOException, InterruptedException {

		// 在页面停留三秒,让二维码刷新出来，因为它是ajax加载的
		Thread.sleep(3500);
		WebElement ele = driver.findElement(By.xpath("//img[@id='validateCode']"));
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File screenshotLocationq = new File("/Users/liquanliang/Desktop/coinOrder/" + ppNum + "_fullScreenshot.png");
		FileUtils.copyFile(screenshot, screenshotLocationq);

		// Get entire page screenshot
		Thread.sleep(2000);
		BufferedImage fullImg = ImageIO.read(screenshotLocationq); // 读取生成好的整个截图

		// Get the location of element on the page
		org.openqa.selenium.Point point = ele.getLocation();

		// Get width and height of the element
		int eleWidth = ele.getSize().getWidth(); // 300
		int eleHeight = ele.getSize().getHeight(); // 40

		// Crop the entire page screenshot to get only element screenshot
		BufferedImage eleScreenshot = fullImg.getSubimage(point.getX() + 550, point.getY() + 550, eleWidth + eleWidth,
				eleHeight + eleHeight);
		ImageIO.write(eleScreenshot, "png", screenshot);

		// Copy the element screenshot to disk
		File screenshotLocation = new File("/Users/liquanliang/Desktop/coinOrder/" + ppNum + "_test.png");
		FileUtils.copyFile(screenshot, screenshotLocation);

	}

	
	/**
	 * 读取txt文件
	 * @param path
	 * @return
	 */
	public static List<Map<String, String>> readFile(String path) {
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		File file = new File(path);
		BufferedReader reader = null;
		String dateStr = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((dateStr = reader.readLine()) != null) {
				dateStr = replaceBlank(dateStr); // 去掉所有空格，包括首尾、中间
				String[] arr = dateStr.split(";");
				Map<String, String> temp = new HashMap<String, String>();
				temp.put("name", arr[0]);
				temp.put("ppNum", arr[1]);
				temp.put("wantBuyNum", arr[2]);
				temp.put("tel", arr[3]);
				dataList.add(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return dataList;

	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*||\t||\r||\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

}