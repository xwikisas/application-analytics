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
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.xwiki.test.docker.internal.junit5.DockerTestUtils;
import org.xwiki.test.docker.junit5.TestConfiguration;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.XWikiWebDriver;

import com.xwiki.analytics.test.po.HomePageViewPage;

@UITest
public class AnalyticsIT
{
    @BeforeAll
    void congig(TestConfiguration testConfiguration)
    {

        MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7"))
            .withDatabaseName("matomo")
            .withUsername("matomo")
            .withPassword("secret")
            .withExposedPorts(3306);
        mysqlContainer.setPortBindings(Collections.singletonList("9034:3306"));
        try {
            //172.17.0.1
            DockerTestUtils.startContainer(mysqlContainer, testConfiguration);
            GenericContainer<?> matomoContainer = new GenericContainer<>("matomo:latest")
                .withExposedPorts(80)
                .withEnv("MATOMO_DATABASE_HOST",
                    "172.17.0.1" + ":" + mysqlContainer.getMappedPort(3306))
                .withEnv("MATOMO_DATABASE_USERNAME", "matomo")
                .withEnv("MATOMO_DATABASE_PASSWORD", "secret")
                .withEnv("MATOMO_DATABASE_DBNAME", "matomo")
                .withEnv("MATOMO_FIRST_USER_NAME", "ADMIN1")
                .withEnv("MATOMO_FIRST_USER_EMAIL", "ADMIN1@xwiki.com")
                .withEnv("MATOMO_FIRST_USER_PASSWORD", "ADMIN1");
                //.withFileSystemBind("src/main/resources/config.ini.php","/var/www/html/config/config.ini.php");
            matomoContainer.setPortBindings(
                // !After the container has been opened and the selenium register a user I have to overwrite the
                // config.ini.php to make Matomo work
                Collections.singletonList("9999:80"));    // Map host port 9999 to container port 80
            DockerTestUtils.startContainer(matomoContainer, testConfiguration);
            matomoContainer.execInContainer("apt-get -y update");
            matomoContainer.execInContainer("apt install git");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void appEntryRedirectsToHomePage(XWikiWebDriver driver, TestUtils setup) throws InterruptedException
    {

        HomePageViewPage.gotoPage();
        while (true) {
            Thread.sleep(10 * 1000);
            System.out.println("Test");
        }
    }
}
