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
import { BasicAuthentication } from '@model/users';
import { PlanStatus } from '@model/plan';

export function getPlans(auth: BasicAuthentication, apiId: string, status: PlanStatus) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/plans`,
    auth,
    failOnStatusCode: false,
    qs: {
      status: status,
    },
  });
}

export function getPlan(auth: BasicAuthentication, apiId: string, planId: string) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/plans/${planId}`,
    auth,
    failOnStatusCode: false,
  });
}
