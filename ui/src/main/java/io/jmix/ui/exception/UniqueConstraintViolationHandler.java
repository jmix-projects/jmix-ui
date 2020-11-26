/*
 * Copyright 2020 Haulmont.
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

package io.jmix.ui.exception;

import io.jmix.core.JmixOrder;
import io.jmix.core.Messages;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("ui_UniqueConstraintViolationHandler")
public class UniqueConstraintViolationHandler implements UiExceptionHandler, Ordered {

    private static final String DEFAULT_MESSAGE_PROPERTY = "uniqueConstraintViolation.message";
    private static final String MESSAGE_PREFIX = "databaseUniqueConstraintViolation.";

    @Autowired
    private Messages messages;

    @Autowired
    private UiProperties uiProperties;

    @Override
    public boolean handle(Throwable exception, UiContext context) {
        Throwable throwable = exception;
        try {
            while (throwable != null) {
                if (throwable.toString().contains("org.eclipse.persistence.exceptions.DatabaseException")) {
                    return doHandle(throwable, context);
                }
                throwable = throwable.getCause();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean doHandle(Throwable exception, UiContext context) {
        Pattern pattern = uiProperties.getUniqueConstraintViolationPattern();
        Matcher matcher = pattern.matcher(exception.toString());
        if (matcher.find()) {
            String constraintName = resolveConstraintName(matcher);
            String message = getMessage(constraintName);
            context.getNotifications().create(Notifications.NotificationType.ERROR).withDescription(message).show();
            return true;
        }
        return false;
    }

    private String resolveConstraintName(Matcher matcher) {
        String constraintName = "";
        if (matcher.groupCount() == 1) {
            constraintName = matcher.group(1);
        } else {
            for (int i = 1; i < matcher.groupCount(); i++) {
                if (StringUtils.isNotBlank(matcher.group(i))) {
                    constraintName = matcher.group(i);
                    break;
                }
            }
        }
        return constraintName.toUpperCase();
    }

    private String getMessage(String constraintName) {
        String messageKey = MESSAGE_PREFIX + constraintName;
        String message = messages.getMessage(messageKey);

        if (messageKey.equals(message)) {
            message = getDefaultMessage(constraintName);
        }
        return message;
    }

    private String getDefaultMessage(String constraintName) {
        String msg = messages.getMessage(DEFAULT_MESSAGE_PROPERTY);
        if (StringUtils.isNotBlank(constraintName)) {
            msg = msg + " (" + constraintName + ")";
        }
        return msg;
    }

    @Override
    public int getOrder() {
        return JmixOrder.HIGHEST_PRECEDENCE + 60;
    }
}
