package easyenterprise.lib.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

public class LineWriter extends Writer {

	protected final PrintStream output;
	protected ArrayList<String> lines = new ArrayList<String>();
	protected StringBuilder builder = new StringBuilder();
	private final String fill;
	
	public LineWriter() {
		this(null, "   ");
	}
	
	public LineWriter(PrintStream output) {
		this(output, "   ");
	}

	public LineWriter(PrintStream output, String fill) {
		this.output = output;
		this.fill = fill;
	}
	
	
	public String[] getLines() {
		return lines.toArray(new String[lines.size()]);
	}
	
	public void writeln(Object[] values) {
		writeln(Arrays.asList(values));
	}
	
	public void writeln(Object value) {
		try {
			if (value == null) {
				value = "(null)";
			}
			System.err.flush();
			write(value.toString());
			write("\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void clear() {
		lines.clear();
		builder.setLength(0);
	}
	
	@Override
	public void close() throws IOException {
		flush();
		builder = null;
	}

	@Override
	public void flush() throws IOException {
		if (builder.length() != 0) {
			flushLine(builder.toString());
			builder.setLength(0);
		}
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) {
		for (int i = 0; i < len; i++) {
			char c = cbuf[off++];
			if (c == '\n') {
				String line = builder.toString();
				builder.setLength(0);
				flushLine(line);
			} else {
				builder.append(c);
			}
		}
	}
	
	protected void flushLine(String line) {
		lines.add(line);
		if (output != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("line ");
			String size = "" + lines.size();
			if (fill.length() > size.length()) {
				builder.append(fill.substring(size.length()));
			}
			builder.append(size);
			builder.append(": ");
			builder.append(line);
			output.println(builder);
		}
	}
}
