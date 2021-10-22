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

package io.jmix.ui.testassist.junit.extension.context;

import io.jmix.ui.testassist.ui.AppUiManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;

public class TestExtensionContext {

    protected TestContextManager testContextManager;
    protected AppUiManager appUiManager;

    public TestExtensionContext(Class<?> testClass) {
        testContextManager = new TestContextManager(testClass);
        appUiManager = getApplicationContext().getBean(AppUiManager.class);
    }

    public ApplicationContext getApplicationContext() {
        return testContextManager.getTestContext().getApplicationContext();
    }

    public AppUiManager getAppUiManager() {
        return appUiManager;
    }
}
