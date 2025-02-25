package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FacilityConfiguration extends LoginAndLocationTest {



    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void FacilityConfigurateAgeInMonthEnable()
    {
        if(isLoginSuccessful) {
            menuPanelClick("Facility Configurations");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            WebElement ageFormatElement = driver.findElement(By.xpath("//h2[contains(text(), 'Age Format In Bill')]"));

// Scroll the element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ageFormatElement);

            // Locate all radio buttons
            List<WebElement> radioButtons = driver.findElements(By.xpath("//label[contains(@class, 'fancy-radio')]/input[@type='radio']"));

// Iterate over radio buttons and select the desired one
            for (WebElement radioButton : radioButtons) {
                // Find the label text next to the radio button
                WebElement label = radioButton.findElement(By.xpath("./following-sibling::span"));
                String labelText = label.getText().trim();
                System.out.println("Found Radio Button: " + labelText);

                // Choose based on label text
                if (labelText.contains("Age In Years And Months")) { // Change text if needed

                    // Click if not already selected
                    if (!radioButton.isSelected()) {
                        radioButton.click();
                        System.out.println("Selected Radio Button: " + labelText);
                    } else {
                        System.out.println("Radio Button already selected.");
                    }// Exit after selecting desired radio
                } else if (labelText.contains("Age In Years")) {
                    try {
                        Thread.sleep(500); // Wait after scrolling
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Click if not already selected
                    if (!radioButton.isSelected()) {
                        wait.until(ExpectedConditions.elementToBeClickable(radioButton));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
                        System.out.println("Selected Radio Button: " + labelText);
                    } else {
                        System.out.println("Radio Button already selected.");
                    }
                }
            }

        }
    }

//    @Test(priority = 4, dependsOnMethods = {"testLogin"})
//    public void FacilityConfigurateAgeInYearEnable()
//    {
//        if(isLoginSuccessful) {
//        }
//    }

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

}
