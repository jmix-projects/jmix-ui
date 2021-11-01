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

package io.jmix.ui.testassist.junit.extension;

import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.testassist.app.main.TestMainScreen;
import io.jmix.ui.testassist.ui.AppUiManager;
import io.jmix.ui.testassist.junit.extension.context.TestExtensionContext;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Collections;

/**
 * Exports screen package for {@link TestMainScreen} and opens it before each test.
 *
 * @see UiTest
 */
public class MainScreenExtension implements BeforeEachCallback {

    protected static final ExtensionContext.Namespace MAIN_SCREEN_EXTENSION_NAMESPACE = ExtensionContext.Namespace.create(MainScreenExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AppUiManager appUiManager = getContextStore(context).getAppUiManager();

        appUiManager.exportScreensPackages(Collections.singletonList("io.jmix.ui.testassist.app.main"));

        appUiManager.getScreens()
                .create("testMainScreen", OpenMode.ROOT)
                .show();
    }

    protected TestExtensionContext getContextStore(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();

        ExtensionContext.Store store = context.getStore(MAIN_SCREEN_EXTENSION_NAMESPACE);

        return store.getOrComputeIfAbsent(testClass, TestExtensionContext::new, TestExtensionContext.class);
    }
}
