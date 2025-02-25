package org.example;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected boolean isLoginSuccessful = false;
    protected boolean isSingleLocation = false;
    protected boolean isDashboardLoaded = false;
    protected JSONArray tempPatientData;

    protected  Boolean isAgeInMonth=false;

    protected   List<String> ageLabel =new ArrayList<>();

    protected  Boolean isAgeInYear =false;
    protected List<UserDetails> userDetails = new ArrayList<>();

    @BeforeSuite
    public void setUp() {

        String env = ConfigReader.getProperty("env");
        String baseUrl = ConfigReader.getProperty("url." + env);

        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");

        if (driver == null) {
            driver = new ChromeDriver();
        }
        driver.manage().window().maximize();

        driver.get(baseUrl);
        wait = new WebDriverWait(driver, Duration.ofSeconds(55));

        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(Paths.get("src/test/resources/testing_data_dev.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempPatientData = new JSONArray(jsonData);
//        userDetails.add(new UserDetails("SharmaM", "Admisssssn@123"));
//
//        userDetails.add(new UserDetails("Sharma", "Admisssssn@123"));

        userDetails.add(new UserDetails("scott", "scott"));

//        userDetails.add(new UserDetails("unknownusernamessssssssssssssss", "Admin@123"));

        ageLabel.add("Age In Years And Months");
        ageLabel.add("Age In Years");

    }
    public void threadTimer(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    protected void menuPanelClick(String panel) {
        threadTimer(3000);
        WebElement menuButton = driver.findElement(By.id("mega-menu-nav-btn"));
        if (menuButton.isDisplayed()) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", menuButton);
            System.out.println("Clicked on Menu Button");
        } else {
            System.out.println("Menu Button is not visible, skipping click action.");
        }
        threadTimer(3000);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("page-loader-wrapper")));

        WebElement panelClick = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + panel + "')]"))
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red';", panelClick); // 🔴 Red border highlight
        js.executeScript("arguments[0].scrollIntoView(true);", panelClick);        // 🔄 Scroll into view
        System.out.println("Highlighted Panel: " + panel);


        js.executeScript("arguments[0].style.border='';", panelClick); // ❌ Remove border
        System.out.println("Cleared highlight for Panel: " + panel);

        panelClick.click();
    }
    @AfterSuite
    public void tearDown() {
//        if (driver != null && isLoginSuccessful) {
//            driver.quit();
//        }
    }
}
