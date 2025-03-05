package org.example;

import org.json.JSONArray;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected boolean isLoginSuccessful = false;
    protected boolean isSingleLocation = false;
    protected boolean isDashboardLoaded = false;
    protected  JSONArray tempPatientData;
    protected Boolean isAgeInMonth = false;
    protected List<String> ageLabel = new ArrayList<>();
    protected Boolean isAgeInYear = false;
    protected List<UserDetails> userDetails = new ArrayList<>();

    @BeforeClass
    public void setUp() {
        String env = ConfigReader.getProperty("env");
        String baseUrl = ConfigReader.getProperty("url." + env);
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");
//        WebDriverManager.chromedriver().setup();
        // Use WebDriverManager to avoid hardcoded path
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless"); // Run in Jenkins without GUI
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        driver = new ChromeDriver(options);
//
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(55));

        try {
            String filePath = System.getProperty("user.dir") + "/src/test/resources/testing_data_dev.json";
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            tempPatientData = new JSONArray(jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         userDetails = Arrays.asList(
                new UserDetails("incorrectUsername", "correctPassword"), // Username Incorrect
                new UserDetails("FAC-973-support", "incorrectPassword"), // Username and Password Incorrect
                new UserDetails("Sharma", "correctPassword") ,
                new UserDetails("scott", "scott")
                // Account Locked (after multiple attempts)
        );

        ageLabel.add("Age In Years And Months");
        ageLabel.add("Age In Years");
    }

    public void threadTimer(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void menuPanelClick(String panel) {
        wait =new WebDriverWait(driver,Duration.ofSeconds(50));
        threadTimer(3000);
        WebElement menuButton = driver.findElement(By.id("mega-menu-nav-btn"));
        if (menuButton.isDisplayed()) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", menuButton);
        } else {
            System.out.println(" ⚠ Menu Button is not visible, skipping click action.");
        }
        threadTimer(3000);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("page-loader-wrapper")));
        WebElement panelClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + panel + "')]")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red';", panelClick); // Highlight
        js.executeScript("arguments[0].scrollIntoView(true);", panelClick);
        panelClick.click();
        System.out.println("✅ Panel Click :-"+panel);

    }

    @AfterClass
    public void tearDown() {
//
//        driver.quit();
    }
}
