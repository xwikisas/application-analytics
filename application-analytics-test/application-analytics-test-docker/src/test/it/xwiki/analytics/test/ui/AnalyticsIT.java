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
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.xwiki.test.docker.internal.junit5.DockerTestUtils;
import org.xwiki.test.docker.junit5.TestConfiguration;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.po.BaseModal;

import com.xwiki.analytics.test.po.AdminViewPage;
import com.xwiki.analytics.test.po.HomePageViewPage;
import com.xwiki.analytics.test.po.MatomoViewPage;
import com.xwiki.analytics.test.po.MostViewedMacroViewPages;

import xwiki.analytics.test.ui.config.Config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UITest
public class AnalyticsIT
{
    /**
     * Creates the Admin user
     */
    public void setupUsers(TestUtils testUtils)
    {
        testUtils.loginAsSuperAdmin();
        testUtils.setGlobalRights("XWiki.XWikiAdminGroup", "", "admin", true);
        testUtils.createAdminUser();
        testUtils.loginAsAdmin();
    }

    /**
     * Start the matomo and sql containers, rename the matomo.js to a random int to force the browser to load the
     * tracking script without explicit settings and generate a new auth token for matomo to be used in the tests.
     */
    public void setupContainers(TestConfiguration testConfiguration) throws Exception
    {
        GenericContainer<?> sqlContainer = startDb(testConfiguration);
        GenericContainer<?> matomoContainer = startMatomo(testConfiguration, sqlContainer);
        Container.ExecResult result = matomoContainer.execInContainer("sh", "-c",
            "grep -rl 'matomo.js' /var/www/html/ | xargs -d '\\n' -I {} sed -i 's/matomo.js/36011373.js/g' \"{}\"");
        matomoContainer.execInContainer("sh", "-c", "mv /var/www/html/matomo.js /var/www/html/36011373.js");
        Config.MATOMO_AUTH_TOKEN =
            MatomoViewPage.createToken("http://" + Config.ADDRESS + ":" + matomoContainer.getMappedPort(80));
    }

    /**
     * Import the platform.html.head UIExtension point to make the tracking code work in the test environmnet.
     */
    private void setupUIExtension(TestUtils testUtils) throws Exception
    {
        testUtils.setWikiPreference("meta",
            "#foreach($uix in $services.uix.getExtensions(\"org.xwiki.platform.html.head\","
                + " {'sortByParameter' : 'order'}))\n" + "  $services.rendering.render($uix.execute(), 'xhtml/1.0')\n"
                + "#end");
    }

    @BeforeAll
    void setup(TestConfiguration testConfiguration, TestUtils testUtils) throws Exception
    {
        setupContainers(testConfiguration);
        setupUIExtension(testUtils);
        setupUsers(testUtils);
        HomePageViewPage.gotoPageHomePage();
    }

    /**
     * The function checks that the "Save" button displays the correct messages when the configurations provided by the
     * users are incorrect.
     */
    @Test
    @Order(1)
    void checkWrongConfigs(XWikiWebDriver driver) throws InterruptedException
    {
        AdminViewPage adminViewPage = new AdminViewPage();
        adminViewPage.gotoAdminPage();
        System.out.println("/q/q " + Config.MATOMO_AUTH_TOKEN + " /q/q");
        adminViewPage.setTrackingCode("").setAuthTokenId(Config.MATOMO_AUTH_TOKEN).setIdSiteId("1")
            .setRequestAddressId(Config.ADDRESS + ":" + Config.MATOMO_BRIDGE_PORT).bringSaveButtonIntoView();

        adminViewPage.inProgressNotification("Saving...");
        adminViewPage.successNotification("Saved");
        adminViewPage.inProgressNotification("Checking connection to Matomo.");
        adminViewPage.errorNotification("Failed to connect to Matomo. Please check your configuration " + "values.");
        HomePageViewPage.gotoPageHomePage();
    }

    /**
     * The function checks that the "Save" button displays the correct messages when the configurations provided by the
     * users are correct.
     */
    @Test
    @Order(2)
    void checkValidConfigs(XWikiWebDriver driver) throws InterruptedException
    {

        AdminViewPage adminViewPage = new AdminViewPage();

        adminViewPage.gotoAdminPage();
        adminViewPage.setTrackingCode(Config.getTrackingCode()).setAuthTokenId(Config.MATOMO_AUTH_TOKEN)
            .setIdSiteId("1").setRequestAddressId("http://" + Config.ADDRESS + ":" + Config.MATOMO_BRIDGE_PORT + "/")
            .bringSaveButtonIntoView();
        adminViewPage.inProgressNotification("Saving...");
        adminViewPage.successNotification("Saved");
        adminViewPage.inProgressNotification("Checking connection to Matomo.");
        adminViewPage.successNotification("Test connection succeeded!");
        HomePageViewPage.gotoPageHomePage();
    }

    /**
     * Checks if the admin has edit permissions in the home page of the application.
     */
    @Test
    @Order(3)
    void checkEditPermissions(XWikiWebDriver driver) throws InterruptedException
    {

        HomePageViewPage.gotoPageHomePage();
        // Add a gadget to the dashboard.
        HomePageViewPage.gotoAndEdit().addNewMacro("searchCategories", "Search Categories").saveDashboard();
        // Wait 2 seconds for the macros to load
        Thread.sleep(2000);
        assertEquals(3,HomePageViewPage.noOfGadgets() );
        // Remove a gadget from the dashboard.
        HomePageViewPage.gotoAndEdit().removeLastMacro().saveDashboard();
        // Wait 2 seconds for the macros to load
        Thread.sleep(2000);
        assertEquals(2,HomePageViewPage.noOfGadgets());
    }

    /**
     * Checks that the description is loaded properly for a macro.
     */
    @Test
    @Order(4)
    void checkMacroDescription(XWikiWebDriver driver) throws InterruptedException
    {
        MostViewedMacroViewPages mostViewedMacroViewPages = new MostViewedMacroViewPages();
        mostViewedMacroViewPages.gotoPage();

        assertEquals("When visitors search on your website, they are looking for a particular page, content, product,"
                + " or service. This report lists the pages that were clicked the most after an internal search.",
            mostViewedMacroViewPages.getMacroDescription());
    }

    /**
     * Checks that the Row Evolution modal is loaded properly.
     */
    @Test
    @Order(5)
    void checkRowEvolutionModal()
    {
        MostViewedMacroViewPages mostViewedMacroViewPages = new MostViewedMacroViewPages();
        BaseModal baseModal = mostViewedMacroViewPages.gotoPage().openRowEvolutionModal();
        System.out.println("/MODAL/ " + baseModal.isDisplayed() + "/A/A");
        assertTrue(baseModal.isDisplayed());
    }

    /**
     * Create and start a container with the database.
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
