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

package io.jmix.ui.testassist.spec

import io.jmix.core.EntityStates
import io.jmix.core.FetchPlanRepository
import io.jmix.core.Metadata
import io.jmix.core.MetadataTools
import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory
import io.jmix.ui.*
import io.jmix.ui.model.DataComponents
import io.jmix.ui.testassist.JmixUiTest
import io.jmix.ui.testassist.ui.AppUiManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@JmixUiTest
abstract class UiTestAssistSpecification extends Specification {

    @Autowired
    Metadata metadata

    @Autowired
    MetadataTools metadataTools

    @Autowired
    FetchPlanRepository viewRepository

    @Autowired
    EntityStates entityStates

    @Autowired
    DataComponents dataComponents

    @Autowired
    UiComponents uiComponents

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    AnnotationScanMetadataReaderFactory metadataReaderFactory

    @Autowired
    WindowConfig windowConfig

    @Autowired
    AppUI vaadinUi

    @Autowired
    AppUiManager appUiManager;

    @SuppressWarnings("GroovyAccessibility")
    void setup() {
        setupAuthentication()

        appUiManager.setupUi()
    }

    void cleanup() {
        appUiManager.cleanupUi()

        cleanupAuthentication()
    }

    /**
     * Implement to set up authentication before each test.
     * For example, use {@code SystemAuthenticator.begin()}.
     */
    protected abstract void setupAuthentication()

    /**
     * Implement to clean up authentication after each test.
     * For example, use {@code SystemAuthenticator.end()}.
     */
    protected abstract void cleanupAuthentication()

    protected void exportScreensPackages(List<String> packages) {
        appUiManager.exportScreensPackages(packages)
    }

    protected void resetScreensConfig() {
        appUiManager.resetScreensConfig()
    }

    protected Screens getScreens() {
        return appUiManager.getScreens()
    }
}
