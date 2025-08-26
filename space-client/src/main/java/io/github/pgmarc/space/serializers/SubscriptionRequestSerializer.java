package io.github.pgmarc.space.serializers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import io.github.pgmarc.space.contracts.AddOn;
import io.github.pgmarc.space.contracts.BillingPeriod;
import io.github.pgmarc.space.contracts.Service;
import io.github.pgmarc.space.contracts.Subscription;
import io.github.pgmarc.space.contracts.SubscriptionRequest;
import io.github.pgmarc.space.contracts.UserContact;

public class SubscriptionRequestSerializer implements JsonSerializable<SubscriptionRequest> {

    @Override
    public JSONObject toJson(SubscriptionRequest object) {

        JSONObject json = new JSONObject()
                .put(Subscription.Keys.USER_CONTACT.toString(), userContact(object.getUserContact()))
                .put(Subscription.Keys.BILLING_PERIOD.toString(),
                        Map.of(BillingPeriod.Keys.AUTORENEW.toString(), object.getRenewalDays() != null))
                .put(Subscription.Keys.CONTRACTED_SERVICES.toString(), contractedServices(object.getServices()))
                .put(Subscription.Keys.SUBSCRIPTION_PLANS.toString(), subscriptionPlans(object.getServices()))
                .put(Subscription.Keys.SUBSCRIPTION_ADDONS.toString(), subscriptionAddOns(object.getServices()));

        if (object.getRenewalDays() != null) {
            json.getJSONObject(Subscription.Keys.BILLING_PERIOD.toString())
                    .put(BillingPeriod.Keys.RENEWAL_DAYS.toString(), object.getRenewalDays().toDays());
        }

        return json;
    }

    public Map<String, String> userContact(UserContact userContact) {
        Map<String, String> res = new HashMap<>();
        res.put(UserContact.Keys.USER_ID.toString(), userContact.getUserId());
        res.put(UserContact.Keys.USERNAME.toString(), userContact.getUsername());
        res.put(UserContact.Keys.FIRST_NAME.toString(), userContact.getFirstName().orElse(null));
        res.put(UserContact.Keys.LAST_NAME.toString(), userContact.getLastName().orElse(null));
        res.put(UserContact.Keys.EMAIL.toString(), userContact.getEmail().orElse(null));
        res.put(UserContact.Keys.PHONE.toString(), userContact.getPhone().orElse(null));
        return res;
    }

    public static Map<String, String> contractedServices(Set<Service> services) {
        Map<String, String> res = new HashMap<>();
        for (Service service : services) {
            res.put(service.getName(), service.getVersion());
        }
        return Collections.unmodifiableMap(res);
    }

    public static Map<String, String> subscriptionPlans(Set<Service> services) {
        Map<String, String> res = new HashMap<>();
        for (Service service : services) {
            if (service.getPlan().isEmpty()) {
                continue;
            }
            res.put(service.getName(), service.getPlan().get());
        }
        return Collections.unmodifiableMap(res);

    }

    public static Map<String, Map<String, Long>> subscriptionAddOns(Set<Service> services) {

        Map<String, Map<String, Long>> res = new HashMap<>();

        for (Service service : services) {
            Map<String, Long> serviceMap = new HashMap<>();
            for (AddOn addOn : service.getAddOns()) {
                serviceMap.put(addOn.getName(), addOn.getQuantity());
            }

            res.put(service.getName(), Collections.unmodifiableMap(serviceMap));
        }
        return Collections.unmodifiableMap(res);
    }

}
