package net.minecraft.util.text;

import java.util.List;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;

public interface ITextComponent extends Iterable<ITextComponent> {
	ITextComponent setStyle(Style style);

	Style getStyle();

	/**
	 * Appends the given text to the end of this component.
	 */
	ITextComponent appendText(String text);

	/**
	 * Appends the given component to the end of this one.
	 */
	ITextComponent appendSibling(ITextComponent component);

	/**
	 * Gets the text of this component, without any special formatting codes added,
	 * for chat. TODO: why is this two different methods?
	 */
	String getUnformattedComponentText();

	/**
	 * Get the text of this component, <em>and all child components</em>, with all
	 * special formatting codes removed.
	 */
	String getUnformattedText();

	/**
	 * Gets the text of this component, with formatting codes added for rendering.
	 */
	String getFormattedText();

	List<ITextComponent> getSiblings();

	/**
	 * Creates a copy of this component. Almost a deep copy, except the style is
	 * shallow-copied.
	 */
	ITextComponent createCopy();

	public static class Serializer implements JSONTypeCodec<ITextComponent, Object> {

		public ITextComponent deserialize(Object p_deserialize_1_) throws JSONException {
			if (p_deserialize_1_ instanceof String) {
				return new TextComponentString((String) p_deserialize_1_);
			} else if (!(p_deserialize_1_ instanceof JSONObject)) {
				if (p_deserialize_1_ instanceof JSONArray) {
					JSONArray jsonarray1 = (JSONArray) p_deserialize_1_;
					ITextComponent itextcomponent1 = null;

					for (int i = 0, l = jsonarray1.length(); i < l; ++i) {
						ITextComponent itextcomponent2 = this.deserialize(jsonarray1.get(i));

						if (itextcomponent1 == null) {
							itextcomponent1 = itextcomponent2;
						} else {
							itextcomponent1.appendSibling(itextcomponent2);
						}
					}

					return itextcomponent1;
				} else {
					throw new JSONException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
				}
			} else {
				JSONObject jsonobject = (JSONObject) p_deserialize_1_;
				ITextComponent itextcomponent;

				if (jsonobject.has("text")) {
					itextcomponent = new TextComponentString(jsonobject.getString("text"));
				} else if (jsonobject.has("translate")) {
					String s = jsonobject.getString("translate");

					if (jsonobject.has("with")) {
						JSONArray jsonarray = jsonobject.getJSONArray("with");
						Object[] aobject = new Object[jsonarray.length()];

						for (int i = 0; i < aobject.length; ++i) {
							aobject[i] = this.deserialize(jsonarray.get(i));

							if (aobject[i] instanceof TextComponentString) {
								TextComponentString textcomponentstring = (TextComponentString) aobject[i];

								if (textcomponentstring.getStyle().isEmpty()
										&& textcomponentstring.getSiblings().isEmpty()) {
									aobject[i] = textcomponentstring.getText();
								}
							}
						}

						itextcomponent = new TextComponentTranslation(s, aobject);
					} else {
						itextcomponent = new TextComponentTranslation(s, new Object[0]);
					}
				} else if (jsonobject.has("score")) {
					JSONObject jsonobject1 = jsonobject.getJSONObject("score");

					if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
						throw new JSONException("A score component needs a least a name and an objective");
					}

					itextcomponent = new TextComponentScore(jsonobject1.getString("name"),
							jsonobject1.getString("objective"));

					if (jsonobject1.has("value")) {
						((TextComponentScore) itextcomponent).setValue(jsonobject1.getString("value"));
					}
				} else if (jsonobject.has("selector")) {
					itextcomponent = new TextComponentSelector(jsonobject.getString("selector"));
				} else {
					if (!jsonobject.has("keybind")) {
						throw new JSONException("Don't know how to turn " + p_deserialize_1_ + " into a Component");
					}

					itextcomponent = new TextComponentKeybind(jsonobject.getString("keybind"));
				}

				if (jsonobject.has("extra")) {
					JSONArray jsonarray2 = jsonobject.getJSONArray("extra");

					if (jsonarray2.length() <= 0) {
						throw new JSONException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonarray2.length(); ++j) {
						itextcomponent.appendSibling(this.deserialize(jsonarray2.get(j)));
					}
				}

				itextcomponent.setStyle((Style) JSONTypeProvider.deserialize(p_deserialize_1_, Style.class));
				return itextcomponent;
			}
		}

		private void serializeChatStyle(Style style, JSONObject object) {
			JSONObject jsonelement = JSONTypeProvider.serialize(style);
			for (String entry : jsonelement.keySet()) {
				object.put(entry, jsonelement.get(entry));
			}
		}

		public Object serialize(ITextComponent p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();

			if (!p_serialize_1_.getStyle().isEmpty()) {
				this.serializeChatStyle(p_serialize_1_.getStyle(), jsonobject);
			}

			if (!p_serialize_1_.getSiblings().isEmpty()) {
				JSONArray jsonarray = new JSONArray();

				for (ITextComponent itextcomponent : p_serialize_1_.getSiblings()) {
					jsonarray.put(this.serialize(itextcomponent));
				}

				jsonobject.put("extra", jsonarray);
			}

			if (p_serialize_1_ instanceof TextComponentString) {
				jsonobject.put("text", ((TextComponentString) p_serialize_1_).getText());
			} else if (p_serialize_1_ instanceof TextComponentTranslation) {
				TextComponentTranslation textcomponenttranslation = (TextComponentTranslation) p_serialize_1_;
				jsonobject.put("translate", textcomponenttranslation.getKey());

				if (textcomponenttranslation.getFormatArgs() != null
						&& textcomponenttranslation.getFormatArgs().length > 0) {
					JSONArray jsonarray1 = new JSONArray();

					for (Object object : textcomponenttranslation.getFormatArgs()) {
						if (object instanceof ITextComponent) {
							jsonarray1.put(this.serialize((ITextComponent) object));
						} else {
							jsonarray1.put(String.valueOf(object));
						}
					}

					jsonobject.put("with", jsonarray1);
				}
			} else if (p_serialize_1_ instanceof TextComponentScore) {
				TextComponentScore textcomponentscore = (TextComponentScore) p_serialize_1_;
				JSONObject jsonobject1 = new JSONObject();
				jsonobject1.put("name", textcomponentscore.getName());
				jsonobject1.put("objective", textcomponentscore.getObjective());
				jsonobject1.put("value", textcomponentscore.getUnformattedComponentText());
				jsonobject.put("score", jsonobject1);
			} else if (p_serialize_1_ instanceof TextComponentSelector) {
				TextComponentSelector textcomponentselector = (TextComponentSelector) p_serialize_1_;
				jsonobject.put("selector", textcomponentselector.getSelector());
			} else {
				if (!(p_serialize_1_ instanceof TextComponentKeybind)) {
					throw new IllegalArgumentException(
							"Don't know how to serialize " + p_serialize_1_ + " as a Component");
				}

				TextComponentKeybind textcomponentkeybind = (TextComponentKeybind) p_serialize_1_;
				jsonobject.put("keybind", textcomponentkeybind.func_193633_h());
			}

			return jsonobject;
		}

		public static String componentToJson(ITextComponent component) {
			if ((component instanceof TextComponentString) && component.getStyle().isEmpty()
					&& component.getSiblings().isEmpty()) {
				String escaped = new JSONObject().put("E", component.getUnformattedComponentText()).toString();
				return escaped.substring(5, escaped.length() - 1);
			} else {
				return JSONTypeProvider.serialize(component).toString();
			}
		}

		@Nullable
		public static ITextComponent jsonToComponent(String json) {
			if (json.equals("null")) {
				return new TextComponentString("");
			}
			return (ITextComponent) JSONTypeProvider.deserialize(json, ITextComponent.class);
		}
	}
}
