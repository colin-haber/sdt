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
public abstract class Projects {
	static public final ICommand getBuilder(final IProject project, final String builderID) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final ICommand[] buildSpec = desc.getBuildSpec();
		for (final ICommand cmd : buildSpec) {
			if (cmd.getBuilderName().equals(builderID)) return cmd;
		}
		return null;
	}
	static public final ICommand installBuilder(final IProject project, final String builderID, final IProgressMonitor monitor) throws CoreException {
		if (project == null) throw new IllegalArgumentException();
		if (builderID == null) throw new IllegalArgumentException();
		if (!isBuilderInstalled(project, builderID)) {
			final IProjectDescription desc = project.getDescription();
			final ICommand[] oldSpec = desc.getBuildSpec();
			final ICommand[] newSpec = new ICommand[oldSpec.length + 1];
			System.arraycopy(oldSpec, 0, newSpec, 0, oldSpec.length);
			final ICommand cmd = desc.newCommand();
			cmd.setBuilderName(builderID);
			newSpec[newSpec.length - 1] = cmd;
			desc.setBuildSpec(newSpec);
			project.setDescription(desc, monitor);
			return cmd;
		} else {
			return getBuilder(project, builderID);
		}
	}
	static public final IProjectNature installNature(final IProject project, final String natureID, final IProgressMonitor monitor) throws CoreException {
		if (project == null) throw new IllegalArgumentException();
		if (natureID == null) throw new IllegalArgumentException();
		if (!isNatureInstalled(project, natureID)) {
			final IProjectDescription desc = project.getDescription();
			final String[] oldIds = desc.getNatureIds();
			final String[] newIds = new String[oldIds.length + 1];
			System.arraycopy(oldIds, 0, newIds, 0, oldIds.length);
			newIds[newIds.length - 1] = natureID;
			desc.setNatureIds(newIds);
			project.setDescription(desc, monitor);
		}
		return project.getNature(natureID);
	}
	static public final boolean isBuilderInstalled(final IProject project, final String builderID) throws CoreException {
		if (project == null) throw new IllegalArgumentException();
		if (builderID == null) throw new IllegalArgumentException();
		final IProjectDescription desc = project.getDescription();
		final ICommand[] buildSpec = desc.getBuildSpec();
		for (final ICommand cmd : buildSpec) {
			if (cmd.getBuilderName().equals(builderID)) return true;
		}
		return false;
	}
	static public final boolean isNatureInstalled(final IProject project, final String natureID) throws CoreException {
		if (project == null) throw new IllegalArgumentException();
		if (natureID == null) throw new IllegalArgumentException();
		final IProjectDescription desc = project.getDescription();
		final String[] natureIds = desc.getNatureIds();
		for (final String id : natureIds) {
			if (id.equals(natureID)) return true;
		}
		return false;
	}
	static public final void uninstallBuilder(final IProject project, final String builderID, final IProgressMonitor monitor) throws CoreException {
		if (project == null) throw new IllegalArgumentException();
		if (builderID == null) throw new IllegalArgumentException();
		if (isBuilderInstalled(project, builderID)) {
			final IProjectDescription desc = project.getDescription();
			final ICommand[] oldSpec = desc.getBuildSpec();
			final ICommand[] newSpec = new ICommand[oldSpec.length + 1];
			if (oldSpec.length > 1) {
				int index = -1;
				for (int i = 0; i < oldSpec.length; i++) {
					if (oldSpec[i].getBuilderName().equals(builderID)) {
						index = i;
						break;
					}
				}
				System.arraycopy(oldSpec, 0, newSpec, 0, index);
				System.arraycopy(oldSpec, index + 1, newSpec, index + 1, oldSpec.length - index);
			}
			desc.setBuildSpec(newSpec);
			project.setDescription(desc, monitor);
		}
	}
	static public final void uninstallNature(final IProject project, final String natureID, final IProgressMonitor monitor) throws CoreException {
		if (project == null) throw new IllegalArgumentException();
		if (natureID == null) throw new IllegalArgumentException();
		if (isNatureInstalled(project, natureID)) {
			final IProjectDescription desc = project.getDescription();
			final String[] oldIds = desc.getNatureIds();
			final String[] newIds = new String[oldIds.length - 1];
			if (oldIds.length > 1) {
				int index = -1;
				for (int i = 0; i < oldIds.length; i++) {
					if (oldIds[i].equals(natureID)) {
						index = i;
						break;
					}
				}
				System.arraycopy(oldIds, 0, newIds, 0, index);
				System.arraycopy(oldIds, index + 1, newIds, index + 1, oldIds.length - index);
			}
			desc.setNatureIds(newIds);
			project.setDescription(desc, monitor);
		}
	}
}
