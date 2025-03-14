package org.example;

import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class PatientRegisterToBillGenerate extends LoginAndLocationTest {
    private long THREAD_SECONDS = 3000;
    static int patientIncrement = 0;

    protected String patientCode;

    protected boolean isAppoinmentCreated = false;


    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void processtempPatientData() throws IOException, InterruptedException {

        if (isLoginSuccessful) {
            for (int i = 0; i < tempPatientData.length(); i++) {
                patientIncrement = i;
                patientRegisterTest();
                menuPanelClick("Dashboard", false, "");
                threadTimer(3000);
                createAppointmentTest();

                if (isAppoinmentCreated) {

                    checkingAppointmentTest();
                    addPrescriptionTest();
                    pharmacyBillTest();
                    menuPanelClick("Dashboard", false, "");
                } else {
                    System.out.println("Appoinment Created failed. Retrying..");
                }
            }
        }
    }

    @Test(priority = 4, dependsOnMethods = {"testLogin"})
    public void patientRegisterTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            patientRegister(patient.getString("patientName"), patient.getString("patientAge"), patient.getString("patientPhone"), patient.getString("gender"), "Patient Registration");
        }
    }

    @Test(priority = 5)
    public void createAppointmentTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            createAppointment(patient.getString("patientName"), patient.getString("admissionType"), patient.getString("doctorName"), patient.getString("scanType"), "Create Appointment");
        }
    }

    @Test(priority = 6, dependsOnMethods = {"createAppointmentTest"})
    public void checkingAppointmentTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            checkingAppointment(patient.getString("patientName"), "View Appointments");
        }
    }

    @Test(priority = 7, dependsOnMethods = {"checkingAppointmentTest"})
    public void addPrescriptionTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            addPrescription(patient.getString("patientName"), "Current Admissions");
        }
    }

    @Test(priority = 8, dependsOnMethods = {"addPrescriptionTest"})
    public void pharmacyBillTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            pharmacyBill(patient.getString("patientName"), "Pharmacy");
        }
    }


    private void patientRegister(String name, String age, String phone, String gender, String panel) {

        menuPanelClick(panel, false, "");
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
            selectFromMatSelectDropdown(driver, wait, "mat-select-0", "Chennai");


            patientFormSubmit(driver);

            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(), 'Registered Successfully')]")
            ));

            String messageText = successMessage.getText();
            System.out.println("Fetched Message: " + messageText);

            if (messageText.contains("New Patient")) {
                patientCode = messageText.replace("New Patient", "")
                        .replace("Registered Successfully", "")
                        .trim();

                System.out.println("Extracted Code: " + patientCode);
            } else {
                patientCode = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void patientFormSubmit(WebDriver driver) {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
        submitButton.click();

    }

    private void errorMessageHandle(WebDriver driver) {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'error-msg')]")
        ));

        highlightElement(driver, errorMessage);

        String errorText = errorMessage.getText().trim();
        System.out.println("Error!:" + errorText);
    }


    private void createAppointment(String name, String admissionType, String doctorName, String scanType, String
            panel) {
        menuPanelClick(panel, false, "");


        WebElement patientSearchLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(), 'Patient Search')]")
        ));
        if(patientSearchLabel.getText().contains("Patient Search")) {
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
            WebElement patientCodeInput = wait.until(
                    ExpectedConditions.elementToBeClickable(By.name("patientCode"))
            );


            patientCodeInput.click();

            threadTimer(1000);

            patientCodeInput.sendKeys(Keys.BACK_SPACE);
            threadTimer(500);
            patientCodeInput.sendKeys(patientCode);


            List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));

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


            WebElement purposeDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='purpose']")
            ));

            Select select = new Select(purposeDropdown);
            select.selectByVisibleText(admissionType);


            if (admissionType.equals("Scan")) {
                WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@formcontrolname='scanType']")));
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
        menuPanelClick(panel, false, "");
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr")
        ));
        row.findElement(By.xpath(".//button[@title='Check In']")).click();
    }

    private void addPrescription(String name, String panel) {
        menuPanelClick(panel, false, "");
        try {


            WebElement patientRow = findAndClickDropdownAndPrescription(name, wait);
            if (patientRow != null) {
                System.out.println("Dropdown clicked successfully.");
            } else {
                System.out.println("Patient not found.");
            }

            Thread.sleep(THREAD_SECONDS);
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("current-admission-prescribedAdd")
            ));
            addButton.click();


            WebElement medicineInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@placeholder='Enter Medicine']")
            ));
            medicineInput.sendKeys("Sulphasala");

            Thread.sleep(THREAD_SECONDS); // Adjust if necessary

            WebElement selectedOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//mat-option//span[contains(text(),'Sulphasalazine Tablet  50 50 Tablets')]")
            ));
            selectedOption.click();


            WebElement quantityInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='number' and @title='Quantity']")
            ));

            quantityInput.clear();
            quantityInput.sendKeys("10");


            WebElement saveCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Save & Close')]")
            ));

            saveCloseButton.click();

            System.out.println("Successfully created Prescription");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void pharmacyBill(String name, String panel) {
        menuPanelClick(panel, false, "");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        // Wait for the row containing "SharmaA"
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td/span[contains(text(),'" + patientCode + "')]]")
        ));

        WebElement billButton = row.findElement(By.xpath(".//button[@title='Bill']"));

        wait.until(ExpectedConditions.elementToBeClickable(billButton)).click();


        threadTimer(3000);
        // Wait for the "Generate Bill" button to be present
        WebElement generateBillButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(text(),'Generate Bill')]")
        ));


        wait.until(ExpectedConditions.elementToBeClickable(generateBillButton)).click();

        System.out.println("Generate Bill button clicked successfully.");

        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Pay')]")
        ));

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
        threadTimer(2000);
        //close print screen
//        robot.keyPress(KeyEvent.VK_ESCAPE);
//        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }


    public WebElement findAndClickDropdownAndPrescription(String patientName, WebDriverWait wait) {
        WebElement row = null;

        while (true) {
            try {
                // ✅ Step 1: Find the row containing the patient name
                row = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//td[span[contains(text(),'" +patientCode+ "')]]/parent::tr"))
                ));
                System.out.println("Patient row found.");

                // ✅ Step 2: Find and click the dropdown inside this row
                WebElement dropdownIcon = row.findElement(By.xpath(".//span[contains(@class,'ti-angle-double-down')]"));
                wait.until(ExpectedConditions.elementToBeClickable(dropdownIcon)).click();
                System.out.println("Dropdown icon clicked successfully.");

                // ✅ Step 3: Wait for Prescription option to appear and scroll into view
                WebElement prescriptionOption = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/following-sibling::td//span[contains(text(),'Prescription')]")
                ));

                // ✅ Scroll into view (in case it's hidden)
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", prescriptionOption);
                Thread.sleep(500); // Small delay for UI adjustment

                // ✅ Attempt to click, fallback to JS click if necessary
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(prescriptionOption)).click();
                } catch (ElementClickInterceptedException e) {
                    System.out.println("Click intercepted, using JS click.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prescriptionOption);
                }

                System.out.println("Clicked on Prescription option.");
                return row; // ✅ Return the row after successful action

            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException caught. Retrying...");
            } catch (TimeoutException e) {
                // ✅ Step 4: Handle pagination (if patient not found on current page)
                List<WebElement> nextPageButton = driver.findElements(By.xpath("//li[@class='ng-star-inserted']/a/span[text()='2']"));
                if (!nextPageButton.isEmpty()) {
                    System.out.println("Patient not found, navigating to the next page...");
                    nextPageButton.get(0).click();
                    wait.until(ExpectedConditions.stalenessOf(nextPageButton.get(0))); // Wait for page reload
                } else {
                    System.out.println("Patient not found on any page.");
                    return null; // Exit if no more pages
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private  void fillInputField(WebDriver driver, WebDriverWait wait, String formControlName, String value) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='" + formControlName + "']")
        ));
        inputField.clear();
        inputField.sendKeys(value);
        System.out.println("Filled " + formControlName + " with value: " + value);
    }

    private void selectRadioButton(WebDriver driver, WebDriverWait wait, String formControlName, String value) {
        JavascriptExecutor js1 = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath("//input[@formcontrolname='" + formControlName + "' and @value='" + value + "']"));
        js1.executeScript("arguments[0].click();", element);

    }

    private  void selectFromMatSelectDropdown(WebDriver driver, WebDriverWait wait, String matSelectId, String optionText) {
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("mat-select-0")));
        matSelect.click();

        WebElement cityOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(), 'Chennai')]")
        ));
        cityOption.click();
        System.out.println("Selected option: " + optionText);
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
}
