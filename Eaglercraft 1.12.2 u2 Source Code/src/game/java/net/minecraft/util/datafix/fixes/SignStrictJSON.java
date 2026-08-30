package net.minecraft.util.datafix.fixes;

import org.json.JSONException;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class SignStrictJSON implements IFixableData {

	public int getFixVersion() {
		return 101;
	}

	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		if ("Sign".equals(compound.getString("id"))) {
			this.updateLine(compound, "Text1");
			this.updateLine(compound, "Text2");
			this.updateLine(compound, "Text3");
			this.updateLine(compound, "Text4");
		}

		return compound;
	}

	private void updateLine(NBTTagCompound compound, String key) {
		String s = compound.getString(key);
		ITextComponent itextcomponent = null;

		if (!"null".equals(s) && !StringUtils.isNullOrEmpty(s)) {
			if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"'
					|| s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
				try {
					itextcomponent = (ITextComponent) JSONTypeProvider.deserialize(JSONTypeProvider.parse(s),
							ITextComponent.class);

					if (itextcomponent == null) {
						itextcomponent = new TextComponentString("");
					}
				} catch (JSONException var8) {
					;
				}

				if (itextcomponent == null) {
					try {
						itextcomponent = ITextComponent.Serializer.jsonToComponent(s);
					} catch (JSONException var7) {
						;
					}
				}

				if (itextcomponent == null) {
					try {
						itextcomponent = ITextComponent.Serializer.jsonToComponent(s);
					} catch (JSONException var6) {
						;
					}
				}

				if (itextcomponent == null) {
					itextcomponent = new TextComponentString(s);
				}
			} else {
				itextcomponent = new TextComponentString(s);
			}
		} else {
			itextcomponent = new TextComponentString("");
		}

		compound.setString(key, ITextComponent.Serializer.componentToJson(itextcomponent));
	}
}
