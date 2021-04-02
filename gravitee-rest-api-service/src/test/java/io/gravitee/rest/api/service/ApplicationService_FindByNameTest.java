/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.service;

import io.gravitee.repository.management.api.ApplicationRepository;
import io.gravitee.repository.management.model.ApplicationStatus;
import io.gravitee.rest.api.model.application.ApplicationListItem;
import io.gravitee.rest.api.service.impl.ApplicationServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationService_FindByNameTest {

    @InjectMocks
    private ApplicationServiceImpl applicationService = new ApplicationServiceImpl();

    @Mock
    private ApplicationRepository applicationRepository;

    @Test
    public void shouldNotFindByNameWhenNull() throws Exception {
        Set<ApplicationListItem> set = applicationService.findByNameAndStatus(null, null);
        assertNotNull(set);
        assertEquals("result is empty", 0, set.size());
        verify(applicationRepository, never()).findByNameAndStatuses(any(), any());
    }

    @Test
    public void shouldNotFindByNameWhenEmpty() throws Exception {
        Set<ApplicationListItem> set = applicationService.findByNameAndStatus(" ", "");
        assertNotNull(set);
        assertEquals("result is empty", 0, set.size());
        verify(applicationRepository, never()).findByNameAndStatuses(any(), any());
    }

    @Test
    public void shouldNotFindByName() throws Exception {
        Set<ApplicationListItem> set = applicationService.findByNameAndStatus("a", "ACTIVE");
        assertNotNull(set);
        assertEquals("result is empty", 0, set.size());
        verify(applicationRepository, times(1)).findByNameAndStatuses("a", ApplicationStatus.ACTIVE);
    }
}
