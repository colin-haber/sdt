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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.common.project.facet.core.*;
import com.n1nja.eclipse.wst.sdt.core.internal.Projects;
public abstract class SassFacet {
	static public class Installer implements IDelegate {
		static public class ConfigFactory implements IActionConfigFactory {
			public Object create() throws CoreException {
				final Sasspath sasspath = new Sasspath(Path.fromOSString(CorePlugin.DEFAULT_OUTPUT_FOLDER));
				sasspath.add(sasspath.new Entry(Path.fromOSString(CorePlugin.DEFAULT_INPUT_FOLDER)));
				return new Configuration(sasspath);
			}
		}
		static public class Configuration {
			private Sasspath sasspath;
			public Configuration(final Sasspath sasspath) {
				this.setSasspath(sasspath);
			}
			public Sasspath getSasspath() {
				return this.sasspath;
			}
			public void setSasspath(final Sasspath sasspath) {
				if (sasspath == null) throw new IllegalArgumentException();
				this.sasspath = sasspath;
			}
		}
		static public final String ID = "com.n1nja.eclipse.wst.sdt.core.facet.install";
		public void execute(final IProject project, final IProjectFacetVersion fv, final Object config, final IProgressMonitor monitor) throws CoreException {
			final SassNature nature = (SassNature) Projects.installNature(project, SassNature.ID, monitor);
			nature.setSasspath(((Configuration) config).sasspath);
		}
	}
	static public class Uninstaller implements IDelegate {
		static public final String ID = "com.n1nja.eclipse.wst.sdt.core.facet.uninstall";
		public void execute(final IProject project, final IProjectFacetVersion fv, final Object config, final IProgressMonitor monitor) throws CoreException {
			Projects.uninstallNature(project, SassNature.ID, monitor);
		}
	}
	static public final String ID = "com.n1nja.eclipse.wst.sdt.core.facet";
}
