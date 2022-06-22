/*
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
import { describe } from '@jest/globals';
import { PortalApi } from '@portal-apis/PortalApi';
import { forPortalAsAdminUser } from '@client-conf/*';
import { notFound } from '@lib/jest-utils';

const portalApiAsAdmin = new PortalApi(forPortalAsAdminUser());

describe('Portal: Business Error - configuration', () => {
  test('should return not found ', async () => {
    const identityProviderId = 'IDENTITY_PROVIDER';
    const expectedMessage = { message: `Identity provider [${identityProviderId}] can not be found.` };
    await notFound(portalApiAsAdmin.getPortalIdentityProviderRaw({ identityProviderId }), expectedMessage);
  });
});
