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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PropertyPage;
import com.n1nja.eclipse.wst.sdt.core.*;
public class SasspathPropertyPage extends PropertyPage {
	static public final String ID = "com.n1nja.eclipse.wst.sdt.ui.propertyPages.sasspathPage";
	private Sasspath sasspath;
	private SassNature nature;
	public SasspathPropertyPage() {
		this.noDefaultAndApplyButton();
		this.setTitle("Sass Build Path");
	}
	@Override
	public boolean performOk() {
		this.nature.setSasspath(this.sasspath);
		return true;
	}
	@Override
	public void setElement(final IAdaptable element) {
		final IProject project = (IProject) element;
		try {
			this.nature = (SassNature) project.getNature(SassNature.ID);
		} catch (final CoreException ce) {
			throw new RuntimeException(ce);
		}
		this.sasspath = this.nature.getSasspath().clone();
	}
	@Override
	protected Control createContents(final Composite parent) {
		final GridLayoutFactory pageLayout = GridLayoutFactory.swtDefaults().numColumns(2);
		final GridDataFactory primaryData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true);
		final GridDataFactory secondaryData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false);
		final Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(pageLayout.create());
		final SasspathTree tree = new SasspathTree(this.sasspath, page, SWT.BORDER);
		tree.getComposite().setLayoutData(primaryData.create());
		final GridLayoutFactory buttonLayout = GridLayoutFactory.fillDefaults().numColumns(1);
		final Composite buttons = new Composite(page, SWT.NONE);
		buttons.setLayoutData(primaryData.create());
		buttons.setLayout(buttonLayout.create());
		final Button addButton = new Button(buttons, SWT.NONE);
		addButton.setLayoutData(secondaryData.create());
		addButton.setText("Add Input\u2026");
		addButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {}
			public void widgetSelected(final SelectionEvent e) {
				tree.add();
			}
		});
		final Button editButton = new Button(buttons, SWT.NONE);
		editButton.setLayoutData(secondaryData.create());
		editButton.setText("Edit\u2026");
		editButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {}
			public void widgetSelected(final SelectionEvent e) {
				tree.edit();
			}
		});
		final Button removeButton = new Button(buttons, SWT.NONE);
		removeButton.setLayoutData(secondaryData.create());
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {}
			public void widgetSelected(final SelectionEvent e) {
				tree.remove();
			}
		});
		tree.getComposite().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {}
			public void widgetSelected(final SelectionEvent e) {
				final boolean selected = tree.getComposite().getSelection().length != 1;
				editButton.setGrayed(selected);
				removeButton.setGrayed(selected);
			}
		});
		final Button enableButton = new Button(page, SWT.CHECK);
		enableButton.setLayoutData(secondaryData.create());
		enableButton.setText("Allow individual output folders");
		enableButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(final SelectionEvent e) {
				SasspathPropertyPage.this.sasspath.setEntryOutputEnabled(enableButton.getSelection());
				tree.update();
			}
			public void widgetDefaultSelected(final SelectionEvent e) {}
		});
		final Label defoutLabel = new Label(page, SWT.NONE);
		defoutLabel.setLayoutData(secondaryData.copy().span(2, 1).create());
		defoutLabel.setText("Default output folder:");
		final Text defoutField = new Text(page, SWT.BORDER);
		defoutField.setLayoutData(secondaryData.copy().span(2, 1).create());
		defoutField.setText(this.sasspath.getDefaultOutput().toString());
		defoutField.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				SasspathPropertyPage.this.sasspath.setDefaultOutput(Path.fromOSString(defoutField.getText()));
			}
		});
		return page;
	}
}
