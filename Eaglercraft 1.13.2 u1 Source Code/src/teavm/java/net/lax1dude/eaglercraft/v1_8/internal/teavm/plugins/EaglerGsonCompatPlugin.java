package net.lax1dude.eaglercraft.v1_8.internal.teavm.plugins;

import org.teavm.backend.javascript.TeaVMJavaScriptHost;
import org.teavm.backend.javascript.codegen.SourceWriter;
import org.teavm.backend.javascript.rendering.RenderingManager;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.spi.RendererListener;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

public class EaglerGsonCompatPlugin implements TeaVMPlugin {

	@Override
	public void install(TeaVMHost host) {
		TeaVMJavaScriptHost jsHost = host.getExtension(TeaVMJavaScriptHost.class);
		if(jsHost != null) {
			jsHost.add(new GsonCompatRendererListener());
		}
	}

	private static class GsonCompatRendererListener implements RendererListener {

		@Override
		public void begin(RenderingManager manager, BuildTarget buildTarget) {
			SourceWriter writer = manager.getWriter();
			writer.append("    // EaglerGsonCompatPlugin: patch missing Class.isAnonymousClass runtime path").newLine();
			writer.append("    var eagler$installGsonCompat = function() {").newLine();
			writer.append("        if (typeof cggi_Excluder_isAnonymousOrLocal !== 'function'").newLine();
			writer.append("                || cggi_Excluder_isAnonymousOrLocal.$eaglerGsonCompat) {").newLine();
			writer.append("            return false;").newLine();
			writer.append("        }").newLine();
			writer.append("        cggi_Excluder_isAnonymousOrLocal = function($this, $clazz) {").newLine();
			writer.append("            if ($clazz === null || $clazz === $rt_globals.undefined) {").newLine();
			writer.append("                return 0;").newLine();
			writer.append("            }").newLine();
			writer.append("            try {").newLine();
			writer.append("                if (typeof jl_Enum !== 'undefined'").newLine();
			writer.append("                        && $rt_cls(jl_Enum).$isAssignableFrom($clazz)) {").newLine();
			writer.append("                    return 0;").newLine();
			writer.append("                }").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("            try {").newLine();
			writer.append("                if (typeof $clazz.$isLocalClass === 'function'").newLine();
			writer.append("                        && $clazz.$isLocalClass()) {").newLine();
			writer.append("                    return 1;").newLine();
			writer.append("                }").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("            try {").newLine();
			writer.append("                if (typeof $clazz.$getName0 === 'function') {").newLine();
			writer.append("                    var nObj = $clazz.$getName0();").newLine();
			writer.append("                    if (nObj !== null && nObj !== $rt_globals.undefined) {").newLine();
			writer.append("                        var n = $rt_ustr(nObj);").newLine();
			writer.append("                        var idx = n.lastIndexOf('$');").newLine();
			writer.append("                        if (idx >= 0 && idx + 1 < n.length) {").newLine();
			writer.append("                            var anon = true;").newLine();
			writer.append("                            for (var i = idx + 1; i < n.length; ++i) {").newLine();
			writer.append("                                var ch = n.charCodeAt(i);").newLine();
			writer.append("                                if (ch < 48 || ch > 57) {").newLine();
			writer.append("                                    anon = false;").newLine();
			writer.append("                                    break;").newLine();
			writer.append("                                }").newLine();
			writer.append("                            }").newLine();
			writer.append("                            if (anon) {").newLine();
			writer.append("                                return 1;").newLine();
			writer.append("                            }").newLine();
			writer.append("                        }").newLine();
			writer.append("                    }").newLine();
			writer.append("                }").newLine();
			writer.append("            } catch (ignored) {}").newLine();
			writer.append("            return 0;").newLine();
			writer.append("        };").newLine();
			writer.append("        cggi_Excluder_isAnonymousOrLocal.$eaglerGsonCompat = true;").newLine();
			writer.append("        return true;").newLine();
			writer.append("    };").newLine();
			writer.append("    if (!eagler$installGsonCompat()) {").newLine();
			writer.append("        if (typeof $rt_globals.queueMicrotask === 'function') {").newLine();
			writer.append("            $rt_globals.queueMicrotask(eagler$installGsonCompat);").newLine();
			writer.append("        } else if (typeof $rt_globals.Promise === 'function') {").newLine();
			writer.append("            $rt_globals.Promise.resolve().then(eagler$installGsonCompat);").newLine();
			writer.append("        }").newLine();
			writer.append("        if (typeof $rt_globals.setTimeout === 'function') {").newLine();
			writer.append("            $rt_globals.setTimeout(eagler$installGsonCompat, 0);").newLine();
			writer.append("        }").newLine();
			writer.append("    }").newLine();
		}

		@Override
		public void complete() {
		}
	}
}
