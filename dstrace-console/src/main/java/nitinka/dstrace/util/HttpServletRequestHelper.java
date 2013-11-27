package nitinka.dstrace.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 27/11/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpServletRequestHelper {
    public static Map<String, String> parseQueryParameters(HttpServletRequest request) {
        Map<String, String> queryParameters = null;
        String queryString = request.getQueryString();
        if(queryString != null && !queryString.trim().equals("")) {
            queryParameters = new HashMap<String, String>();
            String[] parameters = queryString.split("&");
            for(String parameter : parameters) {
                String[] parameterTokens = parameter.split("=");
                if(parameterTokens.length == 2) {
                    queryParameters.put(parameterTokens[0], parameterTokens[1]);
                }
            }
        }
        return queryParameters;
    }

}
