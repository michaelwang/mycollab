/**
 * This file is part of mycollab-mobile.
 *
 * mycollab-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.mobile.module.project

import com.esofthead.mycollab.common.UrlTokenizer
import com.esofthead.mycollab.eventmanager.EventBusFactory
import com.esofthead.mycollab.mobile.module.project.events.ProjectEvent
import com.esofthead.mycollab.mobile.module.project.view.bug.BugUrlResolver
import com.esofthead.mycollab.mobile.module.project.view.message.MessageUrlResolver
import com.esofthead.mycollab.mobile.module.project.view.milestone.MilestoneUrlResolver
import com.esofthead.mycollab.mobile.module.project.view.parameters.ProjectScreenData
import com.esofthead.mycollab.mobile.module.project.view.settings.UserUrlResolver
import com.esofthead.mycollab.mobile.module.project.view.task.TaskModuleUrlResolver
import com.esofthead.mycollab.mobile.shell.ModuleHelper
import com.esofthead.mycollab.mobile.shell.events.ShellEvent
import com.esofthead.mycollab.vaadin.mvp.{PageActionChain, UrlResolver}

/**
 * @author MyCollab Ltd
 * @since 5.0.9
 */
class ProjectUrlResolver extends UrlResolver {
    def build: UrlResolver = {
        this.addSubResolver("dashboard", new DashboardUrlResolver)
        this.addSubResolver("activities", new ActivityUrlResolver)
        this.addSubResolver("message", new MessageUrlResolver)
        this.addSubResolver("milestone", new MilestoneUrlResolver)
        this.addSubResolver("task", new TaskModuleUrlResolver)
        this.addSubResolver("bug", new BugUrlResolver)
        this.addSubResolver("user", new UserUrlResolver)
        return this
    }

    override def handle(params: String*) {
        if (!ModuleHelper.isCurrentProjectModule) {
            EventBusFactory.getInstance.post(new ShellEvent.GotoProjectModule(this, null))
        }
        else {
            super.handle(params:_*)
        }
    }

    protected def defaultPageErrorHandler {
        EventBusFactory.getInstance.post(new ShellEvent.GotoProjectModule(this, null))
    }

    protected override def handlePage(params: String*) {
        super.handlePage(params:_*)
        EventBusFactory.getInstance.post(new ProjectEvent.GotoProjectList(this, null))
    }

    class DashboardUrlResolver extends ProjectUrlResolver {
        protected override def handlePage(params: String*) {
            if (params.length > 0) {
                EventBusFactory.getInstance.post(new ShellEvent.GotoProjectModule(this, null))
            }
            else {
                val projectId: Int = new UrlTokenizer(params(0)).getInt
                val chain: PageActionChain = new PageActionChain(new ProjectScreenData.Goto(projectId), new ProjectScreenData.GotoDashboard)
                EventBusFactory.getInstance.post(new ProjectEvent.GotoMyProject(this, chain))
            }
        }
    }

    class ActivityUrlResolver extends ProjectUrlResolver {
        protected override def handlePage(params: String*) {
            if (params.length > 0) {
                EventBusFactory.getInstance.post(new ProjectEvent.AllActivities(this, new ProjectScreenData.AllActivities))
            }
            else {
                val projectId: Int = new UrlTokenizer(params(0)).getInt
                val chain: PageActionChain = new PageActionChain(new ProjectScreenData.Goto(projectId), new ProjectScreenData.ViewActivities)
                EventBusFactory.getInstance.post(new ProjectEvent.GotoMyProject(this, chain))
            }
        }
    }
}
