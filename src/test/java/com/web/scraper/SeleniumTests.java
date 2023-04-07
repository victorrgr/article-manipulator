package com.web.scraper;

import com.web.scraper.data.repository.ArticleRepository;
import com.web.scraper.operation.ACMLibrary;
import com.web.scraper.utils.UrlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class SeleniumTests {

	private final ChromeOptions chromeOptions;
	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ArticleRepository articleRepository;

	public SeleniumTests() {
		String driverPath = getClass().getResource("/drivers/chromedriver.exe").getPath();
		System.setProperty("webdriver.chrome.driver", driverPath);
		this.chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--remote-debugging-port=0");
		chromeOptions.addArguments("--remote-allow-origins=*");
		chromeOptions.addArguments("--disable-popup-blocking");
		chromeOptions.addArguments("--no-sandbox");
	}

	@Test
	void testUrlEncodingNeed() {
		var afterMonth = "1";
		var beforeMonth = "12";
		var afterYear = "2018";
		var beforeYear = "2023";
		var query = "AllField:(\"integration\")";

		Map<String, Object> params = new HashMap<>();
		params.put("fillQuickSearch", false);
		params.put("target", "advanced");
		params.put("expanded", "dl");
		params.put("AfterMonth", afterMonth);
		params.put("BeforeMonth", beforeMonth);
		params.put("AfterYear", afterYear);
		params.put("BeforeYear", beforeYear);
		params.put("AllField", query);
		params.put("pageSize", 10);
		params.put("startPage", 1);

		var driver = new ChromeDriver(chromeOptions);

		var url = ACMLibrary.DO_SEARCH.getUrl().concat(UrlUtils.asString(params));
		driver.get(url);
	}
}
