/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.crm.view.activity;

import com.esofthead.mycollab.common.ModuleNameConstants;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.i18n.TaskI18nEnum;
import com.esofthead.mycollab.utils.FieldGroupFormatter;
import com.esofthead.mycollab.vaadin.ui.HistoryLogComponent;

/**
 * 
 * @author MyCollab Ltd.
 * @since 2.0
 * 
 */
public class AssignmentHistoryLogList extends HistoryLogComponent {
	private static final long serialVersionUID = 1L;

	public static final FieldGroupFormatter assignmentFormatter;

	static {
		assignmentFormatter = new FieldGroupFormatter();

		assignmentFormatter.generateFieldDisplayHandler("subject",
				TaskI18nEnum.FORM_SUBJECT);
		assignmentFormatter.generateFieldDisplayHandler("startdate",
				TaskI18nEnum.FORM_START_DATE);
		assignmentFormatter.generateFieldDisplayHandler("duedate",
				TaskI18nEnum.FORM_DUE_DATE);
		assignmentFormatter.generateFieldDisplayHandler("status",
				TaskI18nEnum.FORM_STATUS);
		assignmentFormatter.generateFieldDisplayHandler("assignuser",
				GenericI18Enum.FORM_ASSIGNEE);
		assignmentFormatter.generateFieldDisplayHandler("priority",
				TaskI18nEnum.FORM_PRIORITY);
		assignmentFormatter.generateFieldDisplayHandler("description",
				GenericI18Enum.FORM_DESCRIPTION);
	}

	public AssignmentHistoryLogList() {
		super(ModuleNameConstants.CRM, CrmTypeConstants.TASK);
	}

	@Override
	protected FieldGroupFormatter buildFormatter() {
		return assignmentFormatter;
	}

}
