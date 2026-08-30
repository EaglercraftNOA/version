package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

@OnlyIn(Dist.CLIENT)
public class OpenGlHelper {
   public static boolean nvidia = false;
   public static boolean ati = false;
   public static final int GL_FRAMEBUFFER = 36160;
   public static final int GL_RENDERBUFFER = 36161;
   public static final int GL_COLOR_ATTACHMENT0 = 36064;
   public static final int GL_DEPTH_ATTACHMENT = 36096;
   public static final int GL_FRAMEBUFFER_COMPLETE = 36053;
   public static final int GL_FB_INCOMPLETE_ATTACHMENT = 36054;
   public static final int GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
   public static final int GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
   public static final int GL_FB_INCOMPLETE_READ_BUFFER = 36060;
   public static final boolean framebufferSupported = true;
   public static final int GL_LINK_STATUS = 35714;
   public static final int GL_COMPILE_STATUS = 35713;
   public static final int GL_VERTEX_SHADER = 35633;
   public static final int GL_FRAGMENT_SHADER = 35632;
   public static final int GL_TEXTURE0 = 33984;
   public static final int GL_TEXTURE1 = 33985;
   public static final int GL_TEXTURE2 = 33986;
   public static final int GL_COMBINE = 34160;
   public static final int GL_INTERPOLATE = 34165;
   public static final int GL_PRIMARY_COLOR = 34167;
   public static final int GL_CONSTANT = 34166;
   public static final int GL_PREVIOUS = 34168;
   public static final int GL_COMBINE_RGB = 34161;
   public static final int GL_SOURCE0_RGB = 34176;
   public static final int GL_SOURCE1_RGB = 34177;
   public static final int GL_SOURCE2_RGB = 34178;
   public static final int GL_OPERAND0_RGB = 34192;
   public static final int GL_OPERAND1_RGB = 34193;
   public static final int GL_OPERAND2_RGB = 34194;
   public static final int GL_COMBINE_ALPHA = 34162;
   public static final int GL_SOURCE0_ALPHA = 34184;
   public static final int GL_SOURCE1_ALPHA = 34185;
   public static final int GL_SOURCE2_ALPHA = 34186;
   public static final int GL_OPERAND0_ALPHA = 34200;
   public static final int GL_OPERAND1_ALPHA = 34201;
   public static final int GL_OPERAND2_ALPHA = 34202;
   public static final boolean extBlendFuncSeparate = false;
   public static final boolean openGL21 = true;
   public static final boolean shadersSupported = true;
   private static String logText = "";
   private static String cpu;
   public static final boolean vboSupported = true;
   public static final boolean vboSupportedAti = false;
   public static final int GL_ARRAY_BUFFER = 34962;
   public static final int GL_STATIC_DRAW = 35044;
   private static final Map<Integer, String> MAP_ERROR_MESSAGES = Util.make(Maps.newHashMap(), (p_203093_0_) -> {
      p_203093_0_.put(0, "No error");
      p_203093_0_.put(1280, "Enum parameter is invalid for this function");
      p_203093_0_.put(1281, "Parameter is invalid for this function");
      p_203093_0_.put(1282, "Current state is invalid for this function");
      p_203093_0_.put(1283, "Stack overflow");
      p_203093_0_.put(1284, "Stack underflow");
      p_203093_0_.put(1285, "Out of memory");
      p_203093_0_.put(1286, "Operation on incomplete framebuffer");
   });

   public static void init() {
      cpu = "Jenny CPU";
   }

   public static boolean areShadersSupported() {
      return shadersSupported;
   }

   public static String getLogText() {
      return logText;
   }

   public static int glGetProgrami(int program, int pname) {
      return GL20.glGetProgrami(program, pname);
   }

   public static void glAttachShader(int program, int shaderIn) {
      GL20.glAttachShader(program, shaderIn);
   }

   public static void glDeleteShader(int shaderIn) {
      GL20.glDeleteShader(shaderIn);
   }

   public static int glCreateShader(int type) {
      return GL20.glCreateShader(type);
   }

   public static void glShaderSource(int shaderIn, CharSequence string) {
      GL20.glShaderSource(shaderIn, string);
   }

   public static void glCompileShader(int shaderIn) {
      GL20.glCompileShader(shaderIn);
   }

   public static int glGetShaderi(int shaderIn, int pname) {
      return GL20.glGetShaderi(shaderIn, pname);
   }

   public static String glGetShaderInfoLog(int shaderIn, int maxLength) {
      return GL20.glGetShaderInfoLog(shaderIn, maxLength);
   }

   public static String glGetProgramInfoLog(int program, int maxLength) {
      return GL20.glGetProgramInfoLog(program, maxLength);
   }

   public static void glUseProgram(int program) {
      GL20.glUseProgram(program);
   }

   public static int glCreateProgram() {
      return GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int program) {
      GL20.glDeleteProgram(program);
   }

   public static void glLinkProgram(int program) {
      GL20.glLinkProgram(program);
   }

   public static int glGetUniformLocation(int programObj, CharSequence name) {
      return GL20.glGetUniformLocation(programObj, name);
   }

   public static void glUniform1iv(int location, IntBuffer values) {
      GL20.glUniform1iv(location, values);
   }

   public static void glUniform1i(int location, int v0) {
      GL20.glUniform1i(location, v0);
   }

   public static void glUniform1fv(int location, FloatBuffer values) {
      GL20.glUniform1fv(location, values);
   }

   public static void glUniform2iv(int location, IntBuffer values) {
      GL20.glUniform2iv(location, values);
   }

   public static void glUniform2fv(int location, FloatBuffer values) {
      GL20.glUniform2fv(location, values);
   }

   public static void glUniform3iv(int location, IntBuffer values) {
      GL20.glUniform3iv(location, values);
   }

   public static void glUniform3fv(int location, FloatBuffer values) {
      GL20.glUniform3fv(location, values);
   }

   public static void glUniform4iv(int location, IntBuffer values) {
      GL20.glUniform4iv(location, values);
   }

   public static void glUniform4fv(int location, FloatBuffer values) {
      GL20.glUniform4fv(location, values);
   }

   public static void glUniformMatrix2fv(int location, boolean transpose, FloatBuffer matrices) {
      GL20.glUniformMatrix2fv(location, transpose, matrices);
   }

   public static void glUniformMatrix3fv(int location, boolean transpose, FloatBuffer matrices) {
      GL20.glUniformMatrix3fv(location, transpose, matrices);
   }

   public static void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer matrices) {
      GL20.glUniformMatrix4fv(location, transpose, matrices);
   }

   public static int glGetAttribLocation(int program, CharSequence name) {
      return GL20.glGetAttribLocation(program, name);
   }

   public static int glGenBuffers() {
      return GL15.glGenBuffers();
   }

   public static void glBindBuffer(int target, int buffer) {
      GL15.glBindBuffer(target, buffer);
   }

   public static void glBufferData(int target, ByteBuffer data, int usage) {
      GL15.glBufferData(target, data, usage);
   }

   public static void glBufferData(int target, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer data, int usage) {
      GL15.glBufferData(target, data, usage);
   }

   public static void glDeleteBuffers(int buffer) {
      GL15.glDeleteBuffers(buffer);
   }

   public static boolean useVbo() {
      return false;
   }

   public static void glBindFramebuffer(int target, int framebufferIn) {
      GL30.glBindFramebuffer(target, framebufferIn);
   }

   public static void glBindRenderbuffer(int target, int renderbuffer) {
      GL30.glBindRenderbuffer(target, renderbuffer);
   }

   public static void glDeleteRenderbuffers(int renderbuffer) {
      GL30.glDeleteRenderbuffers(renderbuffer);
   }

   public static void glDeleteFramebuffers(int framebufferIn) {
      GL30.glDeleteFramebuffers(framebufferIn);
   }

   public static int glGenFramebuffers() {
      return GL30.glGenFramebuffers();
   }

   public static int glGenRenderbuffers() {
      return GL30.glGenRenderbuffers();
   }

   public static void glRenderbufferStorage(int target, int internalFormat, int width, int height) {
      GL30.glRenderbufferStorage(target, internalFormat, width, height);
   }

   public static void glFramebufferRenderbuffer(int target, int attachment, int renderBufferTarget, int renderBuffer) {
      GL30.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
   }

   public static int glCheckFramebufferStatus(int target) {
      return GL30.glCheckFramebufferStatus(target);
   }

   public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
      GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
   }

   public static void glActiveTexture(int texture) {
      GL13.glActiveTexture(texture);
   }

   public static void glClientActiveTexture(int texture) {
      GL13.glClientActiveTexture(texture);
   }

   public static void glMultiTexCoord2f(int target, float x, float y) {
      EaglercraftGPU.glMultiTexCoord2f(target, x, y);
   }

   public static void glBlendFuncSeparate(int sFactorRGB, int dFactorRGB, int sfactorAlpha, int dfactorAlpha) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.tryBlendFuncSeparate(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
   }

   public static boolean isFramebufferEnabled() {
      return Minecraft.getInstance().gameSettings.fboEnable;
   }

   public static String getCpu() {
      return cpu == null ? "<unknown>" : cpu;
   }

   public static void renderDirections(int size) {
      renderDirections(size, true, true, true);
   }

   public static void renderDirections(int size, boolean renderX, boolean renderY, boolean renderZ) {
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GL11.glLineWidth(4.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (renderX) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)size, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (renderY) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)size, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (renderZ) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)size).color(0, 0, 0, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(2.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (renderX) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)size, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if (renderY) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)size, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if (renderZ) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)size).color(127, 127, 255, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(1.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
   }

   public static String getErrorMessage(int error) {
      return MAP_ERROR_MESSAGES.get(error);
   }
}
