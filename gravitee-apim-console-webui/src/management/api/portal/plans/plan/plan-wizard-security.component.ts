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

import * as _ from 'lodash';

import ApiEditPlanController from './edit-plan.controller';

import PolicyService from '../../../../../services/policy.service';
import ResourceService from '../../../../../services/resource.service';
import '@gravitee/ui-components/wc/gv-schema-form';
import '@gravitee/ui-components/wc/gv-row';
import { PlanSecurityType } from '../../../../../entities/plan/plan';

const ApiPlanWizardSecurityComponent: ng.IComponentOptions = {
  require: {
    parent: '^editPlan',
  },
  template: require('./plan-wizard-security.html'),
  controller: class {
    private securityTypes: any[];
    private securityDefinition: any;
    private securitySchema: any;
    private parent: ApiEditPlanController;

    constructor(private PolicyService: PolicyService, Constants: any, private ResourceService: ResourceService) {
      'ngInject';

      this.securityTypes = _.filter(
        [
          {
            id: PlanSecurityType.OAUTH2,
            name: 'OAuth2',
            policy: 'oauth2',
          },
          {
            id: PlanSecurityType.JWT,
            name: 'JWT',
            policy: 'jwt',
          },
          {
            id: PlanSecurityType.API_KEY,
            name: 'API Key',
            policy: 'api-key',
          },
          {
            id: PlanSecurityType.KEY_LESS,
            name: 'Keyless (public)',
          },
        ],
        (security) => {
          return Constants.env.settings.plan.security[_.replace(security.id.toLowerCase(), '_', '')].enabled;
        },
      );
    }

    $onInit() {
      if (this.parent.plan.security) {
        this.onSecurityTypeChange();
      }
    }

    onSecurityDefinitionChange({ detail: { values } }) {
      this.parent.plan.securityDefinition = values;
    }

    onFetchResources(event) {
      if (this.parent.resourceTypes != null && this.parent.api.resources != null) {
        const { currentTarget, regexTypes } = event.detail;
        const options = this.parent.api.resources
          .filter((resource) => regexTypes == null || new RegExp(regexTypes).test(resource.type))
          .map((resource) => {
            const resourceType = this.parent.resourceTypes.find((type) => type.id === resource.type);
            const row = document.createElement('gv-row');
            const picture = resourceType.icon ? resourceType.icon : null;
            (row as any).item = { picture, name: resource.name };
            return {
              element: row,
              value: resource.name,
              id: resource.type,
            };
          });

        currentTarget.options = options;
      }
    }

    onSecurityTypeChange() {
      const securityType: any = _.find(this.securityTypes, { id: this.parent.plan.security });
      if (securityType && securityType.policy) {
        this.PolicyService.getSchema(securityType.policy).then((schema) => {
          this.securitySchema = schema.data;
          if (this.parent.plan.securityDefinition) {
            try {
              // Try a double parsing (it appears that sometimes the json of security definition is double-encoded
              this.parent.plan.securityDefinition = JSON.parse(this.parent.plan.securityDefinition);
            } catch (e) {}
          } else {
            this.parent.plan.securityDefinition = {};
          }
        });
      } else {
        this.securitySchema = undefined;
        this.parent.plan.securityDefinition = {};
      }

      if (this.parent.plan.id === undefined) {
        this.parent.plan.securityDefinition = {};
      }
    }

    gotoNextStep() {
      this.parent.vm.stepData[1].data = this.parent.plan;
      if (!this.parent.hasRestrictionStep()) {
        this.parent.saveOrUpdate();
      } else {
        this.parent.moveToNextStep(this.parent.vm.stepData[1]);
      }
    }
  },
};

export default ApiPlanWizardSecurityComponent;
