package com.mojang.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import net.lax1dude.eaglercraft.v1_8.EaglerUUIDHelper;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;

/**
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class UUIDTypeAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {
	public static String fromUUID(UUID value) {
		return value.toString().replace("-", "");
	}

	public static UUID fromString(String input) {
		if (input.indexOf('-') < 0 && input.length() == 32) {
			input = input.substring(0, 8) + "-" + input.substring(8, 12) + "-" + input.substring(12, 16) + "-"
					+ input.substring(16, 20) + "-" + input.substring(20);
		}
		EaglercraftUUID uuid = EaglercraftUUID.fromString(input);
		return EaglerUUIDHelper.fromBits(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	@Override
	public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return fromString(json.getAsString());
	}

	@Override
	public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(fromUUID(src));
	}
}
