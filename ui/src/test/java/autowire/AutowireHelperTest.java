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

package autowire;

import io.jmix.core.CoreConfiguration;
import io.jmix.core.Metadata;
import io.jmix.data.DataConfiguration;
import io.jmix.eclipselink.EclipselinkConfiguration;
import io.jmix.ui.UiConfiguration;
import io.jmix.ui.sys.AutowireHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_support.UiTestConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CoreConfiguration.class, UiConfiguration.class, DataConfiguration.class,
        EclipselinkConfiguration.class, UiTestConfiguration.class})
public class AutowireHelperTest {

    @Autowired
    ApplicationContext applicationContext;

    class Foo implements ApplicationContextAware, InitializingBean {

        @Autowired
        private Metadata metadata;

        private boolean property;

        private ApplicationContext applicationContext;

        @Override
        public void afterPropertiesSet() throws Exception {
            property = true;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }

    @Test
    public void testAutowire() {
        Foo foo = (Foo) AutowireHelper.autowire(new Foo(), applicationContext);

        assertNotNull(foo.metadata);
        assertNotNull(foo.applicationContext);
        assertTrue(foo.property);
    }
}
