import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class MitigationTest {

    private static final LdapMock mockLdap = new LdapMock();

    private static final Logger logger = LogManager.getLogger(MitigationTest.class);

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockLdap.start();
    }

    @AfterAll
    public static void afterAll() {
        mockLdap.stop();
    }

    @BeforeEach
    public void beforeEach() {
        assumeTrue(System.getProperty("log4j2.formatMsgNoLookups", "").equals("true"), "log4j2.formatMsgNoLookups MUST be set to true");

        mockLdap.resetConnectionCount();
    }

    @Test
    public void shouldNotBeExploitable() {

        logger.error("${jndi:ldap://127.0.0.1:1389/a}");

        assertThat(mockLdap.getConnectionCount()).isEqualTo(0);
    }

    @Test
    public void shouldNotBeExploitableButIs() {
        logger.error("Username: {}", "${jndi:ldap://127.0.0.1:1389/a}");

        assertThat(mockLdap.getConnectionCount()).isEqualTo(0);
    }
}
