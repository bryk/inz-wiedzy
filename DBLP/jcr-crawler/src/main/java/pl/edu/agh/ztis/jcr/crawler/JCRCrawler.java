package pl.edu.agh.ztis.jcr.crawler;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public class JCRCrawler {

    public static final String POST_PARAMS = "?edition=science&science_year=2013&social_year=2013&view=category&RQ=SELECT_ALL&change_limits=&Submit.x=1&SID=%s&query_new=true";
    public static final String JCR_URL = "http://admin-apps.webofknowledge.com/JCR/JCR?RQ=SELECT_ALL&cursor=%d";
    public static final Pattern SESSION_ID_PATTERN = Pattern.compile("jcrsid=([A-Za-z0-9]+);.+");

    private String sessionId;

    public String crawlOnePage(int cursor) {
        initializeSession();
        String paramString = String.format(POST_PARAMS, sessionId);
        String jcrUrlWithCursor = String.format(JCR_URL, cursor);
        String result = null;
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(paramString), "UTF-8");
            result = Request.Post(jcrUrlWithCursor).bodyForm(params).execute().returnContent().asString();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initializeSession() {
        if (sessionId == null) {
            sessionId = extractSessionId();
        }
    }



    private String extractSessionId() {
        String result = null;
        try {
            Header[] allHeaders = Request.Get("http://admin-router.webofknowledge.com/?DestApp=JCR").execute().returnResponse().getAllHeaders();
            String cookie = Stream.of(allHeaders).filter(header -> header.getValue().contains("jcrsid"))
                    .map(Header::getValue).reduce((s, s2) -> s + s2).get();
            Matcher matcher = SESSION_ID_PATTERN.matcher(cookie);
            if (matcher.matches()) {
                result = matcher.group(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


}
