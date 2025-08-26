package io.github.pgmarc.space.examples;

import java.io.IOException;
import java.util.Objects;

import io.github.pgmarc.space.SpaceClient;
import io.github.pgmarc.space.contracts.Subscription;
import io.github.pgmarc.space.contracts.SubscriptionRequest;
import io.github.pgmarc.space.contracts.SubscriptionUpdateRequest;
import io.github.pgmarc.space.contracts.UserContact;
import okhttp3.*;

/**
 * In this example you will be subscribing to a service with only a single
 * plan available. After that you will update your subscription to the
 * lastest available version of the service pricing.
 *
 * <p>
 * You have the corresponding <code>Pricing2Yaml</code> files in
 * <code>src/main/resources/single</code> folder.
 * </p>
 */
public class SingleService {

    /**
     * Before executing the code snippets you have to do the following:
     * <ul>
     *     <li>Start a SPACE instance</li>
     *     <li>Get a <code>MANAGER</code> or <code>ADMIN</code> api key</li>
     * </ul>
     *
     * You are free to experiment with the API and adjust the code to your needs.
     * Happy Hacking :)
     *
     * @see <a href="https://github.com/isa-group/Pricing4SaaS-docs"></a>
     *
     */
    public static void main(String[] args) throws IOException {

        String apiKey = Objects.requireNonNull(System.getenv("SPACE_API_KEY"),
            "You need to set SPACE_API_KEY env variable") ;

        SpaceClient client = SpaceClient.builder("localhost", apiKey).build();

        // A subscription at least requires a user id and a username
        String userId = "4427d118-073d-4da2-a145-f77a75b52595";
        String username = "alex";
        UserContact contact = UserContact.builder(userId, username).build();

        // But, you can provide more user contact information if you want
        UserContact contact2 = UserContact
            .builder("3f5f934c-951b-4a2d-ad10-b1679ac9b7ba", "janedoe")
            .firstName("Jane")
            .lastName("Doe")
            .email("janedoe@example.com")
            .phone("280-689-4646").build();

        SubscriptionRequest subReq = SubscriptionRequest.builder(contact)
            .startService("WireMock", "2024")
                .plan("Enterprise")
            .endService()
            .build();

        Subscription newSubscription = client.contracts().addContract(subReq);
        System.out.println(newSubscription);

        Subscription subscription = client.contracts().getContractByUserId(userId);
        System.out.println(subscription);

        // Updating (novating) the contract to version of 2025
        SubscriptionUpdateRequest upReq = SubscriptionUpdateRequest.builder()
            .service("WireMock", "2025")
                .plan("Enterprise")
            .add();

        Subscription updatedSubscription = client.contracts().updateContractByUserId(userId, upReq);
        System.out.println(updatedSubscription);
    }

}
