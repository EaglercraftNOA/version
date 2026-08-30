package net.minecraft.client.renderer.block.model;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.vector.Quaternion;
import net.minecraft.util.math.MathHelper;

public class ItemCameraTransforms {
	public static final ItemCameraTransforms DEFAULT = new ItemCameraTransforms();
	public static float offsetTranslateX;
	public static float offsetTranslateY;
	public static float offsetTranslateZ;
	public static float offsetRotationX;
	public static float offsetRotationY;
	public static float offsetRotationZ;
	public static float offsetScaleX;
	public static float offsetScaleY;
	public static float offsetScaleZ;
	public final ItemTransformVec3f thirdperson_left;
	public final ItemTransformVec3f thirdperson_right;
	public final ItemTransformVec3f firstperson_left;
	public final ItemTransformVec3f firstperson_right;
	public final ItemTransformVec3f head;
	public final ItemTransformVec3f gui;
	public final ItemTransformVec3f ground;
	public final ItemTransformVec3f fixed;

	private ItemCameraTransforms() {
		this(ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
				ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
				ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
	}

	public ItemCameraTransforms(ItemCameraTransforms transforms) {
		this.thirdperson_left = transforms.thirdperson_left;
		this.thirdperson_right = transforms.thirdperson_right;
		this.firstperson_left = transforms.firstperson_left;
		this.firstperson_right = transforms.firstperson_right;
		this.head = transforms.head;
		this.gui = transforms.gui;
		this.ground = transforms.ground;
		this.fixed = transforms.fixed;
	}

	public ItemCameraTransforms(ItemTransformVec3f thirdperson_leftIn, ItemTransformVec3f thirdperson_rightIn,
			ItemTransformVec3f firstperson_leftIn, ItemTransformVec3f firstperson_rightIn, ItemTransformVec3f headIn,
			ItemTransformVec3f guiIn, ItemTransformVec3f groundIn, ItemTransformVec3f fixedIn) {
		this.thirdperson_left = thirdperson_leftIn;
		this.thirdperson_right = thirdperson_rightIn;
		this.firstperson_left = firstperson_leftIn;
		this.firstperson_right = firstperson_rightIn;
		this.head = headIn;
		this.gui = guiIn;
		this.ground = groundIn;
		this.fixed = fixedIn;
	}

	public void applyTransform(ItemCameraTransforms.TransformType type) {
		applyTransformSide(this.getTransform(type), false);
	}

	public static void applyTransformSide(ItemTransformVec3f vec, boolean leftHand) {
		if (vec != ItemTransformVec3f.DEFAULT) {
			int i = leftHand ? -1 : 1;
			GlStateManager.translate((float) i * (offsetTranslateX + vec.translation.x),
					offsetTranslateY + vec.translation.y, offsetTranslateZ + vec.translation.z);
			float f = offsetRotationX + vec.rotation.x;
			float f1 = offsetRotationY + vec.rotation.y;
			float f2 = offsetRotationZ + vec.rotation.z;

			if (leftHand) {
				f1 = -f1;
				f2 = -f2;
			}

			GlStateManager.rotate(f, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(f2, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(offsetScaleX + vec.scale.x, offsetScaleY + vec.scale.y, offsetScaleZ + vec.scale.z);
		}
	}

	// TODO: Lags the fuck out of the game
	private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {
		float f = p_188035_0_ * 0.017453292F;
		float f1 = p_188035_1_ * 0.017453292F;
		float f2 = p_188035_2_ * 0.017453292F;
		float f3 = MathHelper.sin(0.5F * f);
		float f4 = MathHelper.cos(0.5F * f);
		float f5 = MathHelper.sin(0.5F * f1);
		float f6 = MathHelper.cos(0.5F * f1);
		float f7 = MathHelper.sin(0.5F * f2);
		float f8 = MathHelper.cos(0.5F * f2);
		return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7,
				f4 * f6 * f8 - f3 * f5 * f7);
	}

	public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
		switch (type) {
		case THIRD_PERSON_LEFT_HAND:
			return this.thirdperson_left;

		case THIRD_PERSON_RIGHT_HAND:
			return this.thirdperson_right;

		case FIRST_PERSON_LEFT_HAND:
			return this.firstperson_left;

		case FIRST_PERSON_RIGHT_HAND:
			return this.firstperson_right;

		case HEAD:
			return this.head;

		case GUI:
			return this.gui;

		case GROUND:
			return this.ground;

		case FIXED:
			return this.fixed;

		default:
			return ItemTransformVec3f.DEFAULT;
		}
	}

	public boolean hasCustomTransform(ItemCameraTransforms.TransformType type) {
		return this.getTransform(type) != ItemTransformVec3f.DEFAULT;
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, ItemCameraTransforms> {
		public ItemCameraTransforms deserialize(JSONObject jsonobject) throws JSONException {
			ItemTransformVec3f itemtransformvec3f = this.getTransform(jsonobject, "thirdperson_righthand");
			ItemTransformVec3f itemtransformvec3f1 = this.getTransform(jsonobject, "thirdperson_lefthand");

			if (itemtransformvec3f1 == ItemTransformVec3f.DEFAULT) {
				itemtransformvec3f1 = itemtransformvec3f;
			}

			ItemTransformVec3f itemtransformvec3f2 = this.getTransform(jsonobject, "firstperson_righthand");
			ItemTransformVec3f itemtransformvec3f3 = this.getTransform(jsonobject, "firstperson_lefthand");

			if (itemtransformvec3f3 == ItemTransformVec3f.DEFAULT) {
				itemtransformvec3f3 = itemtransformvec3f2;
			}

			ItemTransformVec3f itemtransformvec3f4 = this.getTransform(jsonobject, "head");
			ItemTransformVec3f itemtransformvec3f5 = this.getTransform(jsonobject, "gui");
			ItemTransformVec3f itemtransformvec3f6 = this.getTransform(jsonobject, "ground");
			ItemTransformVec3f itemtransformvec3f7 = this.getTransform(jsonobject, "fixed");
			return new ItemCameraTransforms(itemtransformvec3f1, itemtransformvec3f, itemtransformvec3f3,
					itemtransformvec3f2, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6,
					itemtransformvec3f7);
		}

		private ItemTransformVec3f getTransform(JSONObject p_181683_2_, String p_181683_3_) {
			return p_181683_2_.has(p_181683_3_)
					? (ItemTransformVec3f) JSONTypeProvider.deserialize(p_181683_2_.get(p_181683_3_),
							ItemTransformVec3f.class)
					: ItemTransformVec3f.DEFAULT;
		}
	}

	public static enum TransformType {
		NONE, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, HEAD,
		GUI, GROUND, FIXED;
	}
}