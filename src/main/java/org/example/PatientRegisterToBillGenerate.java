package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class PatientRegisterToBillGenerate extends LoginAndLocationTest {
    private long THREAD_SECONDS = 3000;
    static int patientIncrement = 0;


    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void processtempPatientData() throws IOException, InterruptedException {

        for (int i = 2; i < tempPatientData.length(); i++) {
            System.out.println("Template data:-"+tempPatientData.get(i));
            patientIncrement = i;
            patientRegisterTest();
            threadTimer();
            createAppointmentTest();
            threadTimer();
            checkingAppointmentTest();
            threadTimer();
            addPrescriptionTest();
            threadTimer();
            pharmacyBillTest();
            threadTimer();
        }
    }

    @Test(priority = 4, dependsOnMethods = {"testLogin"})
    public void patientRegisterTest() throws IOException {
        if (isLoginSuccessful) {
            JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
            patientRegister(patient.getString("patientName"), patient.getString("patientAge"), patient.getString("patientPhone"), patient.getString("gender"), "Patient Registration");
        }
    }

    @Test(priority = 5, dependsOnMethods = {"patientRegisterTest"})
    public void createAppointmentTest() throws IOException {
        if (isLoginSuccessful) {
            String jsonData = new String(Files.readAllBytes(Paths.get("D:\\TestingData\\testing_data.json")));
            JSONArray tempPatientData = new JSONArray(jsonData);
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

        menuPanelClick(panel);

        List<WebElement> asteriskElements = driver.findElements(By.xpath(
                "//span[contains(@style,'color: red') and text()='*']"
        ));

        System.out.println("Total Fields Marked with Red Asterisk: " + asteriskElements.size());

        // Loop through mandatory fields
        for (WebElement asterisk : asteriskElements) {
            WebElement inputField = null;
            try {
                inputField = asterisk.findElement(By.xpath("./ancestor::label/following-sibling::input | ./parent::div//input | ./parent::div//select | ./parent::div//textarea"));
            } catch (Exception e) {
                System.out.println("No direct input found for this asterisk.");
            }

            if (inputField != null) {
                // Highlight the field
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red'", inputField);

                // Fill values based on field attributes
                String title = inputField.getAttribute("title");
                switch (title) {
                    case "First Name":
                        inputField.sendKeys(name);
                        break;
                    case "Age":
                        inputField.sendKeys(age);
                        break;
                    case "Phone Number":
                        inputField.sendKeys(phone);
                        break;
                    case "Gender":
                        inputField.sendKeys(gender);
                        break;
                    case "State":
                        new Select(inputField).selectByVisibleText("Tamil Nadu"); // Replace with state if needed
                        break;
                    case "City":
                        new Select(inputField).selectByVisibleText("Chennai"); // Replace with city if needed
                        break;
                    default:
                        System.out.println("Field not handled: " + title);
                }
            }
        }

//        List<WebElement> asteriskElements = driver.findElements(By.xpath(
//                "//span[contains(@style,'color: red') and text()='*']"
//        ));
//
//        System.out.println("Total Fields Marked with Red Asterisk: " + asteriskElements.size());
//
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//
//        for (WebElement asterisk : asteriskElements) {
//            WebElement field = null;
//
//            try {
//                // Look for input, select, or radio fields near the asterisk
//                field = asterisk.findElement(By.xpath(
//                        "./ancestor::label/following-sibling::input | " +
//                                "./ancestor::label/following-sibling::select | " +
//                                "./parent::div//input | " +
//                                "./parent::div//select | " +
//                                "./parent::div//input[@type='radio']"
//                ));
//            } catch (Exception e) {
//                System.out.println("No direct field found for this asterisk.");
//            }
//
//            if (field != null) {
//                String tagName = field.getTagName();
//                String fieldType = field.getAttribute("type");
//
//                // Highlight fields with different colors based on type
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
//
//                // Print field details
//                String title = field.getAttribute("title");
//                String formControlName = field.getAttribute("formcontrolname");
//                String placeholder = field.getAttribute("placeholder");
//
//                System.out.println("Mandatory Field -> Title: " + title +
//                        ", FormControlName: " + formControlName +
//                        ", Placeholder: " + placeholder);
//            }
//        }

        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='firstName']")));

        firstNameField.sendKeys(name);


        WebElement ageField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='age']")));

        ageField.sendKeys(age);


        WebElement phoneNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='phoneNumber']")));

        phoneNumberField.sendKeys(phone);


        JavascriptExecutor js1 = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath("//input[@formcontrolname='gender' and @value='" + gender + "']"));
        js1.executeScript("arguments[0].click();", element);


        // Step 1: Click the MatSelect dropdown to open the options
        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("mat-select-0")));
        matSelect.click();

        // Step 2: Wait for the options to be visible
        WebElement cityOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(), 'Chennai')]")
        ));

        // Step 3: Click on the option to select it
        cityOption.click();

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
        submitButton.click();
    }

    private void createAppointment(String name, String admissionType, String doctorName, String scanType, String panel) {
        menuPanelClick(panel);
        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement patientCodeInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("patientCode"))
        );
        patientCodeInput.click();
        try {
            Thread.sleep(1000); // Ensure dropdown gets triggered
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        patientCodeInput.sendKeys(Keys.BACK_SPACE); // Clear previous input
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        patientCodeInput.sendKeys(name);


        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));

        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().contains(name)) {
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
    }

    private void checkingAppointment(String name, String panel) {
        menuPanelClick(panel);
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + name + "')]]/parent::tr")
        ));
        row.findElement(By.xpath(".//button[@title='Check In']")).click();
    }

    private void addPrescription(String name, String panel) {
        menuPanelClick(panel);
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
        menuPanelClick(panel);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        // Wait for the row containing "SharmaA"
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td/span[contains(text(),'" + name + "')]]")
        ));

        WebElement billButton = row.findElement(By.xpath(".//button[@title='Bill']"));

        wait.until(ExpectedConditions.elementToBeClickable(billButton)).click();


        try {
            Thread.sleep(THREAD_SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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

// Scroll to the button (if needed)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payButton);
        try {
            Thread.sleep(THREAD_SECONDS); // Small delay to ensure visibility
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

// Click the button
        payButton.click();

        System.out.println("Pay button clicked successfully.");


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }


        try {
            Thread.sleep(2000); // Wait for print dialog
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Press ESC to cancel print
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }


    public WebElement findAndClickDropdownAndPrescription(String patientName, WebDriverWait wait) {
        WebElement row = null;

        while (true) {
            try {
                // Step 1: Find the row containing the patient name
                row = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//td[span[contains(text(),'" + patientName + "')]]/parent::tr"))
                ));
                System.out.println("Patient row found.");

                // Step 2: Find and click the dropdown inside this row
                WebElement dropdownIcon = row.findElement(By.xpath(".//span[contains(@class,'ti-angle-double-down')]"));
                wait.until(ExpectedConditions.elementToBeClickable(dropdownIcon)).click();
                System.out.println("Dropdown icon clicked successfully.");

                // Step 3: Locate and click "Prescription" inside the same row

                Thread.sleep(1000);
                WebElement prescriptionOption = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//td[span[contains(text(),'" + patientName + "')]]/following-sibling::td//span[contains(text(),'Prescription')]")
                        )
                ));
                wait.until(ExpectedConditions.elementToBeClickable(prescriptionOption)).click();
                System.out.println("Clicked on Prescription option.");

                return row; // Return the WebElement after clicking the dropdown and Prescription

            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException caught. Retrying...");
            } catch (TimeoutException e) {
                // Step 4: If patient row is not found, check if there is a next page button
                List<WebElement> nextPageButton = driver.findElements(By.xpath("//li[@class='ng-star-inserted']/a/span[text()='2']"));
                if (!nextPageButton.isEmpty()) {
                    System.out.println("Patient not found, navigating to the next page...");
                    nextPageButton.get(0).click();
                    wait.until(ExpectedConditions.stalenessOf(nextPageButton.get(0))); // Wait for page to reload
                } else {
                    System.out.println("Patient not found on any page.");
                    return null; // Return null if the patient is not found
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void menuPanelClick(String panel) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement menuButton = driver.findElement(By.id("mega-menu-nav-btn"));
        if (menuButton.isDisplayed()) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", menuButton);
            System.out.println("Clicked on Menu Button");
        } else {
            System.out.println("Menu Button is not visible, skipping click action.");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("page-loader-wrapper")));

        WebElement panelClick = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + panel + "')]"))
        );


        panelClick.click();
    }

}
