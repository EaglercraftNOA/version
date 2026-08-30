package net.peyton.eagler.minecraft;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;

public class TextureLocation {
	
	private int textureID = -1;
	public static TextureLocation terrain = new TextureLocation("/terrain.png");
	public static TextureLocation particles = new TextureLocation("/particles.png");
	public static TextureLocation items = new TextureLocation("/gui/items.png");
	public static TextureLocation gui = new TextureLocation("/gui/gui.png");
	public static TextureLocation guiContainer = new TextureLocation("/gui/container.png");
	public static TextureLocation icons = new TextureLocation("/gui/icons.png");
	
	private String textureName;
	private boolean init = false;
	
	public TextureLocation(String s) {
		textureName = s;
	}
	
	public void bindTexture() {
		if(textureName == null) {
			return;
		}
		if(textureID == -1 && !init) {
			textureID = Minecraft.getMinecraft().renderEngine.getTexture(textureName);
			init = true;
		}
		if(textureID >= 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		}
	}
	
	public int getTextureID() {
		if(textureID == -1) {
			textureID = Minecraft.getMinecraft().renderEngine.getTexture(textureName);
		}
		return textureID;
	}

}
