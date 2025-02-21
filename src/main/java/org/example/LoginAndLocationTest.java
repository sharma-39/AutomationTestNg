package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;

public class LoginAndLocationTest extends BaseTest {


    @Test(priority = 1)
    public void testLogin() {

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signin-email")));
        WebElement passwordField = driver.findElement(By.id("signin-password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

        typeSlowly(usernameField, userDetails.get(0).getUserName(), 200);
        typeSlowly(passwordField, userDetails.get(0).getPassword(), 200);
        loginButton.click();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement resultElement = null;

        try {
            // Try finding success message
            resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[normalize-space(text())='Select Your Location']")
            ));
            isLoginSuccessful=true;
            System.out.println("Login Successfully");
        } catch (TimeoutException e) {
            System.out.println("No success message found, checking for error...");
        }
        if (resultElement == null) {
            try {
                // Try finding error message
                resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Invalid Username')]")
                ));
                isLoginSuccessful = false;
                System.out.println("Login failed: " + resultElement.getText());
                // Clear fields using JS in case of reload
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", usernameField);
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", passwordField);

                // Highlight error
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='2px solid red'", resultElement);
                usernameField.click();
            } catch (TimeoutException ex) {
                System.out.println("No error message found either.");
            }
        }

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

    private void threadTimer() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
