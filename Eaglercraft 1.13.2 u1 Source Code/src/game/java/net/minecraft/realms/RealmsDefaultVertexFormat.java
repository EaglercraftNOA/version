package net.minecraft.realms;

import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsDefaultVertexFormat {
   public static final RealmsVertexFormat BLOCK = new RealmsVertexFormat(VertexFormat.BLOCK);
   public static final RealmsVertexFormat BLOCK_NORMALS = new RealmsVertexFormat(VertexFormat.ITEM);
   public static final RealmsVertexFormat ENTITY = new RealmsVertexFormat(VertexFormat.OLDMODEL_POSITION_TEX_NORMAL);
   public static final RealmsVertexFormat PARTICLE = new RealmsVertexFormat(VertexFormat.PARTICLE_POSITION_TEX_COLOR_LMAP);
   public static final RealmsVertexFormat POSITION = new RealmsVertexFormat(VertexFormat.POSITION);
   public static final RealmsVertexFormat POSITION_COLOR = new RealmsVertexFormat(VertexFormat.POSITION_COLOR);
   public static final RealmsVertexFormat POSITION_TEX = new RealmsVertexFormat(VertexFormat.POSITION_TEX);
   public static final RealmsVertexFormat POSITION_NORMAL = new RealmsVertexFormat(VertexFormat.POSITION_NORMAL);
   public static final RealmsVertexFormat POSITION_TEX_COLOR = new RealmsVertexFormat(VertexFormat.POSITION_TEX_COLOR);
   public static final RealmsVertexFormat POSITION_TEX_NORMAL = new RealmsVertexFormat(VertexFormat.POSITION_TEX_NORMAL);
   public static final RealmsVertexFormat POSITION_TEX2_COLOR = new RealmsVertexFormat(VertexFormat.POSITION_TEX_LMAP_COLOR);
   public static final RealmsVertexFormat POSITION_TEX_COLOR_NORMAL = new RealmsVertexFormat(VertexFormat.POSITION_TEX_COLOR_NORMAL);
}
