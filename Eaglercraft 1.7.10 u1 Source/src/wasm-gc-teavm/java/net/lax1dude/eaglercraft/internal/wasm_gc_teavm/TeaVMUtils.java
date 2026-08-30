/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.internal.wasm_gc_teavm;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Uint8Array;

public class TeaVMUtils {

	@JSBody(params = { "obj" }, script = "return !!obj;")
	public static native boolean isTruthy(JSObject object);

	@JSBody(params = { "obj" }, script = "return !obj;")
	public static native boolean isNotTruthy(JSObject object);

	@JSBody(params = { "obj" }, script = "return obj === undefined;")
	public static native boolean isUndefined(JSObject object);

	@JSBody(params = { "buf" }, script = "return new Int8Array(buf);")
	public static native Int8Array unwrapByteArray(byte[] buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native ArrayBuffer unwrapArrayBuffer(byte[] buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native ArrayBufferView unwrapArrayBufferView(byte[] buf);

	@JSBody(params = { "buf" }, script = "return new Int32Array(buf);")
	public static native Int32Array unwrapIntArray(int[] buf);

	@JSBody(params = { "buf" }, script = "return new Float32Array(buf);")
	public static native Float32Array unwrapFloatArray(float[] buf);

	@JSBody(params = { "buf" }, script = "return new Int16Array(buf);")
	public static native Int16Array unwrapShortArray(short[] buf);

	@JSBody(params = { "buf" }, script = "return new Uint8Array(buf);")
	public static native Uint8Array unwrapUnsignedByteArray(byte[] buf);

	@JSBody(params = { "buf" }, script = "return buf;")
	public static native byte[] wrapByteArray(Int8Array buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native byte[] wrapByteArrayBuffer(ArrayBuffer buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native byte[] wrapByteArrayBufferView(ArrayBufferView buf);

	@JSBody(params = { "buf" }, script = "return buf;")
	public static native int[] wrapIntArray(Int32Array buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native int[] wrapIntArrayBuffer(ArrayBuffer buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native int[] wrapIntArrayBufferView(ArrayBufferView buf);

	@JSBody(params = { "buf" }, script = "return buf;")
	public static native float[] wrapFloatArray(Float32Array buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native float[] wrapFloatArrayBuffer(ArrayBuffer buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native float[] wrapFloatArrayBufferView(ArrayBufferView buf);

	@JSBody(params = { "buf" }, script = "return buf;")
	public static native short[] wrapShortArray(Int16Array buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native short[] wrapShortArrayBuffer(ArrayBuffer buf);

	@JSBody(params = { "buf" }, script = "return new DataView(buf.buffer, buf.byteOffset, buf.byteLength);")
	public static native short[] wrapShortArrayBufferView(ArrayBufferView buf);

	@JSBody(params = { "buf" }, script = "return buf;")
	public static native byte[] wrapUnsignedByteArray(Uint8Array buf);

}