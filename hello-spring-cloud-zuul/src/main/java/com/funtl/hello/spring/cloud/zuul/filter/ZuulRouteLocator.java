package com.funtl.hello.spring.cloud.zuul.filter;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by W2G on 2019/4/22.
 * 自定义动态路由定位器
 * Refer https://github.com/lexburner/zuul-gateway-demo
 */
@Component
public class ZuulRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {
    public final static Logger logger = LoggerFactory.getLogger(ZuulRouteLocator.class);

    private final static String DEFAULT_PATH_SEPARATOR = "/";
    private JdbcTemplate jdbcTemplate;
    private ZuulProperties properties;

    @Autowired
    public ZuulRouteLocator(ServerProperties server, ZuulProperties properties, JdbcTemplate jdbcTemplate) {
        super(server.getServlet().getContextPath(), properties);
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
        logger.info("servletPath:{}", server.getServlet().getContextPath());
    }


    @Override
    public void refresh() {
        super.doRefresh();
    }

    /**
     * 这个方法在网关产品中也很重要，可以根据实际路径匹配到Route来进行业务逻辑的操作，进行一些加工
     *
     * @param path
     * @return
     */
    @Override
    public Route getMatchingRoute(String path) {
        return super.getMatchingRoute(path);
    }

    @Override
    protected Route getRoute(ZuulProperties.ZuulRoute route, String path) {
        int index = route.getPath().indexOf("*") - 1;
        if (index > 0) {
            return super.getRoute(route, path);
        }

        if (route == null) {
            return null;
        } else {
            CustomZuulRoute customZuulRoute = jdbcTemplate.queryForObject("select * from gateway_zuul_route where id =? ", new BeanPropertyRowMapper<>(CustomZuulRoute.class), route.getId());

            String prefix = customZuulRoute.getPrefix() + customZuulRoute.getVersion() + customZuulRoute.getResourceName();
            String targetPath = route.getPath().replaceFirst(prefix, "");
            Boolean retryable = this.properties.getRetryable();
            if (route.getRetryable() != null) {
                retryable = route.getRetryable();
            }

            return new Route(route.getId(), targetPath, route.getLocation(), prefix, retryable, route.isCustomSensitiveHeaders() ? route.getSensitiveHeaders() : null, route.isStripPrefix());
        }
    }

    /**
     * 在simpleRouteLocator中具体就是在这儿定位路由信息的
     * 在这里重写方法后我们之后从数据库加载路由信息
     *
     * @return
     */
    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();

        //先后顺序很重要，这里优先采用DB中配置的路由映射信息，然后才使用本地文件路由配置
        routesMap.putAll(locateRoutesFromDB());
        routesMap.putAll(super.locateRoutes());

        //
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (StringUtils.isNotBlank(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
            }
            values.put(path, entry.getValue());
        }

        return values;
    }

    @Cacheable(value = "locateRoutes", key = "RoutesFromDB", condition = "true")
    public Map<String, ZuulProperties.ZuulRoute> locateRoutesFromDB() {
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        //创建动态路由配置类，用来获取所有的路由配置
        List<CustomZuulRoute> results = jdbcTemplate.query("select * from gateway_zuul_route where enabled =1 ", new BeanPropertyRowMapper<>(CustomZuulRoute.class));

        for (CustomZuulRoute result : results) {
            if (StringUtils.isBlank(result.getPath())
                    || (StringUtils.isBlank(result.getServiceId()) && StringUtils.isBlank(result.getUrl()))) {
                continue;
            }

            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            try {
                BeanUtils.copyProperties(result, zuulRoute);
                //拼接path
                StringBuffer sb = new StringBuffer();
                if (StringUtils.isNotBlank(result.getPrefix())) {
                    sb.append(result.getPrefix());
                }
                if (StringUtils.isNotBlank(result.getVersion())) {
                    sb.append(result.getVersion());
                }
                if (StringUtils.isNotBlank(result.getResourceName())) {
                    sb.append(result.getResourceName());
                }
                String path = sb.toString() + result.getPath();
                zuulRoute.setPath(path);
            } catch (Exception e) {
                logger.error("load zuul route info from db has error", e);
            }
            routes.put(zuulRoute.getPath(), zuulRoute);
        }

        return routes;
    }


    public static class CustomZuulRoute extends ZuulProperties.ZuulRoute {
        private String prefix;
        private String version;
        private String resourceName;

        public String getPrefix() {
            if (StringUtils.isBlank(prefix)) {
                return "";
            } else {
                if (prefix.startsWith(DEFAULT_PATH_SEPARATOR)) {
                    return prefix;
                } else {
                    return DEFAULT_PATH_SEPARATOR + prefix;
                }
            }
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getVersion() {
            if (StringUtils.isBlank(version)) {
                return "";
            } else {
                if (version.startsWith(DEFAULT_PATH_SEPARATOR)) {
                    return version;
                } else {
                    return DEFAULT_PATH_SEPARATOR + version;
                }
            }
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getResourceName() {
            if (StringUtils.isBlank(resourceName)) {
                return "";
            } else {
                if (resourceName.startsWith(DEFAULT_PATH_SEPARATOR)) {
                    return resourceName;
                } else {
                    return DEFAULT_PATH_SEPARATOR + resourceName;
                }
            }
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }
    }

}

