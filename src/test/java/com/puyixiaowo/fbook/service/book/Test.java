package com.puyixiaowo.fbook.service.book;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

public class Test {

    public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {

        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);

        String url = "http://www.mxguan.com/book/840/8360558.html";
        System.out.println("Loading page now-----------------------------------------------: " + url);

        // HtmlUnit 模拟浏览器
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);              // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false);                    // 禁用css支持
        webClient.getOptions().setThrowExceptionOnScriptError(false);   // js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(5 * 1000);                   // 设置连接超时时间
        HtmlPage page = webClient.getPage(url);
//        webClient.waitForBackgroundJavaScript(30 * 1000);               // 等待js后台执行30秒

        String pageAsXml = page.asXml();

        // Jsoup解析处理
        Document doc = Jsoup.parse(pageAsXml, "http://www.mxguan.com/");
        // 此处省略其他操作
        System.out.println(doc.toString());
    }
}
