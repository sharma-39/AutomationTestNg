package org.example;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfigurateAge extends LoginAndLocationTest {

    StringBuffer stringBuffer = new StringBuffer();
    private long THREAD_SECONDS = 3000;
    static int patientIncrement = 0;

    String patientLabelCaption = null;

    Boolean ageInYearConfig = false;
    Boolean ageInMonthConfig = false;

    static String labelTextAge = null;
    protected String patientCode;

    protected boolean isAppoinmentCreated = false;


    //    @Test(priority = 4, dependsOnMethods = {"testLogin"})
    public void facilityConfigAge() {

        patientLabelCaption=null;
        JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
        if (isLoginSuccessful) {
            menuPanelClick("Facility Configurations");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            WebElement ageFormatElement = driver.findElement(By.xpath("//h2[contains(text(), 'Age Format In Bill')]"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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
                // Select the desired radio button based on labelTextAge
                if (labelText.contains(labelTextAge) && !radioButton.isSelected()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
                    configureSaveButtonClick(); // Save after clicking

                }


            }

        }
    }


    private void configureSaveButtonClick() {
        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Save') and contains(@class, 'saveNdClose')]"));

// Click the button
        saveButton.click();

        System.out.println("Save button clicked successfully.");
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void processtempPatientData() throws IOException, InterruptedException {

        threadTimer(3000);
        System.out.println("Processing automate data logined"+isLoginSuccessful);
        if (isLoginSuccessful) {

            List<String> logSummaryList = new ArrayList<>();
            for (int j = 0; j < ageLabel.size(); j++) {


                StringBuilder logSummary = new StringBuilder();
                labelTextAge = ageLabel.get(j);
                patientCode = null;
                isAgeInMonth = false;
                isAgeInYear = false;
                isAppoinmentCreated = false;
                Thread.sleep(4000);
                facilityConfigAge();


                logSummary.append("✅ Enable: " + labelTextAge + " : ").append("|");

                String log = "";
                for (int i = 11 + j; i <= 11 + j; i++) {
                    patientIncrement = i;
                    namePatientAndAge(ageLabel.get(j));

                    patientRegisterTest();
                    logSummary.append("✅ Patient Registered: ").append(patientCode).append(" | ");

                    menuPanelClick("Dashboard");
                    threadTimer(3000);

                    if (patientCode != null) {
                        createAppointmentTest();
                        logSummary.append("✅ Appointment Created: ").append(patientCode).append(" | ");

                        if (isAppoinmentCreated) {
                            Thread.sleep(3000);
                            checkingAppointmentTest();
                            logSummary.append("✅ Checked In | ");

                            addPrescriptionTest();
                            logSummary.append("✅ Prescription Added | ");

                            pharmacyBillTest();
                            logSummary.append("✅ Pharmacy Bill Paid | ");

                            Thread.sleep(4000);
                            PharmacyView();
                            logSummary.append("✅ Pharmacy Viewed | ");

                            menuPanelClick("Dashboard");
                            logSummary.append("✅ Loop Completed");
                        } else {
                            logSummary.append("❌ Appointment Creation Failed. Retrying...");
                        }
                    } else {
                        logSummary.append("❌ Patient Code is null");
                    }

                    logSummary.append(" | Completed ")
                            .append(isAgeInMonth ? "Age In Month ✅" : "")
                            .append(isAgeInYear ? "Age in Year ✅" : "").toString();

                    DBUtil.insertScenario(logSummary.toString(), "Success");
                }
                logSummaryList.add(log);

            }


            for (int l = 0; l < logSummaryList.size(); l++) {
                System.out.println("Scanerio " + l + "✅" + logSummaryList);
            }
        }
    }

    private void namePatientAndAge(String ageLabel) {

        if (ageLabel.contains("Age In Years And Months")) {
            ageInMonthConfig = true;
            ageInYearConfig = false;

        } else if (ageLabel.contains("Age In Years")) {
            ageInYearConfig = true;
            ageInMonthConfig = false;
        }
        else {
            ageInYearConfig=false;
            ageInMonthConfig=false;
        }
    }

    public void patientRegisterTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            patientRegister(patient.getString("patientName"), patient.getString("patientAge"), patient.getString("patientPhone"), patient.getString("gender"), "Patient Registration");
        }
    }

    public void createAppointmentTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            createAppointment(patient.getString("patientName"), patient.getString("admissionType"), patient.getString("doctorName"), patient.getString("scanType"), "Create Appointment");
        }
    }

    public void checkingAppointmentTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            checkingAppointment(patient.getString("patientName"), "View Appointments");
        }
    }

    public void addPrescriptionTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);

            addPrescription(patient.getString("patientName"), "Current Admissions", patient.getString("prescriptionSearch"), patient.getString("prescriptionSelect"));
        }
    }

    public void pharmacyBillTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            pharmacyBill(patient.getString("patientName"), "Pharmacy");
        }
    }

    public void PharmacyView() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            pharmacyViewBill(patient.getString("patientName"), "Pharmacy", patient.getString("patientAge"), patient.getString("gender"));
        }
    }


    private void patientRegister(String name, String age, String phone, String gender, String panel) {

        menuPanelClick(panel);
        try {

            patientFormSubmit(driver);
            errorMessageHandle(driver);
            fillInputField(driver, wait, "firstName", name);
            patientFormSubmit(driver);
            errorMessageHandle(driver);
            fillInputField(driver, wait, "age", age);
            patientFormSubmit(driver);
            errorMessageHandle(driver);
            fillInputField(driver, wait, "phoneNumber", phone);
            patientFormSubmit(driver);
            errorMessageHandle(driver);
            selectRadioButton(driver, wait, "gender", gender);
            patientFormSubmit(driver);
            errorMessageHandle(driver);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            selectFromMatSelectDropdown(driver, wait, "Select", "Chennai");


            patientFormSubmit(driver);

            //500 error handle and failed backend connection and deployment scerio etc
            String messageText = handleRuntimeError("Patient Registeration");

            if(messageText!=null && !messageText.isEmpty()) {
                System.out.println("Fetched Message: " + messageText);

                if (messageText.contains("New Patient")) {
                    patientCode = messageText.replace("New Patient", "")
                            .replace("Registered Successfully", "")
                            .trim();

                    System.out.println("Extracted Code: " + patientCode);
                } else {
                    patientCode = null;
                }
            }
            else {
                Assert.fail("❌ Failed to Patient Registered");
            }
        } catch (Exception e) {
            patientCode = null;
        }
    }

    private String handleRuntimeError(String type) {
        WebElement resultElement = wait.until(driver -> {
            List<By> locators = Arrays.asList(
                    By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'')]"),
                    By.xpath("//p[normalize-space(text())='Error 403']"),
                    By.xpath("//p[contains(text(),'Error 0')]"),
                    By.xpath("//p[contains(text(),'Error 2')]"),
                    By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Something Went Wrong')]")
            );

            for (By locator : locators) {
                List<WebElement> elements = driver.findElements(locator);
                String messageText = elements.get(0).getText();
                System.out.println("Element Found: " + messageText);

                // Assert failure if it's an error message
                if (messageText.contains("Error 403") || messageText.contains("Error 0") ||
                        messageText.contains("Error 2") || messageText.contains("Something Went Wrong") || messageText.isEmpty()) {
                    Assert.fail("Test failed due to error message: " + messageText);
                }

                return elements.get(0);
            }
            return null;
        });

        String messageText = resultElement.getText();
        if(messageText!=null && !messageText.isEmpty())
        {
            return messageText;
        }
        else{
            Assert.fail("❌ Error"+type);
        }
        return messageText;
    }

    private void patientFormSubmit(WebDriver driver) {
        WebElement submitButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]"))));
        submitButton.click();

    }

    private void errorMessageHandle(WebDriver driver) {
        WebElement errorMessage = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'error-msg')]")
        )));

        highlightElement(driver, errorMessage);

        String errorText = errorMessage.getText().trim();
        System.out.println("Error!:" + errorText);
    }


    private void createAppointment(String name, String admissionType, String doctorName, String scanType, String
            panel) {
        menuPanelClick(panel);

        WebElement patientSearchLabel = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(), 'Patient Search')]")
        )));
        if (patientSearchLabel.getText().contains("Patient Search")) {
            System.out.println("Patient Search label found and loaded.");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

            WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));

            JavascriptExecutor js = (JavascriptExecutor) driver;

// Set the value directly
            js.executeScript("arguments[0].value='byCode';", dropdown1);

// Trigger the change event for Angular/React
            js.executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);

            System.out.println("Selected 'By Code' using JavaScript.");

            System.out.println("Custom dropdown option 'By Code' selected.");
            WebElement patientCodeInput = wait.until(ExpectedConditions.refreshed(
                    ExpectedConditions.elementToBeClickable(By.name("patientCode"))
            ));


            patientCodeInput.click();

            threadTimer(1000);

            patientCodeInput.sendKeys(Keys.BACK_SPACE);
            threadTimer(500);
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


            WebElement purposeDropdown = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='purpose']")
            )));

            Select select = new Select(purposeDropdown);
            select.selectByVisibleText(admissionType);


            if (admissionType.equals("Scan")) {
                WebElement dropdown = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath("//select[@formcontrolname='scanType']"))));
                Select selectScan = new Select(dropdown);
                selectScan.selectByVisibleText(scanType);
            }


            WebElement selectDoctorId = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='doctorId']")
            ));

            Select selectDr = new Select(selectDoctorId);
            selectDr.selectByVisibleText(doctorName);


            WebElement saveButton = driver.findElement(By.id("saveNdCloseAp"));
            saveButton.click();

            // Get all <p> elements inside the container
            // Locate the <p> element containing the message
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));


            WebElement resultElement = wait.until(driver -> {
                List<By> locators = Arrays.asList(
                        By.xpath("//div[contains(@class, 'toast-right-top')]//p[contains(text(), 'Appointment Already Created')]"),
                        By.xpath("//div[contains(@class, 'toast-right-top')]//p[contains(text(), 'Appointment Saved Successfully')]")
                );

                for (By locator : locators) {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        System.out.println("Elemenets" + elements.toString());
                        return elements.get(0);
                    }
                }
                return null;
            });

            if (resultElement != null) {
                String resultText = resultElement.getText().trim();
                if (resultText.contains("Saved Successfully")) {
                    isAppoinmentCreated = true;
                } else {
                    isAppoinmentCreated = false;
                    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement closeButton = driver.findElement(By.id("CloseAp"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeButton);
                    try {
                        Thread.sleep(500); // Small delay after scrolling
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    closeButton.click();
                }

            }

            threadTimer(3000);
        }


    }


    private void checkingAppointment(String name, String panel) {
        menuPanelClick(panel);
        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr")
        )));
        row.findElement(By.xpath(".//button[@title='Check In']")).click();
    }

    private void addPrescription(String name, String panel, String prescriptionSearch, String prescriptionSelect) {
        menuPanelClick(panel);
        try {
            Thread.sleep(3000);

            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String pageText = (String) js.executeScript(
                    "return document.querySelector('li.small-screen')?.textContent.trim();"
            );

            int totalPages = 1;
            if (pageText != null && !pageText.isEmpty()) {
                System.out.println("📄 Pagination Text (via JS): " + pageText);
                String[] pageParts = pageText.split("/");
                int currentPage = Integer.parseInt(pageParts[0].trim());
                totalPages = Integer.parseInt(pageParts[1].trim());
                System.out.println("➡️ Current Page: " + currentPage);
                System.out.println("📊 Total Pages: " + totalPages);
            } else {
                System.out.println("❌ Pagination text still not found using JS.");
            }

            Thread.sleep(3000);
            WebElement patientRow = findAndClickDropdownAndPrescription(patientCode, wait, totalPages);
            if (patientRow != null) {
                System.out.println("Dropdown clicked successfully.");
            } else {
                System.out.println("Patient not found.");
            }

            Thread.sleep(THREAD_SECONDS);


            List<String> search = Arrays.asList(prescriptionSearch.split(","));
            List<String> select = Arrays.asList(prescriptionSelect.split(","));

            for (int i = 0; i < search.size(); i++) {
                Boolean isSelected = false;
                System.out.println("Search item:==" + search.get(i));
                System.out.println("Selected item:==" + select.get(i));
                while (!isSelected) {
                    wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                    WebElement addButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                            By.id("current-admission-prescribedAdd")
                    )));
                    addButton.click();

                    WebElement medicineInput = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                            By.xpath("//input[@placeholder='Enter Medicine']")
                    )));
                    medicineInput.click();
                    medicineInput.clear();
                    medicineInput.sendKeys(search.get(i));

                    Thread.sleep(THREAD_SECONDS); // Give time for autocomplete

                    // Print all available options for debugging
                    List<WebElement> options = driver.findElements(By.xpath("//mat-option//span[@class='mat-option-text']"));
                    System.out.println("" + options.size());
                    if (options.size() != 1) {
                        for (WebElement option : options) {
                            String optionText = option.getText().trim();
                            System.out.println("Found option: " + optionText);

                            // Exact match selection
                            if (optionText.contains(select.get(i))) {
                                wait.until(ExpectedConditions.visibilityOf(option));
                                wait.until(ExpectedConditions.elementToBeClickable(option));
                                // Use JS click
                                js = (JavascriptExecutor) driver;
                                js.executeScript("arguments[0].scrollIntoView(true);", option); // Scroll to the element
                                Thread.sleep(500); // Slight delay to ensure stability
                                js.executeScript("arguments[0].click();", option);
                                System.out.println("✅ Selected via JS Click: " + optionText);
                                isSelected = true;
                                break;
                            }
                        }
                    } else {
                        WebElement option = options.get(0);
                        js = (JavascriptExecutor) driver;
                        js.executeScript("arguments[0].scrollIntoView(true);", option); // Scroll to the element
                        Thread.sleep(500); // Slight delay to ensure stability
                        js.executeScript("arguments[0].click();", option);
                        isSelected = true;
                    }
                }

                if (isSelected) {
                    WebElement quantityInput = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                            By.xpath("//input[@type='number' and @title='Quantity']")
                    )));
                    quantityInput.clear();
                    quantityInput.sendKeys("10");
                }
            }

            WebElement saveCloseButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Save & Close')]")
            )));
            saveCloseButton.click();

            System.out.println("✅ Successfully created Prescription");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pharmacyBill(String name, String panel) {
        menuPanelClick(panel);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        // Wait for the row containing "SharmaA"
        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td/span[contains(text(),'" + patientCode + "')]]")
        )));

        WebElement billButton = row.findElement(By.xpath(".//button[@title='Bill']"));

        wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(billButton))).click();


        threadTimer(3000);
        // Wait for the "Generate Bill" button to be present
        WebElement generateBillButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(text(),'Generate Bill')]")
        )));


        wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(generateBillButton))).click();

        System.out.println("Generate Bill button clicked successfully.");

        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement payButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Pay')]")
        )));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payButton);

        threadTimer(3000);
        payButton.click();

        System.out.println("Pay button clicked successfully.");

        threadTimer(5000);
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        //close print screen
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
        threadTimer(2000);
    }


    public WebElement findAndClickDropdownAndPrescription(String patientName, WebDriverWait wait, int page) {

        boolean isFound = false;

        if (page != 1) {
            WebElement pageNo = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[contains(@class, 'ngx-pagination')]//li/a/span[text()='" + page + "']")
            ));

            // ✅ Scroll into view (in case it's not visible)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageNo);
            try {
                Thread.sleep(500); // Small delay for UI adjustment
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // ✅ Click the Page 3 button
            pageNo.click();
            System.out.println("➡️ Successfully navigated to Page " + pageNo);


            // ✅ Optional: Wait for the content of page 3 to load (Example: wait for a specific element)
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.xpath("//li[contains(@class, 'current')]//span[text()='" + page + "']"),
                    String.valueOf(page)
            ));
        }
        System.out.println("✅ Page  is now active" + page);
        while (!isFound) {
            try {
                Thread.sleep(2000); // Small delay for DOM stabilization

                // ✅ Step 1: Find the row containing the patient code
                WebElement row = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr"))
                ));
                System.out.println("✅ Patient row found: " + patientCode);

                // ✅ Step 2: Find and click the dropdown icon inside the row
                WebElement dropdownIcon = row.findElement(By.xpath(".//span[contains(@class,'ti-angle-double-down')]"));
                wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(dropdownIcon))).click();
                System.out.println("✅ Dropdown icon clicked successfully.");

                // ✅ Step 3: Find the "Prescription" option
                WebElement prescriptionOption = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/following-sibling::td//span[contains(text(),'Prescription')]")
                        )
                ));

                // ✅ Scroll the "Prescription" option into view
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", prescriptionOption);
                Thread.sleep(500); // Small delay for UI adjustment

                // ✅ Step 4: Attempt to click the "Prescription" option
                try {
                    wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(prescriptionOption))).click();
                } catch (ElementClickInterceptedException e) {
                    System.out.println("⚠️ Click intercepted, using JS click.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prescriptionOption);
                }

                System.out.println("✅ Clicked on Prescription option.");
                isFound = true; // ✅ Mark patient as found and exit loop

            } catch (TimeoutException e) {
                System.out.println("🔄 Patient not found on this page. Checking next page...");

                // ✅ Handle pagination if patient not found on current page
                List<WebElement> nextPageButton = driver.findElements(By.xpath("//li[contains(@class, 'pagination-next')]/a"));

                if (!nextPageButton.isEmpty() && nextPageButton.get(0).isDisplayed()) {
                    nextPageButton.get(0).click();
                    System.out.println("➡️ Navigated to next page.");
                    wait.until(ExpectedConditions.stalenessOf(nextPageButton.get(0))); // Wait for page reload
                } else {
                    System.out.println("❌ Patient not found. Reached last page.");
                    break; // Exit if last page is reached
                }

            } catch (StaleElementReferenceException e) {
                System.out.println("⚠️ StaleElementReferenceException caught. Retrying...");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

// ✅ Final status
        if (isFound) {
            System.out.println("🎉 Patient found and Prescription clicked successfully.");
        } else {
            System.out.println("❌ Patient not found in the entire pagination.");
        }

        return null;
    }

    private void fillInputField(WebDriver driver, WebDriverWait wait, String formControlName, String value) {

        WebElement inputField = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='" + formControlName + "']")
        )));
        inputField.clear();
        inputField.sendKeys(value);
        System.out.println("Filled " + formControlName + " with value: " + value);
    }

    private void selectRadioButton(WebDriver driver, WebDriverWait wait, String formControlName, String value) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));
        JavascriptExecutor js1 = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath("//input[@formcontrolname='" + formControlName + "' and @value='" + value + "']"));
        js1.executeScript("arguments[0].click();", element);

    }

    private void selectFromMatSelectDropdown(WebDriver driver, WebDriverWait wait, String matSelectId, String optionText) {

        try {
            // Step 1: Click the dropdown to open it
            WebElement matDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), '" + matSelectId + "')]/ancestor::div[contains(@class, 'mat-select-trigger')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matDropdown);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matDropdown);

            // Step 2: Wait for dropdown options to appear (Angular uses overlay for options)
            WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//mat-option//span[contains(text(), '" + optionText + "')]")
            ));

            // Step 3: Click the desired option
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

            System.out.println("✅ Selected option: " + optionText);

        } catch (TimeoutException e) {
            System.out.println("❌ Failed to select option: " + optionText);
            throw new RuntimeException("Dropdown selection failed", e);
        }
    }


    private void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red';", element);
        System.out.println("Highlighted the error message element.");

        threadTimer(2000);


    }

    public void threadTimer(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void pharmacyViewBill(String patientName, String pharmacy, String age, String gender) {

        System.out.println("Age in Year" + ageInYearConfig + "");
        System.out.println("Age in Month" + ageInYearConfig + "");
        System.out.println("Caption:---" + patientLabelCaption);
        wait = new WebDriverWait(driver, Duration.ofSeconds(65));
        JavascriptExecutor js = (JavascriptExecutor) driver;


        if (ageInMonthConfig) {
            patientLabelCaption = age + "Y | " + gender;
        } else if (ageInYearConfig) {
            patientLabelCaption = age + " | " + gender;
        }

        System.out.println("Updated Patient Label: " + patientLabelCaption);
        try {
            boolean isViewClicked = false;

            // 🟢 Loop until "View" button clicks successfully
            while (!isViewClicked) {
                try {
                    // 🔄 Refresh Patient Row before each retry
                    WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//tr[td/span[contains(text(),'" + patientCode + "')]]")
                    ));

                    WebElement billButton = wait.until(ExpectedConditions.elementToBeClickable(
                            row.findElement(By.xpath(".//button[@title='View']"))
                    ));

                    // ✅ JS Click for reliability
                    js.executeScript("arguments[0].click();", billButton);
                    System.out.println("✅ 'View' button clicked");
                    isViewClicked = true; // Exit loop after success
                    threadTimer(3000); // Small delay after click
                    // 🟢 Proceed to find Age
                    boolean isAgeFound = false;
                    while (!isAgeFound) {
                        try {
                            WebElement ageGenderElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//span[contains(text(), '" + patientLabelCaption + "')]/following-sibling::span[4]")
                            ));

                            // Highlight and scroll
                            js.executeScript("arguments[0].style.border='3px solid green';", ageGenderElement);
                            js.executeScript("arguments[0].scrollIntoView(true);", ageGenderElement);

                            String ageText = ageGenderElement.getText().trim();
                            System.out.println("Check Age: " + ageText);

                            // ✅ Age Check
                            if (ageText.contains("M") || ageText.contains("Y")) {
                                System.out.println(ageText + " → Age In Month ✅");
                                isAgeInMonth = true;
                            } else {
                                System.out.println(ageText + " → Age In Year ✅");
                                isAgeInYear = true;
                            }

                            isAgeFound = true; // Age found, exit loop

                        } catch (StaleElementReferenceException | NoSuchElementException e) {
                            System.out.println("⚠️ Age element not stable, retrying...");
                            Thread.sleep(2000);
                        } catch (TimeoutException te) {
                            System.out.println("⚠️ Timeout on age search, retrying 'View' click...");
                            isViewClicked = false; // Retry "View" if age fails
                            break;
                        }
                    }

                } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
                    System.out.println("⚠️ 'View' click failed, retrying...");
                    Thread.sleep(2000); // Retry after short wait
                }
            }

            // ✅ Close the popup after success
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Close')]")
            ));

            js.executeScript("arguments[0].click();", closeButton);
            System.out.println("✅ Close button clicked via JS");
            threadTimer(3000);

        } catch (Exception ex) {
            System.out.println("❌ Unexpected error: " + ex.getMessage());
        }

    }


}

