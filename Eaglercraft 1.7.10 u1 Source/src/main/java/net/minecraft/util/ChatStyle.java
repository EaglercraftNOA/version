package net.minecraft.util;

import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import org.json.JSONObject;
import org.json.JSONException;
import net.minecraft.util.ChatComponentText;

public class ChatStyle {
	/**
	 * The parent of this ChatStyle. Used for looking up values that this instance
	 * does not override.
	 */
	private ChatStyle parentStyle;
	private EnumChatFormatting color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private ClickEvent chatClickEvent;
	private HoverEvent chatHoverEvent;

	/**
	 * The base of the ChatStyle hierarchy. All ChatStyle instances are implicitly
	 * children of this.
	 */
	private static final ChatStyle rootStyle = new ChatStyle() {
		private static final String __OBFID = "CL_00001267";

		public EnumChatFormatting getColor() {
			return null;
		}

		public boolean getBold() {
			return false;
		}

		public boolean getItalic() {
			return false;
		}

		public boolean getStrikethrough() {
			return false;
		}

		public boolean getUnderlined() {
			return false;
		}

		public boolean getObfuscated() {
			return false;
		}

		public ClickEvent getChatClickEvent() {
			return null;
		}

		public HoverEvent getChatHoverEvent() {
			return null;
		}

		public ChatStyle setColor(EnumChatFormatting p_150238_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setBold(Boolean p_150227_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setItalic(Boolean p_150217_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setStrikethrough(Boolean p_150225_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setUnderlined(Boolean p_150228_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setObfuscated(Boolean p_150237_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setChatClickEvent(ClickEvent p_150241_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setChatHoverEvent(HoverEvent p_150209_1_) {
			throw new UnsupportedOperationException();
		}

		public ChatStyle setParentStyle(ChatStyle p_150221_1_) {
			throw new UnsupportedOperationException();
		}

		public String toString() {
			return "Style.ROOT";
		}

		public ChatStyle createShallowCopy() {
			return this;
		}

		public ChatStyle createDeepCopy() {
			return this;
		}

		public String getFormattingCode() {
			return "";
		}
	};
	private static final String __OBFID = "CL_00001266";

	/**
	 * Gets the effective color of this ChatStyle.
	 */
	public EnumChatFormatting getColor() {
		return this.color == null ? this.getParent().getColor() : this.color;
	}

	/**
	 * Whether or not text of this ChatStyle should be in bold.
	 */
	public boolean getBold() {
		return this.bold == null ? this.getParent().getBold() : this.bold.booleanValue();
	}

	/**
	 * Whether or not text of this ChatStyle should be italicized.
	 */
	public boolean getItalic() {
		return this.italic == null ? this.getParent().getItalic() : this.italic.booleanValue();
	}

	/**
	 * Whether or not to format text of this ChatStyle using strikethrough.
	 */
	public boolean getStrikethrough() {
		return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough.booleanValue();
	}

	/**
	 * Whether or not text of this ChatStyle should be underlined.
	 */
	public boolean getUnderlined() {
		return this.underlined == null ? this.getParent().getUnderlined() : this.underlined.booleanValue();
	}

	/**
	 * Whether or not text of this ChatStyle should be obfuscated.
	 */
	public boolean getObfuscated() {
		return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated.booleanValue();
	}

	/**
	 * Whether or not this style is empty (inherits everything from the parent).
	 */
	public boolean isEmpty() {
		return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null
				&& this.obfuscated == null && this.color == null && this.chatClickEvent == null
				&& this.chatHoverEvent == null;
	}

	/**
	 * The effective chat click event.
	 */
	public ClickEvent getChatClickEvent() {
		return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
	}

	/**
	 * The effective chat hover event.
	 */
	public HoverEvent getChatHoverEvent() {
		return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
	}

	/**
	 * Sets the color for this ChatStyle to the given value. Only use color values
	 * for this; set other values using the specific methods.
	 */
	public ChatStyle setColor(EnumChatFormatting p_150238_1_) {
		this.color = p_150238_1_;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be in bold. Set to false
	 * if, e.g., the parent style is bold and you want text of this style to be
	 * unbolded.
	 */
	public ChatStyle setBold(Boolean p_150227_1_) {
		this.bold = p_150227_1_;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be italicized. Set to false
	 * if, e.g., the parent style is italicized and you want to override that for
	 * this style.
	 */
	public ChatStyle setItalic(Boolean p_150217_1_) {
		this.italic = p_150217_1_;
		return this;
	}

	/**
	 * Sets whether or not to format text of this ChatStyle using strikethrough. Set
	 * to false if, e.g., the parent style uses strikethrough and you want to
	 * override that for this style.
	 */
	public ChatStyle setStrikethrough(Boolean p_150225_1_) {
		this.strikethrough = p_150225_1_;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be underlined. Set to false
	 * if, e.g., the parent style is underlined and you want to override that for
	 * this style.
	 */
	public ChatStyle setUnderlined(Boolean p_150228_1_) {
		this.underlined = p_150228_1_;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be obfuscated. Set to false
	 * if, e.g., the parent style is obfuscated and you want to override that for
	 * this style.
	 */
	public ChatStyle setObfuscated(Boolean p_150237_1_) {
		this.obfuscated = p_150237_1_;
		return this;
	}

	/**
	 * Sets the event that should be run when text of this ChatStyle is clicked on.
	 */
	public ChatStyle setChatClickEvent(ClickEvent p_150241_1_) {
		this.chatClickEvent = p_150241_1_;
		return this;
	}

	/**
	 * Sets the event that should be run when text of this ChatStyle is hovered
	 * over.
	 */
	public ChatStyle setChatHoverEvent(HoverEvent p_150209_1_) {
		this.chatHoverEvent = p_150209_1_;
		return this;
	}

	/**
	 * Sets the fallback ChatStyle to use if this ChatStyle does not override some
	 * value. Without a parent, obvious defaults are used (bold: false, underlined:
	 * false, etc).
	 */
	public ChatStyle setParentStyle(ChatStyle p_150221_1_) {
		this.parentStyle = p_150221_1_;
		return this;
	}

	/**
	 * Gets the equivalent text formatting code for this style, without the initial
	 * section sign (U+00A7) character.
	 */
	public String getFormattingCode() {
		if (this.isEmpty()) {
			return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
		} else {
			StringBuilder var1 = new StringBuilder();

			if (this.getColor() != null) {
				var1.append(this.getColor());
			}

			if (this.getBold()) {
				var1.append(EnumChatFormatting.BOLD);
			}

			if (this.getItalic()) {
				var1.append(EnumChatFormatting.ITALIC);
			}

			if (this.getUnderlined()) {
				var1.append(EnumChatFormatting.UNDERLINE);
			}

			if (this.getObfuscated()) {
				var1.append(EnumChatFormatting.OBFUSCATED);
			}

			if (this.getStrikethrough()) {
				var1.append(EnumChatFormatting.STRIKETHROUGH);
			}

			return var1.toString();
		}
	}

	/**
	 * Gets the immediate parent of this ChatStyle.
	 */
	private ChatStyle getParent() {
		return this.parentStyle == null ? rootStyle : this.parentStyle;
	}

	public String toString() {
		return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold
				+ ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated
				+ ", clickEvent=" + this.getChatClickEvent() + ", hoverEvent=" + this.getChatHoverEvent() + '}';
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof ChatStyle)) {
			return false;
		} else {
			ChatStyle var2 = (ChatStyle) p_equals_1_;
			boolean var10000;

			if (this.getBold() == var2.getBold() && this.getColor() == var2.getColor()
					&& this.getItalic() == var2.getItalic() && this.getObfuscated() == var2.getObfuscated()
					&& this.getStrikethrough() == var2.getStrikethrough()
					&& this.getUnderlined() == var2.getUnderlined()) {
				label56: {
					if (this.getChatClickEvent() != null) {
						if (!this.getChatClickEvent().equals(var2.getChatClickEvent())) {
							break label56;
						}
					} else if (var2.getChatClickEvent() != null) {
						break label56;
					}

					if (this.getChatHoverEvent() != null) {
						if (!this.getChatHoverEvent().equals(var2.getChatHoverEvent())) {
							break label56;
						}
					} else if (var2.getChatHoverEvent() != null) {
						break label56;
					}

					var10000 = true;
					return var10000;
				}
			}

			var10000 = false;
			return var10000;
		}
	}

	public int hashCode() {
		int var1 = this.color.hashCode();
		var1 = 31 * var1 + this.bold.hashCode();
		var1 = 31 * var1 + this.italic.hashCode();
		var1 = 31 * var1 + this.underlined.hashCode();
		var1 = 31 * var1 + this.strikethrough.hashCode();
		var1 = 31 * var1 + this.obfuscated.hashCode();
		var1 = 31 * var1 + this.chatClickEvent.hashCode();
		var1 = 31 * var1 + this.chatHoverEvent.hashCode();
		return var1;
	}

	/**
	 * Creates a shallow copy of this style. Changes to this instance's values will
	 * not be reflected in the copy, but changes to the parent style's values WILL
	 * be reflected in both this instance and the copy, wherever either does not
	 * override a value.
	 */
	public ChatStyle createShallowCopy() {
		ChatStyle var1 = new ChatStyle();
		var1.bold = this.bold;
		var1.italic = this.italic;
		var1.strikethrough = this.strikethrough;
		var1.underlined = this.underlined;
		var1.obfuscated = this.obfuscated;
		var1.color = this.color;
		var1.chatClickEvent = this.chatClickEvent;
		var1.chatHoverEvent = this.chatHoverEvent;
		var1.parentStyle = this.parentStyle;
		return var1;
	}

	/**
	 * Creates a deep copy of this style. No changes to this instance or its parent
	 * style will be reflected in the copy.
	 */
	public ChatStyle createDeepCopy() {
		ChatStyle var1 = new ChatStyle();
		var1.setBold(Boolean.valueOf(this.getBold()));
		var1.setItalic(Boolean.valueOf(this.getItalic()));
		var1.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
		var1.setUnderlined(Boolean.valueOf(this.getUnderlined()));
		var1.setObfuscated(Boolean.valueOf(this.getObfuscated()));
		var1.setColor(this.getColor());
		var1.setChatClickEvent(this.getChatClickEvent());
		var1.setChatHoverEvent(this.getChatHoverEvent());
		return var1;
	}

	public static class Serializer {
		public static JSONObject toJsonObject(ChatStyle style) throws JSONException {
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
					hover.put("value", IChatComponent.Serializer.toJsonObject(style.getChatHoverEvent().getValue()));
				} else {
					hover.put("value", style.getChatHoverEvent().getValue().getUnformattedText());
				}
				obj.put("hoverEvent", hover);
			}
			return obj;
		}

		public static ChatStyle fromJsonObject(JSONObject obj) throws JSONException {
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
			if (obj.has("color"))
				style.setColor(EnumChatFormatting.valueOf(obj.getString("color")));
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
						IChatComponent valueComponent;
						if (action == HoverEvent.Action.SHOW_TEXT) {

							valueComponent = IChatComponent.Serializer.fromJsonObject(hover.getJSONObject("value"));
						} else {

							String valueStr;
							try {
								valueStr = hover.getString("value");
							} catch (JSONException ex) {

								valueStr = hover.get("value").toString();
							}
							valueComponent = new ChatComponentText(valueStr);
						}
						if (valueComponent != null) {
							style.setChatHoverEvent(new HoverEvent(action, valueComponent));
						}
					}
				}
			}
			return style;
		}
	}
}
