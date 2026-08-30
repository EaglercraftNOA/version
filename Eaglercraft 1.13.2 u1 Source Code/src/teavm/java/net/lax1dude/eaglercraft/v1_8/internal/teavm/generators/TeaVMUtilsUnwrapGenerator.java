package net.lax1dude.eaglercraft.v1_8.internal.teavm.generators;

import org.teavm.backend.javascript.codegen.SourceWriter;
import org.teavm.backend.javascript.spi.Generator;
import org.teavm.backend.javascript.spi.GeneratorContext;
import org.teavm.backend.javascript.spi.Injector;
import org.teavm.backend.javascript.spi.InjectorContext;
import org.teavm.model.MethodReference;

/**
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
public class TeaVMUtilsUnwrapGenerator {

	// WARNING: This code uses internal TeaVM APIs that may not have
	// been intended for end users of the compiler to program with

	private static void writeWrappedTypedArray(SourceWriter writer, String parName, String arrayClassName,
			String typedArrayExpr) {
		writer.append("return ").append(parName).ws().append('?').ws().append("new ").appendFunction(arrayClassName)
				.append("(").append(typedArrayExpr).append(")").ws().append(':').ws().append("null;").softNewLine();
	}

	public static class UnwrapArrayBuffer implements Injector {

		@Override
		public void generate(InjectorContext context, MethodReference methodRef) {
			context.writeExpr(context.getArgument(0));
			context.getWriter().append(".data.buffer");
		}

	}

	public static class UnwrapTypedArray implements Injector {

		@Override
		public void generate(InjectorContext context, MethodReference methodRef) {
			context.writeExpr(context.getArgument(0));
			context.getWriter().append(".data");
		}

	}

	public static class WrapArrayBuffer implements Generator {

		@Override
		public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef)
				{
			String parName = context.getParameterName(1);
			switch (methodRef.getName()) {
			case "wrapByteArrayBuffer":
				writeWrappedTypedArray(writer, parName, "$rt_byteArrayCls", "new Int8Array(" + parName + ")");
				break;
			case "wrapIntArrayBuffer":
				writeWrappedTypedArray(writer, parName, "$rt_intArrayCls", "new Int32Array(" + parName + ")");
				break;
			case "wrapFloatArrayBuffer":
				writeWrappedTypedArray(writer, parName, "$rt_floatArrayCls", "new Float32Array(" + parName + ")");
				break;
			case "wrapShortArrayBuffer":
				writeWrappedTypedArray(writer, parName, "$rt_shortArrayCls", "new Int16Array(" + parName + ")");
				break;
			default:
				break;
			}
		}

	}

	public static class WrapArrayBufferView implements Generator {

		@Override
		public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef)
				{
	        String parName = context.getParameterName(1);
			switch (methodRef.getName()) {
			case "wrapByteArrayBufferView":
			case "wrapUnsignedByteArray":
				writeWrappedTypedArray(writer, parName, "$rt_byteArrayCls", "new Int8Array(" + parName + ".buffer)");
				break;
			case "wrapIntArrayBufferView":
				writeWrappedTypedArray(writer, parName, "$rt_intArrayCls", "new Int32Array(" + parName + ".buffer)");
				break;
			case "wrapFloatArrayBufferView":
				writeWrappedTypedArray(writer, parName, "$rt_floatArrayCls", "new Float32Array(" + parName + ".buffer)");
				break;
			case "wrapShortArrayBufferView":
				writeWrappedTypedArray(writer, parName, "$rt_shortArrayCls", "new Int16Array(" + parName + ".buffer)");
				break;
			default:
				break;
			}
		}

	}

	public static class WrapTypedArray implements Generator {

		@Override
		public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef)
				{
			String parName = context.getParameterName(1);
			switch (methodRef.getName()) {
			case "wrapByteArray":
				writeWrappedTypedArray(writer, parName, "$rt_byteArrayCls", parName);
				break;
			case "wrapIntArray":
				writeWrappedTypedArray(writer, parName, "$rt_intArrayCls", parName);
				break;
			case "wrapFloatArray":
				writeWrappedTypedArray(writer, parName, "$rt_floatArrayCls", parName);
				break;
			case "wrapShortArray":
				writeWrappedTypedArray(writer, parName, "$rt_shortArrayCls", parName);
				break;
			default:
				break;
			}
		}

	}

	public static class UnwrapUnsignedTypedArray implements Injector {

		@Override
		public void generate(InjectorContext context, MethodReference methodRef) {
			context.getWriter().append("new Uint8Array(");
			context.writeExpr(context.getArgument(0));
			context.getWriter().append(".data.buffer)");
		}

	}

}
