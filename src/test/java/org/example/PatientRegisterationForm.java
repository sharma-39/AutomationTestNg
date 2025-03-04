package org.example;

import org.json.JSONObject;
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
import java.util.*;

public class PatientRegisterationForm extends LoginAndLocationTest {

    private JSONObject patientData;
    Map<String, Boolean> mandatoryFieldsMap = new HashMap<>();



    public PatientRegisterationForm() {
        // Initialize the JSON object with patient data
        String jsonData = "{ \"salutation\": \"Mr.\", \"firstName\": \"SharmaMurugaiyan\", \"lastName\": \"M\", \"title\": \"D/O\", \"bloodGroup\": \"A +ve\", \"guardianName\": \"Intulogic\", \"dob\": \"05-05-1994\", \"phoneNumber\": \"9791310502\", \"parentName\": \"Murugaiyan\", \"parentNumber\": \"9883834874\", \"gender\": \"Male\", \"maritalStatus\": \"Married\", \"address\": \"77 west street srinivasonnalur kumbakonam\", \"email\": \"sharmamurugaiyan@gmail.com\", \"state\": \"Tamil Nadu\", \"city\": \"Chennai\", \"caseType\": \"F CVT\", \"diagnosis\": \"fill any diagnonsis\", \"postalCode\": \"612204\", \"inchargeName\": \"Test\", \"inchargeRelationship\": \"Brother\", \"inchargePhone\": \"9477477478\", \"inchargeEmail\": \"test@gmail.com\", \"citizian\": \"Indian\", \"aadharNumber\": \"267323633773\", \"knownAllergies\": \"application\", \"previousMedicalIssue\": \"good\", \"insuranceSelect\": \"Yes\", \"insuranceCode\": \"8273827383\", \"expiryDateInsurance\": \"10-10-2025\", \"notes\": \"testing purpose\" }";
        patientData = new JSONObject(jsonData);
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void processtempPatientData() throws IOException, InterruptedException {
        if (isLoginSuccessful) {

            menuPanelClick("Patient Registration");
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
            submitButton.click();
            findMantatoryFields();
            fillFormWithPatientData();

            System.out.println("\nMandatory Fields Stored in HashMap:");
            for (Map.Entry<String, Boolean> entry : mandatoryFieldsMap.entrySet()) {
                System.out.println("FormControlName: " + entry.getKey() + " required:- " + entry.getValue());
            }
        }
    }

    private void fillFormWithPatientData() throws InterruptedException {



        saluationInputJquery(patientData.getString("salutation"));
        fillInputField("firstName", patientData.getString("firstName"),   mandatoryFieldsMap.containsKey("firstName") ? mandatoryFieldsMap.get("firstName") :false);
        fillInputField("lastName", patientData.getString("lastName"), mandatoryFieldsMap.containsKey("lastName")  );
        selectField("title", patientData.getString("title"));
        selectField("bloodGroup", patientData.getString("bloodGroup"));

        if (patientData.has("guardianName")) {
            fillInputField("guardianName", patientData.getString("guardianName"), true);
        }

        handleDatePickerInLoop(patientData.getString("dob"), "patRegDob12");

        if (getAgeFromAgeField() >= 18) {
            fillInputField("phoneNumber", patientData.getString("phoneNumber"), true);
        } else {
            fillInputField("parentName", patientData.getString("parentName"), true);
            fillInputField("parentNumber", patientData.getString("parentNumber"), true);
        }

        selectRadioButton("gender", patientData.getString("gender"));
        selectRadioButton("maritalStatus", patientData.getString("maritalStatus").toLowerCase());
        fillInputField("address", patientData.getString("address"), true);
        fillInputField("email", patientData.getString("email"), true);
        selectSelectDropdown("cityChange", patientData.getString("state"));
        matSelectDropDown("city", patientData.getString("city"));
        matSelectDropDown("caseType", patientData.getString("caseType"));
        fillInputField("diagnosis", patientData.getString("diagnosis"), true);
        fillInputField("postalCode", patientData.getString("postalCode"), true);
        fillInputField("incharge1Name", patientData.getString("inchargeName"), true);
        selectField("incharge1Relationship", patientData.getString("inchargeRelationship"));
        fillInputField("incharge1Phone", patientData.getString("inchargePhone"), true);
        fillInputField("incharge1Email", patientData.getString("inchargeEmail"), true);
        selectRadioButton("nri", patientData.getString("citizian"));

        if (patientData.getString("citizian").equals("Indian")) {
            fillInputField("aadharNumber", patientData.getString("aadharNumber"), true);
        }

        fillInputField("knownAllergies", patientData.getString("knownAllergies"), true);
        fillInputField("previousMedicalIssue", patientData.getString("previousMedicalIssue"), true);
        selectRadioButton("insurance", patientData.getString("insuranceSelect"));

        if (patientData.getString("insuranceSelect").equals("Yes")) {
            fillInputField("insuranceCode", patientData.getString("insuranceCode"), true);
            selectInsuranceId();
            fillExpiryDate(patientData.getString("expiryDateInsurance"));
        }

        fillInputField("notes", patientData.getString("notes"), true);
    }

    private void findMantatoryFields() {
        // Find all labels containing an asterisk

        List<WebElement> asteriskElements = driver.findElements(By.xpath(
                "//span[contains(@style,'color: red') and text()='*']"
        ));

        System.out.println("Total Fields Marked with Red Asterisk: " + asteriskElements.size());

        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (WebElement asterisk : asteriskElements) {
            WebElement field = null;

            try {
                // Look for input, select, or radio fields near the asterisk
                field = asterisk.findElement(By.xpath(
                        "./ancestor::label/following-sibling::input | " +
                                "./ancestor::label/following-sibling::select | " +
                                "./parent::div//input | " +
                                "./parent::div//select | " +
                                "./parent::div//input[@type='radio']"
                ));
            } catch (Exception e) {
                System.out.println("No direct field found for this asterisk.");
            }

            if (field != null) {
//                String tagName = field.getTagName();
//                String fieldType = field.getAttribute("type");

                // Highlight fields with different colors based on type
//                if ("select".equals(tagName)) {
//                    js.executeScript("arguments[0].style.border='3px solid blue'", field); // Dropdown
//                    System.out.println("Highlighted SELECT field.");
//                } else if ("radio".equals(fieldType)) {
//                    js.executeScript("arguments[0].style.outline='3px solid green'", field); // Radio button
//                    System.out.println("Highlighted RADIO button.");
//                } else {
//                    js.executeScript("arguments[0].style.border='3px solid red'", field); // Text Input
//                    System.out.println("Highlighted INPUT field.");
//                }

                // Print field details
                String title = field.getAttribute("title");
                String formControlName = field.getAttribute("formcontrolname");
                String placeholder = field.getAttribute("placeholder");

                if (formControlName != null && !formControlName.isEmpty()) {
                    mandatoryFieldsMap.put(formControlName, true);
                }


            }
        }
    }

    // Helper methods (unchanged from original code)
    private void fillInputField(String formControlName, String value, boolean mantatory) {

        if (mantatory) {
            WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'] | //app-input-text[@formcontrolname='" + formControlName + "']//input | //textarea[@formcontrolname='" + formControlName + "']")
            ));
            inputField.clear();
            inputField.sendKeys(value);
            System.out.println("Filled " + formControlName + " with value: " + value);
        }
    }

    private void selectField(String title, String value) {
        WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
        Select select = new Select(titleDropdown);
        select.selectByVisibleText(value);
        threadTimer(500);
    }

    private void selectRadioButton(String formControlName, String value) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private void selectSelectDropdown(String id, String value) {
        WebElement dropDownField = driver.findElement(By.id(id));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + value + "'; arguments[0].dispatchEvent(new Event('change'));", dropDownField);
        System.out.println(value + " selected using JavaScript");
    }

    private void matSelectDropDown(String formControlName, String optionText) {
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-select[@formcontrolname='" + formControlName + "']")
        ));
        matSelect.click();
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


    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }


    private int getAgeFromAgeField() {
        WebElement ageInput = driver.findElement(By.cssSelector("input[formcontrolname='age']"));
        String ageValue = ageInput.getAttribute("value");
        return Integer.parseInt(ageValue);
    }

    private void selectInsuranceId() {
        WebElement insuranceDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='insuranceProviderId']")));
        Select select = new Select(insuranceDropdown);
        List<WebElement> options = select.getOptions();
        if (options.size() > 1) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(options.size() - 1) + 1;
            select.selectByIndex(randomIndex);
            System.out.println("Selected Insurance Provider: " + options.get(randomIndex).getText());
        } else {
            System.out.println("No available options to select.");
        }
    }

    private void fillExpiryDate(String date) {

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


// **Re-locate the year dropdown after UI refresh**
        WebElement yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        js.executeScript("arguments[0].style.display='block';", yearSelectElement);

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

    private void resetDatePicker() {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ESCAPE).perform(); // Close the date picker if open.
        actions.moveByOffset(0, 0).click().perform(); // Click outside to ensure it's closed.
    }

    private void saluationInputJquery(String salutation) {
        WebElement salutationDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='salutation']")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + salutation + "'; arguments[0].dispatchEvent(new Event('change'));", salutationDropdown);
        System.out.println("Salutation 'Mr.' selected using JavaScript.");
    }

    private void errorMessageHandle(WebDriver driver, WebDriverWait wait) {
        WebElement errorMessage = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'error-msg')]")
        )));

        highlightElement(driver, errorMessage);

        String errorText = errorMessage.getText().trim();
        System.out.println("Error!:" + errorText);
    }

    private void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red';", element);
        System.out.println("Highlighted the error message element.");

        threadTimer(2000);


    }


}