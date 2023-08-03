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
    private ConfigurationSource configDocument;
    @Override
    public String getRequestAddress() {
        return null;
    }

    @Override
    public String getIdSite() {
        return null;
    }

    @Override
    public String getAuthenticationToken() {
        return null;
    }
}
