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

import { Metrics, Scope } from '../../../../../entities/alert';

const AlertTriggerConditionStringComponent: ng.IComponentOptions = {
  bindings: {
    condition: '<',
    metrics: '<',
    isReadonly: '<',
  },
  template: require('./trigger-condition-string.html'),
  controller: function ($injector, $state) {
    'ngInject';

    this.$onInit = () => {
      // Get the metric field according to the condition property
      const metric = _.find(this.metrics as Metrics[], (metric) => metric.key === this.condition.property);

      if (metric.loader) {
        let referenceId;
        let referenceType;

        if ($state.params.apiId) {
          referenceType = Scope.API;
          referenceId = $state.params.apiId;
        } else if ($state.params.applicationId) {
          referenceType = Scope.APPLICATION;
          referenceId = $state.params.applicationId;
        } else {
          referenceType = Scope.ENVIRONMENT;
        }

        this.values = metric.loader(referenceType, referenceId, $injector);
      }
    };

    this.displaySelect = () => {
      return this.values !== undefined && (this.condition.operator === 'EQUALS' || this.condition.operator === 'NOT_EQUALS');
    };
  },
};

export default AlertTriggerConditionStringComponent;
