package com.xwiki.analytics.script;

import com.xwiki.analytics.configuration.AnalyticsConfiguration;
import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Component
@Named("analytics")
@Unstable
@Singleton
public class AnalyticsScriptService implements ScriptService {
    @Inject
    private AnalyticsConfiguration configuration;
}
