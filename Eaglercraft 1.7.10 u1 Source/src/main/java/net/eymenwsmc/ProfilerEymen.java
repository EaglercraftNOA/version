package net.eymenwsmc;

public class ProfilerEymen {
	private long startTime;
	private String sectionName;

	public void start(String section) {
		this.sectionName = section;
		this.startTime = System.nanoTime();
	}

	public void end() {
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		System.out.println("[Profiler] " + sectionName + " took " + duration + " ms");
	}

	public static ProfilerSection section(String name) {
		return new ProfilerSection(name);
	}

	public static class ProfilerSection implements AutoCloseable {
		private final String name;
		private final long start;

		public ProfilerSection(String name) {
			this.name = name;
			this.start = System.nanoTime();
		}

		@Override
		public void close() {
			long end = System.nanoTime();
			long duration = (end - start) / 1000000;
			System.out.println("[Profiler] " + name + " took " + duration + " ms");
		}
	}
}