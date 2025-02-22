package org.example;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

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
    protected boolean isDashboardLoaded = false;
    protected JSONArray tempPatientData;

    protected  Boolean isAgeInMonth=false;

    protected  Boolean getIsAgeInYear=false;
    protected List<UserDetails> userDetails = new ArrayList<>();

    @BeforeClass
    public void setUp() {

        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver-win64\\chromedriver.exe");

        if (driver == null) {
            driver = new ChromeDriver();
        }
        driver.manage().window().maximize();
        driver.get("http://18.215.63.38:8095/#/auth/login");
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));

        String jsonData = null;
        try {
            jsonData = new String(Files.readAllBytes(Paths.get("src/test/resources/patients.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempPatientData = new JSONArray(jsonData);
//        userDetails.add(new UserDetails("SharmaM", "Admisssssn@123"));
//
//        userDetails.add(new UserDetails("Sharma", "Admisssssn@123"));

        userDetails.add(new UserDetails("scott", "scott"));

//        userDetails.add(new UserDetails("unknownusernamessssssssssssssss", "Admin@123"));

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


        panelClick.click();
    }
    @AfterClass
    public void tearDown() {
//        if (driver != null && isLoginSuccessful) {
//            driver.quit();
//        }
    }
}
