package io.github.pgmarc.space.serializers;

import org.json.JSONObject;

public interface JsonDeserializable<U> {

    U fromJson(JSONObject json);
}
