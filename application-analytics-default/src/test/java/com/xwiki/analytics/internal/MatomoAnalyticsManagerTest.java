package com.xwiki.analytics.internal;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.resource.CreateResourceReferenceException;
import org.xwiki.resource.CreateResourceTypeException;
import org.xwiki.resource.UnsupportedResourceReferenceException;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xwiki.analytics.JsonNormaliser;
import com.xwiki.analytics.configuration.AnalyticsConfiguration;

import static org.mockito.Mockito.when;

@ComponentTest
public class MatomoAnalyticsManagerTest
{
    @InjectMockComponents
    private MatomoAnalyticsManager matomoAnalyticsManager;

    @MockComponent
    @Named("MostViewedPages")
    private JsonNormaliser jsonNormaliser;

    @MockComponent
    private AnalyticsConfiguration configuration;

    @Test
    public void MatomoAnalyticsManagerTest()
        throws ComponentLookupException, IOException, UnsupportedResourceReferenceException, InterruptedException,
        CreateResourceTypeException, CreateResourceReferenceException
    {
        when(this.configuration.getAuthenticationToken()).thenReturn("token");
        when(this.configuration.getRequestAddress()).thenReturn("http://130.61.233.19//matomo");
        when(this.configuration.getIdSite()).thenReturn("3");
        this.matomoAnalyticsManager.requestData(new HashMap<>(), "MostViewedPages");
    }
}
