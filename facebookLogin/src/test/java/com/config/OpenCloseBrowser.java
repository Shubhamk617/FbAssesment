package com.config;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class OpenCloseBrowser {

	public static WebDriver driver;
	public WebDriverWait wait;
	ReadConfiguration rc = new ReadConfiguration();

	public static ExtentReports extent;
	public static ExtentTest test;

	@BeforeSuite(groups = "Sanity")
	public void beforeSuite() {
		extent = new ExtentReports("ExtentReports//Test_Report.html", true);
		extent.loadConfig(new File("extent-config.xml"));
		extent.addSystemInfo("Environment", "Selenium-Training");
	}

	@BeforeTest
	public void openChrome() {
		System.setProperty("webdriver.chrome.driver", rc.getDriverPath());
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(rc.getImplicitlyWait(), TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, rc.getExplicitWait());

	}

	@BeforeMethod(groups = "Sanity")
	public void beforeMethod(Method method) {
		test = extent.startTest((this.getClass().getSimpleName() + " :: " + method.getName()), method.getName()); 
		test.assignAuthor("Shubham Kumar");
	}
	@AfterMethod
	public static void PublishSnapShots(ITestResult result) throws Exception
	{
		String fn1=result.getMethod().getMethodName();
		String fn=result.getTestClass().getName()+"_"+fn1;
		if(!result.isSuccess())
		{
			System.setProperty("org.uncommons.reportng.escape-output", "false");			
			String FileName=fn+new SimpleDateFormat("dd-MM-yyyy_hh_mm_ss").format(new Date());
			File srcFile=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			String projectPath=System.getProperty("user.dir");
			System.out.println(projectPath);
			String dest=projectPath+"\\SnapShots\\"+FileName+".jpg";
			File destFile=new File(dest) ;
			FileUtils.copyFile(srcFile, destFile);
			String destPath=destFile.getAbsolutePath();
			destPath=destPath.replace('\\', '/');
			destPath="file:///"+destPath;
			String rprt="<Html><Body><p><font color=\"red\">Method " +fn + "   FAILED <a href=\"" +destPath+ "\" >SnapShot</a></p></Body></Html>";
			Reporter.log(rprt);			
			test.log(LogStatus.FAIL, "Test : " +  fn + " - Failed","Snapshot at : " + rprt);			
		}
		else
		{
			Reporter.log("<p><font color=\"green\">Method " +fn + "   PASSED</p>");
			test.log(LogStatus.PASS, "Test Passed : - " + fn);		
		}
	}
	
	@AfterTest
	public void closeChrome() {
		driver.quit();
		extent.endTest(test);
		extent.flush();
	}
	
	@AfterSuite
	public void tearDown(){
		sendPDFReportByGMail("shubhamlist617@gmail.com", "Shubhamk%617%", "shubhamk617@gmail.com", "PDF Report", "");
	}
	

	private static void sendPDFReportByGMail(String from, String pass, String to, String subject, String body) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
        	//Set from address
            message.setFrom(new InternetAddress(from));
             message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
           //Set subject
            message.setSubject(subject);
            message.setText(body);
          
            BodyPart objMessageBodyPart = new MimeBodyPart();
            
            objMessageBodyPart.setText("Please Find The Attached Report File!");
            
            Multipart multipart = new MimeMultipart();

            multipart.addBodyPart(objMessageBodyPart);

            objMessageBodyPart = new MimeBodyPart();

            //Set path to the pdf report file
            String filename = System.getProperty("user.dir")+"\\Default test.pdf"; 
            //Create data source to attach the file in mail
            DataSource source = new FileDataSource(filename);
            
            objMessageBodyPart.setDataHandler(new DataHandler(source));

            objMessageBodyPart.setFileName(filename);

            multipart.addBodyPart(objMessageBodyPart);

            message.setContent(multipart);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }

}
