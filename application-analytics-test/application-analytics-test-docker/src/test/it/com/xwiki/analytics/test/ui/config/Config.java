/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.analytics.test.ui.config;

public class Config
{
    // The custom sql container is needed to skip the Matomo installation wizard.
    public static final String DB_CONTAINER_NAME = "farcasut/custom-mysql:latest";

    public static final int DB_CONTAINER_EXPOSED_PORT = 3306;

    public static final int DB_BRIDGE_PORT = 9034;

    public static final String DB_NAME = "matomo";

    public static final String DB_USERNAME = "matomo";

    public static final String DB_PASSWORD = "secret";

    public static final String ADDRESS = "172.17.0.1";

    public static final String MATOMO_CONTAINER_NAME = "matomo:4.15.1";

    public static final String MATOMO_CONFIG_FILE_PATH = "/var/www/html/config/config.ini.php";

    public static final int MATOMO_BRIDGE_PORT = 9999;

    public static final String MATOMO_CREDENTIALS = "ADMIN1";

    public static String MATOMO_AUTH_TOKEN = "";

    public static String getTrackingCode()
    {
        String sb = "<!-- Matomo -->\n"
            + "<script>\n"
            + "  var _paq = window._paq = window._paq || [];\n"
            + "  /* tracker methods like \"setCustomDimension\" should be called before \"trackPageView\" */\n"
            + "  _paq.push(['trackPageView']);\n"
            + "  _paq.push(['enableLinkTracking']);\n"
            + "  (function() {\n"
            + "    var u=\"http://" + Config.ADDRESS + ":" + Config.MATOMO_BRIDGE_PORT
            + "/\";\n"
            + "    _paq.push(['setTrackerUrl', u+'matomo.php']);\n"
            + "    _paq.push(['setSiteId', '1']);\n"
            + "    var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];\n"
            + "    g.async=true; g.src=u+'36011373.js'; s.parentNode.insertBefore(g,s);\n"
            + "  })();\n"
            + "</script>\n"
            + "<!-- End Matomo Code -->\n";
        return sb;
    }
}
