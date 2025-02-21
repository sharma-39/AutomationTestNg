package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class LoginAndLocationTest extends BaseTest {


    @Test(priority = 1)
    public void testLogin() {

        for (int i = 0; i < userDetails.size(); i++) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            WebElement resultElement=null;
            boolean isLoginSuccessful = false; // Track success per user

            for (int attempt = 1; attempt <= 3; attempt++) { // Allow 3 attempts
                System.out.println("Attempt " + attempt + " for user: " + userDetails.get(i).getUserName());

                WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin-email")));
                WebElement passwordField = driver.findElement(By.id("signin-password"));
                WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

                // ✅ Type credentials slowly
                typeSlowly(usernameField, userDetails.get(i).getUserName(), 200);
                typeSlowly(passwordField, userDetails.get(i).getPassword(), 200);
                loginButton.click();




               resultElement = wait.until(driver -> {
                    List<By> locators = Arrays.asList(
                            By.xpath("//p[normalize-space(text())='Select Your Location']"), // ✅ PRIORITY: Success
                            By.xpath("//p[contains(text(),'Your account has been temporarily locked')]"), // 🚫 Locked
                            By.xpath("//p[contains(text(),'Username or Password entered is incorrect')]"), // ❓ Incorrect
                            By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Invalid Username')]") // ❓ Invalid
                    );

                    for (By locator : locators) {
                        List<WebElement> elements = driver.findElements(locator);
                        if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                            System.out.println("Elemenets"+elements.toString());
                            return elements.get(0); // ✅ First visible element
                        }
                    }
                    return null; // ⛔ No match found
                });

                System.out.println("==================result set "+resultElement+""+resultElement.getText().trim()+"================");
                if (resultElement != null) {
                    String resultText = resultElement.getText().trim();

                    // ✅ PRIORITIZE SUCCESS
                    if (resultText.contains("Select Your Location")) {
                        System.out.println("Login Successfully");
                        System.out.println("clearrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                        DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "Login Successfully", "Success");

                        isLoginSuccessful=true;

                        // ✅ Exit loop for this user

                        // 🚫 HANDLE ACCOUNT LOCKED
                    } else if (resultText.contains("temporarily locked")) {
                        System.out.println("Account Locked: " + resultText);
                        DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), resultText, "Locked");
                        break;
                        // ❓ HANDLE INCORRECT CREDENTIALS
                    } else if(resultText.contains("Username or Password entered is incorrect")) {
                        System.out.println("Login failed: " + resultText);
                        DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), resultText, "Failed (Attempt " + attempt + ")");
                    } else if (resultText.contains("Invalid Username")) {

                        DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), resultText, "Failed (Attempt " + attempt + ")");
                        break;
                    }
                    else {
                        System.out.println("Login Successfully");
                        DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "Login Successfully", "Success");
                        isLoginSuccessful=true;
                    }
                } else {
                    System.out.println("No success or error message found.");
                    DBUtil.userNameValidation(userDetails.get(i).getUserName(), userDetails.get(i).getPassword(), "No success or error message", "Unknown");
                }


                // ✅ Exit loop if login was successful
                if (isLoginSuccessful) {
                    break;
                }
            }
        }



    }

    public void clearFieldsWithHighlight(WebElement usernameField, WebElement passwordField, WebElement resultElement) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", usernameField);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", passwordField);
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='2px solid red'", resultElement);
        usernameField.click(); // Refocus for next attempt
    }


    @Test(priority = 2, dependsOnMethods = {"testLogin"})
    public void testLocationSelection() {
        System.out.println("login value"+isLoginSuccessful);

        if (isLoginSuccessful) {
            threadTimer();
            WebElement locationDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@title='Location']")));
            Select select = new Select(locationDropdown);
            select.selectByVisibleText("Navaur branch");

            threadTimer();
            WebElement proceedButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space(text())='Proceed Next']")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", proceedButton);
            proceedButton.click();


            threadTimer();

            WebElement welcomeText = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li/span[contains(text(),'Welcome')]")));
            if(welcomeText.isDisplayed())
            {
                System.out.println("Successfully loaded dashboard");
            }
            else {
                System.out.println("Something wrong");
            }

        }
    }

    protected void threadTimer() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


    public void typeSlowly(WebElement element, String text, int delayMillis) {
        for (char ch : text.toCharArray()) {
            element.sendKeys(String.valueOf(ch));
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
