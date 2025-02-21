package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ForgetPasswordTest extends BaseTest {


    @Test(priority = 0)
    public void forgetPasswordUrl() {


        for (int i = 0; i < userDetails.size(); i++) {
            WebElement forgotPasswordLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(),'Forgot password?')]")
            ));
            forgotPasswordLink.click();

            WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@placeholder='Username']")
            ));

            usernameField.click(); // Click the input field
            usernameField.clear(); // Clear existing text if any
            usernameField.sendKeys(userDetails.get(i).getUserName()); // Fill with desired text

            WebElement resetButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Reset Password')]")
            ));
            resetButton.click();
            try {
                // Capture Success Message
                WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[contains(text(),'A password reset link has been sent')]")
                ));

                // Highlight in GREEN
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid green';", successMsg);

                String messageText = successMsg.getText();
                System.out.println("Captured Success Message: " + messageText);

                Assert.assertEquals(messageText,
                        "A password reset link has been sent to your email. Please check your inbox and follow the instructions to reset your password.",
                        "Success message mismatch!");

                // Take Success Screenshot
                File successScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File successDestination = new File("D:\\TestingScreenshot\\"+userDetails.get(i).getUserName()+"_"+i+"_highlighted_success_message.png");
                Files.copy(successScreenshot.toPath(), successDestination.toPath());
                System.out.println("Success Screenshot saved at: " + successDestination.getAbsolutePath());

            } catch (TimeoutException e) {
                try {
                    // Capture Failure/Error Message
                    WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//p[contains(text(),'Invalid Username') or contains(text(),'Error')]")
                    ));

                    // Highlight in RED
                    ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red';", errorMsg);

                    String errorText = errorMsg.getText();
                    System.out.println("Captured Error Message: " + errorText);


                    Assert.assertTrue(errorText.contains("Invalid Username"), "Unexpected error message!");

                    // Take Error Screenshot
                    File errorScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    File errorDestination = new File("D:\\TestingScreenshot\\"+userDetails.get(i).getUserName()+i+"_highlighted_error_message.png");
                    Files.copy(errorScreenshot.toPath(), errorDestination.toPath());
                    System.out.println("Error Screenshot saved at: " + errorDestination.getAbsolutePath());

                } catch (TimeoutException | IOException ex) {
                    System.out.println("No success or error message displayed.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }


}
