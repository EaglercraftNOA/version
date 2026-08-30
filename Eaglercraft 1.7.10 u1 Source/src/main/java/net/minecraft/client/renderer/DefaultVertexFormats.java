package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.opengl.VertexFormat;

public class DefaultVertexFormats {
	public static final VertexFormat BLOCK = new VertexFormat(true, true, false, true);
	public static final VertexFormat POSITION = new VertexFormat(false, false, false, false);
	public static final VertexFormat POSITION_COLOR = new VertexFormat(false, true, false, false);
	public static final VertexFormat POSITION_TEX = new VertexFormat(true, false, false, false);
	public static final VertexFormat POSITION_TEX_COLOR = new VertexFormat(true, true, false, false);
	public static final VertexFormat POSITION_TEX_LMAP_COLOR = new VertexFormat(true, true, false, true);
}