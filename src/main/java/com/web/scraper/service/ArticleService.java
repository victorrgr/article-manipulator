package com.web.scraper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.scraper.data.entity.Article;
import com.web.scraper.data.enums.Library;
import com.web.scraper.data.repository.ArticleRepository;
import com.web.scraper.operation.ACMLibrary;
import com.web.scraper.operation.Mendeley;
import com.web.scraper.utils.UrlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

import static com.web.scraper.utils.Utils.mapper;

@Service
public class ArticleService {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final ChromeOptions chromeOptions;

    public static final String SUCCESS_ENTRY = "Metadata found. Forms were successfully populated";

    @Value("${mendeley.username}")
    private String mendeleyUsername;

    @Value("${mendeley.password}")
    private String mendeleyPassword;

    @Autowired
    private ArticleRepository articleRepository;

    public ArticleService() {
        String driverPath = getClass().getResource("/drivers/chromedriver.exe").getPath();
        System.setProperty("webdriver.chrome.driver", driverPath);
        this.chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-debugging-port=0");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");
    }

    public List<Map<String, Object>> importAcmLibraryArticles(String afterMonth, String beforeMonth, String afterYear, String beforeYear, String query) throws InterruptedException {
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

        List<Map<String, Object>> processes = new ArrayList<>();
        var driver = new ChromeDriver(chromeOptions);

        int startPage = 0;
        boolean stop = false;
        while (!stop) {
            params.put("startPage", startPage);
            startPage++;
            var url = ACMLibrary.DO_SEARCH.getUrl().concat(UrlUtils.asString(params));
            try {
                driver.get(url);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                processes.add(Map.of(
                        "status", "error",
                        "message", "Error getting the URL: " + url,
                        "page", startPage));
                break;
            }

            int constantStartPage = startPage;
            var elements = driver.findElements(By.xpath("/html/body/div[1]/div/main/div[1]/div/div[2]/div/ul/li"));
            if (elements.isEmpty())
                break;
            IntStream.range(0, elements.size()).forEach(count -> {
                var element = elements.get(count);
                logger.info("PAGE >>> " + constantStartPage);
                logger.info("LI   >>> " + (count + 1));
                var article = saveAcmLibraryArticle(element);
                if (article.isPresent()) {
                    processes.add(Map.of(
                            "status", "success",
                            "page", constantStartPage,
                            "elementNumber", (count + 1),
                            "article", article.get()));
                }
                else {
                    Map<String, Object> details = new HashMap<>();
                    details.put("status", "error");
                    details.put("page", constantStartPage);
                    details.put("elementNumber", (count + 1));
                    details.put("html", element.getAttribute("innerHTML"));
                    try {
                        details.put("webElement", mapper.writeValueAsString(element));
                    } catch (JsonProcessingException e) {
                        details.put("webElement", "ERROR PARSING WEB ELEMENT AS JSON");
                    }
                    processes.add(details);
                }
            });
            Thread.sleep(2000);
        }

        driver.quit();
        return processes;
    }

    private Optional<Article> saveAcmLibraryArticle(WebElement element) {
        String title = null;
        try {
            var titleElement = element.findElement(By.xpath("./div[2]/div[2]/div/h5/span/a"));
            title = titleElement.getText();
            logger.info("Title: " + title);
        } catch (Exception e) {
            logger.info("Title: ERROR");
        }

        String abstractt = null;
        try {
            var abstractElement = element.findElement(By.xpath("./div[2]/div[2]/div/div[2]/p"));
            abstractt = abstractElement.getText();
            logger.info("Abstract: " + abstractt);
        } catch (Exception e) {
            logger.info("Abstract: ERROR");
        }

        String doiLink = null;
        try {
            var doiElement = element.findElement(By.xpath("./div[2]/div[2]/div/div[1]/span[2]/a"));
            doiLink = doiElement.getAttribute("href");
            logger.info("DOI: " + doiLink);
        } catch (Exception e) {
            logger.info("DOI: ERROR");
        }

        try {
            var article = articleRepository.save(Article.builder()
                    .title(title)
                    .abstractt(abstractt)
                    .doiLink(doiLink)
                    .library(Library.ACM_LIBRARY)
                    .build());
            logger.info("Article Save: " + article);
            return Optional.of(article);
        } catch (Exception e) {
            logger.info("Article Save: ERROR");
            logger.error(e.getLocalizedMessage(), e);
            return Optional.empty();
        }
    }

    public List<Map<String, Object>> processMendeleyExport(Library library) throws InterruptedException {
        var driver = new ChromeDriver(chromeOptions);
        try {
            signInMendeley(driver);

            try {
                var url = Mendeley.REFERENCE_LIBRARY.getUrl();
                driver.get(url);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                throw new IllegalStateException("Unexpected error opening Mendeley Library");
            }

            try {
                var cookieOption = driver.findElement(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]"));
                cookieOption.click();
            } catch (Exception ignored) {}

            List<Map<String, Object>> processes = new ArrayList<>();

            Thread.sleep(5000);

            var articles = articleRepository.findByLibraryAndExportedIsFalseAndDoiLinkIsNotNull(library);
            for (var article : articles) {
                var addNewButton = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/div[1]/nav/div/div[1]/button"));
                addNewButton.click();

                Thread.sleep(500);
                var menuOption = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/div/div[1]/nav/div/div[1]/div/div/div[2]"));
                menuOption.click();

                Thread.sleep(500);
                var doiField = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div[6]/section/div[2]/div/form/div[1]/div[2]/input"));
                doiField.clear();
                doiField.sendKeys(article.getDoiLink(), Keys.RETURN);

                Thread.sleep(500);

                // Criar um metodo que tenta um certo codigo varias vezes
                var stop2 = false;
                var tries2 = 0;
                while (!stop2) {
                    var messageElement = driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div[6]/section/div[2]/div/form/div[1]/span"));
                    var text = messageElement.getText();
                    if (SUCCESS_ENTRY.equals(text)) {
                        var addEntryButton = driver.findElement(By.xpath("//*[@id=\"manualentry/form/submit\"]"));
                        addEntryButton.click();
                        logger.info("[SUCCESS] ADD ENTRY [CLICK]: " + article);

                        Thread.sleep(1000);
                        article.setExported(true);
                        processes.add(Map.ofEntries(
                                Map.entry("status", "success"),
                                Map.entry("article", articleRepository.save(article))));
                        stop2 = true;
                    } else if (tries2 >= 10) {
                        logger.error("[ERROR] EXPIRED TRIES: " + article);
                        processes.add(Map.ofEntries(
                                Map.entry("status", "error"),
                                Map.entry("message", text),
                                Map.entry("article", article)));
                        try {
                            var cancelButton = driver.findElement(By.xpath("//*[@id=\"manualentry/form/cancel\"]"));
                            cancelButton.click();
                        } catch (Exception e) {}
                        stop2 = true;
                    } else {
                        Thread.sleep(500);
                    }
                    tries2++;
                }
            }

            driver.quit();

            return processes;
        } catch (Exception e) {
            driver.quit();
            throw e;
        }
    }

    private void signInMendeley(ChromeDriver driver) {
        try {
            var url = Mendeley.DO_AUTH.getUrl();
            driver.get(url);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new IllegalStateException("Unexpected error authenticating to Mendeley");
        }

        var emailInput = driver.findElement(By.xpath("//*[@id=\"bdd-email\"]"));
        emailInput.sendKeys(mendeleyUsername, Keys.RETURN);

        var passwordInput = driver.findElement(By.xpath("//*[@id=\"bdd-password\"]"));
        passwordInput.sendKeys(mendeleyPassword, Keys.RETURN);

    }

}
