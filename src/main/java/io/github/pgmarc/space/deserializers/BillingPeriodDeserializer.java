package io.github.pgmarc.space.serializers;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.json.JSONObject;

import io.github.pgmarc.space.contracts.BillingPeriod;

class BillingPeriodDeserializer implements JsonDeserializable<BillingPeriod> {

    @Override
    public BillingPeriod fromJson(JSONObject json) {

        Objects.requireNonNull(json, "billing period json must not be null");
        ZonedDateTime start = ZonedDateTime.parse(json.getString(BillingPeriod.Keys.START_DATE.toString()));
        ZonedDateTime end = ZonedDateTime.parse(json.getString(BillingPeriod.Keys.END_DATE.toString()));
        Duration renewalDays = null;
        if (json.has(BillingPeriod.Keys.RENEWAL_DAYS.toString())) {
            renewalDays = Duration.ofDays(json.getLong(BillingPeriod.Keys.RENEWAL_DAYS.toString()));
        }

        return BillingPeriod.of(start, end, renewalDays);

    }

}
