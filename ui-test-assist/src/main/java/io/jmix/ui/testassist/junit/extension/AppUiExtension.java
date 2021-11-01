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

import io.jmix.ui.testassist.junit.extension.context.TestExtensionContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Initialize UI before each test and cleans it after each test.
 *
 * @see UiTest
 */
public class AppUiExtension implements BeforeEachCallback, AfterEachCallback {

    protected static final ExtensionContext.Namespace APP_UI_EXTENSION_NAMESPACE =
            ExtensionContext.Namespace.create(AppUiExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        getTestContext(context).getAppUiManager().setupUi();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        getTestContext(context).getAppUiManager().cleanupUi();
    }

    protected TestExtensionContext getTestContext(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();

        ExtensionContext.Store store = context.getStore(APP_UI_EXTENSION_NAMESPACE);

        return store.getOrComputeIfAbsent(testClass, TestExtensionContext::new, TestExtensionContext.class);
    }
}
