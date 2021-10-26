/*
 * Copyright 2021 Haulmont.
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

package io.jmix.ui.testassist.junit;

import io.jmix.ui.Screens;
import io.jmix.ui.testassist.UiTest;
import io.jmix.ui.testassist.ui.AppUiManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Base test class for JUnit tests.
 */
@UiTest
public abstract class UiTestAssistJUnit {

    @Autowired
    protected AppUiManager appUiManager;

    @BeforeEach
    protected void setupJmixApp() {
        setupAuthentication();

        appUiManager.setupUi();
    }

    @AfterEach
    protected void cleanupJmixApp() {
        appUiManager.cleanupUi();

        cleanupAuthentication();
    }

    protected abstract void setupAuthentication();

    protected abstract void cleanupAuthentication();

    protected void exportScreensPackages(List<String> packages) {
        appUiManager.exportScreensPackages(packages);
    }

    protected void resetScreensConfig() {
        appUiManager.resetScreensConfig();
    }

    protected Screens getScreens() {
        return appUiManager.getScreens();
    }
}
