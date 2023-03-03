import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	private HashSet<String> visited = new HashSet<>();
	private String fetchData = "";
	private String downloadData = "";
	private String discoverData = "";
	
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
    		 + "|png|mp3|mp3|zip|gz))$");
    @Override
    public Object getMyLocalData() {
        return new String[]{fetchData, downloadData, discoverData};
    }
    
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "https://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         System.out.println(href);
         
         Boolean previouslyVisited = visited.contains(href);
         
         Boolean crawl = !FILTERS.matcher(href).matches()
                 && (href.startsWith("https://www.latimes.com/")
                         || href.startsWith("http://www.latimes.com/")
                         || href.startsWith("https://latimes.com/")
                         || href.startsWith("http://latimes.com/"));

         if(crawl)
         {
        	 discoverData += href + ",OK\n";
         }
         else 
         {
        	 discoverData += href + ",N_OK\n";
         }
         
         return crawl && !previouslyVisited;
     }

     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         String contentType = page.getContentType();
         int dataSize = page.getContentData().length;
         int linksOut = 0; 
         
         if (contentType.contains("html") || contentType.contains("doc") || contentType.contains("pdf") || contentType.contains("image"))
         {

             if (page.getParseData() instanceof HtmlParseData) {
                 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                 Set<WebURL> links = htmlParseData.getOutgoingUrls();
                 linksOut = links.size();
             }
         }
         
         downloadData += url + "," + dataSize + "," + linksOut + "," + contentType + "\n";
    }
     
     @Override
     protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
         String url = webUrl.getURL().toLowerCase().replaceAll(",", "_");
         fetchData += url + "," + statusCode + "\n";
         visited.add(url);
     }
}