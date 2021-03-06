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
package com.esofthead.mycollab.shell.view.components;

import com.esofthead.mycollab.core.MyCollabVersion;
import com.esofthead.mycollab.vaadin.ui.MyCollabResource;
import com.esofthead.mycollab.vaadin.ui.WebResourceIds;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Text;
import com.vaadin.server.Page;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author MyCollab Ltd.
 * @since 5.0.5
 */
public class AboutWindow extends Window {
    public AboutWindow() {
        super("About");
        this.setModal(true);
        this.setResizable(false);
        this.center();
        this.setWidth("600px");

        MHorizontalLayout content = new MHorizontalLayout().withMargin(true).withWidth("100%");
        this.setContent(content);

        Image about = new Image("", MyCollabResource.newResource(WebResourceIds._about));
        MVerticalLayout rightPanel = new MVerticalLayout();
        Label versionLbl = new Label(String.format("MyCollab Community Edition %s", MyCollabVersion.getVersion()));
        versionLbl.addStyleName("h2");
        Label javaNameLbl = new Label(String.format("%s, %s", System.getProperty("java.vm.name"),
                System.getProperty("java.runtime.version")));
        WebBrowser browser = Page.getCurrent().getWebBrowser();
        Label osLbl = new Label(String.format("%s, %s", System.getProperty("os.name"),
                browser.getBrowserApplication()));
        osLbl.addStyleName("wordWrap");
        Div licenseDiv = new Div().appendChild(new Text("Powered by: "))
                .appendChild(new A("https://www.mycollab.com")
                .appendText("MyCollab")).appendChild(new Text(". Open source under GPL license"));
        Label licenseLbl = new Label(licenseDiv.write(), ContentMode.HTML);
        Label copyRightLbl = new Label(String.format("&copy; %s - %s MyCollab Ltd. All rights reserved", "2011",
                new GregorianCalendar().get(Calendar.YEAR) + ""), ContentMode.HTML);
        rightPanel.with(versionLbl, javaNameLbl, osLbl, licenseLbl, copyRightLbl)
                .withAlign(copyRightLbl, Alignment.BOTTOM_LEFT);
        content.with(about, rightPanel).expand(rightPanel);
    }
}
