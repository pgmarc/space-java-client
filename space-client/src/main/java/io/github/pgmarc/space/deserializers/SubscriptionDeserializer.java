package io.github.pgmarc.space.deserializers;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import io.github.pgmarc.space.contracts.BillingPeriod;
import io.github.pgmarc.space.contracts.UsageLevel;
import io.github.pgmarc.space.contracts.UserContact;
import io.github.pgmarc.space.contracts.Service;
import io.github.pgmarc.space.contracts.Subscription;
import io.github.pgmarc.space.contracts.Subscription.Snapshot;

public final class SubscriptionDeserializer implements JsonDeserializable<Subscription> {

    private final BillingPeriodDeserializer billingSerializer = new BillingPeriodDeserializer();
    private final UserContactDeserializer userContactDeserializer = new UserContactDeserializer();
    private final UsageLevelDeserializer usageLevelDeserializer = new UsageLevelDeserializer();
    private final ServicesDeserializer servicesDeserializer = new ServicesDeserializer();
    private final SnapshotsDeserializer historyDeserializer = new SnapshotsDeserializer(servicesDeserializer);

    @Override
    public Subscription fromJson(JSONObject json) {
        BillingPeriod billingPeriod = billingSerializer.fromJson(json.getJSONObject(Subscription.Keys.BILLING_PERIOD.toString()));
        UserContact userContact = userContactDeserializer.fromJson(json.getJSONObject(Subscription.Keys.USER_CONTACT.toString()));
        Map<String, Map<String, UsageLevel>> usageLevels = usageLevelDeserializer.fromJson(
                json.getJSONObject(Subscription.Keys.USAGE_LEVELS.toString()));
        Map<String, Service> services = servicesDeserializer.fromJson(json);
        List<Snapshot> history = historyDeserializer.fromJson(json);
        return Subscription.builder(userContact, billingPeriod, services.values())
                .addUsageLevels(usageLevels).addSnapshots(history).build();
    }

}
