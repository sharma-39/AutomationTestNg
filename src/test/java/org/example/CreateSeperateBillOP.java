package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class CreateSeperateBillOP extends LoginAndLocationTest {


    @Test(priority = 3)
    public void createBill() {

        if (isLoginSuccessful) {
            menuPanelClick("OP");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            threadTimer(2000);
            WebElement createNewBillButton = driver.findElement(By.id("opBIllCrBtn"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", createNewBillButton);
            threadTimer(1000); // Small wait to ensure it's visible
            createNewBillButton.click();

            WebElement patientSearchLabel = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(text(), 'Patient Search')]")
            )));
            if (patientSearchLabel.getText().contains("Patient Search")) {
                // System.out.println("Patient Search label found and loaded.");
                wait = new WebDriverWait(driver, Duration.ofSeconds(50));

                WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));

                JavascriptExecutor js = (JavascriptExecutor) driver;

// Set the value directly
                js.executeScript("arguments[0].value='byCode';", dropdown1);

// Trigger the change event for Angular/React
                js.executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);


                // System.out.println("Custom dropdown option 'By Code' selected.");
                WebElement patientCodeInput = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.elementToBeClickable(By.name("patientCode"))
                ));


                patientCodeInput.click();

                threadTimer(1000);

                patientCodeInput.sendKeys(Keys.BACK_SPACE);
                threadTimer(500);
                String patientCode = "SWI-2-910-P-2025";
                patientCodeInput.sendKeys(patientCode);

                List<WebElement> options = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option"))));

                boolean found = false;
                for (WebElement option : options) {
                    if (option.getText().contains(patientCode)) {
                        option.click();
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("Patient name not found in dropdown.");
                }


                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement doctorDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.className("form-control")));

// Click the dropdown to open options
                doctorDropdown.click();

// Locate the desired option and click it
                WebElement doctorOption = driver.findElement(By.xpath("//option[contains(text(), 'Dr.Vishnu V')]"));
                doctorOption.click();

                List<String> status = Arrays.asList("Partially Paid", "Paid");

                addBillingDetails("Partially Paid");
                amountTabClick();
                enterAmounts("Partially Paid", 0);
                threadTimer(3000);

                submitBilling();
                closePrintScreen();
                threadTimer(2000);
                int totalPages = getPaginationDetails();

                if (findRow(patientCode, "View Bill", "Partially Paid".toUpperCase(), totalPages)) {
                    threadTimer(3000);
                    submitBilling();
                    closePrintScreen();
                }


            }
        }

    }

    private int getPaginationDetails() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String pageText = (String) js.executeScript("return document.querySelector('li.small-screen')?.textContent.trim();");

        if (pageText != null && !pageText.isEmpty()) {
            String[] pageParts = pageText.split("/");
            return Integer.parseInt(pageParts[1].trim());
        }
        return 1;
    }

    public Boolean findRow(String patientCode, String title, String status, int totalPages) {


        boolean isFound = false;
        int currentPage = 1;

        while (!isFound && currentPage <= totalPages) {

            // ✅ Re-fetch the table rows after each page change
            List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));
            for (int i = 0; i < rows.size(); i++) {
                String rowText = rows.get(i).getText();
                if (rowText.contains(patientCode) && rowText.contains(status)) {
                    System.out.println("Row Found at Index: " + (i + 1));

                    // ✅ Highlight the row
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].style.backgroundColor = 'yellow'", rows.get(i));

                    System.out.println("Row Highlighted!");
                    isFound = true;
                    WebElement viewButton = rows.get(i).findElement(By.xpath(".//button[@title='" + title + "']"));
                    scrollToElement(viewButton);
                    viewButton.click();
                    break;
                }
            }

            if (!isFound && currentPage < totalPages) {
                try {
                    currentPage++; // Increment before clicking

                    WebElement pageNo = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//ul[contains(@class, 'ngx-pagination')]//li/a/span[text()='" + currentPage + "']")
                    ));

                    // ✅ Scroll into view to ensure it's visible
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageNo);
                    Thread.sleep(500); // Small delay for UI adjustment

                    // ✅ Click the next page button
                    pageNo.click();
                    Thread.sleep(3000); // Allow time for the new page to load

                } catch (Exception e) {
                    System.out.println("Pagination button not found or not clickable.");
                    break;
                }
            }
        }

        if (!isFound) {
            System.out.println("Patient Code not found in any pages.");
            return false;
        } else {
            return true;
        }
    }

    private void submitBilling() {
        clickElement(By.xpath("//label[contains(text(), 'Remarks')]"));
        clickElement(By.xpath("//button[contains(text(), 'Pay Bill')]"));
        threadTimer(3000);
        clickElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]"));
        threadTimer(5000);
        closePrintScreen();
    }


    private void addBillingDetails(String statusText) {
        List<String> optionTexts = Arrays.asList("Consultation Charge", "Doctor Fees 5");

// First, click "Add New" to enable the dropdown if needed
        WebElement addNewButton = driver.findElement(By.xpath("//div[contains(@class, 'addIcon-button')]/span[text()='Add New']"));
        addNewButton.click();
        threadTimer(3000); // Wait for UI update

        for (String optionText : optionTexts) {
            // Find the dropdown
            WebElement dropdown = driver.findElement(By.xpath("//mat-select//span[contains(text(), 'Select')]"));

            // Check if the dropdown is disabled
            if (!dropdown.isEnabled()) {
                System.out.println("Dropdown is disabled, clicking 'Add New' to enable it...");
                addNewButton.click(); // Click Add New again to enable
                threadTimer(2000); // Wait for UI update
            }

            // Click the dropdown
            dropdown.click();
            threadTimer(2000); // Wait for dropdown options to appear

            // Select the option from the dropdown
            selectDropdownOption(optionText);

            // Click "Add New" again to add the selected option
            addNewButton.click();
            threadTimer(3000); // Wait for UI update before selecting the next option
        }

    }

    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollToElement(element);
        element.click();
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void selectDropdownOption(String optionText) {
        List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-option//span")));
        for (WebElement option : options) {
            if (option.getText().trim().equals(optionText)) {
                scrollToElement(option);
                threadTimer(500);
                option.click();
                threadTimer(1000);
                break;
            }
        }
    }

    private void amountTabClick() {
        // ✅ Locate the table using class name or other attributes
        WebElement table = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table[contains(@class, 'hm-p table-disable-hover')]")
        ));

// ✅ Scroll into view before clicking (if needed)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table);

// ✅ Click the table
        table.click();
    }


    private void enterAmounts(String statusText, int discount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            fillDiscountAmount("Overall Discount Percentage", discount);
            // ✅ Get all table rows inside tbodyIn2
            List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//tbody[@id='tbodyIn2']/tr")
            ));

            WebElement finalAmountElement = driver.findElement(By.xpath("//tr[th[contains(text(), 'Final Amount')]]/td"));
            String finalAmount = finalAmountElement.getText();
            String amountOnly = finalAmount.replaceAll("[^0-9]", "");
            System.out.println("Final Amount: " + finalAmount);
            WebElement finalTotalPaidElement = driver.findElement(By.xpath("//tr[th[contains(text(), 'Total Paid Amount')]]/td"));
            String totalPaidAmount = finalTotalPaidElement.getText();
            String amountTotalOnly = totalPaidAmount.replaceAll("[^0-9]", "");

            System.out.println("Total rows found: " + rows.size());

            for (int i = 0; i < rows.size(); i++) {
                try {
                    // ✅ Re-fetch rows each iteration to prevent stale elements
                    rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tbody[@id='tbodyIn2']/tr")));
                    WebElement row = rows.get(i);

                    // ✅ Locate the "Amount" input field inside the refreshed row
                    WebElement amountInput = row.findElement(By.xpath(".//td[contains(@class, 'text-right')]//input[@type='number']"));

                    System.out.println("old Amount" + amountInput.getAttribute("v"));
                    if (amountInput.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", amountInput);
                        if (statusText.equals("Partially Paid")) {
                            amountInput.clear();
                            amountInput.sendKeys(String.valueOf(Math.round(Integer.parseInt(amountOnly) / 2)));
                        } else {
                            amountInput.clear();
                            amountInput.sendKeys(String.valueOf(Math.round(Float.parseFloat(amountOnly) - Float.parseFloat(amountTotalOnly))));
                        }
                        System.out.println("✅ Filled Amount Field with 500 in Row " + (i + 1));
                    }

                } catch (NoSuchElementException e) {
                    System.out.println("❌ No Amount Field Found in Row " + (i + 1));
                } catch (StaleElementReferenceException e) {
                    System.out.println("🔄 Stale element detected. Retrying Row " + (i + 1));
                    i--; // Retry the same row
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void fillDiscountAmount(String label, int discount) {
        By discountAmountInput = By.xpath("//tr[th[contains(text(),'" + label + "')]]//input");

        WebElement discountAmountField = driver.findElement(discountAmountInput);

        String currentAmount = discountAmountField.getAttribute("value");
        System.out.println("Current Discount Amount: " + currentAmount);

        discountAmountField.clear();
        discountAmountField.sendKeys(String.valueOf(discount));
    }

    private void closePrintScreen() {

        try {
            Robot robot = new Robot();
            robot.delay(1000); // Wait before sending key
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
            threadTimer(4000);
        } catch (AWTException ignored) {
            ignored.printStackTrace();
        }
    }
}

