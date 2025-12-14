/**
 *
 * Copyright (c) 2014-2015, Openflexo
 *
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure
 * developed at Openflexo.
 *
 *
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either
 * version 1.1 of the License, or any later version ), which is available at
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 *
 * You can redistribute it and/or modify under the terms of either of these licenses
 *
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or
 *          combining it with software containing parts covered by the terms
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. *
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See http://www.openflexo.org/license.html for details.
 *
 *
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 *
 */

package org.openflexo.technologyadapter.csv.gui;

import org.junit.Test;
import org.openflexo.gina.test.GenericFIBInspectorTestCase;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.ResourceLocator;


public class TestCSVInspectors extends GenericFIBInspectorTestCase {

	/**
	 * Use this method to print all inspector test cases
	 * Then copy-paste the generated code below
	 */
	public static void main(String[] args) {
		System.out.println(generateInspectorTestCaseClass(
				((FileResourceImpl) ResourceLocator.locateResource("Inspectors/CSV")).getFile(),
				"Inspectors/CSV/"));
	}

	// ==================== Model Object Inspectors ====================

	@Test
	public void testCSVDocumentInspector() {
		validateFIB("Inspectors/CSV/CSVDocument.inspector");
	}

	@Test
	public void testCSVRowInspector() {
		validateFIB("Inspectors/CSV/CSVRow.inspector");
	}

	@Test
	public void testCSVCellInspector() {
		validateFIB("Inspectors/CSV/CSVCell.inspector");
	}

	@Test
	public void testCSVColumnInspector() {
		validateFIB("Inspectors/CSV/CSVColumn.inspector");
	}

	// ==================== Edition Action Inspectors ====================

	@Test
	public void testAddCSVRowInspector() {
		validateFIB("Inspectors/CSV/EditionAction/AddCSVRow.inspector");
	}

	@Test
	public void testAddCSVCellInspector() {
		validateFIB("Inspectors/CSV/EditionAction/AddCSVCell.inspector");
	}

	@Test
	public void testSelectCSVRowInspector() {
		validateFIB("Inspectors/CSV/EditionAction/SelectCSVRow.inspector");
	}

	@Test
	public void testSelectCSVCellInspector() {
		validateFIB("Inspectors/CSV/EditionAction/SelectCSVCell.inspector");
	}

	@Test
	public void testAbstractSelectCSVRowInspector() {
		validateFIB("Inspectors/CSV/EditionAction/AbstractSelectCSVRow.inspector");
	}

	@Test
	public void testAbstractSelectCSVCellInspector() {
		validateFIB("Inspectors/CSV/EditionAction/AbstractSelectCSVCell.inspector");
	}

	@Test
	public void testSelectUniqueCSVRowInspector() {
		validateFIB("Inspectors/CSV/EditionAction/SelectUniqueCSVRow.inspector");
	}

	@Test
	public void testSelectUniqueCSVCellInspector() {
		validateFIB("Inspectors/CSV/EditionAction/SelectUniqueCSVCell.inspector");
	}

	@Test
	public void testCreateCSVResourceInspector() {
		validateFIB("Inspectors/CSV/EditionAction/CreateCSVResource.inspector");
	}

	// ==================== Role Inspectors ====================

	@Test
	public void testCSVDocumentRoleInspector() {
		validateFIB("Inspectors/CSV/CSVDocumentRole.inspector");
	}

	@Test
	public void testCSVRowRoleInspector() {
		validateFIB("Inspectors/CSV/CSVRowRole.inspector");
	}

	@Test
	public void testCSVCellRoleInspector() {
		validateFIB("Inspectors/CSV/CSVCellRole.inspector");
	}

	@Test
	public void testCSVColumnRoleInspector() {
		validateFIB("Inspectors/CSV/CSVColumnRole.inspector");
	}

	// ==================== ModelSlot Inspector ====================

	@Test
	public void testCSVModelSlotInspector() {
		validateFIB("Inspectors/CSV/CSVModelSlot.inspector");
	}

}