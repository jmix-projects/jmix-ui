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

import io.jmix.ui.testassist.JmixUiTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * The annotation is for classes that test Jmix screens on JUnit.
 * <p>
 * For instance, test for simple application that is created from single module template:
 * <pre>
 * &#64;UiTest
 * &#64;ContextConfiguration(classes = {MyDemoApplication.class, TestMyDemoConfiguration.class})
 * public class UserScreenTest {
 *
 *     &#64;Autowired
 *     protected AppUiManager appUiManager;
 *
 *     &#64;BeforeEach
 *     public void beforeEach() {
 *         appUiManager.exportScreensPackages(Collections.singletonList("com.company.mydemo.screen.user"));
 *     }
 *
 *     &#64;Test
 *     public void openUserBrowse() {
 *         UserBrowse userBrowse = appUiManager.getScreens().create(UserBrowse.class);
 *         userBrowse.show();
 *     }
 * }
 * </pre>
 */
@JmixUiTest
@ExtendWith(value = {SystemAuthExtension.class, AppUiExtension.class, MainScreenExtension.class})
@Documented
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UiTest {
}
