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
package com.n1nja.eclipse.wst.sdt.core.internal;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
public abstract class Resources {
	static public final void createFolder(final IFolder folder, final boolean force, final boolean local, final IProgressMonitor monitor) throws CoreException {
		if (folder.exists()) return;
		final IContainer parent = folder.getParent();
		if (!parent.exists() && parent instanceof IFolder) {
			createFolder((IFolder) parent, force, local, monitor);
		}
		if (parent == null || parent.exists()) {
			folder.create(force, local, monitor);
		}
	}
}
