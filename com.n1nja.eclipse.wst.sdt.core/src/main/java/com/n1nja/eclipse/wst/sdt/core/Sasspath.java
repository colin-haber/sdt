/**
 * This file is part of SDT.
 * 
 * SDT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SDT. If not, see <http://www.gnu.org/licenses/>.
 */
package com.n1nja.eclipse.wst.sdt.core;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.core.runtime.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
@SuppressWarnings("serial")
public class Sasspath extends ArrayList<Sasspath.Entry> implements Cloneable {
	public class Entry {
		static public final int DEFAULT_PRECISION = 3;
		private IPath input;
		private IPath output;
		private Style style;
		private int precision;
		{
			this.style = Style.DEFAULT;
			this.precision = DEFAULT_PRECISION;
		}
		public Entry(final IPath input) {
			this(input, Path.EMPTY);
		}
		public Entry(final IPath input, final IPath output) {
			this.setInput(input);
			this.setOutput(output);
		}
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (!(obj instanceof Entry)) return false;
			final Entry other = (Entry) obj;
			if (this.input == null && other.input != null) return false;
			else if (!this.input.equals(other.input)) return false;
			if (this.output == null && other.output != null) return false;
			else if (!this.output.equals(other.output)) return false;
			if (this.style != other.style) return false;
			if (this.precision != other.precision) return false;
			return true;
		}
		public String getCommand() {
			final String prefix = ((Platform.isRunning() && Platform.getOS() == Platform.OS_WIN32) || System.getProperty("os.name").contains("Windows")) ? "cmd.exe /c sass.bat" : "sass";
			final String options = String.format(" --update %1$s:%2$s --style %3$s --precision %4$d", this.input, Sasspath.this.isEntryOutputEnabled && !this.output.isEmpty() ? this.output : Sasspath.this.defaultOutput, this.style, this.precision);
			return prefix + options;
		}
		public IPath getInput() {
			return this.input;
		}
		public IPath getOutput() {
			return this.output;
		}
		public int getPrecision() {
			return this.precision;
		}
		public Style getStyle() {
			return this.style;
		}
		@Override
		public int hashCode() {
			int result = 0;
			result += ((this.input == null) ? 0 : this.input.hashCode());
			result += ((this.output == null) ? 0 : this.output.hashCode());
			result += ((this.style == null) ? 0 : this.style.hashCode());
			result += this.precision;
			return result;
		}
		public void setInput(final IPath input) {
			if (input == null) throw new IllegalArgumentException();
			this.input = input;
		}
		/**
		 * Sets the output path for this entry.
		 * 
		 * @param output the output path. <b>Note:</b> passing an empty path
		 * indicates that this entry has no individual output path and should
		 * use the one provided by the parent sasspath.
		 */
		public void setOutput(final IPath output) {
			if (output == null) throw new IllegalArgumentException();
			this.output = output;
		}
		public void setPrecision(final int precision) {
			if (precision < 0) throw new IllegalArgumentException();
			this.precision = precision;
		}
		public void setStyle(final Style style) {
			if (style == null) throw new IllegalArgumentException();
			this.style = style;
		}
	}
	static public Sasspath unmarshal(final InputStream data) {
		final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db;
		try {
			db = dbfac.newDocumentBuilder();
		} catch (final ParserConfigurationException pce) {
			throw new RuntimeException(pce);
		}
		final Document doc;
		try {
			doc = db.parse(data);
		} catch (final SAXException saxe) {
			throw new RuntimeException(saxe);
		} catch (final IOException ioe) {
			throw new RuntimeException(ioe);
		}
		final Element root = doc.getDocumentElement();
		final IPath defaultOutput = Path.fromOSString(root.getAttribute("defaultOutput"));
		final boolean isEntryOutputEnabled = Boolean.parseBoolean(root.getAttribute("entryOutputEnabled"));
		final Sasspath sasspath = new Sasspath(defaultOutput);
		sasspath.setEntryOutputEnabled(isEntryOutputEnabled);
		//FIXME add namespace
		final NodeList entries = root.getElementsByTagName("sdt:entry");
		for (int i = 0; i < entries.getLength(); i++) {
			final Element el = (Element) entries.item(i);
			final IPath input = Path.fromOSString(el.getAttribute("input"));
			final IPath output;
			if (el.hasAttribute("output")) {
				output = Path.fromOSString(el.getAttribute("output"));
			} else {
				output = Path.EMPTY;
			}
			final Style style = Style.valueOf(el.getAttribute("style").toUpperCase(new java.util.Locale("")));
			final int precision = Integer.parseInt(el.getAttribute("precision"));
			final Sasspath.Entry entry = sasspath.new Entry(input, output);
			entry.setStyle(style);
			entry.setPrecision(precision);
			sasspath.add(entry);
		}
		return sasspath;
	}
	private IPath defaultOutput;
	private boolean isEntryOutputEnabled;
	{
		this.isEntryOutputEnabled = false;
	}
	static private final String XML_NS = "http://eclipse.n1nja.com/wst/sdt";
	public Sasspath(final IPath defaultOutput) {
		this.setDefaultOutput(defaultOutput);
	}
	public IPath getDefaultOutput() {
		return this.defaultOutput;
	}
	public boolean isEntryOutputEnabled() {
		return this.isEntryOutputEnabled;
	}
	public InputStream marshal() {
		/* Build the DOM */
		final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		final DocumentBuilder db;
		try {
			db = dbfac.newDocumentBuilder();
		} catch (final ParserConfigurationException pce) {
			throw new RuntimeException(pce);
		}
		final Document doc = db.newDocument();
		final Element root = doc.createElementNS(XML_NS, "sdt:sasspath");
		root.setAttribute("defaultOutput", this.defaultOutput.toString());
		root.setAttribute("entryOutputEnabled", Boolean.toString(this.isEntryOutputEnabled));
		for (final Sasspath.Entry entry : this) {
			final Element el = doc.createElementNS(XML_NS, "sdt:entry");
			el.setAttribute("input", entry.getInput().toString());
			if (!entry.getOutput().isEmpty()) {
				el.setAttribute("output", entry.getOutput().toString());
			}
			el.setAttribute("style", entry.getStyle().toString());
			el.setAttribute("precision", Integer.toString(entry.getPrecision()));
			root.appendChild(el);
		}
		doc.appendChild(root);
		/* Transform DOM to XML */
		final TransformerFactory tfac = TransformerFactory.newInstance();
		final Transformer traf;
		try {
			traf = tfac.newTransformer();
		} catch (final TransformerConfigurationException tce) {
			throw new RuntimeException(tce);
		}
		traf.setOutputProperty(OutputKeys.METHOD, "xml");
		traf.setOutputProperty(OutputKeys.INDENT, "yes");
		final DOMSource src = new DOMSource(doc);
		final StreamResult res = new StreamResult(new ByteArrayOutputStream());
		try {
			traf.transform(src, res);
		} catch (final TransformerException te) {
			throw new RuntimeException(te);
		}
		final ByteArrayOutputStream bytes = (ByteArrayOutputStream) res.getOutputStream();
		final ByteArrayInputStream ret = new ByteArrayInputStream(bytes.toByteArray());
		return ret;
	}
	/**
	 * Deep&#x2010;clones this sasspath. The new sasspath is guaranteed to have
	 * an equivalent marshalled form.
	 * 
	 * @return a clone of this {@link Sasspath}.
	 */
	@Override
	public Sasspath clone() {
		return Sasspath.unmarshal(this.marshal());
	}
	public void setDefaultOutput(final IPath defaultOutput) {
		if (defaultOutput == null) throw new IllegalArgumentException();
		this.defaultOutput = defaultOutput;
	}
	public void setEntryOutputEnabled(final boolean isEntryOutputEnabled) {
		this.isEntryOutputEnabled = isEntryOutputEnabled;
	}
}
