/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

/**
 * Stores the capabilities of an OpenGL context. Created by {@link GL#createCapabilities()}.
 */
public final class GLCapabilities {

	// OpenGL version support flags
	public final boolean OpenGL11 = true;
	public final boolean OpenGL12 = true;
	public final boolean OpenGL13 = true;
	public final boolean OpenGL14 = true;
	public final boolean OpenGL15 = true;
	public final boolean OpenGL20 = true;
	public final boolean OpenGL21 = true;
	public final boolean OpenGL30;
	public final boolean OpenGL31 = false;
	public final boolean OpenGL32 = false;
	public final boolean OpenGL33 = false;
	public final boolean OpenGL40 = false;
	public final boolean OpenGL41 = false;
	public final boolean OpenGL42 = false;
	public final boolean OpenGL43 = false;
	public final boolean OpenGL44 = false;
	public final boolean OpenGL45 = false;
	public final boolean OpenGL46 = false;

	// Common extension flags
	public final boolean GL_ARB_vertex_array_object;
	public final boolean GL_ARB_framebuffer_object = true;
	public final boolean GL_ARB_texture_storage;
	public final boolean GL_ARB_instanced_arrays;
	public final boolean GL_ARB_draw_instanced;
	public final boolean GL_EXT_texture_filter_anisotropic;
	public final boolean GL_ARB_texture_non_power_of_two;
	public final boolean GL_ARB_texture_env_combine;
	public final boolean GL_ARB_shader_objects = true;
	public final boolean GL_ARB_vertex_shader = true;
	public final boolean GL_ARB_fragment_shader = true;
	public final boolean GL_ARB_vertex_buffer_object = true;
	public final boolean GL_ARB_multitexture = true;
	public final boolean GL_EXT_framebuffer_object;
	public final boolean GL_EXT_blend_func_separate;
	public final boolean GL_NV_fog_distance = false;
	public final boolean GL_KHR_debug = false;
	public final boolean GL_ARB_debug_output = false;

	GLCapabilities() {
		boolean vaoCapable = net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU.checkVAOCapable();
		boolean instancingCapable = net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU.checkInstancingCapable();
		boolean texStorageCapable = net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU.checkTexStorageCapable();
		boolean npotCapable = net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU.checkNPOTCapable();
		int esVersion = net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU.checkOpenGLESVersion();

		this.OpenGL30 = esVersion >= 300;

		this.GL_ARB_vertex_array_object = vaoCapable;
		this.GL_ARB_texture_storage = texStorageCapable;
		this.GL_ARB_instanced_arrays = instancingCapable;
		this.GL_ARB_draw_instanced = instancingCapable;
		this.GL_EXT_texture_filter_anisotropic = true; // typically available
		this.GL_ARB_texture_non_power_of_two = npotCapable;
		this.GL_ARB_texture_env_combine = true;
		this.GL_EXT_framebuffer_object = true;
		this.GL_EXT_blend_func_separate = true;
	}
}
