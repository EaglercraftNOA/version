package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.lax1dude.eaglercraft.vector.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class BlockPart {
	public final Vector3f positionFrom;
	public final Vector3f positionTo;
	public final Map<EnumFacing, BlockPartFace> mapFaces;
	public final BlockPartRotation partRotation;
	public final boolean shade;

	public BlockPart(Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn,
			@Nullable BlockPartRotation partRotationIn, boolean shadeIn) {
		this.positionFrom = positionFromIn;
		this.positionTo = positionToIn;
		this.mapFaces = mapFacesIn;
		this.partRotation = partRotationIn;
		this.shade = shadeIn;
		this.setDefaultUvs();
	}

	private void setDefaultUvs() {
		for (Entry<EnumFacing, BlockPartFace> entry : this.mapFaces.entrySet()) {
			float[] afloat = this.getFaceUvs(entry.getKey());
			(entry.getValue()).blockFaceUV.setUvs(afloat);
		}
	}

	private float[] getFaceUvs(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return new float[] { this.positionFrom.x, 16.0F - this.positionTo.z, this.positionTo.x,
					16.0F - this.positionFrom.z };
		case UP:
			return new float[] { this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z };
		case NORTH:
		default:
			return new float[] { 16.0F - this.positionTo.x, 16.0F - this.positionTo.y, 16.0F - this.positionFrom.x,
					16.0F - this.positionFrom.y };
		case SOUTH:
			return new float[] { this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x,
					16.0F - this.positionFrom.y };
		case WEST:
			return new float[] { this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z,
					16.0F - this.positionFrom.y };
		case EAST:
			return new float[] { 16.0F - this.positionTo.z, 16.0F - this.positionTo.y, 16.0F - this.positionFrom.z,
					16.0F - this.positionFrom.y };
		}
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, BlockPart> {
		public BlockPart deserialize(JSONObject jsonobject) throws JSONException {
			Vector3f vector3f = this.parsePositionFrom(jsonobject);
			Vector3f vector3f1 = this.parsePositionTo(jsonobject);
			BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
			Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(jsonobject);

			if (jsonobject.has("shade") && !(jsonobject.get("shade") instanceof Boolean)) {
				throw new JSONException("Expected shade to be a Boolean");
			} else {
				boolean flag = jsonobject.optBoolean("shade", true);
				return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
			}
		}

		@Nullable
		private BlockPartRotation parseRotation(JSONObject object) {
			BlockPartRotation blockpartrotation = null;

			if (object.has("rotation")) {
				JSONObject jsonobject = object.getJSONObject("rotation");
				Vector3f vector3f = this.parsePosition(jsonobject, "origin");
				vector3f.scale(0.0625F);
				EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
				float f = this.parseAngle(jsonobject);
				boolean flag = jsonobject.optBoolean("rescale", false);
				blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
			}

			return blockpartrotation;
		}

		private float parseAngle(JSONObject object) {
			float f = object.getFloat("angle");

			if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
				throw new JSONException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			} else {
				return f;
			}
		}

		private EnumFacing.Axis parseAxis(JSONObject object) {
			String s = object.getString("axis");
			EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase());

			if (enumfacing$axis == null) {
				throw new JSONException("Invalid rotation axis: " + s);
			} else {
				return enumfacing$axis;
			}
		}

		private Map<EnumFacing, BlockPartFace> parseFacesCheck(JSONObject object) {
			Map<EnumFacing, BlockPartFace> map = this.parseFaces(object);

			if (map.isEmpty()) {
				throw new JSONException("Expected between 1 and 6 unique faces, got 0");
			} else {
				return map;
			}
		}

		private Map<EnumFacing, BlockPartFace> parseFaces(JSONObject object) {
			Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
			JSONObject jsonobject = object.getJSONObject("faces");

			for (String entry : jsonobject.keySet()) {
				EnumFacing enumfacing = this.parseEnumFacing(entry);
				map.put(enumfacing, (BlockPartFace) JSONTypeProvider.deserialize(jsonobject.getJSONObject(entry),
						BlockPartFace.class));
			}

			return map;
		}

		private EnumFacing parseEnumFacing(String name) {
			EnumFacing enumfacing = EnumFacing.byName(name);

			if (enumfacing == null) {
				throw new JSONException("Unknown facing: " + name);
			} else {
				return enumfacing;
			}
		}

		private Vector3f parsePositionTo(JSONObject object) {
			Vector3f vector3f = this.parsePosition(object, "to");

			if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F
					&& vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
				return vector3f;
			} else {
				throw new JSONException("'to' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f parsePositionFrom(JSONObject object) {
			Vector3f vector3f = this.parsePosition(object, "from");

			if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F
					&& vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
				return vector3f;
			} else {
				throw new JSONException("'from' specifier exceeds the allowed boundaries: " + vector3f);
			}
		}

		private Vector3f parsePosition(JSONObject object, String memberName) {
			JSONArray jsonarray = object.getJSONArray(memberName);

			if (jsonarray.length() != 3) {
				throw new JSONException("Expected 3 " + memberName + " values, found: " + jsonarray.length());
			} else {
				float[] afloat = new float[3];

				for (int i = 0; i < afloat.length; ++i) {
					afloat[i] = jsonarray.getFloat(i);
				}

				return new Vector3f(afloat[0], afloat[1], afloat[2]);
			}
		}
	}
}
