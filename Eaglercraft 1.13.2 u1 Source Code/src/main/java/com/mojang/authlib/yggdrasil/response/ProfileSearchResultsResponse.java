package com.mojang.authlib.yggdrasil.response;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;


public class ProfileSearchResultsResponse extends Response {
    private GameProfile[] profiles;

    public GameProfile[] getProfiles() {
        return profiles;
    }

    public static class Serializer implements JsonDeserializer<ProfileSearchResultsResponse> {
        @Override
        public ProfileSearchResultsResponse deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final ProfileSearchResultsResponse result = new ProfileSearchResultsResponse();

            if (json instanceof JsonObject) {
                final JsonObject object = (JsonObject) json;
                if (object.has("error")) {
                    result.setError(object.getAsJsonPrimitive("error").getAsString());
                }
                if (object.has("errorMessage")) {
                    result.setError(object.getAsJsonPrimitive("errorMessage").getAsString());
                }
                if (object.has("cause")) {
                    result.setError(object.getAsJsonPrimitive("cause").getAsString());
                }
            } else if (json instanceof JsonArray) {
                JsonArray array = json.getAsJsonArray();
                GameProfile[] profiles = new GameProfile[array.size()];
                for (int i = 0; i < profiles.length; ++i) {
                    profiles[i] = context.deserialize(array.get(i), GameProfile.class);
                }
                result.profiles = profiles;
            }

            return result;
        }
    }
}
