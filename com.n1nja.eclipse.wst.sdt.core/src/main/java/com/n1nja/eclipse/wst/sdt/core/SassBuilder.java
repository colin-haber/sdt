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
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
public class SassBuilder extends IncrementalProjectBuilder {
	static public final String ID = "com.n1nja.eclipse.wst.sdt.core.builder";
	static private Thread pipeIO(final InputStream input, final OutputStream output) {
		return new Thread(new Runnable() {
			public void run() {
				final byte[] buf = new byte[1024];
				int len = 0;
				try {
					while ((len = input.read(buf)) != -1) {
						output.write(buf, 0, len);
					}
				} catch (final IOException ioe) {
					throw new RuntimeException(ioe);
				}
			}
		});
	}
	static private void pipeProcess(final Process proc, final InputStream in, final OutputStream out, final OutputStream err) {
		if (in != null) {
			pipeIO(in, proc.getOutputStream()).start();
		}
		if (out != null) {
			pipeIO(proc.getInputStream(), out).start();
		}
		if (err != null) {
			pipeIO(proc.getErrorStream(), err).start();
		}
	}
	@Override
	protected IProject[] build(final int kind, final Map<String, String> args, final IProgressMonitor monitor) throws CoreException {
		final IProject project = this.getProject();
		final IResourceDelta delta = this.getDelta(project);
		final SassNature nature = (SassNature) this.getProject().getNature(SassNature.ID);
		final Sasspath sasspath = nature.getSasspath();
		final Set<IFolder> outputs = new HashSet<IFolder>();
		for (final Sasspath.Entry entry : sasspath) {
			if (delta == null || delta.findMember(entry.getInput()) != null) {
				final IFolder output = project.getFolder(sasspath.isEntryOutputEnabled() ? entry.getOutput() : sasspath.getDefaultOutput());
				try {
					final Process proc = Runtime.getRuntime().exec(entry.getCommand(), null, project.getLocation().toFile());
					pipeProcess(proc, System.in, System.out, System.err);
					final int code;
					if ((code = proc.waitFor()) != 0) throw CorePlugin.core(IStatus.ERROR, IStatus.OK, String.format("Sass executable terminated with status code %1$d.", code), null);
				} catch (final IOException ioe) {
					throw CorePlugin.core(ioe);
				} catch (final InterruptedException ie) {
					throw CorePlugin.core(ie);
				} finally {
					outputs.add(output);
				}
			}
		}
		for (final IFolder output : outputs) {
			output.refreshLocal(IResource.DEPTH_ONE, monitor);
		}
		return null;
	}
}
