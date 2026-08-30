package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;

import org.apache.commons.lang3.ArrayUtils;

public class CrashReport {
	/** Description of the crash report. */
	private final String description;

	/** The Throwable that is the "cause" for this crash and Crash Report. */
	private final Throwable cause;

	/** Category of crash */
	private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
	private final List<CrashReportCategory> crashReportSections = Lists.<CrashReportCategory>newArrayList();

	/** Is true when the current category is the first in the crash report */
	private boolean firstCategoryInCrashReport = true;
	private String[] stacktrace;

	public CrashReport(String descriptionIn, Throwable causeThrowable) {
		this.description = descriptionIn;
		this.cause = causeThrowable;
		this.stacktrace = EagRuntime.getStackTraceElements(causeThrowable);
		this.populateEnvironment();
	}

	/**
	 * Populates this crash report with initial information about the running server
	 * and operating system / java environment
	 */
	private void populateEnvironment() {
		this.theReportCategory.setDetail("Minecraft Version", new ICrashReportDetail<String>() {
			public String call() {
				return "1.12.2";
			}
		});
		this.theReportCategory.setDetail("Operating System", new ICrashReportDetail<String>() {
			public String call() {
				return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version "
						+ System.getProperty("os.version");
			}
		});
		this.theReportCategory.setDetail("Java Version", new ICrashReportDetail<String>() {
			public String call() {
				return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
			}
		});
		this.theReportCategory.setDetail("Java VM Version", new ICrashReportDetail<String>() {
			public String call() {
				return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), "
						+ System.getProperty("java.vm.vendor");
			}
		});
		if (EagRuntime.getPlatformType() != EnumPlatformType.JAVASCRIPT) {
			this.theReportCategory.setDetail("Memory", new ICrashReportDetail<String>() {
				public String call() {
					long i = EagRuntime.maxMemory();
					long j = EagRuntime.totalMemory();
					long k = EagRuntime.freeMemory();
					long l = i / 1024L / 1024L;
					long i1 = j / 1024L / 1024L;
					long j1 = k / 1024L / 1024L;
					return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l
							+ " MB)";
				}
			});
		}
		this.theReportCategory.setDetail("IntCache", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return IntCache.getCacheSizes();
			}
		});
	}

	/**
	 * Returns the description of the Crash Report.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the Throwable object that is the cause for the crash and Crash
	 * Report.
	 */
	public Throwable getCrashCause() {
		return this.cause;
	}

	/**
	 * Gets the various sections of the crash report into the given StringBuilder
	 */
	public void getSectionsInStringBuilder(StringBuilder builder) {
		if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
			this.stacktrace = (String[]) ArrayUtils
					.subarray(((CrashReportCategory) this.crashReportSections.get(0)).getStackTrace(), 0, 1);
		}

		if (this.stacktrace != null && this.stacktrace.length > 0) {
			builder.append("-- Head --\n");
			builder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
			builder.append("Stacktrace:\n");

			for (int i = 0; i < this.stacktrace.length; i++) {
				builder.append("\t").append("at ").append(this.stacktrace[i]);
				builder.append("\n");
			}

			builder.append("\n");
		}

		for (CrashReportCategory crashreportcategory : this.crashReportSections) {
			crashreportcategory.appendToStringBuilder(builder);
			builder.append("\n\n");
		}

		this.theReportCategory.appendToStringBuilder(builder);
	}

	/**
	 * Gets the stack trace of the Throwable that caused this crash report, or if
	 * that fails, the cause .toString().
	 */
	public String getCauseStackTraceOrString() {
		StringBuilder stackTrace = new StringBuilder();

		if ((this.cause.getMessage() == null || this.cause.getMessage().length() == 0)
				&& ((this.cause instanceof NullPointerException) || (this.cause instanceof StackOverflowError)
						|| (this.cause instanceof OutOfMemoryError))) {
			stackTrace.append(this.cause.getClass().getName()).append(": ");
			stackTrace.append(this.description).append('\n');
		} else {
			stackTrace.append(this.cause.toString()).append('\n');
		}

		EagRuntime.getStackTrace(this.cause, (s) -> {
			stackTrace.append("\tat ").append(s).append('\n');
		});

		return stackTrace.toString();
	}

	/**
	 * Gets the complete report with headers, stack trace, and different sections as
	 * a string.
	 */
	public String getCompleteReport() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("---- Minecraft Crash Report ----\n");
		stringbuilder.append("// ");
		stringbuilder.append(getWittyComment());
		stringbuilder.append("\n\n");
		stringbuilder.append("Time: ");
		stringbuilder.append((new SimpleDateFormat()).format(new Date()));
		stringbuilder.append("\n");
		stringbuilder.append("Description: ");
		stringbuilder.append(this.description);
		stringbuilder.append("\n\n");
		stringbuilder.append(this.getCauseStackTraceOrString());
		stringbuilder.append(
				"\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

		for (int i = 0; i < 87; ++i) {
			stringbuilder.append("-");
		}

		stringbuilder.append("\n\n");
		this.getSectionsInStringBuilder(stringbuilder);
		return stringbuilder.toString();
	}

	public CrashReportCategory getCategory() {
		return this.theReportCategory;
	}

	/**
	 * Creates a CrashReportCategory
	 */
	public CrashReportCategory makeCategory(String name) {
		return this.makeCategoryDepth(name, 1);
	}

	/**
	 * Creates a CrashReportCategory for the given stack trace depth
	 */
	public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
		CrashReportCategory crashreportcategory = new CrashReportCategory(this, categoryName);

		if (this.firstCategoryInCrashReport) {
			int i = crashreportcategory.getPrunedStackTrace(stacktraceLength);
			String[] astacktraceelement = EagRuntime.getStackTraceElements(cause);
			String stacktraceelement = null;
			String stacktraceelement1 = null;
			int j = astacktraceelement.length - i;

			if (j < 0) {
				System.out.println(
						"Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
			}

			if (astacktraceelement != null && 0 <= j && j < astacktraceelement.length) {
				stacktraceelement = astacktraceelement[j];

				if (astacktraceelement.length + 1 - i < astacktraceelement.length) {
					stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
				}
			}

			this.firstCategoryInCrashReport = crashreportcategory.firstTwoElementsOfStackTraceMatch(stacktraceelement,
					stacktraceelement1);

			if (i > 0 && !this.crashReportSections.isEmpty()) {
				CrashReportCategory crashreportcategory1 = this.crashReportSections
						.get(this.crashReportSections.size() - 1);
				crashreportcategory1.trimStackTraceEntriesFromBottom(i);
			} else if (astacktraceelement != null && astacktraceelement.length >= i && 0 <= j
					&& j < astacktraceelement.length) {
				this.stacktrace = new String[j];
				System.arraycopy(astacktraceelement, 0, this.stacktrace, 0, this.stacktrace.length);
			} else {
				this.firstCategoryInCrashReport = false;
			}
		}

		this.crashReportSections.add(crashreportcategory);
		return crashreportcategory;
	}

	/**
	 * Gets a random witty comment for inclusion in this CrashReport
	 */
	private static String getWittyComment() {
		String[] astring = new String[] { "Eagler? Eaglerrrrrr...",
				"I bet this would have worked if you forked over $30." };

		try {
			return astring[(int) (EagRuntime.nanoTime() % (long) astring.length)];
		} catch (Throwable var2) {
			return "We can't call you out for being cheap :(";
		}
	}

	/**
	 * Creates a crash report for the exception
	 */
	public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
		CrashReport crashreport;

		if (causeIn instanceof ReportedException) {
			crashreport = ((ReportedException) causeIn).getCrashReport();
		} else {
			crashreport = new CrashReport(descriptionIn, causeIn);
		}

		return crashreport;
	}
}
