package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.tomakehurst.wiremock.client.WireMock;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import io.qameta.allure.AllureId;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static guru.qa.niffler.jupiter.extension.CreateUserExtension.Selector.METHOD;
import static guru.qa.niffler.utils.Error.BAD_CREDENTIALS;

//@Disabled
@Epic("[WEB][niffler-frontend]: Авторизация")
@DisplayName("[WEB][niffler-frontend]: Авторизация")

//@WireMockTest(httpPort = 8089)
public class LoginTest extends BaseWebTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginTest.class);

//    private static ObjectMapper om = new ObjectMapper();
//
//    @RegisterExtension
//    static WireMockExtension wme = new WireMockExtension.Builder()
//            .options(WireMockConfiguration.options().port(8089))
//            .build();

//    private WireMockServer wireMockServer = new WireMockServer(8089);

    private WireMock standaloneWm = new WireMock("localhost", 8089);

    @BeforeEach
    void configure() {

        LOGGER.info("BeforeEach: start");

        standaloneWm.register(get(urlPathEqualTo("/currentUser"))
                .withQueryParam("username", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody("""
                                {                                   
                                    "id": "60412cfd-a9d0-4ead-8839-1a7e055c0de4",
                                    "username": "{{request.query.username}}",
                                    "firstname": null,
                                    "surname": null,
                                    "currency": "RUB",
                                    "photo ": null
                                }

                                """)
                )
        );

//        wireMockServer.start();

//        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/currentUser"))
//                .withQueryParam("username", matching(".*"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-type", "application/json")
//                        .withBody("""
//                                {
//                                    "id": "60412cfd-a9d0-4ead-8839-1a7e055c0de4",
//                                    "username": "{{request.query.username}}",
//                                    "firstname": null,
//                                    "surname": null,
//                                    "currency": "RUB",
//                                    "photo ": null
//                                }
//
//                                """)
//                )
//        );
    }

    @AfterEach
    void stop() {
//        wireMockServer.stop();
    }

    @Test
    @AllureId("500001")
    @DisplayName("WEB: Главная страница должна отображаться после логина новым юзером")
    @Tag("WEB")
//    @GenerateUser()
    void mainPageShouldBeDisplayedAfterSuccessLogin2() throws Exception {

        LOGGER.info("Start...");

        UserJson user = new UserJson();
        user.setId(UUID.randomUUID());
        user.setUsername("dima");
        user.setPassword("12345");
        user.setCurrency(CurrencyValues.RUB);
//
//        while(true) {
//
//        }
//
        Selenide.open(WelcomePage.URL, WelcomePage.class)
                .doLogin()
                .fillLoginPage(user.getUsername(), user.getPassword())
                .submit(new MainPage())
                .waitForPageLoaded();

        LOGGER.info("Finish...");
    }


    @Test
    @AllureId("500001")
    @DisplayName("WEB: Главная страница должна отображаться после логина новым юзером")
    @Tag("WEB")
    @GenerateUser()
    void mainPageShouldBeDisplayedAfterSuccessLogin(@User(selector = METHOD) UserJson user) {
        Selenide.open(WelcomePage.URL, WelcomePage.class)
                .doLogin()
                .fillLoginPage(user.getUsername(), user.getPassword())
                .submit(new MainPage())
                .waitForPageLoaded();
    }

    @Test
    @AllureId("500002")
    @DisplayName("WEB: При неверно введенных логине/пароле пользователь остается неавторизованным")
    @Tag("WEB")
    @GenerateUser()
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(@User(selector = METHOD) UserJson user) {
        LoginPage loginPage = Selenide.open(WelcomePage.URL, WelcomePage.class)
                .doLogin()
                .fillLoginPage(user.getUsername(), user.getPassword() + "BAD");

        loginPage.submit(loginPage)
                .checkError(BAD_CREDENTIALS.content);
    }
}
