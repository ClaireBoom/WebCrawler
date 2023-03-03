import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
    public static List<String[]> fetchList;

	public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "C:/Users/clair/OneDrive/Documents/USC/CSCI 572 Search Engines/WebCrawler";
        int numberOfCrawlers = 7;
        
        String fetchPath = "C:/Users/clair/OneDrive/Documents/USC/CSCI 572 Search Engines/WebCrawler/fetch_LATimes.csv";
        String downloadPath = "C:/Users/clair/OneDrive/Documents/USC/CSCI 572 Search Engines/WebCrawler/visit_LATimes.csv";
        String discoverPath = "C:/Users/clair/OneDrive/Documents/USC/CSCI 572 Search Engines/WebCrawler/urls_LATimes.csv";

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(3); //16
        config.setMaxPagesToFetch(3); //20000
        config.setPolitenessDelay(1000);
        config.setIncludeHttpsPages(true);
        config.setIncludeBinaryContentInCrawling(true);        

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("https://www.latimes.com");        
        
        System.out.println("start crawl");
        
        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(MyCrawler.class, numberOfCrawlers);
        
        StringBuilder fetchedData = new StringBuilder("URL,Status\n");
        StringBuilder downloadedData = new StringBuilder("URL,Size,Outgoing Links,Content Type\n");
        StringBuilder discoveredData = new StringBuilder("URL,Status\n");
        
        for (Object d : controller.getCrawlersLocalData()) {
            String[] data = (String[]) d;
            fetchedData.append(data[0]);
            downloadedData.append(data[1]);
            discoveredData.append(data[2]);
        }
        
        writeFile(fetchedData, fetchPath);
        writeFile(downloadedData, downloadPath);
        writeFile(discoveredData, discoverPath);
        
        System.out.print("done");
    }
	
	private static void writeFile(StringBuilder data, String path) throws IOException {
        PrintWriter csvWriter = new PrintWriter(path, StandardCharsets.UTF_8);
        csvWriter.println(data.toString().trim());
        csvWriter.flush();
        csvWriter.close();
    }
}