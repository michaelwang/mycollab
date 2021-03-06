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
package com.esofthead.mycollab.module.project.view.settings;

import com.esofthead.mycollab.configuration.SiteConfiguration;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.mail.service.ExtMailService;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.ProjectLinkGenerator;
import com.esofthead.mycollab.module.project.ProjectRolePermissionCollections;
import com.esofthead.mycollab.module.project.events.ProjectMemberEvent;
import com.esofthead.mycollab.module.project.events.ProjectMemberEvent.InviteProjectMembers;
import com.esofthead.mycollab.module.project.service.ProjectMemberService;
import com.esofthead.mycollab.module.project.view.ProjectBreadcrumb;
import com.esofthead.mycollab.module.user.events.UserEvent;
import com.esofthead.mycollab.shell.view.SystemUIChecker;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.mvp.*;
import com.esofthead.mycollab.vaadin.mvp.PageView.ViewEvent;
import com.esofthead.mycollab.vaadin.mvp.PageView.ViewListener;
import com.esofthead.mycollab.vaadin.ui.AbstractPresenter;
import com.esofthead.mycollab.vaadin.ui.Hr;
import com.esofthead.mycollab.vaadin.ui.NotificationUtil;
import com.esofthead.mycollab.vaadin.ui.UIConstants;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.B;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Text;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.commons.collections.CollectionUtils;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class ProjectMemberInvitePresenter extends AbstractPresenter<ProjectMemberInviteView> {
    private static final long serialVersionUID = 1L;

    public ProjectMemberInvitePresenter() {
        super(ProjectMemberInviteView.class);
    }

    @Override
    protected void postInitView() {
        view.addViewListener(new ViewListener<ProjectMemberEvent.InviteProjectMembers>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void receiveEvent(ViewEvent<InviteProjectMembers> event) {
                InviteProjectMembers inviteMembers = (InviteProjectMembers) event
                        .getData();
                ProjectMemberService projectMemberService = ApplicationContextUtil
                        .getSpringBean(ProjectMemberService.class);
                List<String> inviteEmails = inviteMembers.getEmails();
                if (CollectionUtils.isNotEmpty(inviteEmails)) {
                    projectMemberService.inviteProjectMembers(
                            inviteEmails.toArray(new String[0]),
                            CurrentProjectVariables.getProjectId(),
                            inviteMembers.getRoleId(),
                            AppContext.getUsername(),
                            inviteMembers.getInviteMessage(),
                            AppContext.getAccountId());

                    ExtMailService mailService = ApplicationContextUtil.getSpringBean(ExtMailService.class);
                    if (mailService.isMailSetupValid()) {
                        NotificationUtil.showNotification("Success", "Invitation is sent successfully", Notification.Type
                                .HUMANIZED_MESSAGE);
                        EventBusFactory.getInstance().post(
                                new ProjectMemberEvent.GotoList(this, null));
                    } else {
                        UI.getCurrent().addWindow(new GetStartedInstructionWindow(inviteMembers));
                    }
                }
            }
        });
    }

    @Override
    protected void onGo(ComponentContainer container, ScreenData<?> data) {
        if (CurrentProjectVariables.canWrite(ProjectRolePermissionCollections.USERS)) {
            ProjectUserContainer userGroupContainer = (ProjectUserContainer) container;
            userGroupContainer.removeAllComponents();
            userGroupContainer.addComponent(view.getWidget());

            view.display();

            ProjectBreadcrumb breadcrumb = ViewManager.getCacheComponent(ProjectBreadcrumb.class);
            breadcrumb.gotoUserAdd();
            SystemUIChecker.hasValidSmtpAccount();
        } else {
            NotificationUtil.showMessagePermissionAlert();
        }
    }

    private static class GetStartedInstructionWindow extends Window {
        private MVerticalLayout contentLayout;

        public GetStartedInstructionWindow(InviteProjectMembers invitation) {
            super("Getting started instructions");
            this.setResizable(false);
            this.setModal(true);
            this.setWidth("600px");
            contentLayout = new MVerticalLayout();
            this.setContent(contentLayout);
            center();
            displayInfo(invitation);
        }

        private void displayInfo(InviteProjectMembers invitation) {
            Div infoDiv = new Div().appendText("You have not setup SMTP account properly. So we can not send the invitation by email automatically. Please copy/paste below paragraph and inform to the user by yourself").setStyle("font-weight:bold;color:red");
            Label infoLbl = new Label(infoDiv.write(), ContentMode.HTML);
            contentLayout.with(infoLbl);

            Div introDiv = new Div().appendText("Below users are invited to the project ")
                    .appendChild(new B().appendText(CurrentProjectVariables.getProject().getName()))
                    .appendText(" as role ").appendChild(new B().appendText(invitation.getRoleName()));
            contentLayout.with(new Label(introDiv.write(), ContentMode.HTML));

            List<String> inviteEmails = invitation.getEmails();
            Date nowTime = new GregorianCalendar().getTime();
            for (String inviteEmail : inviteEmails) {
                Div userEmailDiv = new Div().appendText("&nbsp;&nbsp;&nbsp;&nbsp;Email: ").appendChild(new A().setHref("mailto:" + inviteEmail).appendText(inviteEmail));

                String acceptLinkVal = SiteConfiguration.getSiteUrl(AppContext.getSubDomain())
                        + "project/member/invitation/confirm_invite/"
                        + ProjectLinkGenerator
                        .generateAcceptInvitationParams(
                                inviteEmail, AppContext.getAccountId(),
                                CurrentProjectVariables.getProjectId(), invitation.getRoleId(),
                                AppContext.getUsername(), AppContext.getUsername(),
                                nowTime);
                Div acceptLinkDiv = new Div().appendChild(new Text("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Accept: "), new A().setHref(acceptLinkVal).appendText(acceptLinkVal));

                String denyLinkVal = SiteConfiguration.getSiteUrl(AppContext.getSubDomain())
                        + "project/member/invitation/deny_invite/"
                        + ProjectLinkGenerator
                        .generateDenyInvitationParams(inviteEmail,
                                AppContext.getAccountId(), CurrentProjectVariables.getProjectId(),
                                AppContext.getUsername(), AppContext.getUsername());
                Div denyLink = new Div().appendChild(new Text("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Deny: "), new A().setHref(denyLinkVal).appendText(denyLinkVal));
                contentLayout.with(new Label(userEmailDiv.write(), ContentMode.HTML), new Label(acceptLinkDiv.write(), ContentMode.HTML),
                        new Label(denyLink.write(), ContentMode.HTML));
                contentLayout.add(new Hr());
            }

            MHorizontalLayout controlsBtn = new MHorizontalLayout().withMargin(new MarginInfo(true, true, true, false));

            Button addNewBtn = new Button("Invite more member(s)", new Button.ClickListener() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            EventBusFactory.getInstance().post(new ProjectMemberEvent.GotoInviteMembers(GetStartedInstructionWindow.this, null));
                            GetStartedInstructionWindow.this.close();
                        }
                    });
            addNewBtn.setStyleName(UIConstants.THEME_BLUE_LINK);

            Button doneBtn = new Button("Done", new Button.ClickListener() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            ViewState viewState = HistoryViewManager.back();
                            if (viewState instanceof NullViewState) {
                                EventBusFactory.getInstance().post(
                                        new UserEvent.GotoList(this, null));
                            }
                            GetStartedInstructionWindow.this.close();
                        }
                    });
            doneBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
            controlsBtn.with(addNewBtn, doneBtn);
            contentLayout.with(controlsBtn).withAlign(controlsBtn, Alignment.MIDDLE_RIGHT);
        }
    }
}
