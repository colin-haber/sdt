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
package com.n1nja.eclipse.wst.sdt.ui;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import com.n1nja.eclipse.wst.sdt.core.*;
public class SasspathFacetInstallWizardPage extends AbstractFacetWizardPage {
	private SassFacet.Installer.Configuration config;
	public SasspathFacetInstallWizardPage() {
		super("com.n1nja.eclipse.wst.sdt.ui.facetWizards.installWizard.page");
		this.setTitle("Sass");
		this.setDescription("Configure the default Sass paths.");
	}
	public void setConfig(final Object config) {
		this.config = (SassFacet.Installer.Configuration) config;
	}
	public void createControl(final Composite parent) {
		final Sasspath sasspath = this.config.getSasspath();
		if (sasspath == null || sasspath.isEmpty()) throw new IllegalStateException();
		final Sasspath.Entry entry = sasspath.get(0);
		final GridLayoutFactory pageLayout = GridLayoutFactory.swtDefaults().numColumns(1);
		final GridDataFactory primaryData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true);
		final GridDataFactory secondaryData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false);
		final Composite page = new Composite(parent, SWT.NONE);
		page.setLayoutData(primaryData.create());
		page.setLayout(pageLayout.create());
		final Label inputLabel = new Label(page, SWT.NONE);
		inputLabel.setLayoutData(secondaryData.create());
		inputLabel.setText("Input path:");
		final Text inputField = new Text(page, SWT.BORDER);
		inputField.setLayoutData(secondaryData.create());
		inputField.setText(entry.getInput().toString());
		inputField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				entry.setInput(Path.fromOSString(inputField.getText()));
			}
		});
		final Label outputLabel = new Label(page, SWT.NONE);
		outputLabel.setLayoutData(secondaryData.create());
		outputLabel.setText("Output path:");
		final Text outputField = new Text(page, SWT.BORDER);
		outputField.setLayoutData(secondaryData.create());
		outputField.setText(sasspath.getDefaultOutput().toString());
		outputField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				sasspath.setDefaultOutput(Path.fromOSString(outputField.getText()));
			}
		});
		this.setControl(page);
	}
}

