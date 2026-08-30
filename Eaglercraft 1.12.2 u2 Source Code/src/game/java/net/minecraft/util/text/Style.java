package net.minecraft.util.text;

import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Style {
	/**
	 * The parent of this ChatStyle. Used for looking up values that this instance
	 * does not override.
	 */
	private Style parentStyle;
	private TextFormatting color;
	private Boolean bold;
	private Boolean italic;
	private Boolean underlined;
	private Boolean strikethrough;
	private Boolean obfuscated;
	private ClickEvent clickEvent;
	private HoverEvent hoverEvent;
	private String insertion;

	/**
	 * The base of the ChatStyle hierarchy. All ChatStyle instances are implicitly
	 * children of this.
	 */
	private static final Style ROOT = new Style() {
		@Nullable
		public TextFormatting getColor() {
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

		@Nullable
		public ClickEvent getClickEvent() {
			return null;
		}

		@Nullable
		public HoverEvent getHoverEvent() {
			return null;
		}

		@Nullable
		public String getInsertion() {
			return null;
		}

		public Style setColor(TextFormatting color) {
			throw new UnsupportedOperationException();
		}

		public Style setBold(Boolean boldIn) {
			throw new UnsupportedOperationException();
		}

		public Style setItalic(Boolean italic) {
			throw new UnsupportedOperationException();
		}

		public Style setStrikethrough(Boolean strikethrough) {
			throw new UnsupportedOperationException();
		}

		public Style setUnderlined(Boolean underlined) {
			throw new UnsupportedOperationException();
		}

		public Style setObfuscated(Boolean obfuscated) {
			throw new UnsupportedOperationException();
		}

		public Style setClickEvent(ClickEvent event) {
			throw new UnsupportedOperationException();
		}

		public Style setHoverEvent(HoverEvent event) {
			throw new UnsupportedOperationException();
		}

		public Style setParentStyle(Style parent) {
			throw new UnsupportedOperationException();
		}

		public String toString() {
			return "Style.ROOT";
		}

		public Style createShallowCopy() {
			return this;
		}

		public Style createDeepCopy() {
			return this;
		}

		public String getFormattingCode() {
			return "";
		}
	};

	@Nullable

	/**
	 * Gets the effective color of this ChatStyle.
	 */
	public TextFormatting getColor() {
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
				&& this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null
				&& this.insertion == null;
	}

	@Nullable

	/**
	 * The effective chat click event.
	 */
	public ClickEvent getClickEvent() {
		return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
	}

	@Nullable

	/**
	 * The effective chat hover event.
	 */
	public HoverEvent getHoverEvent() {
		return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
	}

	@Nullable

	/**
	 * Get the text to be inserted into Chat when the component is shift-clicked
	 */
	public String getInsertion() {
		return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
	}

	/**
	 * Sets the color for this ChatStyle to the given value. Only use color values
	 * for this; set other values using the specific methods.
	 */
	public Style setColor(TextFormatting color) {
		this.color = color;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be in bold. Set to false
	 * if, e.g., the parent style is bold and you want text of this style to be
	 * unbolded.
	 */
	public Style setBold(Boolean boldIn) {
		this.bold = boldIn;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be italicized. Set to false
	 * if, e.g., the parent style is italicized and you want to override that for
	 * this style.
	 */
	public Style setItalic(Boolean italic) {
		this.italic = italic;
		return this;
	}

	/**
	 * Sets whether or not to format text of this ChatStyle using strikethrough. Set
	 * to false if, e.g., the parent style uses strikethrough and you want to
	 * override that for this style.
	 */
	public Style setStrikethrough(Boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be underlined. Set to false
	 * if, e.g., the parent style is underlined and you want to override that for
	 * this style.
	 */
	public Style setUnderlined(Boolean underlined) {
		this.underlined = underlined;
		return this;
	}

	/**
	 * Sets whether or not text of this ChatStyle should be obfuscated. Set to false
	 * if, e.g., the parent style is obfuscated and you want to override that for
	 * this style.
	 */
	public Style setObfuscated(Boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}

	/**
	 * Sets the event that should be run when text of this ChatStyle is clicked on.
	 */
	public Style setClickEvent(ClickEvent event) {
		this.clickEvent = event;
		return this;
	}

	/**
	 * Sets the event that should be run when text of this ChatStyle is hovered
	 * over.
	 */
	public Style setHoverEvent(HoverEvent event) {
		this.hoverEvent = event;
		return this;
	}

	/**
	 * Set a text to be inserted into Chat when the component is shift-clicked
	 */
	public Style setInsertion(String insertion) {
		this.insertion = insertion;
		return this;
	}

	/**
	 * Sets the fallback ChatStyle to use if this ChatStyle does not override some
	 * value. Without a parent, obvious defaults are used (bold: false, underlined:
	 * false, etc).
	 */
	public Style setParentStyle(Style parent) {
		this.parentStyle = parent;
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
			StringBuilder stringbuilder = new StringBuilder();

			if (this.getColor() != null) {
				stringbuilder.append((Object) this.getColor());
			}

			if (this.getBold()) {
				stringbuilder.append((Object) TextFormatting.BOLD);
			}

			if (this.getItalic()) {
				stringbuilder.append((Object) TextFormatting.ITALIC);
			}

			if (this.getUnderlined()) {
				stringbuilder.append((Object) TextFormatting.UNDERLINE);
			}

			if (this.getObfuscated()) {
				stringbuilder.append((Object) TextFormatting.OBFUSCATED);
			}

			if (this.getStrikethrough()) {
				stringbuilder.append((Object) TextFormatting.STRIKETHROUGH);
			}

			return stringbuilder.toString();
		}
	}

	/**
	 * Gets the immediate parent of this ChatStyle.
	 */
	private Style getParent() {
		return this.parentStyle == null ? ROOT : this.parentStyle;
	}

	public String toString() {
		return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold
				+ ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated
				+ ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion="
				+ this.getInsertion() + '}';
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof Style)) {
			return false;
		} else {
			boolean flag;
			label77: {
				Style style = (Style) p_equals_1_;

				if (this.getBold() == style.getBold() && this.getColor() == style.getColor()
						&& this.getItalic() == style.getItalic() && this.getObfuscated() == style.getObfuscated()
						&& this.getStrikethrough() == style.getStrikethrough()
						&& this.getUnderlined() == style.getUnderlined()) {
					label71: {
						if (this.getClickEvent() != null) {
							if (!this.getClickEvent().equals(style.getClickEvent())) {
								break label71;
							}
						} else if (style.getClickEvent() != null) {
							break label71;
						}

						if (this.getHoverEvent() != null) {
							if (!this.getHoverEvent().equals(style.getHoverEvent())) {
								break label71;
							}
						} else if (style.getHoverEvent() != null) {
							break label71;
						}

						if (this.getInsertion() != null) {
							if (this.getInsertion().equals(style.getInsertion())) {
								break label77;
							}
						} else if (style.getInsertion() == null) {
							break label77;
						}
					}
				}

				flag = false;
				return flag;
			}
			flag = true;
			return flag;
		}
	}

	public int hashCode() {
		int i = this.color.hashCode();
		i = 31 * i + this.bold.hashCode();
		i = 31 * i + this.italic.hashCode();
		i = 31 * i + this.underlined.hashCode();
		i = 31 * i + this.strikethrough.hashCode();
		i = 31 * i + this.obfuscated.hashCode();
		i = 31 * i + this.clickEvent.hashCode();
		i = 31 * i + this.hoverEvent.hashCode();
		i = 31 * i + this.insertion.hashCode();
		return i;
	}

	/**
	 * Creates a shallow copy of this style. Changes to this instance's values will
	 * not be reflected in the copy, but changes to the parent style's values WILL
	 * be reflected in both this instance and the copy, wherever either does not
	 * override a value.
	 */
	public Style createShallowCopy() {
		Style style = new Style();
		style.bold = this.bold;
		style.italic = this.italic;
		style.strikethrough = this.strikethrough;
		style.underlined = this.underlined;
		style.obfuscated = this.obfuscated;
		style.color = this.color;
		style.clickEvent = this.clickEvent;
		style.hoverEvent = this.hoverEvent;
		style.parentStyle = this.parentStyle;
		style.insertion = this.insertion;
		return style;
	}

	/**
	 * Creates a deep copy of this style. No changes to this instance or its parent
	 * style will be reflected in the copy.
	 */
	public Style createDeepCopy() {
		Style style = new Style();
		style.setBold(Boolean.valueOf(this.getBold()));
		style.setItalic(Boolean.valueOf(this.getItalic()));
		style.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
		style.setUnderlined(Boolean.valueOf(this.getUnderlined()));
		style.setObfuscated(Boolean.valueOf(this.getObfuscated()));
		style.setColor(this.getColor());
		style.setClickEvent(this.getClickEvent());
		style.setHoverEvent(this.getHoverEvent());
		style.setInsertion(this.getInsertion());
		return style;
	}

	public static class Serializer implements JSONTypeCodec<Style, JSONObject> {
		@Nullable
		public Style deserialize(JSONObject jsonobject) throws JSONException {
			Style style = new Style();

			if (jsonobject == null) {
				return null;
			} else {
				if (jsonobject.has("bold")) {
					style.bold = jsonobject.getBoolean("bold");
				}

				if (jsonobject.has("italic")) {
					style.italic = jsonobject.getBoolean("italic");
				}

				if (jsonobject.has("underlined")) {
					style.underlined = jsonobject.getBoolean("underlined");
				}

				if (jsonobject.has("strikethrough")) {
					style.strikethrough = jsonobject.getBoolean("strikethrough");
				}

				if (jsonobject.has("obfuscated")) {
					style.obfuscated = jsonobject.getBoolean("obfuscated");
				}

				if (jsonobject.has("color")) {
					style.color = TextFormatting.getValueByName(jsonobject.getString("color"));
				}

				if (jsonobject.has("insertion")) {
					style.insertion = jsonobject.getString("insertion");
				}

				if (jsonobject.has("clickEvent")) {
					JSONObject jsonobject1 = jsonobject.getJSONObject("clickEvent");

					if (jsonobject1 != null) {
						String jsonprimitive = jsonobject1.optString("action");
						ClickEvent.Action clickevent$action = jsonprimitive == null ? null
								: ClickEvent.Action.getValueByCanonicalName(jsonprimitive);
						String jsonprimitive1 = jsonobject1.optString("value");

						if (clickevent$action != null && jsonprimitive1 != null
								&& clickevent$action.shouldAllowInChat()) {
							style.clickEvent = new ClickEvent(clickevent$action, jsonprimitive1);
						}
					}
				}

				if (jsonobject.has("hoverEvent")) {
					JSONObject jsonobject2 = jsonobject.getJSONObject("hoverEvent");

					if (jsonobject2 != null) {
						String jsonprimitive2 = jsonobject2.optString("action");
						HoverEvent.Action hoverevent$action = jsonprimitive2 == null ? null
								: HoverEvent.Action.getValueByCanonicalName(jsonprimitive2);
						ITextComponent itextcomponent = (ITextComponent) JSONTypeProvider
								.deserializeNoCast(jsonobject2.get("value"), ITextComponent.class);

						if (hoverevent$action != null && itextcomponent != null
								&& hoverevent$action.shouldAllowInChat()) {
							style.hoverEvent = new HoverEvent(hoverevent$action, itextcomponent);
						}
					}
				}

				return style;
			}
		}

		@Nullable
		public JSONObject serialize(Style p_serialize_1_) {
			if (p_serialize_1_.isEmpty()) {
				return null;
			} else {
				JSONObject jsonobject = new JSONObject();

				if (p_serialize_1_.bold != null) {
					jsonobject.put("bold", p_serialize_1_.bold);
				}

				if (p_serialize_1_.italic != null) {
					jsonobject.put("italic", p_serialize_1_.italic);
				}

				if (p_serialize_1_.underlined != null) {
					jsonobject.put("underlined", p_serialize_1_.underlined);
				}

				if (p_serialize_1_.strikethrough != null) {
					jsonobject.put("strikethrough", p_serialize_1_.strikethrough);
				}

				if (p_serialize_1_.obfuscated != null) {
					jsonobject.put("obfuscated", p_serialize_1_.obfuscated);
				}

				if (p_serialize_1_.color != null) {
					jsonobject.put("color", p_serialize_1_.color.getFriendlyName());
				}

				if (p_serialize_1_.insertion != null) {
					jsonobject.put("insertion", p_serialize_1_.insertion);
				}

				if (p_serialize_1_.clickEvent != null) {
					JSONObject jsonobject1 = new JSONObject();
					jsonobject1.put("action", p_serialize_1_.clickEvent.getAction().getCanonicalName());
					jsonobject1.put("value", p_serialize_1_.clickEvent.getValue());
					jsonobject.put("clickEvent", jsonobject1);
				}

				if (p_serialize_1_.hoverEvent != null) {
					JSONObject jsonobject2 = new JSONObject();
					jsonobject2.put("action", p_serialize_1_.hoverEvent.getAction().getCanonicalName());
					jsonobject2.put("value", p_serialize_1_.hoverEvent.getValue());
					jsonobject.put("hoverEvent", jsonobject2);
				}

				return jsonobject;
			}
		}
	}
}
