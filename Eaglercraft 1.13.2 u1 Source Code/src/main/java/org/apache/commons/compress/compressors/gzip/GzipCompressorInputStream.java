package org.apache.commons.compress.compressors.gzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class GzipCompressorInputStream extends GZIPInputStream {
	public GzipCompressorInputStream(InputStream in) throws IOException {
		super(in);
	}
}
