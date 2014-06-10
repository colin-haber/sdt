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
import org.eclipse.core.runtime.*;
import org.eclipse.wst.project.facet.*;
public class CorePlugin extends Plugin {
	static public final String ID = "com.n1nja.eclipse.wst.sdt.core.plugin";
	static public final String DEFAULT_INPUT_FOLDER = ProductManager.getProperty(IProductConstants.DEFAULT_SOURCE_FOLDER) + "/sass";
	static public final String DEFAULT_OUTPUT_FOLDER = ProductManager.getProperty(IProductConstants.WEB_CONTENT_FOLDER);
	static private CorePlugin singleton;
	static public CorePlugin getDefault() {
		return singleton != null ? singleton : (singleton = new CorePlugin());
	}
	static CoreException core(final Throwable t) {
		return new CoreException(new Status(IStatus.ERROR, ID, IStatus.OK, t.getLocalizedMessage(), t));
	}
	static CoreException core(final int severity, final int code, final String message, final Throwable cause) {
		return new CoreException(new Status(severity, ID, code, message, cause));
	}
	static void log(final int code, final Throwable cause) {
		log(new Status(IStatus.ERROR, ID, code, cause.getLocalizedMessage(), cause));
	}
	static void log(final int severity, final String message) {
		log(new Status(severity, ID, message));
	}
	static private void log(final IStatus status) {
		getDefault().getLog().log(status);
	}
}
