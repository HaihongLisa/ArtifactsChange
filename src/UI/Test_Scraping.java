package UI;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test_Scraping {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Document doc = Jsoup.connect("https://github.com/apache/cassandra/commit/fa2fa602d989ed911b60247e3dd8f2d580188782").get();
		System.out.println(doc.text());
		

	}

}
