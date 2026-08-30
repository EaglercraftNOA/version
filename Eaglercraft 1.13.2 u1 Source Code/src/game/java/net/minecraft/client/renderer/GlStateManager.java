package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GlStateManager {
   private static final float[] MATRIX_SCRATCH = new float[16];
   private static final net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer VEC4_SCRATCH = EagRuntime.allocateFloatBuffer(4);
   private static net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer uploadScratch = null;
   private static byte[] uploadScratchArray = new byte[0];

   public static void pushLightingAttrib() {
   }

   public static void popAttrib() {
   }

   public static void disableAlphaTest() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableAlpha();
   }

   public static void enableAlphaTest() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableAlpha();
   }

   public static void alphaFunc(int func, float ref) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.alphaFunc(func, ref);
   }

   public static void enableLighting() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableLighting();
   }

   public static void disableLighting() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableLighting();
   }

   public static void enableLight(int light) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableMCLight(light);
   }

   public static void disableLight(int light) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableMCLight(light);
   }

   public static void enableColorMaterial() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableColorMaterial();
   }

   public static void disableColorMaterial() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableColorMaterial();
   }

   public static void colorMaterial(int face, int mode) {
   }

   public static void lightfv(int light, int pname, FloatBuffer params) {
   }

   public static void lightModelfv(int pname, FloatBuffer params) {
   }

   public static void enableMCLight(int light, float diffuse, double dirX, double dirY, double dirZ, double dirW) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableMCLight(light, diffuse, dirX, dirY, dirZ, dirW);
   }

   public static void disableMCLight(int light) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableMCLight(light);
   }

   public static void setMCLightAmbient(float red, float green, float blue) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setMCLightAmbient(red, green, blue);
   }

   public static void normal3f(float nx, float ny, float nz) {
      EaglercraftGPU.glNormal3f(nx, ny, nz);
   }

   public static void disableDepthTest() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableDepth();
   }

   public static void enableDepthTest() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableDepth();
   }

   public static void depthFunc(int depthFunc) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.depthFunc(depthFunc);
   }

   public static void depthMask(boolean flagIn) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.depthMask(flagIn);
   }

   public static void disableBlend() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableBlend();
   }

   public static void enableBlend() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableBlend();
   }

   public static void blendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.blendFunc(srcFactor.factor, dstFactor.factor);
   }

   public static void blendFunc(int srcFactor, int dstFactor) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.blendFunc(srcFactor, dstFactor);
   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor, GlStateManager.SourceFactor srcFactorAlpha, GlStateManager.DestFactor dstFactorAlpha) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.tryBlendFuncSeparate(srcFactor.factor, dstFactor.factor, srcFactorAlpha.factor, dstFactorAlpha.factor);
   }

   public static void blendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
   }

   public static void blendEquation(int blendEquation) {
      EaglercraftGPU.glBlendEquation(blendEquation);
   }

   public static void enableOutlineMode(int color) {
   }

   public static void disableOutlineMode() {
   }

   public static void enableFog() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableFog();
   }

   public static void disableFog() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableFog();
   }

   public static void fogMode(GlStateManager.FogMode fogMode) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFog(fogMode.capabilityId);
   }

   public static void fogDensity(float param) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFogDensity(param);
   }

   public static void fogStart(float param) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFogStart(param);
   }

   public static void fogEnd(float param) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFogEnd(param);
   }

   public static void fogfv(int pname, FloatBuffer param) {
      if (pname == 2918) {
         int i = param.position();
         VEC4_SCRATCH.put(0, param.get(i));
         VEC4_SCRATCH.put(1, param.get(i + 1));
         VEC4_SCRATCH.put(2, param.get(i + 2));
         VEC4_SCRATCH.put(3, param.get(i + 3));
         VEC4_SCRATCH.position(0).limit(4);
         EaglercraftGPU.glFog(pname, VEC4_SCRATCH);
      }

   }

   public static void fogi(int pname, int param) {
      if (pname == 2917) {
         net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFog(param);
      }

   }

   public static void enableCull() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableCull();
   }

   public static void disableCull() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableCull();
   }

   public static void cullFace(GlStateManager.CullFace cullFace) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.cullFace(cullFace.mode);
   }

   public static void polygonMode(int face, int mode) {
   }

   public static void enablePolygonOffset() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enablePolygonOffset();
   }

   public static void disablePolygonOffset() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disablePolygonOffset();
   }

   public static void polygonOffset(float factor, float units) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.doPolygonOffset(factor, units);
   }

   public static void enableColorLogic() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableColorLogic();
   }

   public static void disableColorLogic() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableColorLogic();
   }

   public static void logicOp(GlStateManager.LogicOp logicOperation) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.colorLogicOp(logicOperation.opcode);
   }

   public static void logicOp(int opcode) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.colorLogicOp(opcode);
   }

   public static void enableTexGen(GlStateManager.TexGen texGen) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableTexGen();
   }

   public static void disableTexGen(GlStateManager.TexGen texGen) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableTexGen();
   }

   public static void texGenMode(GlStateManager.TexGen texGen, int mode) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.texGen(texGen.eagler, mode);
   }

   public static void texGenParam(GlStateManager.TexGen texGen, int pname, FloatBuffer params) {
      int i = params.position();
      VEC4_SCRATCH.put(0, params.get(i));
      VEC4_SCRATCH.put(1, params.get(i + 1));
      VEC4_SCRATCH.put(2, params.get(i + 2));
      VEC4_SCRATCH.put(3, params.get(i + 3));
      VEC4_SCRATCH.position(0).limit(4);
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.func_179105_a(texGen.eagler, pname, VEC4_SCRATCH);
   }

   public static void activeTexture(int texture) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setActiveTexture(texture);
   }

   public static void enableTexture2D() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableTexture2D();
   }

   public static void disableTexture2D() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableTexture2D();
   }

   public static void texEnvfv(int target, int parameterName, FloatBuffer parameters) {
   }

   public static void texEnvi(int target, int parameterName, int parameter) {
   }

   public static void texEnvf(int target, int parameterName, float parameter) {
   }

   public static void texParameterf(int target, int parameterName, float parameter) {
      EaglercraftGPU.glTexParameterf(target, parameterName, parameter);
   }

   public static void texParameteri(int target, int parameterName, int parameter) {
      EaglercraftGPU.glTexParameteri(target, parameterName, parameter);
   }

   public static int glGetTexLevelParameteri(int target, int level, int parameterName) {
      return EaglercraftGPU.glGetTexLevelParameteri(target, level, parameterName);
   }

   public static int generateTexture() {
      return net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.generateTexture();
   }

   public static void deleteTexture(int texture) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.deleteTexture(texture);
   }

   public static void bindTexture(int texture) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.bindTexture(texture);
   }

   public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
      if (pixels == null) {
         EaglercraftGPU.glTexImage2D(target, level, internalFormat, width, height, border, format, type, (net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer)null);
      } else {
         EaglercraftGPU.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
      }

   }

   public static void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, long pixels) {
      GL11.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, pixels);
   }

   public static void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, java.nio.ByteBuffer pixels) {
      int i = pixels.remaining();
      net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer bytebuffer = uploadScratch;
      if (bytebuffer == null || bytebuffer.capacity() < i) {
         if (bytebuffer != null) {
            EagRuntime.freeByteBuffer(bytebuffer);
         }

         bytebuffer = EagRuntime.allocateByteBuffer(i);
         uploadScratch = bytebuffer;
      }

      bytebuffer.clear();
      if (pixels.hasArray()) {
         bytebuffer.put(pixels.array(), pixels.arrayOffset() + pixels.position(), i);
      } else {
         byte[] abyte = uploadScratchArray;
         if (abyte.length < i) {
            abyte = new byte[i];
            uploadScratchArray = abyte;
         }

         pixels.duplicate().get(abyte, 0, i);
         bytebuffer.put(abyte, 0, i);
      }

      bytebuffer.position(0).limit(i);
      EaglercraftGPU.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, bytebuffer);
   }

   public static void getTexImage(int tex, int level, int format, int type, long pixels) {
      GL11.glGetTexImage(tex, level, format, type, pixels);
   }

   public static void enableNormalize() {
   }

   public static void disableNormalize() {
   }

   public static void shadeModel(int mode) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.shadeModel(mode);
   }

   public static void enableRescaleNormal() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableRescaleNormal();
   }

   public static void disableRescaleNormal() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableRescaleNormal();
   }

   public static void viewport(int x, int y, int width, int height) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.viewport(x, y, width, height);
   }

   public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.colorMask(red, green, blue, alpha);
   }

   public static void clearDepth(double depth) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.clearDepth((float)depth);
   }

   public static void clearColor(float red, float green, float blue, float alpha) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.clearColor(red, green, blue, alpha);
   }

   public static void clear(int mask) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.clear(mask);
   }

   public static void matrixMode(int mode) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.matrixMode(mode);
   }

   public static void loadIdentity() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.loadIdentity();
   }

   public static void pushMatrix() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.pushMatrix();
   }

   public static void popMatrix() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.popMatrix();
   }

   public static void getFloatv(int pname, FloatBuffer params) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.getFloat(pname, MATRIX_SCRATCH);
      int i = params.position();
      int j = Math.min(params.remaining(), 16);

      for(int k = 0; k < j; ++k) {
         params.put(i + k, MATRIX_SCRATCH[k]);
      }

   }

   public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.ortho(left, right, bottom, top, zNear, zFar);
   }

   public static void rotatef(float angle, float x, float y, float z) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate(angle, x, y, z);
   }

   public static void rotated(double angle, double x, double y, double z) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate((float)angle, (float)x, (float)y, (float)z);
   }

   public static void scalef(float x, float y, float z) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale(x, y, z);
   }

   public static void scaled(double x, double y, double z) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale(x, y, z);
   }

   public static void translatef(float x, float y, float z) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(x, y, z);
   }

   public static void translated(double x, double y, double z) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(x, y, z);
   }

   public static void multMatrixf(FloatBuffer matrix) {
      int i = matrix.position();

      for(int j = 0; j < 16; ++j) {
         MATRIX_SCRATCH[j] = matrix.get(i + j);
      }

      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.multMatrix(MATRIX_SCRATCH);
   }

   public static void multMatrixf(Matrix4f matrixIn) {
      for(int i = 0; i < 16; ++i) {
         MATRIX_SCRATCH[i] = matrixIn.get(i & 3, i >> 2);
      }

      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.multMatrix(MATRIX_SCRATCH);
   }

   public static void color4f(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
   }

   public static void color3f(float colorRed, float colorGreen, float colorBlue) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.color(colorRed, colorGreen, colorBlue);
   }

   public static void resetColor() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.resetColor();
   }

   public static void normalPointer(int type, int stride, int pointer) {
      GL11.glNormalPointer(type, stride, (long)pointer);
   }

   public static void normalPointer(int type, int stride, java.nio.ByteBuffer buffer) {
      GL11.glNormalPointer(type, stride, buffer);
   }

   public static void normalPointer(int type, int stride, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer buffer) {
      GL11.glNormalPointer(type, stride, buffer);
   }

   public static void texCoordPointer(int size, int type, int stride, int buffer_offset) {
      GL11.glTexCoordPointer(size, type, stride, (long)buffer_offset);
   }

   public static void texCoordPointer(int size, int type, int stride, java.nio.ByteBuffer buffer) {
      GL11.glTexCoordPointer(size, type, stride, buffer);
   }

   public static void texCoordPointer(int size, int type, int stride, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer buffer) {
      GL11.glTexCoordPointer(size, type, stride, buffer);
   }

   public static void vertexPointer(int size, int type, int stride, int buffer_offset) {
      GL11.glVertexPointer(size, type, stride, (long)buffer_offset);
   }

   public static void vertexPointer(int size, int type, int stride, java.nio.ByteBuffer buffer) {
      GL11.glVertexPointer(size, type, stride, buffer);
   }

   public static void vertexPointer(int size, int type, int stride, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer buffer) {
      GL11.glVertexPointer(size, type, stride, buffer);
   }

   public static void colorPointer(int size, int type, int stride, int buffer_offset) {
      GL11.glColorPointer(size, type, stride, (long)buffer_offset);
   }

   public static void colorPointer(int size, int type, int stride, java.nio.ByteBuffer buffer) {
      GL11.glColorPointer(size, type, stride, buffer);
   }

   public static void colorPointer(int size, int type, int stride, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer buffer) {
      GL11.glColorPointer(size, type, stride, buffer);
   }

   public static void disableClientState(int cap) {
      GL11.glDisableClientState(cap);
   }

   public static void enableClientState(int cap) {
      GL11.glEnableClientState(cap);
   }

   public static void drawArrays(int mode, int first, int count) {
      GL11.glDrawArrays(mode, first, count);
   }

   public static void lineWidth(float width) {
      EaglercraftGPU.glLineWidth(width);
   }

   public static void callList(int list) {
      EaglercraftGPU.glCallList(list);
   }

   public static void deleteLists(int list, int range) {
      for(int i = 0; i < range; ++i) {
         EaglercraftGPU.glDeleteLists(list + i);
      }

   }

   public static void newList(int list, int mode) {
      EaglercraftGPU.glNewList(list, mode);
   }

   public static void endList() {
      EaglercraftGPU.glEndList();
   }

   public static int genLists(int range) {
      return EaglercraftGPU.glGenLists(range);
   }

   public static void pixelStorei(int parameterName, int param) {
      GL11.glPixelStorei(parameterName, param);
   }

   public static void pixelTransferf(int param, float value) {
   }

   public static void readPixels(int x, int y, int width, int height, int format, int type, long pixels) {
      GL11.glReadPixels(x, y, width, height, format, type, pixels);
   }

   public static int getError() {
      return EaglercraftGPU.glGetError();
   }

   public static String getString(int name) {
      return EaglercraftGPU.glGetString(name);
   }

   public static void enableBlendProfile(GlStateManager.Profile profile) {
      profile.apply();
   }

   public static void disableBlendProfile(GlStateManager.Profile profile) {
      profile.clean();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int mode;

      private CullFace(int modeIn) {
         this.mode = modeIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int factor;

      private DestFactor(int factorIn) {
         this.factor = factorIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int capabilityId;

      private FogMode(int capabilityIn) {
         this.capabilityId = capabilityIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int opcode;

      private LogicOp(int opcodeIn) {
         this.opcode = opcodeIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Profile {
      DEFAULT {
         public void apply() {
            GlStateManager.disableAlphaTest();
            GlStateManager.alphaFunc(519, 0.0F);
            GlStateManager.disableLighting();

            for(int i = 0; i < 8; ++i) {
               GlStateManager.disableLight(i);
            }

            GlStateManager.disableColorMaterial();
            GlStateManager.disableDepthTest();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendEquation(32774);
            GlStateManager.disableFog();
            GlStateManager.fogi(2917, 2048);
            GlStateManager.fogDensity(1.0F);
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(1.0F);
            GlStateManager.fogfv(2918, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disableColorLogic();
            GlStateManager.logicOp(5379);
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.activeTexture(33984);
            GlStateManager.texParameteri(3553, 10240, 9729);
            GlStateManager.texParameteri(3553, 10241, 9986);
            GlStateManager.texParameteri(3553, 10242, 10497);
            GlStateManager.texParameteri(3553, 10243, 10497);
            GlStateManager.shadeModel(7425);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.lineWidth(1.0F);
            GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
         }

         public void clean() {
         }
      },
      PLAYER_SKIN {
         public void apply() {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         }

         public void clean() {
            GlStateManager.disableBlend();
         }
      },
      TRANSPARENT_MODEL {
         public void apply() {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         public void clean() {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      };

      private Profile() {
      }

      public abstract void apply();

      public abstract void clean();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int factor;

      private SourceFactor(int factorIn) {
         this.factor = factorIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TexGen {
      S(net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.TexGen.S),
      T(net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.TexGen.T),
      R(net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.TexGen.R),
      Q(net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.TexGen.Q);

      final net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.TexGen eagler;

      private TexGen(net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.TexGen eagler) {
         this.eagler = eagler;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;
   }
}
