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

import io.jmix.core.security.ClientDetails;
import io.jmix.core.security.SecurityContextHelper;
import io.jmix.ui.screen.OpenMode;
import io.jmix.ui.screen.Screen;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Locale;

/**
 * Implementation of base test class. It implements default authentication
 * and exports test main screen package.
 */
public class ScreenTestJUnit extends UiTestAssistJUnit {

    @BeforeEach
    protected void exportTestMainScreenPackage() {
        exportScreensPackages(Collections.singletonList("io.jmix.ui.testassist.app.main"));
    }

    @Override
    protected void setupAuthentication() {
        UserDetails user = User.builder()
                .username("system")
                .password("")
                .authorities(Collections.emptyList())
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        authentication.setDetails(ClientDetails.builder().locale(Locale.US).build());

        SecurityContextHelper.setAuthentication(authentication);
    }

    @Override
    protected void cleanupAuthentication() {
        SecurityContextHelper.setAuthentication(null);
    }

    protected Screen showTestMainScreen() {
        return getScreens()
                .create("testMainScreen", OpenMode.ROOT)
                .show();
    }
}
