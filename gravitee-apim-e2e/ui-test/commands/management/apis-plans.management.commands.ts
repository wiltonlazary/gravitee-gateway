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
import { ErrorableManagement, RequestInfo } from '@model/technical';
import Chainable = Cypress.Chainable;
import Response = Cypress.Response;
import { HttpConnector } from '@model/technical.http';
import { Plan } from '@model/plan';

export class ApisPlansManagementCommands extends HttpConnector {
  constructor(requestInfo: RequestInfo) {
    super(requestInfo);
  }

  getAll<T extends ErrorableManagement<Plan[]> = Plan[]>(apiId: string): Chainable<Response<T>> {
    return this.httpClient.get(`/apis/${apiId}/plans`);
  }

  create<T extends ErrorableManagement<Plan> = Plan>(apiId: string, plan: Plan): Chainable<Response<T>> {
    return this.httpClient.post(`/apis/${apiId}/plans`, plan);
  }
}
