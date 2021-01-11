/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmix.ui.action;

import io.jmix.core.Messages;
import io.jmix.ui.Screens;
import io.jmix.ui.app.systeminfo.SystemInfoWindow;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.ComponentsHelper;
import org.springframework.beans.factory.annotation.Autowired;

@ActionType(ShowInfoAction.ACTION_ID)
public class ShowInfoAction extends ItemTrackingAction implements Action.ExecutableAction {

    public static final String ACTION_ID = "showSystemInfo";

    public ShowInfoAction() {
        super(ACTION_ID);
    }

    @Autowired
    public void setMessages(Messages messages) {
        setCaption(messages.getMessage("table.showInfoAction"));
    }

    @Override
    public void actionPerform(Component component) {
        // if standard behaviour
        if (!hasSubscriptions(ActionPerformedEvent.class)) {
            execute();
        } else {
            super.actionPerform(component);
        }
    }

    @Override
    public void execute() {
        if (target == null) {
            throw new IllegalStateException("ShowInfoAction target is not set");
        }

        Object selectedItem = target.getSingleSelected();
        if (selectedItem != null) {
            showInfo(selectedItem, target);
        }
    }

    public void showInfo(Object entity, Component.BelongToFrame component) {
        Screens screens = ComponentsHelper.getScreenContext(component)
                .getScreens();

        SystemInfoWindow screen = screens.create(SystemInfoWindow.class);
        screen.setEntity(entity);

        screen.show();
    }
}
