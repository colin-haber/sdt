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
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import com.n1nja.eclipse.wst.sdt.core.*;
import com.n1nja.eclipse.wst.sdt.core.Sasspath.Entry;
public class SasspathTree {
	class EntryTreeItem {
		private class EntryWizard extends Wizard {
			private class EntryWizardPage extends WizardPage {
				static public final String ID = "com.n1nja.eclipse.wst.sdt.ui.wizards.entryWizard.page";
				private Text inputField;
				private Text outputField;
				private Combo styleField;
				private Spinner precisionField;
				public EntryWizardPage() {
					super(ID);
					this.setTitle("Sass Build");
					this.setDescription("Edit settings for this build.");
				}
				public void createControl(final Composite parent) {
					final Sasspath.Entry entry = EntryTreeItem.this.entry;
					final GridLayoutFactory pageLayout = GridLayoutFactory.swtDefaults().numColumns(1);
					final GridDataFactory pageData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false);
					final Composite page = new Composite(parent, SWT.NONE);
					page.setLayout(pageLayout.create());
					final GridLayoutFactory groupLayout = GridLayoutFactory.fillDefaults().numColumns(1);
					final GridDataFactory groupData = pageData.copy();
					final Composite inputGroup = new Composite(page, SWT.NONE);
					inputGroup.setLayoutData(pageData.create());
					inputGroup.setLayout(groupLayout.create());
					final Label inputLabel = new Label(inputGroup, SWT.NONE);
					inputLabel.setLayoutData(groupData.create());
					inputLabel.setText("Input path:");
					this.inputField = new Text(inputGroup, SWT.BORDER);
					this.inputField.setLayoutData(groupData.create());
					this.inputField.setText(entry.getInput().toString());
					final Composite outputGroup = new Composite(page, SWT.NONE);
					outputGroup.setLayoutData(pageData.create());
					outputGroup.setLayout(groupLayout.create());
					final Label outputLabel = new Label(outputGroup, SWT.NONE);
					outputLabel.setLayoutData(groupData.create());
					outputLabel.setText("Output path:");
					this.outputField = new Text(outputGroup, SWT.BORDER);
					this.outputField.setLayoutData(groupData.create());
					this.outputField.setText(entry.getOutput().toString());
					final Composite styleGroup = new Composite(page, SWT.NONE);
					styleGroup.setLayoutData(pageData.create());
					styleGroup.setLayout(groupLayout.create());
					final Label styleLabel = new Label(styleGroup, SWT.NONE);
					styleLabel.setLayoutData(groupData.create());
					styleLabel.setText("Output style:");
					this.styleField = new Combo(styleGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
					this.styleField.setLayoutData(groupData.create());
					final Style[] styles = Style.values();
					final String[] names = new String[styles.length];
					for (int i = 0; i < names.length; i++) {
						names[i] = styles[i].toString();
					}
					this.styleField.setItems(names);
					this.styleField.setText(entry.getStyle().toString());
					final Composite precisionGroup = new Composite(page, SWT.NONE);
					precisionGroup.setLayoutData(pageData.create());
					precisionGroup.setLayout(groupLayout.create());
					final Label precisionLabel = new Label(precisionGroup, SWT.NONE);
					precisionLabel.setLayoutData(groupData.create());
					precisionLabel.setText("Decimal places:");
					this.precisionField = new Spinner(precisionGroup, SWT.BORDER);
					this.precisionField.setLayoutData(groupData.create());
					this.precisionField.setValues(entry.getPrecision(), 0, Integer.MAX_VALUE, 0, 1, 3);
					this.setControl(page);
				}
			}
			final EntryWizardPage page;
			public EntryWizard() {
				this.addPage((this.page = new EntryWizardPage()));
			}
			@Override
			public boolean performFinish() {
				final Sasspath.Entry entry = EntryTreeItem.this.entry;
				entry.setInput(Path.fromOSString(this.page.inputField.getText()));
				entry.setOutput(Path.fromOSString(this.page.outputField.getText()));
				entry.setStyle(Style.valueOf(this.page.styleField.getText().toUpperCase(new java.util.Locale(""))));
				entry.setPrecision(this.page.precisionField.getSelection());
				return true;
			}
		}
		private final Sasspath.Entry entry;
		private final TreeItem input;
		private final TreeItem output;
		private final TreeItem style;
		private final TreeItem precision;
		public EntryTreeItem(final Sasspath.Entry entry, final Tree parent) {
			SasspathTree.this.tree.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(final SelectionEvent e) {
					final TreeItem[] selection = SasspathTree.this.tree.getSelection();
					if (selection.length == 1) {
						final TreeItem item = selection[0];
						if (item == EntryTreeItem.this.input || item == EntryTreeItem.this.output || item == EntryTreeItem.this.style || item == EntryTreeItem.this.precision) {
							new WizardDialog(SasspathTree.this.tree.getShell(), EntryTreeItem.this.new EntryWizard()).open();
							EntryTreeItem.this.update();
						}
					}
				}
				public void widgetSelected(final SelectionEvent e) {}
			});
			this.entry = entry;
			this.input = new TreeItem(parent, SWT.NONE);
			this.input.setData(entry);
			this.output = new TreeItem(this.input, SWT.NONE);
			this.output.setData(entry);
			this.style = new TreeItem(this.input, SWT.NONE);
			this.style.setData(entry);
			this.precision = new TreeItem(this.input, SWT.NONE);
			this.precision.setData(entry);
			this.update();
			this.input.setExpanded(true);
		}
		public Sasspath.Entry getEntry() {
			return this.entry;
		}
		public final void update() {
			this.input.setText(this.entry.getInput().toString());
			this.output.setText(String.format("Output: %1$s", String.valueOf(this.entry.getOutput())));
			this.output.setForeground(SasspathTree.this.sasspath.isEntryOutputEnabled() && !this.entry.getOutput().isEmpty() ? null : SasspathTree.this.tree.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			this.style.setText(String.format("Style: %1$s", this.entry.getStyle().toString()));
			this.precision.setText(String.format("Precision: %1$d", this.entry.getPrecision()));
		}
	}
	private final Sasspath sasspath;
	private final Tree tree;
	public SasspathTree(final Sasspath sasspath, final Composite parent, final int style) {
		this.sasspath = sasspath;
		this.tree = new Tree(parent, style);
		this.update();
	}
	public void add() {
		final Sasspath.Entry entry = this.sasspath.new Entry(Path.fromOSString(CorePlugin.DEFAULT_INPUT_FOLDER));
		new EntryTreeItem(entry, this.tree);
		this.sasspath.add(entry);
	}
	public void edit() {
		this.tree.notifyListeners(SWT.DefaultSelection, new Event());
	}
	public Tree getComposite() {
		return this.tree;
	}
	public Sasspath getSasspath() {
		return this.sasspath;
	}
	public void remove() {
		final TreeItem[] selection = this.tree.getSelection();
		if (selection.length != 1) throw new IllegalStateException();
		final Sasspath.Entry entry = (Entry) selection[0].getData();
		this.sasspath.remove(entry);
		this.update();
	}
	public final void update() {
		this.tree.removeAll();
		for (final Sasspath.Entry entry : this.sasspath) {
			new EntryTreeItem(entry, this.tree);
		}
	}
}
