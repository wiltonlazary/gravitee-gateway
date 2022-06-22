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
import { Api, ApiLifecycleState, ApiMember, UpdateApiEntity } from '@model/apis';
import {
  ApiImport,
  ImportSwaggerDescriptorEntity,
  ImportSwaggerDescriptorEntityFormat,
  ImportSwaggerDescriptorEntityType,
} from '@model/api-imports';
import { BasicAuthentication } from '@model/users';
import { ProcessSubscriptionEntity } from '@model/api-subscriptions';
import { requestGateway } from 'ui-test/support/common/http.commands';

export function createApi(auth: BasicAuthentication, body: Api, failOnStatusCode = false) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis`,
    body,
    auth,
    failOnStatusCode,
  });
}

export function publishApi(auth: BasicAuthentication, createdApi: Api, failOnStatusCode = false) {
  const apiToPublish = {
    ...createdApi,
    lifecycle_state: ApiLifecycleState.PUBLISHED,
  };
  delete apiToPublish.id;
  delete apiToPublish.state;
  delete apiToPublish.created_at;
  delete apiToPublish.updated_at;
  delete apiToPublish.owner;
  delete apiToPublish.contextPath;
  return cy.request({
    method: 'PUT',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${createdApi.id}`,
    body: apiToPublish,
    auth,
    failOnStatusCode,
  });
}

export function deleteApi(auth: BasicAuthentication, apiId: string, failOnStatusCode = false) {
  return cy.request({
    method: 'DELETE',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}`,
    auth,
    failOnStatusCode,
  });
}

export function deployApi(auth: BasicAuthentication, apiId: string, failOnStatusCode = false) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/deploy`,
    auth,
    failOnStatusCode,
  });
}

export function startApi(auth: BasicAuthentication, apiId: string, failOnStatusCode = false) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}?action=START`,
    auth,
    failOnStatusCode,
  });
}

export function stopApi(auth: BasicAuthentication, apiId: string, failOnStatusCode = false) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}?action=STOP`,
    auth,
    failOnStatusCode,
  });
}

export function importCreateApi(auth: BasicAuthentication, body: ApiImport) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/import`,
    body,
    auth,
    failOnStatusCode: false,
  });
}

export function importSwaggerApi(auth: BasicAuthentication, swaggerImport: string, attributes?: Partial<ImportSwaggerDescriptorEntity>) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/import/swagger`,
    qs: { definitionVersion: '2.0.0' },
    body: {
      payload: swaggerImport,
      ...attributes,
    },
    auth,
    failOnStatusCode: false,
  });
}

export function importUpdateApi(auth: BasicAuthentication, apiId: string, body: ApiImport) {
  return cy.request({
    method: 'PUT',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/import`,
    body,
    auth,
    failOnStatusCode: false,
  });
}

export function exportApi(auth: BasicAuthentication, apiId: string) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/export`,
    auth,
    failOnStatusCode: false,
  });
}

export function getApiById(auth: BasicAuthentication, apiId: string) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}`,
    auth,
    failOnStatusCode: false,
  });
}

export function getApiMetadata(auth: BasicAuthentication, apiId: string) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/metadata`,
    auth,
    failOnStatusCode: false,
  });
}

export function addMemberToApi(auth: BasicAuthentication, apiId: string, body: ApiMember) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/members`,
    body,
    auth,
    failOnStatusCode: false,
  });
}

export function getApiMembers(auth: BasicAuthentication, apiId: string) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/members`,
    auth,
    failOnStatusCode: false,
  });
}

export function updateApi(auth: BasicAuthentication, apiId: string, apiUpdate: UpdateApiEntity, failOnStatusCode = false) {
  return cy.request({
    method: 'PUT',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}`,
    auth,
    body: apiUpdate,
    failOnStatusCode,
  });
}

export function updateApiSubscription(
  auth: BasicAuthentication,
  apiId: string,
  subscriptionId: string,
  subscription: ProcessSubscriptionEntity,
) {
  return cy.request({
    method: 'POST',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/subscriptions/${subscriptionId}/_process`,
    auth,
    body: subscription,
    failOnStatusCode: false,
  });
}

export function getApiKeys(auth: BasicAuthentication, apiId: string, subscriptionId: string) {
  return cy.request({
    method: 'GET',
    url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/subscriptions/${subscriptionId}/apikeys`,
    auth,
    failOnStatusCode: false,
  });
}

export function getApiAnalytics(auth: BasicAuthentication, apiId: string, field = 'mapped-path') {
  return requestGateway(
    {
      url: `${Cypress.config().baseUrl}${Cypress.env('managementApi')}/apis/${apiId}/analytics`,
      auth,
      headers: {
        'Cache-Control': 'no-cache, no-store',
        Connection: 'close',
      },
      qs: {
        type: 'group_by',
        field,
        interval: 10000,
        from: Date.now() - 5 * 60 * 1000,
        to: Date.now(),
      },
    },
    {
      validWhen: (response) => !Cypress._.isEmpty(response.body.values),
    },
  );
}
