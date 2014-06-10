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
import java.util.Collection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import com.n1nja.eclipse.wst.sdt.core.internal.*;
public class SassNature implements IProjectNature {
	static public final String ID = "com.n1nja.eclipse.wst.sdt.core.nature";
	static public final String DEFAULT_SASSPATH_FILENAME = ".sasspath";
	private IProject project;
	private Sasspath sasspath;
	private IFile sasspathFile;
	public void addEntries(final Collection<Sasspath.Entry> entries) {
		for (final Sasspath.Entry entry : entries) {
			this.sasspath.add(entry);
			final IFolder input = this.project.getFolder(entry.getInput());
			final IFolder output = this.project.getFolder(this.sasspath.isEntryOutputEnabled() ? entry.getOutput() : this.sasspath.getDefaultOutput());
			try {
				Resources.createFolder(input, false, true, null);
				Resources.createFolder(output, false, true, null);
			} catch (final CoreException ce) {
				throw new RuntimeException(ce);
			}
		}
		this.saveSasspath();
	}
	public void addEntry(final Sasspath.Entry entry) {
		this.sasspath.add(entry);
		final IFolder input = this.project.getFolder(entry.getInput());
		final IFolder output = this.project.getFolder(this.sasspath.isEntryOutputEnabled() ? entry.getOutput() : this.sasspath.getDefaultOutput());
		try {
			Resources.createFolder(input, false, true, null);
			Resources.createFolder(output, false, true, null);
		} catch (final CoreException ce) {
			throw new RuntimeException(ce);
		}
		this.saveSasspath();
	}
	public void clearEntries() {
		this.sasspath.clear();
		this.saveSasspath();
	}
	public void configure() throws CoreException {
		Projects.installBuilder(this.project, SassBuilder.ID, null);
	}
	public void deconfigure() throws CoreException {
		Projects.uninstallBuilder(this.project, SassBuilder.ID, null);
	}
	public IProject getProject() {
		return this.project;
	}
	public void removeEntries(final Collection<Sasspath.Entry> entries) {
		this.sasspath.removeAll(entries);
		this.saveSasspath();
	}
	public void removeEntry(final Sasspath.Entry entry) {
		this.sasspath.remove(entry);
		this.saveSasspath();
	}
	public void setProject(final IProject project) {
		this.project = project;
		this.setup();
	}
	public Sasspath getSasspath() {
		return this.sasspath;
	}
	public void setSasspath(final Sasspath sasspath) {
		if (sasspath == null) throw new IllegalArgumentException();
		this.sasspath = new Sasspath(sasspath.getDefaultOutput());
		this.sasspath.setEntryOutputEnabled(sasspath.isEntryOutputEnabled());
		this.addEntries(sasspath);
	}
	private void saveSasspath() {
		if (!this.sasspathFile.exists()) {
			try {
				this.sasspathFile.create(this.sasspath.marshal(), false, null);
			} catch (final CoreException ce) {
				throw new RuntimeException(ce);
			}
		} else {
			try {
				this.sasspathFile.setContents(this.sasspath.marshal(), false, true, null);
			} catch (final CoreException ce) {
				throw new RuntimeException(ce);
			}
		}
	}
	private void loadSasspath() {
		try {
			this.sasspath = Sasspath.unmarshal(this.sasspathFile.getContents());
		} catch (final CoreException ce) {
			throw new RuntimeException(ce);
		}
	}
	private void setup() {
		this.sasspathFile = this.project.getFile(DEFAULT_SASSPATH_FILENAME);
		if (this.sasspathFile.exists()) {
			this.loadSasspath();
		}
	}
}
