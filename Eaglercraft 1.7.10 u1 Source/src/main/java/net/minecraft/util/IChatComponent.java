package net.minecraft.util;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public interface IChatComponent extends Iterable {
	IChatComponent setChatStyle(ChatStyle p_150255_1_);

	ChatStyle getChatStyle();

	/**
	 * Appends the given text to the end of this component.
	 */
	IChatComponent appendText(String p_150258_1_);

	/**
	 * Appends the given component to the end of this one.
	 */
	IChatComponent appendSibling(IChatComponent p_150257_1_);

	/**
	 * Gets the text of this component, without any special formatting codes added,
	 * for chat. TODO: why is this two different methods?
	 */
	String getUnformattedTextForChat();

	/**
	 * Gets the text of this component, without any special formatting codes added.
	 * TODO: why is this two different methods?
	 */
	String getUnformattedText();

	/**
	 * Gets the text of this component, with formatting codes added for rendering.
	 */
	String getFormattedText();

	/**
	 * Gets the sibling components of this one.
	 */
	List getSiblings();

	/**
	 * Creates a copy of this component. Almost a deep copy, except the style is
	 * shallow-copied.
	 */
	IChatComponent createCopy();

	public static class Serializer {
		public static String toJson(IChatComponent component) {
			if (component == null)
				return null;
			return toJsonObject(component).toString();
		}

		public static IChatComponent fromJson(String json) {
			if (json == null)
				return null;
			try {
				Object parsed = new org.json.JSONTokener(json).nextValue();
				if (parsed instanceof JSONObject) {
					return fromJsonObject((JSONObject) parsed);
				} else if (parsed instanceof JSONArray) {
					JSONArray arr = (JSONArray) parsed;
					IChatComponent result = null;
					for (int i = 0; i < arr.length(); ++i) {
						Object elem = arr.get(i);
						IChatComponent comp;
						if (elem instanceof JSONObject) {
							comp = fromJsonObject((JSONObject) elem);
						} else if (elem instanceof JSONArray) {
							comp = fromJson(((JSONArray) elem).toString());
						} else {
							comp = new ChatComponentText(String.valueOf(elem));
						}
						if (result == null)
							result = comp;
						else
							result.appendSibling(comp);
					}
					return result;
				} else if (parsed instanceof String) {
					return new ChatComponentText((String) parsed);
				}
			} catch (JSONException e) {
				throw new RuntimeException("Failed to parse chat component JSON", e);
			}
			return null;
		}

		public static JSONObject toJsonObject(IChatComponent component) {
			try {
				if (component instanceof ChatComponentText && component.getChatStyle().isEmpty()
						&& component.getSiblings().isEmpty()) {
					JSONObject obj = new JSONObject();
					obj.put("text", ((ChatComponentText) component).getChatComponentText_TextValue());
					return obj;
				}
				JSONObject obj = new JSONObject();
				if (!component.getChatStyle().isEmpty()) {
					ChatStyle style = component.getChatStyle();
					JSONObject styleObj = ChatStyleSerializer.toJsonObject(style);
					for (String key : styleObj.keySet()) {
						obj.put(key, styleObj.get(key));
					}
				}
				if (!component.getSiblings().isEmpty()) {
					JSONArray extra = new JSONArray();
					for (Object sibling : component.getSiblings()) {
						extra.put(toJsonObject((IChatComponent) sibling));
					}
					obj.put("extra", extra);
				}
				if (component instanceof ChatComponentText) {
					obj.put("text", ((ChatComponentText) component).getChatComponentText_TextValue());
				} else if (component instanceof ChatComponentTranslation) {
					ChatComponentTranslation tr = (ChatComponentTranslation) component;
					obj.put("translate", tr.getKey());
					if (tr.getFormatArgs() != null && tr.getFormatArgs().length > 0) {
						JSONArray withArr = new JSONArray();
						for (Object arg : tr.getFormatArgs()) {
							if (arg instanceof IChatComponent) {
								withArr.put(toJsonObject((IChatComponent) arg));
							} else {
								withArr.put(String.valueOf(arg));
							}
						}
						obj.put("with", withArr);
					}
				} else {
					throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
				}
				return obj;
			} catch (JSONException e) {
				throw new RuntimeException("Failed to serialize chat component", e);
			}
		}

		public static IChatComponent fromJsonObject(JSONObject obj) {
			try {
				IChatComponent result;
				if (obj.has("text")) {
					result = new ChatComponentText(obj.getString("text"));
				} else if (obj.has("translate")) {
					String key = obj.getString("translate");
					Object[] with = new Object[0];
					if (obj.has("with")) {
						JSONArray withArr = obj.getJSONArray("with");
						with = new Object[withArr.length()];
						for (int i = 0; i < withArr.length(); ++i) {
							Object val = withArr.get(i);
							if (val instanceof JSONObject) {
								IChatComponent comp = fromJsonObject((JSONObject) val);
								if (comp instanceof ChatComponentText && comp.getChatStyle().isEmpty()
										&& comp.getSiblings().isEmpty()) {
									with[i] = ((ChatComponentText) comp).getChatComponentText_TextValue();
								} else {
									with[i] = comp;
								}
							} else {
								with[i] = String.valueOf(val);
							}
						}
					}
					result = new ChatComponentTranslation(key, with);
				} else {
					throw new RuntimeException("Don't know how to turn " + obj + " into a Component");
				}
				if (obj.has("extra")) {
					JSONArray extra = obj.getJSONArray("extra");
					for (int i = 0; i < extra.length(); ++i) {
						Object val = extra.get(i);
						if (val instanceof JSONObject) {
							result.appendSibling(fromJsonObject((JSONObject) val));
						} else if (val instanceof JSONArray) {
							IChatComponent comp = fromJson(((JSONArray) val).toString());
							if (comp != null)
								result.appendSibling(comp);
						} else {
							result.appendSibling(new ChatComponentText(String.valueOf(val)));
						}
					}
				}
				result.setChatStyle(ChatStyleSerializer.fromJsonObject(obj));
				return result;
			} catch (JSONException e) {
				throw new RuntimeException("Failed to deserialize chat component", e);
			}
		}

		private static class ChatStyleSerializer {
			static JSONObject toJsonObject(ChatStyle style) throws JSONException {
				JSONObject obj = new JSONObject();
				if (style.getBold())
					obj.put("bold", true);
				if (style.getItalic())
					obj.put("italic", true);
				if (style.getUnderlined())
					obj.put("underlined", true);
				if (style.getStrikethrough())
					obj.put("strikethrough", true);
				if (style.getObfuscated())
					obj.put("obfuscated", true);
				if (style.getColor() != null)
					obj.put("color", style.getColor().toString());
				if (style.getChatClickEvent() != null) {
					JSONObject click = new JSONObject();
					click.put("action", style.getChatClickEvent().getAction().getCanonicalName());
					click.put("value", style.getChatClickEvent().getValue());
					obj.put("clickEvent", click);
				}
				if (style.getChatHoverEvent() != null) {
					JSONObject hover = new JSONObject();
					hover.put("action", style.getChatHoverEvent().getAction().getCanonicalName());
					if (style.getChatHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
						hover.put("value",
								IChatComponent.Serializer.toJsonObject(style.getChatHoverEvent().getValue()));
					} else {
						hover.put("value", style.getChatHoverEvent().getValue().getUnformattedText());
					}
					obj.put("hoverEvent", hover);
				}
				return obj;
			}

			static ChatStyle fromJsonObject(JSONObject obj) throws JSONException {
				ChatStyle style = new ChatStyle();
				if (obj.has("bold"))
					style.setBold(obj.getBoolean("bold"));
				if (obj.has("italic"))
					style.setItalic(obj.getBoolean("italic"));
				if (obj.has("underlined"))
					style.setUnderlined(obj.getBoolean("underlined"));
				if (obj.has("strikethrough"))
					style.setStrikethrough(obj.getBoolean("strikethrough"));
				if (obj.has("obfuscated"))
					style.setObfuscated(obj.getBoolean("obfuscated"));
				if (obj.has("color")) {
					String colorStr = obj.getString("color");
					EnumChatFormatting fmt = EnumChatFormatting.getValueByName(colorStr);
					if (fmt == null) {
						try {
							fmt = EnumChatFormatting.valueOf(colorStr.toUpperCase());
						} catch (IllegalArgumentException ex) {
							fmt = null;
						}
					}
					if (fmt != null) {
						style.setColor(fmt);
					}
				}
				if (obj.has("clickEvent")) {
					JSONObject click = obj.getJSONObject("clickEvent");
					if (click.has("action") && click.has("value")) {
						ClickEvent.Action action = ClickEvent.Action.getValueByCanonicalName(click.getString("action"));
						String value = click.getString("value");
						if (action != null && value != null && action.shouldAllowInChat()) {
							style.setChatClickEvent(new ClickEvent(action, value));
						}
					}
				}
				if (obj.has("hoverEvent")) {
					JSONObject hover = obj.getJSONObject("hoverEvent");
					if (hover.has("action") && hover.has("value")) {
						HoverEvent.Action action = HoverEvent.Action.getValueByCanonicalName(hover.getString("action"));
						if (action != null && action.shouldAllowInChat()) {
							IChatComponent valueComp;
							if (action == HoverEvent.Action.SHOW_TEXT) {
								valueComp = IChatComponent.Serializer.fromJsonObject(hover.getJSONObject("value"));
							} else {
								String valueStr;
								try {
									valueStr = hover.getString("value");
								} catch (JSONException ex) {
									valueStr = hover.get("value").toString();
								}
								valueComp = new ChatComponentText(valueStr);
							}
							if (valueComp != null) {
								style.setChatHoverEvent(new HoverEvent(action, valueComp));
							}
						}
					}
				}
				return style;
			}
		}
	}
}
