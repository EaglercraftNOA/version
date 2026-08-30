package com.arlenh7.eaglercraft.v1_17.teavm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.teavm.dependency.AbstractDependencyListener;
import org.teavm.dependency.DependencyAgent;
import org.teavm.model.AccessLevel;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassReader;
import org.teavm.model.ElementModifier;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

public class WasmGCMissingInterfacePlugin implements TeaVMPlugin {

	@Override
	public void install(TeaVMHost host) {
		host.add(new MissingInterfaceListener());
	}

	private static final class MissingInterfaceListener extends AbstractDependencyListener {

		private final Set<String> synthesised = new HashSet<>();

		@Override
		public void classReached(DependencyAgent agent, String className) {
			ClassReader cls = agent.getClassSource().get(className);
			if (cls == null) {
				synthesiseInterface(agent, className);
				return;
			}
			Deque<String> queue = new ArrayDeque<>();
			Set<String> visited = new HashSet<>();
			for (String itf : cls.getInterfaces()) {
				if (visited.add(itf)) {
					queue.add(itf);
				}
			}
			while (!queue.isEmpty()) {
				String itf = queue.poll();
				ClassReader itfCls = agent.getClassSource().get(itf);
				if (itfCls == null) {
					synthesiseInterface(agent, itf);
					continue;
				}
				for (String superItf : itfCls.getInterfaces()) {
					if (visited.add(superItf)) {
						queue.add(superItf);
					}
				}
			}
		}

		private void synthesiseInterface(DependencyAgent agent, String name) {
			if (!synthesised.add(name)) {
				return;
			}
			if (agent.getClassSource().get(name) != null) {
				return;
			}
			ClassHolder iface = new ClassHolder(name);
			iface.setParent("java.lang.Object");
			iface.setLevel(AccessLevel.PUBLIC);
			iface.getModifiers().add(ElementModifier.INTERFACE);
			iface.getModifiers().add(ElementModifier.ABSTRACT);
			agent.submitClass(iface);
			agent.linkClass(name);
		}
	}
}
