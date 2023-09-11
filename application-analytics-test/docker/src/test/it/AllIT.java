
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.PageObjectSuite;
import org.xwiki.test.ui.XWikiWebDriver;
import org.xwiki.test.ui.TestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.xwiki.application.test.po.ApplicationIndexHomePage;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.menu.test.po.MenuHomePage;
@UITest
public class AllIT
{
    @Test
    @Order(1)
    void verifyMenuInApplicationsIndex(TestUtils setup)
    {
        // Log in as superadmin
        setup.loginAsSuperAdmin();

        ApplicationIndexHomePage applicationIndexHomePage = ApplicationIndexHomePage.gotoPage();

        assertTrue(applicationIndexHomePage.containsApplication("Menu"));
        ViewPage vp = applicationIndexHomePage.clickApplication("Menu");

        // Verify we're on the right page!
        assertEquals(MenuHomePage.getSpace(), vp.getMetaDataValue("space"));
        assertEquals(MenuHomePage.getPage(), vp.getMetaDataValue("page"));

        // Now log out to verify that the Menu entry is not displayed for guest users
        setup.forceGuestUser();
        // Navigate again to the Application Index page to perform the verification
        applicationIndexHomePage = ApplicationIndexHomePage.gotoPage();
        assertFalse(applicationIndexHomePage.containsApplication("Menu"));
    }
}