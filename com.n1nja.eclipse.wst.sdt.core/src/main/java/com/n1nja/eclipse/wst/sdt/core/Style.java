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
public enum Style {
	NESTED,
	EXPANDED,
	COMPACT,
	COMPRESSED;
	static public final Style DEFAULT = NESTED;
	private final String lower;
	{
		this.lower = this.name().toLowerCase(new java.util.Locale(""));
	}
	/**
	 * Returns the name of this style in a lower&#x2010;case format suitable for
	 * use with the command&#x2010;line.
	 * 
	 * @return the name of this enum in locale&#x2010;independent
	 * lower&#x2010;case.
	 */
	@Override
	public String toString() {
		return this.lower;
	}
}
