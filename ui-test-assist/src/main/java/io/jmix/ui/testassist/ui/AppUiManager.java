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

package io.jmix.ui.testassist.ui;

import com.vaadin.server.Extension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import io.jmix.core.EntityStates;
import io.jmix.core.FetchPlanRepository;
import io.jmix.core.Metadata;
import io.jmix.core.MetadataTools;
import io.jmix.core.impl.scanning.AnnotationScanMetadataReaderFactory;
import io.jmix.ui.*;
import io.jmix.ui.model.DataComponents;
import io.jmix.ui.sys.AppCookies;
import io.jmix.ui.sys.UiControllersConfiguration;
import io.jmix.ui.theme.ThemeConstants;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.apache.commons.lang3.reflect.FieldUtils.getDeclaredField;

public class AppUiManager {

    @Autowired
    protected Metadata metadata;
    @Autowired
    protected MetadataTools metadataTools;
    @Autowired
    protected FetchPlanRepository viewRepository;
    @Autowired
    protected EntityStates entityStates;
    @Autowired
    protected DataComponents dataComponents;
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected AnnotationScanMetadataReaderFactory metadataReaderFactory;
    @Autowired
    protected WindowConfig windowConfig;
    @Autowired
    protected AppUI vaadinUi;

    public void setupUi() {
        AutowireCapableBeanFactory injectFactory = applicationContext.getAutowireCapableBeanFactory();

        JmixApp app = new JmixApp();
        try {
            getDeclaredField(App.class, "themeConstants", true)
                    .set(app, new ThemeConstants(Collections.emptyMap()));
            getDeclaredField(App.class, "cookies", true)
                    .set(app, new AppCookies());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot initialize " + JmixApp.class.getName(), e);
        }

        TestVaadinSession vaadinSession = new TestVaadinSession(new WebBrowser(), Locale.ENGLISH);

        vaadinSession.setAttribute(App.class, app);
        vaadinSession.setAttribute("ui_App", app);

        VaadinSession.setCurrent(vaadinSession);

        injectFactory.autowireBean(app);

        UI.setCurrent(vaadinUi);

        TestConnectorTracker connectorTracker = new TestConnectorTracker(vaadinUi);
        try {
            getDeclaredField(UI.class, "connectorTracker", true)
                    .set(vaadinUi, connectorTracker);
            getDeclaredField(UI.class, "session", true)
                    .set(vaadinUi, vaadinSession);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot initialize " + UI.class.getName(), e);
        }

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(Mockito.mock(HttpServletRequest.class)));

        TestVaadinRequest vaadinRequest = new TestVaadinRequest();
        vaadinUi.getPage().init(vaadinRequest);

        try {
            Method method = ReflectionUtils.findMethod(AppUI.class, "init", VaadinRequest.class);
            method.setAccessible(true);
            method.invoke(vaadinUi, vaadinRequest);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Cannot invoke init method of " + AppUI.class.getName(), e);
        }
    }

    public void cleanupUi() {
        resetScreensConfig();

        Collection<Extension> extensions = new ArrayList<>(vaadinUi.getExtensions());
        extensions.forEach(window -> vaadinUi.removeExtension(window));

        Collection<Window> windows = new LinkedHashSet<>(vaadinUi.getWindows());
        windows.forEach(window -> vaadinUi.removeWindow(window));

        UI.setCurrent(null);
    }

    public void resetScreensConfig() {
        try {
            getDeclaredField(WindowConfig.class, "configurations", true)
                    .set(windowConfig, Collections.emptyList());
            getDeclaredField(WindowConfig.class, "initialized", true)
                    .set(windowConfig, false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot reset screen config", e);
        }
    }

    public void exportScreensPackages(List<String> packages) {
        UiControllersConfiguration configuration = new UiControllersConfiguration(applicationContext, metadataReaderFactory);

        AutowireCapableBeanFactory injector = applicationContext.getAutowireCapableBeanFactory();
        injector.autowireBean(configuration);

        configuration.setBasePackages(packages);

        try {
            Field configurationsField = getDeclaredField(WindowConfig.class, "configurations", true);
            //noinspection unchecked
            Collection<UiControllersConfiguration> configurations = (Collection<UiControllersConfiguration>)
                    configurationsField.get(windowConfig);

            List<UiControllersConfiguration> modifiedConfigurations = new ArrayList<>(configurations);
            modifiedConfigurations.add(configuration);

            configurationsField.set(windowConfig, modifiedConfigurations);

            getDeclaredField(WindowConfig.class, "initialized", true)
                    .set(windowConfig, false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot export screen packages", e);
        }
    }

    public Screens getScreens() {
        return vaadinUi.getScreens();
    }
}
