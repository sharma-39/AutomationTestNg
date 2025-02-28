package org.example;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class OpBillConfig extends LoginAndLocationTest {

    private static final long THREAD_SECONDS = 3000;
    private static int patientIncrement = 0;
    private PatientFlowHelper patientFlowHelper;
    private String patientCode;
    private boolean isAppointmentCreated = false;
    private boolean isAppointmentCheckedIn = false;

    public OpBillConfig() {
        this.patientFlowHelper = new PatientFlowHelper();
    }

    @Test(priority = 3)
    public void opBillFlow() {
        if (!isLoginSuccessful) {
            Assert.fail("Login failed");
        }

        JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
        for (int i = 50; i <= 50; i++) {
            patientIncrement = i;
            patientCode = patientFlowHelper.patientRegisterTest(this, patient, driver, wait, "Patient Registration");
            System.out.println("Op Bill flow Start to Patient Code:" + patientCode);
            menuPanelClick("Dashboard");

            if (patientCode != null) {
                isAppointmentCreated = patientFlowHelper.createAppointment(this, patient, driver, wait, "Create Appointment", patientCode);
                if (isAppointmentCreated) {
                    threadTimer(3000);
                    isAppointmentCheckedIn = patientFlowHelper.checkingAppointmentTest(this, driver, wait, "View Appointments", patientCode);

                    if (isAppointmentCheckedIn) {
                        menuPanelClick("OP");
                        List<String> status = Arrays.asList("Partially Paid", "Paid");

                        int discount=50;
                        for (int loop = 0; loop < status.size(); loop++) {
                            System.out.println("STATUS:---" + status.get(loop));
                            opbillPayPartitalStatusTOPaid(patientCode, status.get(loop), loop,discount);
                        }
                    }
                }
            }
        }
    }

    private void opbillPayPartitalStatusTOPaid(String patientCode, String statusText, int loop, int discount) {

        threadTimer(3000);

        int totalPages = getPaginationDetails();
        System.out.println("totalPage" + totalPages);
        System.out.println("patient code:=" + patientCode);
        threadTimer(3000);


        if (findRow(patientCode, "View", "Success", totalPages)) {
            if (statusText.equals("Partially Paid")) {
                addBillingDetails(statusText);
                amountTabClick();
                enterAmounts(statusText,discount);
            }
            threadTimer(3000);
            submitBilling();
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

    private void enterAmounts(String statusText, int discount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            fillDiscountAmount("Overall Discount Percentage",discount);
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

    private void submitBilling() {
        clickElement(By.xpath("//label[contains(text(), 'Remarks')]"));
        clickElement(By.xpath("//button[contains(text(), 'Pay Bill')]"));
        threadTimer(3000);
        clickElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]"));
        threadTimer(5000);
        closePrintScreen();
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

    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollToElement(element);
        element.click();
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public Boolean findRow(String patientCode, String title, String status, int totalPages) {

        boolean isFound = false;
        int currentPage = 1;

        while (!isFound && currentPage <= totalPages) {

            // ✅ Re-fetch the table rows after each page change
            List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).getText().contains(patientCode)) {
                    System.out.println("Row Found at Index: " + (i + 1));

                    // ✅ Highlight the row
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].style.backgroundColor = 'yellow'", rows.get(i));

                    System.out.println("Row Highlighted!");
                    isFound = true;
                    WebElement viewButton = rows.get(i).findElement(By.xpath(".//button[@title='View Bill']"));
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
    private void fillDiscountAmount(String label, int discount)
    {
        By discountAmountInput = By.xpath("//tr[th[contains(text(),'"+label+"')]]//input");

        WebElement discountAmountField = driver.findElement(discountAmountInput);

        String currentAmount = discountAmountField.getAttribute("value");
        System.out.println("Current Discount Amount: " + currentAmount);

        discountAmountField.clear();
        discountAmountField.sendKeys(String.valueOf(discount));



    }
}
