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

import io.jmix.core.security.*;
import io.jmix.ui.testassist.junit.extension.context.TestExtensionContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Sets default authentication before each test and removes it after each test.
 *
 * @see UiTest
 */
public class SystemAuthExtension implements BeforeEachCallback, AfterEachCallback {

    protected static final ExtensionContext.Namespace SYSTEM_AUTH_EXTENSION_NAMESPACE =
            ExtensionContext.Namespace.create(SystemAuthExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        getTestContext(context).getApplicationContext()
                .getBean(SystemAuthenticator.class)
                .begin();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        getTestContext(context).getApplicationContext()
                .getBean(SystemAuthenticator.class)
                .end();
    }

    protected TestExtensionContext getTestContext(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();

        ExtensionContext.Store store = context.getStore(SYSTEM_AUTH_EXTENSION_NAMESPACE);

        return store.getOrComputeIfAbsent(testClass, TestExtensionContext::new, TestExtensionContext.class);
    }
}
