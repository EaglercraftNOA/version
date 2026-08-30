/*
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.json.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.v1_8.json.JSONTypeDeserializer;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;

public class SoundMapDeserializer implements JSONTypeDeserializer<JSONObject, Map<String, SoundList>> {

	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();

	@Override
	public Map<String, SoundList> deserialize(JSONObject json) throws JSONException {
		JsonObject object = (new JsonParser()).parse(json.toString()).getAsJsonObject();
		Map<String, SoundList> ret = new HashMap<String, SoundList>(object.size());
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			ret.put(entry.getKey(), GSON.fromJson(entry.getValue(), SoundList.class));
		}
		return ret;
	}

}
