package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PatientRegisterationForm extends LoginAndLocationTest {

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void processtempPatientData() throws IOException, InterruptedException {

        if (isLoginSuccessful) {


            menuPanelClick("Patient Registration");


            WebElement salutationDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='salutation']")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String salutation = "Mr.";


            js.executeScript("arguments[0].value='" + salutation + "'; arguments[0].dispatchEvent(new Event('change'));", salutationDropdown);
            System.out.println("Salutation 'Mr.' selected using JavaScript.");

            // selectField("salutation","Mr.");
            String firstName = "SharmaMurugaiyan";
            String lastName = "M";
            String title = "D/O";

            String bloodGroup = "A +ve";

            String guardianName = "Intulogic";


            String dob = "05-05-1994";

            fillInputField(driver, wait, "firstName", firstName);

            fillInputField(driver, wait, "lastName", lastName);


            selectField("title", title);

            selectField("bloodGroup", bloodGroup);

            if (title != null) {

                fillInputField(driver, wait, "guardianName", guardianName);
            }

            focusScreenScroll("Surname");

            handleDatePickerInLoop(dob, "patRegDob12");


            if (getAgeFromAgeField() >= 18) {

                String phoneNumber = "9791310502";
                fillInputField(driver, wait, "phoneNumber", phoneNumber);
            } else {
                String parentName = "Murugaiyan";
                fillInputField(driver, wait, "parentName", parentName);

                String parentNumber = "9883834874";
                fillInputField(driver, wait, "parentNumber", parentNumber);


            }

            String gender = "Male";

            String maritalStatus = "Married".toLowerCase();
            selectRadioButton(driver, wait, "gender", gender);

            selectRadioButton(driver, wait, "maritalStatus", maritalStatus);

            String address = "77 west street srinivasonnalur kumbakonam";
            fillInputField(driver, wait, "address", address);
            String emailId = "sharmamurugaiyan@gmail.com";
            fillInputField(driver, wait, "email", emailId);
            String state = "Tamil Nadu";
            selectSelectDropdown(driver, wait, "cityChange", state);
            //selectFromMatSelectDropdown(driver, wait, "mat-select-0", "Mapusa");

            String city = "Chennai";
            matSelectDropDown(driver, wait, "city", city);
            String caseType = "F CVT";
            matSelectDropDown(driver, wait, "caseType", caseType);


            String diagnosis = "fill any diagnonsis";
            String postalCode = "612204";
            fillInputField(driver, wait, "diagnosis", diagnosis);
            fillInputField(driver, wait, "postalCode", postalCode);


            String inchargeName = "Test";
            fillInputField(driver, wait, "incharge1Name", inchargeName);

            String inchargeRelationship="Brother";
            selectField("incharge1Relationship", inchargeRelationship);

            String inchargePhoneNo="9477477478";
            fillInputField(driver, wait, "incharge1Phone", inchargePhoneNo);

            String inchargeEmail="test@gmail.com";
            fillInputField(driver, wait, "incharge1Email", inchargeEmail);

            String citizian="Indian";
            selectRadioButton(driver, wait, "nri", "Indian");
            if(citizian.equals("Indian")) {
                String aadhar = "267323633773";
                fillInputField(driver, wait, "aadharNumber", aadhar);
            }
            String knownAllergies = "application ";
            fillInputField(driver, wait, "knownAllergies", knownAllergies);
            String previousMedicalIssue = "good";
            fillInputField(driver, wait, "previousMedicalIssue", previousMedicalIssue);

            String insuranceSelect = "Yes";

            selectRadioButton(driver, wait, "insurance", insuranceSelect);


            focusScreenScroll("Expiry Date");

            if (insuranceSelect.equals("Yes")) {
                //  selectSelectDropdown(driver,wait,"insuranceProviderId"," ABC life insurance private");
                String insuranceCode = "8273827383";
                fillInputField(driver, wait, "insuranceCode", insuranceCode);
                selectInsuranceId();
                String expiryDateInsurance = "10-10-2025";
                fillExpiryDate(driver, wait, expiryDateInsurance);


            }


            String notes = "testing purpose";
            fillInputField(driver, wait, "notes", notes);


            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
            submitButton.click();


        }
    }

    private void fillExpiryDate(WebDriver driver, WebDriverWait wait, String date) {

        String[] dateFormat = date.split("-");
        String dateText = dateFormat[0]; // Day
        String monthText = dateFormat[1]; // Month (MM)
        String yearText = dateFormat[2]; // Year (YYYY)


        WebElement expiryDateField = driver.findElement(By.id("patient-registration2"));
        expiryDateField.click();

        // Wait for the date picker to be visible
        WebElement dateRangePicker = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.daterangepicker.ltr.auto-apply.single.opensright.show-calendar")
        ));

        // Use JavaScript to highlight the container (optional for debugging)
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='2px solid purple'", dateRangePicker);

        // Locate the left calendar which is displayed
        WebElement leftCalendar = dateRangePicker.findElement(By.cssSelector("div.drp-calendar.left.single"));
        js.executeScript("arguments[0].style.border='2px solid red'", leftCalendar);

        // Locate the month select dropdown inside the calendar header
        WebElement monthSelectElement = leftCalendar.findElement(By.cssSelector("select.monthselect"));

        // Click on the dropdown using JavaScript if normal click fails
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
        // Change to your desired month
        // Locate the year select dropdown inside the calendar header


        System.out.println("part 1");
// **Re-locate the year dropdown after UI refresh**
        WebElement yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        js.executeScript("arguments[0].style.display='block';", yearSelectElement);

        System.out.println("part 1");
        try {
            yearSelectElement.click();
            System.out.println("Clicked year dropdown using normal click.");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", yearSelectElement);
            System.out.println("Clicked year dropdown using JavaScript.");
        }
        System.out.println("part 2");

// Wait for year options to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("select.yearselect option")));

// **Re-locate the year dropdown again**
        yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        Select yearSelect = new Select(yearSelectElement);

        try {
            yearSelect.selectByValue(yearText);
            System.out.println("Year '" + yearText + "' selected.");
        } catch (Exception e) {
            js.executeScript("arguments[0].value='2026'; arguments[0].dispatchEvent(new Event('change'));", yearSelectElement);
            System.out.println("Year '2026' selected using JavaScript.");
        }


        // Define the desired date to select
        String desiredDate = dateText; // Change this to the date you want

// Wait for the date picker to be visible
        WebElement datePicker = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.daterangepicker.ltr.auto-apply.single.opensright.show-calendar")
        ));

// Locate all available dates in the calendar
        List<WebElement> dateElements = datePicker.findElements(By.cssSelector("td.available"));

// Loop through the dates and click the desired one
        boolean dateFound = false;
        for (WebElement dateElement : dateElements) {
            if (dateElement.getText().trim().equals(desiredDate)) {
                dateElement.click();
                System.out.println("Clicked date: " + desiredDate);
                dateFound = true;
                break;
            }
        }

// If the date was not found, print an error
        if (!dateFound) {
            System.out.println("Date not found: " + desiredDate);
        }

    }

    private void focusScreenScroll(String text) {
        String textToFind = "Your Text Here";  // Replace with the actual text
        WebElement element = driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));

// Scroll to the element and bring it into the center of the screen
        ((JavascriptExecutor) driver).executeScript(
                "const rect = arguments[0].getBoundingClientRect();" +
                        "window.scrollBy({top: rect.top - (window.innerHeight / 2), behavior: 'smooth'});",
                element
        );

// Ensure focus is set on the element
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", element);

// Small delay to allow smooth scrolling (optional)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

// Click the element
        element.click();

    }

    private void selectSelectDropdown(WebDriver driver, WebDriverWait wait, String id, String value) {
        WebElement dropDownField = driver.findElement(By.id(id));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + value + "'; arguments[0].dispatchEvent(new Event('change'));", dropDownField);

        System.out.println("Tamil Nadu selected using JavaScript");
    }

    private int getAgeFromAgeField() {
        WebElement ageInput = driver.findElement(By.cssSelector("input[formcontrolname='age']"));

        String ageValue = ageInput.getAttribute("value");

        return Integer.parseInt(ageValue);
    }


    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    private void selectField(String title, String value) {
        WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));

        // Create a Select object
        Select select = new Select(titleDropdown);

        select.selectByVisibleText(value);
        // Select an option by value
        threadTimer(500);
    }

    private void fillInputField(WebDriver driver, WebDriverWait wait, String formControlName, String value) {
        WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@formcontrolname='" + formControlName + "'] | //app-input-text[@formcontrolname='" + formControlName + "']//input | //textarea[@formcontrolname='" + formControlName + "']")
        ));


        inputField.clear();
        inputField.sendKeys(value);
        System.out.println("Filled " + formControlName + " with value: " + value);
    }

    private void selectRadioButton(WebDriver driver, WebDriverWait wait, String formControlName, String value) {
        try {
            // Wait for the radio button to be visible and clickable
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")

            ));

            // Scroll into view before clicking
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);

            // Click using JavaScript to handle hidden elements
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);

            System.out.println("Selected radio button: " + value);
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }


    private void selectFromMatSelectDropdown(WebDriver driver, WebDriverWait wait, String matSelectId, String optionText) {
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id(matSelectId)));
        matSelect.click();

        WebElement cityOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(), '" + optionText + "')]")
        ));
        cityOption.click();
        System.out.println("Selected option: " + optionText);
    }

    private void matSelectDropDown(WebDriver driver, WebDriverWait wait, String formControlName, String optionText) {
        // Open the mat-select dropdown
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-select[@formcontrolname='" + formControlName + "']")
        ));
        matSelect.click();

        // Wait and select the desired option
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option//span[contains(text(), '" + optionText + "')]")
        ));
        option.click();

        System.out.println("Selected option: " + optionText);
    }

    public void handleDatePickerInLoop(String date, String id) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);

        String[] dateFormat = date.split("-");
        String dateText = dateFormat[0]; // Day
        String monthText = dateFormat[1]; // Month (MM)
        String yearText = dateFormat[2]; // Year (YYYY)

        // Step 1: Ensure the date picker is closed
        actions.sendKeys(Keys.ESCAPE).perform();

        // Step 2: Open the date picker
        WebElement datePickerContainer = driver.findElement(By.id(id));
        WebElement calendarIcon = datePickerContainer.findElement(By.cssSelector("i.fa-calendar"));
        calendarIcon.click();

        // Step 3: Select the year
        WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.yearselect")));
        yearDropdown.click();
        Select yearSelect = new Select(yearDropdown);
        yearSelect.selectByValue(yearText);

        // Step 4: Select the month
        WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.monthselect")));
        monthDropdown.click();
        String monthConvertText = convertMMToMonth(monthText);
        WebElement monthOption = monthDropdown.findElement(By.xpath(".//option[text()='" + monthConvertText + "']"));
        monthOption.click();

        // Step 5: Select the day
        WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[text()='" + Integer.parseInt(dateText) + "']")));
        dayElement.click();

        // Step 6: Close the date picker
        actions.sendKeys(Keys.ESCAPE).perform();

        // Step 7: Reset focus
        actions.moveByOffset(0, 0).click().perform();
    }

    public void handleDatePicker(String date, String id) {
        resetDatePicker();
        Actions actions = new Actions(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Parse the date in DD-MM-YYYY format.
        String[] dateParts = date.split("-");
        String dayText = dateParts[0];    // Day
        String monthText = dateParts[1];  // Month (MM)
        String yearText = dateParts[2];   // Year (YYYY)

        try {
            // Step 1: Click the date picker field to open the calendar
            WebElement datePickerContainer = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
            System.out.println("Clicked the date picker field: " + id);

            // Step 2: Find and click the <i> element with class 'fa fa-calendar'
            WebElement calendarIcon = datePickerContainer.findElement(By.xpath(".//i[contains(@class, 'fa-calendar')]"));
            calendarIcon.click();
            System.out.println("Clicked the calendar icon (fa fa-calendar)");


            // Step 3: Find the <span> inside the datePickerContainer
            WebElement spanElement = datePickerContainer.findElement(By.xpath(".//span"));
            System.out.println("Text inside <span>: " + spanElement.getText());

            // Step 4: Select the Year
            WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("select.yearselect")));
            new Select(yearDropdown).selectByValue(yearText);

            // Step 5: Select the Month
            WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("select.monthselect")));
            String monthName = convertMMToMonth(monthText);
            WebElement monthOption = monthDropdown.findElement(By.xpath(".//option[text()='" + monthName + "']"));
            monthOption.click();

            // Step 6: Select the Day
            WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//td[text()='" + Integer.parseInt(dayText) + "']")));
            dayElement.click();

            // Step 7: Close the date picker
            actions.sendKeys(Keys.ESCAPE).perform();
            actions.moveByOffset(0, 0).click().perform();

            System.out.println("Selected date: " + date + " for date picker ID: " + id);

        } catch (StaleElementReferenceException e) {
            System.out.println("Stale element encountered. Retrying...");
            handleDatePicker(date, id); // Retry the operation
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method to convert month number to month name

    private void resetDatePicker() {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ESCAPE).perform(); // Close the date picker if open.
        actions.moveByOffset(0, 0).click().perform(); // Click outside to ensure it's closed.
    }

    private void selectInsuranceId() {// Locate the dropdown element
        WebElement insuranceDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='insuranceProviderId']")));

// Create a Select object
        Select select = new Select(insuranceDropdown);

// Get all available options
        List<WebElement> options = select.getOptions();

// Select a random option (excluding the first one, which is "Select Insurance Provider")
        if (options.size() > 1) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(options.size() - 1) + 1; // Avoid selecting index 0
            select.selectByIndex(randomIndex);
            System.out.println("Selected Insurance Provider: " + options.get(randomIndex).getText());
        } else {
            System.out.println("No available options to select.");
        }
    }
}