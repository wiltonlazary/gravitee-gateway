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
import ApiCreationController from './api-creation.controller';
import { shouldDisplayHint } from './form.helper';

const ApiCreationStep2Component: ng.IComponentOptions = {
  require: {
    parent: '^apiCreation',
  },
  template: require('./api-creation-step2.html'),
  controller: class {
    public shouldDisplayHint = shouldDisplayHint;

    private parent: ApiCreationController;
    private advancedMode: boolean;

    constructor() {
      'ngInject';
      this.advancedMode = false;
    }

    toggleAdvancedMode = () => {
      this.advancedMode = !this.advancedMode;
      if (!this.advancedMode) {
        this.parent.api.tags = [];
        this.parent.selectedTenants = [];
      }
    };
  },
};

export default ApiCreationStep2Component;
