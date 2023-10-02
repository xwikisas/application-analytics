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
package xwiki.analytics.test.ui;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.xwiki.test.docker.internal.junit5.DockerTestUtils;
import org.xwiki.test.docker.junit5.TestConfiguration;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.XWikiWebDriver;

import com.xwiki.analytics.test.po.AdminViewPage;
import com.xwiki.analytics.test.po.HomePageViewPage;

import xwiki.analytics.test.ui.config.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@UITest
public class AnalyticsIT
{
    @BeforeAll
    void setup(TestConfiguration testConfiguration, TestUtils testUtils) throws Exception
    {
        GenericContainer<?> sqlContainer = startDb(testConfiguration);
        GenericContainer<?> matomoContainer = startMatomo(testConfiguration, sqlContainer);
        testUtils.loginAsSuperAdmin();
        testUtils.setGlobalRights("XWiki.XWikiAdminGroup", "", "admin", true);
        testUtils.createAdminUser();
        testUtils.loginAsAdmin();
    }

    /**
     * The function checks that the "Save" button displays the correct messages when the configurations provided by the
     * users are incorrect.
     *
     * @param driver
     */
    //@Test
    // @Order(1)
    void checkWrongConfigs(XWikiWebDriver driver) throws InterruptedException
    {
        AdminViewPage.gotoAdminPage().setTrackingCode(driver, "").setAuthTokenId(driver, Config.MATOMO_AUTH_TOKEN)
            .setIdSiteId(driver, "1").setRequestAddressId(driver, Config.ADDRESS + ":" + Config.MATOMO_BRIDGE_PORT)
            .bringSaveButtonIntoView(driver);
        assertEquals(AdminViewPage.inProgressNotification(driver), "Saving...");
        assertEquals(AdminViewPage.successNotification(driver), "Saved");
        assertEquals(AdminViewPage.inProgressNotification(driver), "Checking connection to Matomo.");
        assertEquals(AdminViewPage.errorNotification(driver),
            "Failed to connect to Matomo. Please check your configuration values.");
    }

    @Test
    @Order(1)
    void checkValidConfigs(XWikiWebDriver driver) throws InterruptedException
    {
        AdminViewPage.gotoAdminPage().setTrackingCode(driver, "<!-- Matomo -->\n"
                + "<script>\n"
                + "  var _paq = window._paq = window._paq || [];\n"
                + "  /* tracker methods like \"setCustomDimension\" should be called before \"trackPageView\" */\n"
                + "  _paq.push(['trackPageView']);\n"
                + "  _paq.push(['enableLinkTracking']);\n"
                + "  (function() {\n"
                + "    var u=\"http://"+ Config.ADDRESS+":"+Config.MATOMO_BRIDGE_PORT+"/matomo/"+"\";\n"
                + "    _paq.push(['setTrackerUrl', u+'matomo.php']);\n"
                + "    _paq.push(['setSiteId', '1']);\n"
                + "    var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];\n"
                + "    g.async=true; g.src=u+'matomo.js'; s.parentNode.insertBefore(g,s);\n"
                + "  })();\n"
                + "</script>\n"
                + "<!-- End Matomo Code -->\n").setAuthTokenId(driver, Config.MATOMO_AUTH_TOKEN)
            .setIdSiteId(driver, "1").setRequestAddressId(driver,
                "http://"+Config.ADDRESS + ":" + Config.MATOMO_BRIDGE_PORT + "/matomo/")
            .bringSaveButtonIntoView(driver);
        assertEquals(AdminViewPage.inProgressNotification(driver), "Saving...");
        assertEquals(AdminViewPage.successNotification(driver), "Saved");
        assertEquals(AdminViewPage.inProgressNotification(driver), "Checking connection to Matomo.");
        driver.waitUntilElementDisappears(By.cssSelector(".xnotification.xnotification-inprogress"));
        // assertEquals(AdminViewPage.successNotification(driver),
        //    "Test connection succeeded!");
        while (true)
        {
            Thread.sleep(10000);
            System.out.printf("test");
        }
    }

    /**
     * Checks if an admin can add/remove macros to the main dashboard.
     *
     * @param driver
     */
    @Test
    @Order(2)
    void appEntryRedirectsToHomePage(XWikiWebDriver driver) throws InterruptedException
    {
        // Add a gadget to the dashboard.
        HomePageViewPage.gotoAndEdit().addNewMacro(driver, "searchCategories", "Search Categories")
            .saveDashboard(driver);
        assertEquals(HomePageViewPage.noOfGadgets(driver), 3);
        // Remove a gadget from the dashboard.
        HomePageViewPage.gotoAndEdit().removeLastMacro(driver).saveDashboard(driver);
        assertEquals(HomePageViewPage.noOfGadgets(driver), 2);
    }

    /**
     * Create and start a container with the database.
     *
     * @param testConfiguration configuration for the test container
     * @return reference to the container
     */
    private MySQLContainer startDb(TestConfiguration testConfiguration) throws Exception
    {
        // Since the MySQL container is derived from the official MySQL image I have to mark the image as compatible
        // with MySQLContainers.
        DockerImageName sqlContainer =
            DockerImageName.parse(Config.DB_CONTAINER_NAME).asCompatibleSubstituteFor("mysql");
        MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>(sqlContainer).withDatabaseName(Config.DB_NAME).withUsername(Config.DB_USERNAME)
                .withPassword(Config.DB_PASSWORD).withExposedPorts(3306);
        mysqlContainer.setPortBindings(
            Collections.singletonList(String.format("%d:%d", Config.DB_BRIDGE_PORT, Config.DB_CONTAINER_EXPOSED_PORT)));
        DockerTestUtils.startContainer(mysqlContainer, testConfiguration);
        return mysqlContainer;
    }

    /**
     * Creates&starts the Matomo container.
     *
     * @param testConfiguration test configuration
     * @param dbContainer reference to the db container
     * @return reference to the container
     */
    private GenericContainer startMatomo(TestConfiguration testConfiguration, GenericContainer dbContainer)
        throws Exception
    {
        GenericContainer<?> matomoContainer = new GenericContainer<>(Config.MATOMO_CONTAINER_NAME).withExposedPorts(80)
            .withEnv("MATOMO_DATABASE_HOST",
                Config.ADDRESS + ":" + dbContainer.getMappedPort(Config.DB_CONTAINER_EXPOSED_PORT))
            .withFileSystemBind("src/main/resources/config.ini.php", Config.MATOMO_CONFIG_FILE_PATH);
        matomoContainer.setPortBindings(Collections.singletonList(String.format("%d:80", Config.MATOMO_BRIDGE_PORT)));
        DockerTestUtils.startContainer(matomoContainer, testConfiguration);
        return matomoContainer;
    }
}
