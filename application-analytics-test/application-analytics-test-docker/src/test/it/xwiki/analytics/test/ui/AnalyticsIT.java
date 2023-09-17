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

import javax.inject.Inject;

import org.hibernate.validator.constraints.ru.INN;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.xwiki.application.test.po.ApplicationIndexHomePage;
import org.xwiki.test.docker.internal.junit5.DockerTestUtils;
import org.xwiki.test.docker.junit5.TestConfiguration;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.docker.junit5.servletengine.ServletEngine;
import org.xwiki.test.ui.TestUtils;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.administration.test.po.AdministrationPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.xwiki.test.ui.po.ViewPage;

import com.xwiki.analytics.test.po.HomePageViewPage;

import java.util.Collections;
import java.util.List;

@UITest
public class AnalyticsIT
{
    @BeforeAll
    void congig(TestConfiguration testConfiguration)
    {

        GenericContainer<?> mariadb =
            new GenericContainer<>(DockerImageName.parse("mariadb")).withEnv("MYSQL_ROOT_PASSWORD",
                    "exampleRootPassword").withEnv("MYSQL_DATABASE", "matomo").withEnv("MYSQL_USER", "matomo")
                .withEnv("MYSQL_PASSWORD", "examplePassword").withExposedPorts(3306)
                .waitingFor(Wait.forListeningPort());

        // Define the Matomo container
        GenericContainer<?> matomo =
            new GenericContainer<>(DockerImageName.parse("matomo")).withEnv("MATOMO_DATABASE_HOST", mariadb.getHost())
                .withEnv("MATOMO_DATABASE_USERNAME", "matomo").withEnv("MATOMO_DATABASE_PASSWORD", "examplePassword")
                .withEnv("MATOMO_DATABASE_DBNAME", "matomo").withExposedPorts(80).dependsOn(mariadb)
                .waitingFor(Wait.forHttp("/"));

/*        GenericContainer genericContainer = new GenericContainer("matomo");
        genericContainer.setPortBindings(Collections.singletonList("9999:80"));*/
                   mariadb.setPortBindings(Collections.singletonList("9999:3306")); // Map host port 9999 to container port 3306
            matomo.setPortBindings(Collections.singletonList("9998:80"));    // Map host port 9998 to container port 80
        try {
            DockerTestUtils.startContainer(mariadb, testConfiguration);
            DockerTestUtils.startContainer(matomo, testConfiguration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void appEntryRedirectsToHomePage(XWikiWebDriver driver, TestUtils setup)
    {

        HomePageViewPage.gotoPage();
        while (true) {
            System.out.print("1");
        }
    }
}
