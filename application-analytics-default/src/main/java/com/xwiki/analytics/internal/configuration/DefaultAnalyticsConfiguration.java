package com.xwiki.analytics.internal.configuration;

import com.xwiki.analytics.configuration.AnalyticsConfiguration;
import groovy.lang.Singleton;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Default implementation of {@AnalyticsConfiguration}
 */
@Component
@Singleton
public class DefaultAnalyticsConfiguration implements AnalyticsConfiguration {
    @Inject
    @Named("analytics")
    private ConfigurationSource configDocument;
    @Override
    public String getRequestAddress() {
        return getProperty("requestAddress", "");
    }

    @Override
    public String getIdSite() {
        return  getProperty("siteId","");
    }

    @Override
    public String getAuthenticationToken() {
        return getProperty("authToken", "");
    }

    private <T> T getProperty(String key, T defaultValue)
    {
            return this.configDocument.getProperty(key, defaultValue);
    }
}
