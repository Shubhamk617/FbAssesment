package com.config;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Reporter;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(JyperionListener.class)
public class facebookLogin extends OpenCloseBrowser {
	ReadConfiguration rc = new ReadConfiguration();

	@Test(priority = 0,description = "Checks facebook login functionality", dataProvider = "facebookLoginData", dataProviderClass = MyDataProviders.class)
	public void fbLoginFunctionality(String uname, String pword) throws InterruptedException {
		driver.navigate().to(rc.getApplicationUrl());
		Reporter.log("facebook opened", true);
		Thread.sleep(3000);
		driver.findElement(By.id("email")).sendKeys(uname);
		Reporter.log("email entered", true);
		driver.findElement(By.id("pass")).sendKeys(pword);
		Reporter.log("password entered", true);
		Thread.sleep(1000);
		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.id("u_0_b"))));
		driver.findElement(By.id("u_0_b")).click();
		Reporter.log("login button clicked", true);

		if (isAlertPresent()) {
			Alert alert = driver.switchTo().alert();
			System.out.println(alert.getText());
			alert.dismiss();
		} else {
			WebElement accSett = driver.findElement(By.xpath("//html[@id='facebook']//div[@id='userNavigationLabel']"));
			wait.until(ExpectedConditions.elementToBeClickable(accSett));
			accSett.click();
			Reporter.log("account Setting clicked", true);
			Thread.sleep(1000);
			driver.findElement(By.xpath(
					"//html[@id='facebook']//div[@id='js_7j']//ul[@role='menu']/li[19]/a[@role='menuitem']//span[@class='_54nh']"))
					.click();
			Reporter.log("Logout Button clicked", true);

		}

		Thread.sleep(1000);

	}

	public boolean isAlertPresent() {
		boolean foundAlert = false;
		try {
			wait.until(ExpectedConditions.alertIsPresent());
			foundAlert = true;
		} catch (TimeoutException eTO) {
			foundAlert = false;
		}
		return foundAlert;
	}

}
