package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PharmacyFlow extends LoginAndLocationTest {

    @Test(priority = 3)
    public void testPharmacyFlow() {
        menuPanelClick("Stock", true, "Purchase");
        waitForSeconds(3);
        verifyPanelName("Purchase Management");
        addStock();
    }

    private void addStock() {
        clickElement(By.xpath("//button[contains(text(),'Add Stock')]"));

        selectDropdownByVisibleText(By.xpath("//select[@formcontrolname='supplierId']"), "ABC Pharma Pvt. Ltd.", "1329");

        enterText(By.cssSelector("input[formcontrolname='invoiceNumber']"), "29379273");

        invoiceDate("05-03-2025", "purDate");
        grnDate("06-03-2025", "grnDate");

        enterText(By.xpath("//input[@title='Cash Discount (%)']"), "20");
        enterText(By.xpath("//input[@title='TCS (%)']"), "5");


        clickElement(By.xpath("//div[contains(@class, 'addIcon-button')]"));
        addStockDetails();


    }

    private void addStockDetails() {
        waitForSeconds(3);
     //   findMandatoryFields();

        selectAutoCompleteItem(By.xpath("//input[@formcontrolname='itemName']"),
                "Sevoflurane",
                "Sevoflurane Inhalation 10 Bottle");

        enterText(By.xpath("//input[@title='Batch Number']"), "BATCH12345");

        selectDropdownByValue(By.xpath("//select[@title='Month']"), "03");
        selectDropdownByValue(By.xpath("//select[@title='Year']"), "2030");

        selectDropdownByIndex(By.xpath("//select[@formcontrolname='purchaseUomId']"), 2);

        enterText(By.xpath("//input[@type='number' and @title='Qty Per Package']"), "5");
        enterText(By.xpath("//input[@type='number' and @title='Purchase Quantity']"), "10");
        enterText(By.xpath("//input[@type='number' and @title='MRP']"), "100");
        enterText(By.xpath("//input[@type='number' and @title='Purchase Rate']"), "500");
        enterText(By.xpath("//input[@type='number' and @title='Purchase Discount (%)']"), "10");
        enterText(By.xpath("//input[@type='number' and @title='GST']"), "18");
    }

    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    private void enterText(By locator, String text) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
    }

    private void selectDropdownByVisibleText(By locator, String text, String fallbackValue) {


        WebElement dropdownElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Select dropdown = new Select(dropdownElement);
        wait.until(d -> dropdown.getOptions().size() > 1);

        // Print options for debugging
        List<WebElement> options = dropdown.getOptions();
        for (WebElement option : options) {
            System.out.println("Option found: '" + option.getText() + "'");
        }
        try {
            dropdown.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            dropdown.selectByIndex(2);
        }
    }

    private void selectDropdownByValue(By locator, String value) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(dropdownElement).selectByValue(value);
    }

    private void selectDropdownByIndex(By locator, int index) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(dropdownElement).selectByIndex(index);
    }


    private void selectAutoCompleteItem(By inputLocator, String inputText, String value) {
        boolean optionFound = false;


        while (!optionFound) {
            WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));
            inputField.clear();
            inputField.click();
            inputField.sendKeys(inputText);
            System.out.println("input text" + inputText);
            try {


                threadTimer(1000);

                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-option")));

                // Re-fetch options to avoid StaleElementReferenceException
                List<WebElement> options = driver.findElements(By.xpath("//mat-option"));


                for (WebElement option : options) {
                    if (option.getText().trim().equalsIgnoreCase(value)) {
                        wait.until(ExpectedConditions.elementToBeClickable(option)).click(); // Ensure element is clickable
                        System.out.println("Selected: " + option.getText());
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Entered Item Name: " + inputField.getAttribute("value"));
                if (!inputField.getAttribute("value").equals("")) {
                    optionFound = true;
                }
            }
        }

    }

    private void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void verifyPanelName(String expectedText) {
        WebElement breadcrumb = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//li[@class='breadcrumb-item active breadcrums-data' and normalize-space()='" + expectedText + "']")
        ));
        System.out.println("Breadcrumb found: " + breadcrumb.getText());
    }

    private void findMandatoryFields() {
        List<WebElement> asteriskElements = driver.findElements(By.xpath("//span[contains(@style,'color: red') and text()='*']"));
        System.out.println("Total Fields Marked with Red Asterisk: " + asteriskElements.size());

        for (WebElement asterisk : asteriskElements) {
            WebElement parentElement = asterisk.findElement(By.xpath("./ancestor::*[1]"));
            List<WebElement> inputFields = parentElement.findElements(By.xpath(".//input | .//select"));
            for (WebElement inputField : inputFields) {
                System.out.println("Field: " + inputField.getTagName() +
                        ", Name: " + inputField.getAttribute("name") +
                        ", Placeholder: " + inputField.getAttribute("placeholder"));
            }
        }
    }


    private void grnDate(String date, String id) {
        String[] dateFormat = date.split("-");
        String dateText = dateFormat[0]; // Day
        String monthText = dateFormat[1]; // Month (MM)
        String yearText = dateFormat[2]; // Year (YYYY)

        WebElement expiryDateField = driver.findElement(By.id(id));
        expiryDateField.click();


        // Wait until the date range picker is visible

        // Locate the parent date range picker div
        WebElement dateRangePicker = driver.findElement(By.xpath("//div[@class='daterangepicker ltr auto-apply show-ranges single opensright']"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='2px solid purple'", dateRangePicker);

        WebElement leftCalendar = dateRangePicker.findElement(By.cssSelector("div.drp-calendar.left.single"));
        js.executeScript("arguments[0].style.border='2px solid red'", leftCalendar);

        WebElement monthSelectElement = leftCalendar.findElement(By.cssSelector("select.monthselect"));

        try {
            monthSelectElement.click();
            System.out.println("Clicked month dropdown using normal click.");
            Select monthSelect = new Select(monthSelectElement);
            monthSelect.selectByVisibleText(convertMMToMonth(monthText));
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", monthSelectElement);
            System.out.println("Clicked month dropdown using JavaScript.");
            js.executeScript("arguments[0].value='" + convertMMToMonth(monthText) + "'; arguments[0].dispatchEvent(new Event('change'));", monthSelectElement);
            System.out.println("Month 'Apr' selected using JavaScript.");
        }

        System.out.println("click month");
        threadTimer(2500);

        WebElement yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        js.executeScript("arguments[0].style.display='block';", yearSelectElement);

        try {
            yearSelectElement.click();
            System.out.println("Clicked year dropdown using normal click.");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", yearSelectElement);
            System.out.println("Clicked year dropdown using JavaScript.");
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("select.yearselect option")));

        yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        Select yearSelect = new Select(yearSelectElement);

        try {
            yearSelect.selectByValue(yearText);
            System.out.println("Year '" + yearText + "' selected.");
        } catch (Exception e) {
            js.executeScript("arguments[0].value='2026'; arguments[0].dispatchEvent(new Event('change'));", yearSelectElement);
            System.out.println("Year '" + yearText + "' selected using JavaScript.");
        }

        String desiredDate = String.valueOf(Integer.parseInt(dateText));

        List<WebElement> dateElements = dateRangePicker.findElements(By.cssSelector("td.available"));

        boolean dateFound = false;
        for (WebElement dateElement : dateElements) {
            if (dateElement.getText().trim().equals(desiredDate)) {
                dateElement.click();
                System.out.println("Clicked date: " + desiredDate);
                dateFound = true;
                break;
            }
        }

        if (!dateFound) {
            System.out.println("Date not found: " + desiredDate);
        }

    }


    public void invoiceDate(String date, String id) {
        try {
            String[] dateFormat = date.split("-");
            String dateText = dateFormat[0];
            String monthText = dateFormat[1];
            String yearText = dateFormat[2];

            // Step 1: Close any open datepicker
            //closeExistingDatePicker();

            // Step 2: Open the datepicker using JavaScript (to prevent multiple triggers)
            WebElement dateField = driver.findElement(By.id(id));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateField);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Step 3: Select the year
            WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.yearselect")));
            new Select(yearDropdown).selectByValue(yearText);

            // Step 4: Select the month
            WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.monthselect")));
            new Select(monthDropdown).selectByVisibleText(convertMMToMonth(monthText));

            // Step 5: Select the day
            WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[text()='" + Integer.parseInt(dateText) + "']")));
            dayElement.click();

            // Step 6: Close the datepicker
            closeExistingDatePicker();

        } catch (Exception e) {
            System.err.println("Error in datePicker method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeExistingDatePicker() {
        try {
            List<WebElement> activeDatePickers = driver.findElements(By.cssSelector(".daterangepicker.show-calendar"));
            if (!activeDatePickers.isEmpty()) {
                Actions actions = new Actions(driver);
                actions.sendKeys(Keys.ESCAPE).perform();
                Thread.sleep(500);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
