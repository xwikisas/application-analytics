package com.xwiki.analytics.configuration;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

@Role
@Unstable
/**
 * Analytics configuration options.
 */
public interface AnalyticsConfiguration {

    /**
     *
     * @return Returns the address where the Matomo requests will be made.
     */
    String getRequestAddress();

    /**
     *
     * @return Returns the id of the site that we want to see the statistics for.
     */
    String getIdSite();

    /**
     *
     * @return  Returns the Authentication Token that permits to access the statistics.
     */
    String getAuthenticationToken();
}
