package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class PharmacyCrud extends LoginAndLocationTest {


    @Test(priority = 3)
    public void menuClick() {
        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy");
        }
    }



    public String generateRandomNumber(String prefix) {


        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    public String generatePhoneNumber() {
        Random random = new Random();
        int firstDigit = 7 + random.nextInt(3); // Ensures first digit is 7, 8, or 9
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append(firstDigit);
        for (int i = 0; i < 9; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    public String generateEmail(String supplierName) {
        Random random = new Random();
        String formattedName = supplierName.toLowerCase().replace(" ", "").replaceAll("[^a-zA-Z0-9]", "");
        String[] domains = {"supplier.com", "traders.net", "business.org"};
        return formattedName + random.nextInt(1000) + "@" + domains[random.nextInt(domains.length)];
    }

    @Test(priority = 4, dependsOnMethods = "menuClick")
    public void addSupplier() {
        verifyPanelName("Pharmacy Master");

        System.out.println("Successfully loaded Pharmacy Master");

        clickElement(By.xpath("//a[@id='Supplier' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        Random random = new Random();
        String supplierName = "Supplier " + random.nextInt(100);
        String phoneNumber = generatePhoneNumber();
        String email = generateEmail(supplierName);

        enterText(By.cssSelector("input[formcontrolname='code']"), generateRandomNumber("SUP"), true);
        enterText(By.cssSelector("input[title='Supplier Name']"), supplierName, true);
        enterText(By.cssSelector("input[formcontrolname='drugLiscense']"), generateRandomNumber("DRUG"), true);
        enterText(By.cssSelector("input[formcontrolname='phoneCode']"), "91", true);
        enterText(By.cssSelector("input[formcontrolname='phoneNumber']"), phoneNumber, true);
        enterText(By.cssSelector("input[formcontrolname='email']"), email, true);
        enterText(By.cssSelector("input[formcontrolname='fax']"), "22389233", true);
        enterText(By.cssSelector("input[formcontrolname='gst']"), "121344", true);
        enterText(By.cssSelector("input[formcontrolname='address']"), "77 west street ", true);
        enterText(By.cssSelector("input[formcontrolname='location']"), "srinivasonnalur kumbakonam", true);
        selectField("state", "Tamil Nadu");
        enterText(By.cssSelector("input[formcontrolname='city']"), "kumbakonam", true);
        enterText(By.cssSelector("input[formcontrolname='zipCode']"), "612204", true);
        enterText(By.cssSelector("input[formcontrolname='website']"), "www.google.com", true);
        enterText(By.cssSelector("input[formcontrolname='pointOfContactName']"), "Supplier M", true);
        enterText(By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"), "91", true);
        enterText(By.cssSelector("input[formcontrolname='pointOfContactNumber']"), generatePhoneNumber(), true);

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));
    }

    private void clickElement(By locator) {

        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");

        }

    }


    private void enterText(By locator, String text, boolean editable) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
    }

    public String generateRondamNumber(String prefix) {

        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }



    private void selectField(String title, String value) {
        WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
        Select select = new Select(titleDropdown);
        select.selectByVisibleText(value);
        threadTimer(500);
    }
}
