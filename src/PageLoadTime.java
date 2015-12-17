import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import edu.umass.cs.benchlab.har.HarBrowser;
import edu.umass.cs.benchlab.har.HarEntries;
import edu.umass.cs.benchlab.har.HarEntry;
import edu.umass.cs.benchlab.har.HarLog;
import edu.umass.cs.benchlab.har.HarPage;
import edu.umass.cs.benchlab.har.HarPageTimings;
import edu.umass.cs.benchlab.har.ISO8601DateFormatter;
import edu.umass.cs.benchlab.har.tools.HarFileReader;

public class PageLoadTime {
	
//	// for testing purposes !
//	public static void main(String[] args) {
//		getPageLoadTime("http://www.yahoo.com");
//	}
	
	// mainly used from the following link:
	// http://www.softwareishard.com/blog/firebug/automate-page-load-performance-testing-with-firebug-and-selenium/
	public static long getPageLoadTime(String URL) {
		FirefoxProfile profile = new FirefoxProfile();

		// Using firefox 42 (the most recent one)
		// Using selenium 2.48.2
        File firebug = new File("firebug-2.0.13b1.xpi");
        File netExport = new File("netExport-0.9b7.xpi");

        try
        {
            profile.addExtension(firebug);
            profile.addExtension(netExport);
        }
        catch (IOException err)
        {
            System.out.println(err);
        }

        // Set default Firefox preferences
        profile.setPreference("app.update.enabled", false);

        String domain = "extensions.firebug.";

        // Set default Firebug preferences
        profile.setPreference(domain + "currentVersion", "2.0");
        profile.setPreference(domain + "allPagesActivation", "on");
        profile.setPreference(domain + "defaultPanelName", "net");
        profile.setPreference(domain + "net.enableSites", true);

        // Set default NetExport preferences
        profile.setPreference(domain + "netexport.alwaysEnableAutoExport", true);
        profile.setPreference(domain + "netexport.showPreview", false);
        profile.setPreference(domain + "netexport.defaultLogDir", "/Users/FM/Desktop/HAR");

        
        WebDriver driver = new FirefoxDriver(profile);
        File HARDirectory = new File("/Users/FM/Desktop/HAR");
        File HARFile = null;
        long pageLaadTime = 0;
        try
        {
        	
            // Wait till Firebug is loaded
            Thread.sleep(5000);

            // Load test page
            driver.get(URL); // "http://www.janodvarko.cz"
            
            
            // Wait till the Har file is generated !
            while (HARDirectory.listFiles().length == 1) {
            	Thread.sleep(1000);
            } 
            
        	File[] files = HARDirectory.listFiles();
        	if (files[0].getName().equals(".DS_Store")) {
        		HARFile = files[1];
        	} else {
        		HARFile = files[0];
        	}
            
            
        }
        catch (InterruptedException err)
        {
            System.out.println(err);
        }

        driver.quit();
        
        //Guide Link: https://sites.google.com/site/frogthinkerorg/projects/harlib
        //Download link: http://sourceforge.net/projects/benchlab/files/Archives/HarLib/1.1.2/
        File f = new File(HARFile.getAbsolutePath());
        HarFileReader r = new HarFileReader();
        try
        {
          System.out.println("Reading " + HARFile.getName());
          HarLog log = r.readHarFile(f);

          // Access all elements as objects
          
          List<HarPage> pages = log.getPages().getPages();
//          HarPages pages = log.getPages();
          long loadTime = 0;
          HarEntries entries = log.getEntries();
          List<HarEntry> entryList = entries.getEntries();
          for (HarEntry entry : entryList)
          {
              long entryLoadTime = entry.getStartedDateTime().getTime() + entry.getTime();
              if(entryLoadTime > loadTime){
                  loadTime = entryLoadTime;
              }
          }
          
          HarPage page = pages.get(0);
          long startTime = page.getStartedDateTime().getTime();
          pageLaadTime = loadTime - startTime;
        		  
          System.out.println("For page: " + page.getTitle() + ", the load time is " + pageLaadTime);


        } catch (JsonParseException e) {
            e.printStackTrace();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        
        
        // deleting the file inside the HAR folder to prepare it for the next possible experiment
        HARFile.delete();
        
        return pageLaadTime;
        
	}
}
