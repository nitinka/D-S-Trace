package nitinka.dstrace.filter.jersey;

import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import nitinka.dstrace.Tracer;
import nitinka.dstrace.conf.TracerFilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: NitinK.Agarwal@yahoo.com
 * This filter can be used by any framework implementing jersey based http resources and expose com.sun.jersey.api.core.ResourceConfig
 * For example dropwizard shares this config object using environment.getJerseyResourceConfig()
 */
public class JerseyTracerFilter implements Filter {
    private TracerFilterConfig tracerFilterConfig;
    private Map<Integer, List<URLPatternInfo>> urlPatternsInfo;
    private boolean successfulInitiation = false;
    private static final Logger logger = LoggerFactory.getLogger(JerseyTracerFilter.class);

    /**
     * contains meta info about a url pattern
     */
    private static class URLPatternInfo {
        private String urlPattern;
        private String httpMethod;
        private String[] tokens;
        private Pattern[] tokensRegExPattern;
        private Map<Integer, List<String>> parametersAtIndex;
        //private static final Pattern REGEX_URL_PARAMETER= Pattern.compile(".*\\{(.*?)\\}.*?");
        private static final Pattern REGEX_URL_PARAMETER= Pattern.compile(".*\\{(.+)\\}.*");

        private URLPatternInfo(String urlPattern, String httpMethod) {
            this.urlPattern = urlPattern;
            this.httpMethod = httpMethod;
            tokens = urlPattern.split("/");
            tokensRegExPattern = new Pattern[tokens.length];
            parametersAtIndex = new HashMap<Integer, List<String>>();

            for(int i=0;i<tokens.length;i++) {
                String regExForToken = "";
                if(tokens[i].contains("{")) {
                    String[] parameterizedTokens = tokens[i].split("}");     // Needed if one token contains multiple params like something/{one}-{two}/something
                    for(int j=0;j<parameterizedTokens.length;j++) {
                        String parameterizedToken = parameterizedTokens[j];
                        if(parameterizedToken.contains("{")) {
                            parameterizedToken = parameterizedTokens[j] + "}";
                            regExForToken +=parameterizedToken.split("\\{")[0] + "(.+)";
                            Matcher matcher = REGEX_URL_PARAMETER.matcher(parameterizedToken);
                            if(matcher.matches()) {
                                String parName = matcher.group(1);

                                List<String> parameters= parametersAtIndex.get(i);
                                if(parameters == null)
                                    parameters = new ArrayList<String>();
                                parameters.add(parName);
                                parametersAtIndex.put(i, parameters);
                            }
                        }
                        else
                            regExForToken +=parameterizedToken;

                    }
                }
                else {
                    regExForToken = tokens[i];
                }

                logger.debug("For token " + tokens[i] + " regex is " + regExForToken);
                tokensRegExPattern[i] = Pattern.compile(regExForToken);

            }
        }

        public String getUrlPattern() {
            return urlPattern;
        }

        public String[] getTokens() {
            return tokens;
        }

        public Map<Integer, List<String>> getParametersAtIndex() {
            return parametersAtIndex;
        }

        public Pattern[] getTokensRegExPattern() {
            return tokensRegExPattern;
        }

        public URLMatchResult match(String url, String httpMethod) {
            String[] urlTokens = url.split("/");
            URLMatchResult result = new URLMatchResult(false);
            if(httpMethod.equals(this.httpMethod)) {
                for(int i=0;i<urlTokens.length;i++) {
                    if(parametersAtIndex.containsKey(i)) {
                        Matcher m = tokensRegExPattern[i].matcher(urlTokens[i]);
                        if(m.matches()) {
                            List<String> parameters = parametersAtIndex.get(i);
                            for(int j=0;j<parameters.size();j++) {
                                result.addParameter(parameters.get(j), m.group(j+1));
                            }
                        }
                        else {
                            return result;
                        }
                    }
                    else {
                        if(!urlTokens[i].equals(tokens[i])){
                            return result;
                        }
                    }
                }
                result.setMatch(true);
                result.setUrlPattern(this.urlPattern);
            }

            return result;
        }

    }

    private static class URLMatchResult {
        private boolean match = false;
        private LinkedHashMap<String, Object> parameters;
        private String urlPattern;

        public URLMatchResult(boolean match) {
            this.match = match;
            parameters = new LinkedHashMap<String, Object>();
        }

        public boolean isMatch() {
            return match;
        }

        public void setMatch(boolean match) {
            this.match = match;
        }

        public LinkedHashMap<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(LinkedHashMap<String, Object> parameters) {
            this.parameters = parameters;
        }

        public void addParameter(String parameter, String value) {
            this.parameters.put(parameter, value);
        }

        public String getUrlPattern() {
            return urlPattern;
        }

        public void setUrlPattern(String urlPattern) {
            this.urlPattern = urlPattern;
        }
    }

    public JerseyTracerFilter(TracerFilterConfig tracerFilterConfig,
                              ResourceConfig resourceConfig,
                              String rootPath) {
        this.tracerFilterConfig = tracerFilterConfig;
        urlPatternsInfo = new HashMap<Integer, List<URLPatternInfo>>();

        try {
            final ImmutableList.Builder<Class<?>> builder = ImmutableList.builder();
            for (Object o : resourceConfig.getSingletons()) {
                if (o.getClass().isAnnotationPresent(Path.class)) {
                    builder.add(o.getClass());
                }
            }

            for (Class<?> klass : resourceConfig.getClasses()) {
                if (klass.isAnnotationPresent(Path.class)) {
                    builder.add(klass);
                }
            }

            for (Class<?> klass : builder.build()) {
                final String path = klass.getAnnotation(Path.class).value();
                for (AnnotatedMethod method : annotatedMethods(klass)) {
                    if (rootPath.endsWith("/*")) {
                        rootPath = rootPath.substring(0, rootPath.length() - 2);
                    }
                    final StringBuilder pathBuilder = new StringBuilder()
                            .append(rootPath)
                            .append(path);
                    if (method.isAnnotationPresent(Path.class)) {
                        final String methodPath = method.getAnnotation(Path.class).value();
                        if (!methodPath.startsWith("/") && !path.endsWith("/")) {
                            pathBuilder.append('/');
                        }
                        pathBuilder.append(methodPath);
                    }

                    String httpMethod = "";
                    if(method.isAnnotationPresent(GET.class)) {
                        httpMethod = "GET";
                    }
                    else if(method.isAnnotationPresent(PUT.class)) {
                        httpMethod = "PUT";
                    }
                    else if(method.isAnnotationPresent(POST.class)) {
                        httpMethod = "POST";
                    }
                    else if(method.isAnnotationPresent(DELETE.class)) {
                        httpMethod = "DELETE";
                    }
                    else if(method.isAnnotationPresent(HEAD.class)) {
                        httpMethod = "HEAD";
                    }
                    else if(method.isAnnotationPresent(OPTIONS.class)) {
                        httpMethod = "OPTIONS";
                    }

                    String patternStr = pathBuilder.toString();
                    String[] tokens = patternStr.toString().split("/");
                    List<URLPatternInfo> urlPatterns = urlPatternsInfo.get(tokens.length);
                    if(urlPatterns == null)
                        urlPatterns = new ArrayList<URLPatternInfo>();
                    urlPatterns.add(new URLPatternInfo(patternStr, httpMethod));
                    logger.info("Resource '" + httpMethod + " " +patternStr + "' instrumented for tracing");
                    urlPatternsInfo.put(tokens.length, urlPatterns);
                }
            }
            successfulInitiation = true;
            logger.info("successfully initialized");
        }
        catch(Exception e) {
            logger.error("Error in initializing", e);
        }
    }

    private MethodList annotatedMethods(Class<?> resource) {
        return new MethodList(resource, true).hasMetaAnnotation(HttpMethod.class);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        URLMatchResult matchResult = null;
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;

        Exception exception = null;
        try {
            if(successfulInitiation && Tracer.isEnabled()) {
                Tracer.sample(httpServletRequest.getHeader(tracerFilterConfig.getHttpHeaderKeyForTracerSampleRequest()));
                Tracer.setCurrentTraceId(httpServletRequest.getHeader(tracerFilterConfig.getHttpHeaderKeyForTrace()));

                matchResult = matchWithURLPatterns(httpServletRequest);
                if(matchResult.isMatch()) {
                    Tracer.startSpan(httpServletRequest.getMethod()+ "_" + matchResult.getUrlPattern(),
                            matchResult.getParameters(),
                            httpServletRequest.getHeader(tracerFilterConfig.getHttpHeaderKeyForParentSpanId()));
                }
            }
        }
        catch(Exception e) {
            logger.error("Exception while tracing", e);
        }
        finally {
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            }
            catch (IOException e) {
                logger.error("", e);
                exception = e;
                throw e;
            }
            catch (javax.servlet.ServletException e) {
                logger.error("", e);
                exception = e;
                throw e;
            } finally {
                try {
                    if(successfulInitiation && Tracer.isEnabled()) {
                        if(matchResult.isMatch()) {
                            String status = "SUCCESS";
                            if(exception != null) {
                                status = "Exception "+exception.getLocalizedMessage();
                            }
                            Tracer.addTag("Status", status);
                            Tracer.endSpan();
                            Tracer.reset();
                        }
                    }
                }
                catch (Exception e) {
                    logger.error("Exception while tracing", e);
                }
            }
        }
    }

    private URLMatchResult matchWithURLPatterns(HttpServletRequest httpServletRequest) throws IOException {
        String url = httpServletRequest.getRequestURI();

        String[] urlTokens = url.split("/");
        List<URLPatternInfo> possiblePatterns = urlPatternsInfo.get(urlTokens.length);
        if(possiblePatterns != null) {
            for(URLPatternInfo urlPatternInfo : possiblePatterns) {
                URLMatchResult matchResult = urlPatternInfo.match(url, httpServletRequest.getMethod());
                if(matchResult.isMatch()) {
                    String queryString = httpServletRequest.getQueryString();
                    if(queryString != null) {
                        String[] queryParams = queryString.split("&");
                        for(String queryParam : queryParams) {
                            String[] queryTokens = queryParam.split("=");
                            if(queryTokens.length > 0) {
                                String queryName = null;
                                String queryValue = null;
                                if(!queryTokens[0].trim().equals("")) {
                                    queryName = queryTokens[0].trim();
                                    if(queryTokens.length>1)
                                        queryValue = queryTokens[1].trim();

                                    matchResult.addParameter(queryName, queryValue);
                                }
                            }
                        }
                    }
                    return matchResult;
                }
            }
        }
        return new URLMatchResult(false);
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
