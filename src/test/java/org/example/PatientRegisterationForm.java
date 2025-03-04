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
import java.util.concurrent.TimeUnit;

public class PatientRegisterationForm extends LoginAndLocationTest {

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void processtempPatientData() throws IOException, InterruptedException {

        if (isLoginSuccessful) {
            for (int i = 0; i < 1; i++) {


                menuPanelClick("Patient Registration");

                List<WebElement> inputFields = driver.findElements(By.xpath("//input[@formcontrolname]"));
                //first name fill
                fillInputField(driver, wait, "firstName", "Sharma");

                fillInputField(driver, wait, "lastName", "M");

                String title = "D/O";
                selectField("title", title);
                String bloodGroup="A +ve";
                selectField("bloodGroup",bloodGroup);

                if (title != null) {
                    fillInputField(driver, wait, "guardianName", "Intulogic");
                }

                focusScreenScroll("Surname");
                String[] date={"05-05-1994","07-07-1995"};




                System.out.println("loop");
                handleDatePickerInLoop("05-05-1994","patRegDob12");


// Use driver.findElements to see if any open datepicker exists

                List<WebElement> openDatepickers = driver.findElements(By.cssSelector("div.daterangepicker"));
                if (!openDatepickers.isEmpty()) {
                    System.out.println("A datepicker is open.");
                    // Forcefully close the date picker
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('div.daterangepicker').style.display = 'none';");
                } else {
                    System.out.println("No datepicker is open.");
                }
                openDatepickers = driver.findElements(By.cssSelector("div.daterangepicker"));
                if (!openDatepickers.isEmpty()) {
                    System.out.println("A datepicker is open.");
                    // Forcefully close the date picker
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("document.querySelector('div.daterangepicker').style.display = 'none';");
                } else {
                    System.out.println("No datepicker is open.");
                }

                new Actions(driver).moveByOffset(0, 0).click().perform();
                threadTimer(1000); // Wait for 1 second


                if (getAgeFromAgeField() >= 18) {

                    fillInputField(driver, wait, "phoneNumber", "9791310502");
                } else {
                    fillInputField(driver, wait, "parentName", "Murugaiyan");

                    fillInputField(driver, wait, "parentNumber", "9484848485");


                }

                selectRadioButton(driver, wait, "gender", "Male");

                selectRadioButton(driver,wait,"maritalStatus","Married".toLowerCase());

                fillInputField(driver,wait,"address","77 west street srinivasonnalur kumbakonam");
                fillInputField(driver, wait, "email", "sharmamurugaiyan@gmail.com");
                selectSelectDropdown(driver, wait, "cityChange", "Goa");
                //selectFromMatSelectDropdown(driver, wait, "mat-select-0", "Mapusa");

                matSelectDropDown(driver, wait, "city", "Margao");
                matSelectDropDown(driver, wait, "caseType", "F CVT");


                fillInputField(driver,wait,"postalCode","612204");


                fillInputField(driver,wait,"incharge1Name","Intulogic");

                selectField("incharge1Relationship", "Brother" );

                fillInputField(driver,wait,"incharge1Phone","9791310502");

                fillInputField(driver,wait,"incharge1Email","sharmamurugaiyan@gmail.com");

                selectRadioButton(driver,wait,"nri","Indian");

                fillInputField(driver,wait,"aadharNumber","267323633773");
                fillInputField(driver,wait,"knownAllergies","application descripition");
                fillInputField(driver,wait,"previousMedicalIssue","good condition");

                String insuranceSelect="Yes";

                selectRadioButton(driver,wait,"insurance",insuranceSelect);


                focusScreenScroll("Expiry Date");

                if(insuranceSelect.equals("Yes"))
                {
                    //  selectSelectDropdown(driver,wait,"insuranceProviderId"," ABC life insurance private");
                    fillInputField(driver,wait,"insuranceCode","8273827383");
                    handleDatePickerInLoop("10-10-2026","patient-registration2");
                }


            }
// Loop through each input field and print its name
        }
    }

    private void focusScreenScroll(String text) {
        String textToFind = "Your Text Here";  // Replace with the actual text
        WebElement element = driver.findElement(By.xpath("//*[contains(text(), '"+text+"')]"));

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

    // Helper method to convert month number to month name
}