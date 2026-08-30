package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import net.lax1dude.eaglercraft.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.IIcon;
import net.minecraft.util.ReportedException;

public class TextureAtlasSprite extends EaglerTextureAtlasSprite {
	public TextureAtlasSprite(String spriteName) {
		super(spriteName);
	}
}
