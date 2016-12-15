package de.otto.rx.composer.acceptance;

import com.github.restdriver.clientdriver.ClientDriverRule;
import de.otto.rx.composer.page.Fragments;
import de.otto.rx.composer.page.Page;
import de.otto.rx.composer.content.Content;
import de.otto.rx.composer.content.Contents;
import de.otto.rx.composer.http.HttpClient;
import org.junit.Rule;
import org.junit.Test;

import static com.damnhandy.uri.template.UriTemplate.fromTemplate;
import static com.github.restdriver.clientdriver.ClientDriverRequest.Method.GET;
import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static com.google.common.collect.ImmutableMap.*;
import static de.otto.rx.composer.page.Fragments.followedBy;
import static de.otto.rx.composer.page.Fragments.fragment;
import static de.otto.rx.composer.page.Page.consistsOf;
import static de.otto.rx.composer.content.AbcPosition.X;
import static de.otto.rx.composer.content.AbcPosition.Y;
import static de.otto.rx.composer.content.AbcPosition.Z;
import static de.otto.rx.composer.content.Parameters.emptyParameters;
import static de.otto.rx.composer.content.Parameters.parameters;
import static de.otto.rx.composer.providers.ContentProviders.*;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class NestedFragmentsAcceptanceTest {

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    @Test
    public void shouldHandleNestedSteps() throws Exception {
        // given
        driver.addExpectation(
                onRequestTo("/someContent").withMethod(GET),
                giveResponse("Hello", "text/plain").withStatus(200));
        driver.addExpectation(
                onRequestTo("/someOtherContent").withParam("param", "Hello").withMethod(GET),
                giveResponse("World", "text/plain").withStatus(200));
        driver.addExpectation(
                onRequestTo("/someOtherContent").withMethod(GET),
                giveResponse("Otto", "text/plain").withStatus(200));

        try (final HttpClient httpClient = new HttpClient(1000, 1000)) {
            final Page page = consistsOf(
                    fragment(
                            X,
                            withSingle(contentFrom(httpClient, driver.getBaseUrl() + "/someContent", TEXT_PLAIN)),
                            followedBy(
                                    (final Content content) -> parameters(of("param", content.getBody())),
                                    fragment(
                                            Y,
                                            withSingle(contentFrom(httpClient, fromTemplate(driver.getBaseUrl() + "/someOtherContent{?param}"), TEXT_PLAIN))
                                    ),
                                    fragment(
                                            Z,
                                            withSingle(contentFrom(httpClient, driver.getBaseUrl() + "/someOtherContent", TEXT_PLAIN))
                                    )
                            )
                    )
            );

            final Contents result = page.fetchWith(emptyParameters());
            assertThat(result.getAll(), hasSize(3));
            assertThat(result.get(X).getBody(), is("Hello"));
            assertThat(result.get(Y).getBody(), is("World"));
            assertThat(result.get(Z).getBody(), is("Otto"));
        }
    }

}