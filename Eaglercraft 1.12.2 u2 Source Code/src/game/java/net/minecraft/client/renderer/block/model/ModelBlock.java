package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelBlock {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<BlockPart> elements;
	private final boolean gui3d;
	private final boolean ambientOcclusion;
	private final ItemCameraTransforms cameraTransforms;
	private final List<ItemOverride> overrides;
	public String name = "";
	protected final Map<String, String> textures;
	protected ModelBlock parent;
	protected ResourceLocation parentLocation;

	public static ModelBlock deserialize(Reader readerIn) {
		return (ModelBlock) JSONTypeProvider.deserialize(readerIn, ModelBlock.class);
	}

	public static ModelBlock deserialize(String jsonString) {
		return deserialize(new StringReader(jsonString));
	}

	public ModelBlock(ResourceLocation parentLocationIn, List<BlockPart> elementsIn, Map<String, String> texturesIn,
			boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn,
			List<ItemOverride> overridesIn) {
		this.elements = elementsIn;
		this.ambientOcclusion = ambientOcclusionIn;
		this.gui3d = gui3dIn;
		this.textures = texturesIn;
		this.parentLocation = parentLocationIn;
		this.cameraTransforms = cameraTransformsIn;
		this.overrides = overridesIn;
	}

	public List<BlockPart> getElements() {
		return this.elements.isEmpty() && this.hasParent() ? this.parent.getElements() : this.elements;
	}

	private boolean hasParent() {
		return this.parent != null;
	}

	public boolean isAmbientOcclusion() {
		return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
	}

	public boolean isGui3d() {
		return this.gui3d;
	}

	public boolean isResolved() {
		return this.parentLocation == null || this.parent != null && this.parent.isResolved();
	}

	public void getParentFromMap(Map<ResourceLocation, ModelBlock> p_178299_1_) {
		if (this.parentLocation != null) {
			this.parent = p_178299_1_.get(this.parentLocation);
		}
	}

	public Collection<ResourceLocation> getOverrideLocations() {
		Set<ResourceLocation> set = Sets.<ResourceLocation>newHashSet();

		for (ItemOverride itemoverride : this.overrides) {
			set.add(itemoverride.getLocation());
		}

		return set;
	}

	protected List<ItemOverride> getOverrides() {
		return this.overrides;
	}

	public ItemOverrideList createOverrides() {
		return this.overrides.isEmpty() ? ItemOverrideList.NONE : new ItemOverrideList(this.overrides);
	}

	public boolean isTexturePresent(String textureName) {
		return !"missingno".equals(this.resolveTextureName(textureName));
	}

	public String resolveTextureName(String textureName) {
		if (!this.startsWithHash(textureName)) {
			textureName = '#' + textureName;
		}

		return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
	}

	private String resolveTextureName(String textureName, ModelBlock.Bookkeep p_178302_2_) {
		if (this.startsWithHash(textureName)) {
			if (this == p_178302_2_.modelExt) {
				LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
				return "missingno";
			} else {
				String s = this.textures.get(textureName.substring(1));

				if (s == null && this.hasParent()) {
					s = this.parent.resolveTextureName(textureName, p_178302_2_);
				}

				p_178302_2_.modelExt = this;

				if (s != null && this.startsWithHash(s)) {
					s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
				}

				return s != null && !this.startsWithHash(s) ? s : "missingno";
			}
		} else {
			return textureName;
		}
	}

	private boolean startsWithHash(String hash) {
		return hash.charAt(0) == '#';
	}

	public ResourceLocation getParentLocation() {
		return this.parentLocation;
	}

	public ModelBlock getRootModel() {
		return this.hasParent() ? this.parent.getRootModel() : this;
	}

	public ItemCameraTransforms getAllTransforms() {
		ItemTransformVec3f itemtransformvec3f = this
				.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
		ItemTransformVec3f itemtransformvec3f1 = this
				.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
		ItemTransformVec3f itemtransformvec3f2 = this
				.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
		ItemTransformVec3f itemtransformvec3f3 = this
				.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
		ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
		ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
		ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
		ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
		return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2,
				itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6,
				itemtransformvec3f7);
	}

	private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
		return this.parent != null && !this.cameraTransforms.hasCustomTransform(type) ? this.parent.getTransform(type)
				: this.cameraTransforms.getTransform(type);
	}

	public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> p_178312_0_) {
		for (ModelBlock modelblock : p_178312_0_.values()) {
			try {
				ModelBlock modelblock1 = modelblock.parent;

				for (ModelBlock modelblock2 = modelblock1.parent; modelblock1 != modelblock2; modelblock2 = modelblock2.parent.parent) {
					modelblock1 = modelblock1.parent;
				}

				throw new ModelBlock.LoopException();
			} catch (ModelBlock.LoopException loopException) {
				throw loopException;
			} catch (Throwable t) {
				;
			}
		}
	}

	static final class Bookkeep {
		public final ModelBlock model;
		public ModelBlock modelExt;

		private Bookkeep(ModelBlock modelIn) {
			this.model = modelIn;
		}
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, ModelBlock> {
		public ModelBlock deserialize(JSONObject jsonobject) throws JSONException {
			List<BlockPart> list = this.getModelElements(jsonobject);
			String s = this.getParent(jsonobject);
			Map<String, String> map = this.getTextures(jsonobject);
			boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
			ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;

			if (jsonobject.has("display")) {
				JSONObject jsonobject1 = jsonobject.getJSONObject("display");
				itemcameratransforms = (ItemCameraTransforms) JSONTypeProvider.deserialize(jsonobject1,
						ItemCameraTransforms.class);
			}

			List<ItemOverride> list1 = this.getItemOverrides(jsonobject);
			ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
			return new ModelBlock(resourcelocation, list, map, flag, true, itemcameratransforms, list1);
		}

		protected List<ItemOverride> getItemOverrides(JSONObject object) {
			List<ItemOverride> list = Lists.<ItemOverride>newArrayList();

			if (object.has("overrides")) {
				for (Object jsonelement : object.getJSONArray("overrides")) {
					list.add((ItemOverride) JSONTypeProvider.deserialize(jsonelement, ItemOverride.class));
				}
			}

			return list;
		}

		private Map<String, String> getTextures(JSONObject object) {
			Map<String, String> map = Maps.<String, String>newHashMap();

			if (object.has("textures")) {
				JSONObject jsonobject = object.getJSONObject("textures");

				for (String entry : jsonobject.keySet()) {
					map.put(entry, jsonobject.getString(entry));
				}
			}

			return map;
		}

		private String getParent(JSONObject object) {
			return object.optString("parent", "");
		}

		protected boolean getAmbientOcclusionEnabled(JSONObject object) {
			return object.optBoolean("ambientocclusion", true);
		}

		protected List<BlockPart> getModelElements(JSONObject object) {
			List<BlockPart> list = Lists.<BlockPart>newArrayList();

			if (object.has("elements")) {
				for (Object jsonelement : object.getJSONArray("elements")) {
					list.add((BlockPart) JSONTypeProvider.deserialize(jsonelement, BlockPart.class));
				}
			}

			return list;
		}
	}

	public static class LoopException extends RuntimeException {
	}
}
