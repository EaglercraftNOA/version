package net.lax1dude.eaglercraft.json;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;

import net.lax1dude.eaglercraft.json.impl.JSONDataParserReader;
import net.lax1dude.eaglercraft.json.impl.JSONDataParserStream;
import net.lax1dude.eaglercraft.json.impl.JSONDataParserString;
import net.lax1dude.eaglercraft.json.impl.SoundMapDeserializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.client.audio.SoundHandler.SoundMap;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.renderer.block.model.multipart.Multipart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.SignStrictJSON;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.properties.EntityOnFire;
import net.minecraft.world.storage.loot.properties.EntityProperty;

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
public class JSONTypeProvider {

	private static final Map<Class<?>, JSONTypeSerializer<?, ?>> serializers = new HashMap();
	private static final Map<Class<?>, JSONTypeDeserializer<?, ?>> deserializers = new HashMap();

	private static final List<JSONDataParserImpl> parsers = new ArrayList();

	public static <J> J serialize(Object object) throws JSONException {
		Class clazz = object.getClass();
		if (clazz.isArray()) {
			throw new RuntimeException("Cannot serialize array type!");
		}
		JSONTypeSerializer<Object, J> ser = (JSONTypeSerializer<Object, J>) serializers.get(clazz);
		if (ser == null) {
			for (Entry<Class<?>, JSONTypeSerializer<?, ?>> etr : serializers.entrySet()) {
				if (etr.getKey().isInstance(object)) {
					ser = (JSONTypeSerializer<Object, J>) etr.getValue();
					break;
				}
			}
		}
		if (ser != null) {
			return ser.serializeToJson(object);
		} else {
			throw new JSONException("Could not find a serializer for " + clazz.getSimpleName());
		}
	}

	public static <O> O deserialize(Object object, Class<O> clazz) throws JSONException {
		if (clazz.isArray()) {
			throw new RuntimeException("Cannot deserialize array type!");
		}
		return deserializeNoCast(parse(object), clazz);
	}

	public static <O> O deserializeNoCast(Object object, Class<O> clazz) throws JSONException {
		if (clazz.isArray()) {
			throw new RuntimeException("Cannot deserialize array type!");
		}
		JSONTypeDeserializer<Object, O> ser = (JSONTypeDeserializer<Object, O>) deserializers.get(clazz);
		if (ser != null) {
			return (O) ser.deserializeFromJson(object);
		} else {
			throw new JSONException("Could not find a deserializer for " + object.getClass().getSimpleName());
		}
	}

	public static <O, J> JSONTypeSerializer<O, J> getSerializer(Class<O> object) {
		return (JSONTypeSerializer<O, J>) serializers.get(object);
	}

	public static <J, O> JSONTypeDeserializer<J, O> getDeserializer(Class<O> object) {
		return (JSONTypeDeserializer<J, O>) deserializers.get(object);
	}

	public static Object parse(Object object) {
		for (int i = 0, l = parsers.size(); i < l; ++i) {
			JSONDataParserImpl parser = parsers.get(i);
			if (parser.accepts(object)) {
				return parser.parse(object);
			}
		}
		return object;
	}

	public static Object[] parse(Object[] object) {
		for (int i = 0; i < object.length; i++) {
			for (int i1 = 0, l = parsers.size(); i1 < l; ++i1) {
				JSONDataParserImpl parser = parsers.get(i1);
				Object object1 = object[i];
				if (parser.accepts(object1)) {
					object1 = parser.parse(object1);
				}
			}
		}
		return object;
	}

	public static void registerType(Class<?> clazz, Object obj) {
		boolean valid = false;
		if (obj instanceof JSONTypeSerializer<?, ?>) {
			serializers.put(clazz, (JSONTypeSerializer<?, ?>) obj);
			valid = true;
		}
		if (obj instanceof JSONTypeDeserializer<?, ?>) {
			deserializers.put(clazz, (JSONTypeDeserializer<?, ?>) obj);
			valid = true;
		}
		if (!valid) {
			throw new IllegalArgumentException(
					"Object " + obj.getClass().getSimpleName() + " is not a JsonSerializer or JsonDeserializer object");
		}
	}

	public static void registerParser(JSONDataParserImpl obj) {
		parsers.add(obj);
	}

	static {

		registerType(SoundMap.class, new SoundMapDeserializer());
		registerType(SoundList.class, new SoundListSerializer());
		registerType(TextureMetadataSection.class, new TextureMetadataSectionSerializer());
		registerType(FontMetadataSection.class, new FontMetadataSectionSerializer());
		registerType(LanguageMetadataSection.class, new LanguageMetadataSectionSerializer());
		registerType(PackMetadataSection.class, new PackMetadataSectionSerializer());
		registerType(AnimationMetadataSection.class, new AnimationMetadataSectionSerializer());

		registerType(ITextComponent.class, new ITextComponent.Serializer());
		registerType(Style.class, new Style.Serializer());
		registerType(BlockFaceUV.class, new BlockFaceUV.Deserializer());
		registerType(BlockPart.class, new BlockPart.Deserializer());
		registerType(BlockPartFace.class, new BlockPartFace.Deserializer());
		registerType(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer());
		registerType(ItemOverride.class, new ItemOverride.Deserializer());
		registerType(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer());
		registerType(ModelBlock.class, new ModelBlock.Deserializer());
		registerType(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer());
		registerType(Multipart.class, new Multipart.Deserializer());
		registerType(Variant.class, new Variant.Deserializer());
		registerType(VariantList.class, new VariantList.Deserializer());
		registerType(Selector.class, new Selector.Deserializer());
		registerType(ChunkGeneratorSettings.Factory.class, new ChunkGeneratorSettings.Serializer());
		registerType(ResourceLocation.class, new ResourceLocation.Serializer());
		registerType(ServerStatusResponse.Players.class, new ServerStatusResponse.Players.Serializer());
		registerType(ServerStatusResponse.Version.class, new ServerStatusResponse.Version.Serializer());
		registerType(ServerStatusResponse.class, new ServerStatusResponse.Serializer());
		registerType(RandomValueRange.class, new RandomValueRange.Serializer());

		registerType(LootPool.class, new LootPool.Serializer());
		registerType(LootTable.class, new LootTable.Serializer());
		registerType(LootEntry.class, new LootEntry.Serializer());
		registerType(LootFunction.class, new LootFunctionManager.Serializer());
		registerType(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
		registerType(LootCondition.class, new LootConditionManager.Serializer());

		registerType(AdvancementRewards.class, new AdvancementRewards.Deserializer());
		registerType(Advancement.Builder.class, new Advancement.Builder.Serializer());
		registerType(AdvancementProgress.class, new AdvancementProgress.Serializer());
		registerType(AdvancementRewards.class, new AdvancementRewards.Deserializer());

		registerParser(new JSONDataParserString());
		registerParser(new JSONDataParserReader());
		registerParser(new JSONDataParserStream());

	}

}