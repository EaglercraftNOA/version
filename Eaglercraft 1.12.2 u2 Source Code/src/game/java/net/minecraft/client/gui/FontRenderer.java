package net.minecraft.client.gui;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.minecraft.FontMappingHelper;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class FontRenderer implements IResourceManagerReloadListener {
	private static final ResourceLocation[] UNICODE_PAGE_LOCATIONS = new ResourceLocation[256];

	/** Array of width of all the characters in default.png */
	protected final int[] charWidth = new int[256];

	/** the height in pixels of default text */
	public int FONT_HEIGHT = 9;
	public EaglercraftRandom fontRandom = new EaglercraftRandom();

	/**
	 * Array of the start/end column (in upper/lower nibble) for every glyph in the
	 * /font directory.
	 */
	private final byte[] glyphWidth = new byte[65536];

	/**
	 * Array of RGB triplets defining the 16 standard chat colors followed by 16
	 * darker version of the same colors for drop shadows.
	 */
	protected final int[] colorCode = new int[32];
	protected final ResourceLocation locationFontTexture;

	/** The RenderEngine used to load and setup glyph textures. */
	protected final TextureManager renderEngine;

	/** Current X coordinate at which to draw the next character. */
	protected float posX;

	/** Current Y coordinate at which to draw the next character. */
	protected float posY;

	/**
	 * If true, strings should be rendered with Unicode fonts instead of the
	 * default.png font
	 */
	protected boolean unicodeFlag;

	/**
	 * If true, the Unicode Bidirectional Algorithm should be run before rendering
	 * any string.
	 */
	private boolean bidiFlag;

	/** Used to specify new red value for the current color. */
	protected float red;

	/** Used to specify new blue value for the current color. */
	protected float blue;

	/** Used to specify new green value for the current color. */
	protected float green;

	/** Used to speify new alpha value for the current color. */
	protected float alpha;

	/** Text color of the currently rendering string. */
	protected int textColor;

	/** Set if the "k" style (random) is active in currently rendering string */
	protected boolean randomStyle;

	/** Set if the "l" style (bold) is active in currently rendering string */
	protected boolean boldStyle;

	/** Set if the "o" style (italic) is active in currently rendering string */
	protected boolean italicStyle;

	/**
	 * Set if the "n" style (underlined) is active in currently rendering string
	 */
	protected boolean underlineStyle;

	/**
	 * Set if the "m" style (strikethrough) is active in currently rendering string
	 */
	protected boolean strikethroughStyle;

	protected static char[] codepointLookup = new char[] { 192, 193, 194, 200, 202, 203, 205, 211, 212, 213, 218, 223,
			227, 245, 287, 304, 305, 338, 339, 350, 351, 372, 373, 382, 519, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36,
			37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
			64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
			91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113,
			114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 199, 252, 233, 226, 228, 224, 229, 231,
			234, 235, 232, 239, 238, 236, 196, 197, 201, 230, 198, 244, 246, 242, 251, 249, 255, 214, 220, 248, 163,
			216, 215, 402, 225, 237, 243, 250, 241, 209, 170, 186, 191, 174, 172, 189, 188, 161, 171, 187, 9617, 9618,
			9619, 9474, 9508, 9569, 9570, 9558, 9557, 9571, 9553, 9559, 9565, 9564, 9563, 9488, 9492, 9524, 9516, 9500,
			9472, 9532, 9566, 9567, 9562, 9556, 9577, 9574, 9568, 9552, 9580, 9575, 9576, 9572, 9573, 9561, 9560, 9554,
			9555, 9579, 9578, 9496, 9484, 9608, 9604, 9612, 9616, 9600, 945, 946, 915, 960, 931, 963, 956, 964, 934,
			920, 937, 948, 8734, 8709, 8712, 8745, 8801, 177, 8805, 8804, 8992, 8993, 247, 8776, 176, 8729, 183, 8730,
			8319, 178, 9632, 0 };

	public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn,
			boolean unicode) {
		this.locationFontTexture = location;
		this.renderEngine = textureManagerIn;
		this.unicodeFlag = unicode;
		textureManagerIn.bindTexture(this.locationFontTexture);

		for (int i = 0; i < 32; ++i) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i >> 0 & 1) * 170 + j;

			if (i == 6) {
				k += 85;
			}

			if (gameSettingsIn.anaglyph) {
				int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
				int k1 = (k * 30 + l * 70) / 100;
				int l1 = (k * 30 + i1 * 70) / 100;
				k = j1;
				l = k1;
				i1 = l1;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}

		this.readGlyphSizes();
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.readFontTexture();
		this.readGlyphSizes();
	}

	private void readFontTexture() {
		IResource iresource = null;
		ImageData bufferedimage;

		try {
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture);
			bufferedimage = TextureUtil.readBufferedImage(iresource.getInputStream());
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}

		int lvt_3_2_ = bufferedimage.width;
		int lvt_4_1_ = bufferedimage.height;
		int[] lvt_5_1_ = new int[lvt_3_2_ * lvt_4_1_];
		bufferedimage.getRGB(0, 0, lvt_3_2_, lvt_4_1_, lvt_5_1_, 0, lvt_3_2_);
		int lvt_6_1_ = lvt_4_1_ / 16;
		int lvt_7_1_ = lvt_3_2_ / 16;
		boolean lvt_8_1_ = true;
		float lvt_9_1_ = 8.0F / (float) lvt_7_1_;

		for (int lvt_10_1_ = 0; lvt_10_1_ < 256; ++lvt_10_1_) {
			int j1 = lvt_10_1_ % 16;
			int k1 = lvt_10_1_ / 16;

			if (lvt_10_1_ == 32) {
				this.charWidth[lvt_10_1_] = 4;
			}

			int l1;

			for (l1 = lvt_7_1_ - 1; l1 >= 0; --l1) {
				int i2 = j1 * lvt_7_1_ + l1;
				boolean flag1 = true;

				for (int j2 = 0; j2 < lvt_6_1_ && flag1; ++j2) {
					int k2 = (k1 * lvt_7_1_ + j2) * lvt_3_2_;

					if ((lvt_5_1_[i2 + k2] >> 24 & 255) != 0) {
						flag1 = false;
					}
				}

				if (!flag1) {
					break;
				}
			}

			++l1;
			this.charWidth[lvt_10_1_] = (int) (0.5D + (double) ((float) l1 * lvt_9_1_)) + 1;
		}
	}

	private void readGlyphSizes() {
		IResource iresource = null;

		try {
			iresource = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("font/glyph_sizes.bin"));
			iresource.getInputStream().read(this.glyphWidth);
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}
	}

	/**
	 * Render the given char
	 */
	private float renderChar(char ch, boolean italic) {
		if (ch == ' ') {
			return 4.0F;
		} else {
			int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
					.indexOf(ch);
			return i != -1 && !this.unicodeFlag ? this.renderDefaultChar(i, italic)
					: this.renderUnicodeChar(ch, italic);
		}
	}

	/**
	 * Render a single character with the default.png font at current (posX,posY)
	 * location...
	 */
	private float renderDefaultChar(int ch, boolean italic) {
		int i = ch % 16 * 8;
		int j = ch / 16 * 8;
		int k = italic ? 1 : 0;
		this.renderEngine.bindTexture(this.locationFontTexture);
		int l = this.charWidth[ch];
		float f = (float) l - 0.01F;

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();

		worldrenderer.begin(Tessellator.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

		worldrenderer.pos(this.posX + (float) k, this.posY, 0.0F).tex((float) i / 128.0F, (float) j / 128.0F)
				.endVertex();

		worldrenderer.pos(this.posX - (float) k, this.posY + 7.99F, 0.0F)
				.tex((float) i / 128.0F, ((float) j + 7.99F) / 128.0F).endVertex();

		worldrenderer.pos(this.posX + f - 1.0F + (float) k, this.posY, 0.0F)
				.tex(((float) i + f - 1.0F) / 128.0F, (float) j / 128.0F).endVertex();

		worldrenderer.pos(this.posX + f - 1.0F - (float) k, this.posY + 7.99F, 0.0F)
				.tex(((float) i + f - 1.0F) / 128.0F, ((float) j + 7.99F) / 128.0F).endVertex();

		tessellator.draw();

		return (float) l;
	}

	private ResourceLocation getUnicodePageLocation(int page) {
		if (UNICODE_PAGE_LOCATIONS[page] == null) {
			UNICODE_PAGE_LOCATIONS[page] = new ResourceLocation(
					HString.format("textures/font/unicode_page_%02x.png", new Object[] { Integer.valueOf(page) }));
		}

		return UNICODE_PAGE_LOCATIONS[page];
	}

	/**
	 * Load one of the /font/glyph_XX.png into a new GL texture and store the
	 * texture ID in glyphTextureName array.
	 */
	private void loadGlyphTexture(int page) {
		this.renderEngine.bindTexture(this.getUnicodePageLocation(page));
	}

	/**
	 * Render a single Unicode character at current (posX,posY) location using one
	 * of the /font/glyph_XX.png files...
	 */
	private float renderUnicodeChar(char ch, boolean italic) {
		int i = this.glyphWidth[ch] & 255;

		if (i == 0) {
			return 0.0F;
		} else {
			int j = ch / 256;
			this.loadGlyphTexture(j);
			int k = i >>> 4;
			int l = i & 15;
			float f = (float) k;
			float f1 = (float) (l + 1);
			float f2 = (float) (ch % 16 * 16) + f;
			float f3 = (float) ((ch & 255) / 16 * 16);
			float f4 = f1 - f - 0.02F;
			float f5 = italic ? 1.0F : 0.0F;
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();

			worldrenderer.begin(Tessellator.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

			worldrenderer.pos(this.posX + f5, this.posY, 0.0F).tex(f2 / 256.0F, f3 / 256.0F).endVertex();

			worldrenderer.pos(this.posX - f5, this.posY + 7.99F, 0.0F).tex(f2 / 256.0F, (f3 + 15.98F) / 256.0F)
					.endVertex();

			worldrenderer.pos(this.posX + f4 / 2.0F + f5, this.posY, 0.0F).tex((f2 + f4) / 256.0F, f3 / 256.0F)
					.endVertex();

			worldrenderer.pos(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F)
					.tex((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F).endVertex();

			tessellator.draw();

			return (f1 - f) / 2.0F + 1.0F;
		}
	}

	/**
	 * Draws the specified string with a shadow.
	 */
	public int drawStringWithShadow(String text, float x, float y, int color) {
		return this.drawString(text, x, y, color, true);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, int x, int y, int color) {
		return this.drawString(text, (float) x, (float) y, color, false);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, float x, float y, int color, boolean dropShadow) {
		GlStateManager.enableAlpha();
		this.resetStyles();
		int i;

		if (dropShadow) {
			i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
			i = Math.max(i, this.renderString(text, x, y, color, false));
		} else {
			i = this.renderString(text, x, y, color, false);
		}

		return i;
	}

	/**
	 * Reset all style flag fields in the class to false; called at the start of
	 * string rendering
	 */
	protected void resetStyles() {
		this.randomStyle = false;
		this.boldStyle = false;
		this.italicStyle = false;
		this.underlineStyle = false;
		this.strikethroughStyle = false;
	}

	/**
	 * Render a single line string at the current (posX,posY) and update posX
	 */
	protected void renderStringAtPos(String text, boolean shadow) {
		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);

			if (c0 == 167 && i + 1 < text.length()) {
				int i1 = "0123456789abcdefklmnor".indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase().charAt(0));

				if (i1 < 16) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					if (i1 < 0 || i1 > 15) {
						i1 = 15;
					}

					if (shadow) {
						i1 += 16;
					}

					int j1 = this.colorCode[i1];
					this.textColor = j1;
					GlStateManager.color((float) (j1 >> 16) / 255.0F, (float) (j1 >> 8 & 255) / 255.0F,
							(float) (j1 & 255) / 255.0F, this.alpha);
				} else if (i1 == 16) {
					this.randomStyle = true;
				} else if (i1 == 17) {
					this.boldStyle = true;
				} else if (i1 == 18) {
					this.strikethroughStyle = true;
				} else if (i1 == 19) {
					this.underlineStyle = true;
				} else if (i1 == 20) {
					this.italicStyle = true;
				} else if (i1 == 21) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					GlStateManager.color(this.red, this.blue, this.green, this.alpha);
				}

				++i;
			} else {
				int j = FontMappingHelper.lookupChar(c0, false);

				if (this.randomStyle && j != -1) {
					int k = this.getCharWidth(c0);
					char[] chars = FontRenderer.codepointLookup;

					char c1;
					while (true) {
						j = this.fontRandom.nextInt(chars.length);
						c1 = chars[j];

						if (k == this.getCharWidth(c1)) {
							break;
						}
					}

					c0 = c1;
				}

				float f1 = this.unicodeFlag ? 0.5F : 1.0F;
				boolean flag = (c0 == 0 || j == -1 || this.unicodeFlag) && shadow;

				if (flag) {
					this.posX -= f1;
					this.posY -= f1;
				}

				float f = this.renderChar(c0, this.italicStyle);

				if (flag) {
					this.posX += f1;
					this.posY += f1;
				}

				if (this.boldStyle) {
					this.posX += f1;

					if (flag) {
						this.posX -= f1;
						this.posY -= f1;
					}

					this.renderChar(c0, this.italicStyle);
					this.posX -= f1;

					if (flag) {
						this.posX += f1;
						this.posY += f1;
					}

					++f;
				}

				if (this.strikethroughStyle) {
					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer bufferbuilder = tessellator.getBuffer();
					GlStateManager.disableTexture2D();
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
					bufferbuilder.pos((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D)
							.endVertex();
					bufferbuilder
							.pos((double) (this.posX + f), (double) (this.posY + (float) (this.FONT_HEIGHT / 2)), 0.0D)
							.endVertex();
					bufferbuilder.pos((double) (this.posX + f),
							(double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
					bufferbuilder
							.pos((double) this.posX, (double) (this.posY + (float) (this.FONT_HEIGHT / 2) - 1.0F), 0.0D)
							.endVertex();
					tessellator.draw();
					GlStateManager.enableTexture2D();
				}

				if (this.underlineStyle) {
					Tessellator tessellator1 = Tessellator.getInstance();
					WorldRenderer bufferbuilder1 = tessellator1.getBuffer();
					GlStateManager.disableTexture2D();
					bufferbuilder1.begin(7, DefaultVertexFormats.POSITION);
					int l = this.underlineStyle ? -1 : 0;
					bufferbuilder1.pos((double) (this.posX + (float) l),
							(double) (this.posY + (float) this.FONT_HEIGHT), 0.0D).endVertex();
					bufferbuilder1.pos((double) (this.posX + f), (double) (this.posY + (float) this.FONT_HEIGHT), 0.0D)
							.endVertex();
					bufferbuilder1
							.pos((double) (this.posX + f), (double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D)
							.endVertex();
					bufferbuilder1.pos((double) (this.posX + (float) l),
							(double) (this.posY + (float) this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
					tessellator1.draw();
					GlStateManager.enableTexture2D();
				}

				this.posX += (float) ((int) f);
			}
		}
	}

	/**
	 * Render string either left or right aligned depending on bidiFlag
	 */
	private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow) {
		return this.renderString(text, (float) x, (float) y, color, dropShadow);
	}

	/**
	 * Render single line string by setting GL color, current (posX,posY), and
	 * calling renderStringAtPos()
	 */
	private int renderString(String text, float x, float y, int color, boolean dropShadow) {
		if (text == null) {
			return 0;
		} else {
			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (dropShadow) {
				color = (color & 16579836) >> 2 | color & -16777216;
			}

			this.red = (float) (color >> 16 & 255) / 255.0F;
			this.blue = (float) (color >> 8 & 255) / 255.0F;
			this.green = (float) (color & 255) / 255.0F;
			this.alpha = (float) (color >> 24 & 255) / 255.0F;
			GlStateManager.color(this.red, this.blue, this.green, this.alpha);
			this.posX = x;
			this.posY = y;
			this.renderStringAtPos(text, dropShadow);
			return (int) this.posX;
		}
	}

	/**
	 * Returns the width of this string. Equivalent of
	 * FontMetrics.stringWidth(String s).
	 */
	public int getStringWidth(String text) {
		if (text == null) {
			return 0;
		} else {
			int i = 0;
			boolean flag = false;

			for (int j = 0; j < text.length(); ++j) {
				char c0 = text.charAt(j);
				int k = this.getCharWidth(c0);

				if (k < 0 && j < text.length() - 1) {
					++j;
					c0 = text.charAt(j);

					if (c0 != 'l' && c0 != 'L') {
						if (c0 == 'r' || c0 == 'R') {
							flag = false;
						}
					} else {
						flag = true;
					}

					k = 0;
				}

				i += k;

				if (flag && k > 0) {
					++i;
				}
			}

			return i;
		}
	}

	/**
	 * Returns the width of this character as rendered.
	 */
	public int getCharWidth(char character) {
		if (character == 167) {
			return -1;
		} else if (character == ' ') {
			return 4;
		} else {
			int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
					.indexOf(character);

			if (character > 0 && i != -1 && !this.unicodeFlag) {
				return this.charWidth[i];
			} else if (this.glyphWidth[character] != 0) {
				int j = this.glyphWidth[character] & 255;
				int k = j >>> 4;
				int l = j & 15;
				++l;
				return (l - k) / 2 + 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public String trimStringToWidth(String text, int width) {
		return this.trimStringToWidth(text, width, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	public String trimStringToWidth(String text, int width, boolean reverse) {
		StringBuilder stringbuilder = new StringBuilder();
		int i = 0;
		int j = reverse ? text.length() - 1 : 0;
		int k = reverse ? -1 : 1;
		boolean flag = false;
		boolean flag1 = false;

		for (int l = j; l >= 0 && l < text.length() && i < width; l += k) {
			char c0 = text.charAt(l);
			int i1 = this.getCharWidth(c0);

			if (flag) {
				flag = false;

				if (c0 != 'l' && c0 != 'L') {
					if (c0 == 'r' || c0 == 'R') {
						flag1 = false;
					}
				} else {
					flag1 = true;
				}
			} else if (i1 < 0) {
				flag = true;
			} else {
				i += i1;

				if (flag1) {
					++i;
				}
			}

			if (i > width) {
				break;
			}

			if (reverse) {
				stringbuilder.insert(0, c0);
			} else {
				stringbuilder.append(c0);
			}
		}

		return stringbuilder.toString();
	}

	/**
	 * Remove all newline characters from the end of the string
	 */
	private String trimStringNewline(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	/**
	 * Splits and draws a String with wordwrap (maximum length is parameter k)
	 */
	public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
		this.resetStyles();
		this.textColor = textColor;
		str = this.trimStringNewline(str);
		this.renderSplitString(str, x, y, wrapWidth, false);
	}

	/**
	 * Perform actual work of rendering a multi-line string with wordwrap and with
	 * darker drop shadow color if flag is set
	 */
	private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
		List<String> lst = this.listFormattedStringToWidth(str, wrapWidth);
		for (int i = 0; i < lst.size(); i++) {
			String s = lst.get(i);
			this.renderStringAligned(s, x, y, wrapWidth, this.textColor, addShadow);
			y += this.FONT_HEIGHT;
		}
	}

	/**
	 * Returns the width of the wordwrapped String (maximum length is parameter k)
	 */
	public int splitStringWidth(String str, int maxLength) {
		return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
	}

	/**
	 * Set unicodeFlag controlling whether strings should be rendered with Unicode
	 * fonts instead of the default.png font.
	 */
	public void setUnicodeFlag(boolean unicodeFlagIn) {
		this.unicodeFlag = unicodeFlagIn;
	}

	/**
	 * Get unicodeFlag controlling whether strings should be rendered with Unicode
	 * fonts instead of the default.png font.
	 */
	public boolean getUnicodeFlag() {
		return this.unicodeFlag;
	}

	/**
	 * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run
	 * before rendering any string.
	 */
	public void setBidiFlag(boolean bidiFlagIn) {
		this.bidiFlag = bidiFlagIn;
	}

	public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
		return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth, 0).split("\n"));
	}

	/**
	 * Inserts newline and formatting into a string to wrap it within the specified
	 * width.
	 */
	String wrapFormattedStringToWidth(String str, int wrapWidth, int depthCheck) {
		if (depthCheck > 20) {
			return str;
		}
		int i = this.sizeStringToWidth(str, wrapWidth);
		if (str.length() <= i) {
			return str;
		} else {
			String s = str.substring(0, i);
			char c0 = str.charAt(i);
			boolean flag = c0 == 32 || c0 == 10;
			String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
			return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth, ++depthCheck);
		}
	}

	/**
	 * Determines how many characters from the string will fit into the specified
	 * width.
	 */
	private int sizeStringToWidth(String str, int wrapWidth) {
		int i = str.length();
		int j = 0;
		int k = 0;
		int l = -1;

		for (boolean flag = false; k < i; ++k) {
			char c0 = str.charAt(k);

			switch (c0) {
			case '\n':
				--k;
				break;

			case ' ':
				l = k;

			default:
				j += this.getCharWidth(c0);

				if (flag) {
					++j;
				}

				break;

			case '\u00a7':
				if (k < i - 1) {
					++k;
					char c1 = str.charAt(k);

					if (c1 != 'l' && c1 != 'L') {
						if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
							flag = false;
						}
					} else {
						flag = true;
					}
				}
			}

			if (c0 == '\n') {
				++k;
				l = k;
				break;
			}

			if (j > wrapWidth) {
				break;
			}
		}

		return k != i && l != -1 && l < k ? l : k;
	}

	/**
	 * Checks if the char code is a hexadecimal character, used to set colour.
	 */
	private static boolean isFormatColor(char colorChar) {
		return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f'
				|| colorChar >= 'A' && colorChar <= 'F';
	}

	/**
	 * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
	 */
	private static boolean isFormatSpecial(char formatChar) {
		return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r'
				|| formatChar == 'R';
	}

	/**
	 * Digests a string for nonprinting formatting characters then returns a string
	 * containing only that formatting.
	 */
	public static String getFormatFromString(String text) {
		String s = "";
		int i = -1;
		int j = text.length();

		while ((i = text.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				char c0 = text.charAt(i + 1);

				if (isFormatColor(c0)) {
					s = "\u00a7" + c0;
				} else if (isFormatSpecial(c0)) {
					s = s + "\u00a7" + c0;
				}
			}
		}

		return s;
	}

	/**
	 * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be
	 * run before rendering any string
	 */
	public boolean getBidiFlag() {
		return this.bidiFlag;
	}

	public int getColorCode(char character) {
		int i = "0123456789abcdef".indexOf(character);
		return i >= 0 && i < this.colorCode.length ? this.colorCode[i] : -1;
	}
}
