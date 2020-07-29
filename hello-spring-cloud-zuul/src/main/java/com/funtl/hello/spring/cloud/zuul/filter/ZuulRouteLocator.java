package com.funtl.hello.spring.cloud.zuul.filter;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
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

    private JdbcTemplate jdbcTemplate;
    private ZuulProperties properties;

    @Autowired
    public ZuulRouteLocator(ServerProperties server, ZuulProperties properties, JdbcTemplate jdbcTemplate) {
        super(server.getServlet().getContextPath(), properties);
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
        logger.info("servletPath:{}",server.getServlet().getContextPath());
    }


    @Override
    public void refresh() {
        super.doRefresh();
    }

    /**
     * 在simpleRouteLocator中具体就是在这儿定位路由信息的
     * 在这里重写方法后我们之后从数据库加载路由信息
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

    @Cacheable(value = "locateRoutes",key = "RoutesFromDB",condition ="true")
    public Map<String, ZuulProperties.ZuulRoute> locateRoutesFromDB(){
        Map<String, ZuulProperties.ZuulRoute> routes = new LinkedHashMap<>();
        //创建动态路由配置类，用来获取所有的路由配置
        List<CustomZuulRoute> results = jdbcTemplate.query("select * from gateway_zuul_route where enabled =1 ",new BeanPropertyRowMapper<>(CustomZuulRoute.class));

        for (CustomZuulRoute result : results) {
            if(StringUtils.isBlank(result.getPath())
                    || (StringUtils.isBlank(result.serviceId) && StringUtils.isBlank(result.getUrl()))){
                continue;
            }

            ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
            try {
                BeanUtils.copyProperties(result,zuulRoute);
            } catch (Exception e) {
                logger.error("load zuul route info from db has error",e);
            }
            routes.put(zuulRoute.getPath(),zuulRoute);
        }

        return routes;
    }


    public static class CustomZuulRoute {
        private String id;
        private String path;
        private String serviceId;
        private String url;
        private boolean stripPrefix = true;
        private Boolean retryable;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isStripPrefix() {
            return stripPrefix;
        }

        public void setStripPrefix(boolean stripPrefix) {
            this.stripPrefix = stripPrefix;
        }

        public Boolean getRetryable() {
            return retryable;
        }

        public void setRetryable(Boolean retryable) {
            this.retryable = retryable;
        }
    }

}

