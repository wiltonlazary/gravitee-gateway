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
import { StateService } from '@uirouter/core';
import { IPromise } from 'angular';
import * as _ from 'lodash';

import { ApiService } from '../../../../services/api.service';
import NotificationService from '../../../../services/notification.service';
import UserService from '../../../../services/user.service';
import NewApiImportController, { getDefinitionVersionDescription, getDefinitionVersionTitle } from '../newApiImport.controller';
import { PlanSecurityType } from '../../../../entities/plan/plan';

interface Page {
  fileName: string;
  published?: boolean;
  name?: string;
}

interface Api {
  name?: string;
  version?: string;
  gravitee: string;
  proxy: {
    endpoints: any[];
    context_path?: string;
  };
  pages: Array<Page>;
  plans: any[];
  tags: any[];
  groups: any[];
  lifecycle_state?: string;
  execution_mode?: string;
}

class ApiCreationController {
  api: Api;
  selectedTenants: any[];
  attachableGroups: any[];
  poGroups: any[];

  private parent: NewApiImportController;
  private vm: {
    selectedStep: number;
    stepProgress: number;
    maxStep: number;
    showBusyText: boolean;
    stepData: {
      step: number;
      label?: string;
      completed: boolean;
      optional: boolean;
      data: any;
    }[];
  };
  private contextPathInvalid: boolean;
  private plan: any;

  private pages: any;
  private securityTypes: { id: string; name: string }[];
  private rateLimitTimeUnits: string[];
  private quotaTimeUnits: string[];
  private methods: string[];
  private resourceFiltering: {
    whitelist: any;
  };
  private skippedStep: boolean;
  private apiSteps: any[];
  private endpoint: any;
  private rateLimit: any;
  private quota: any;

  // Inject with component binding & route resolver
  // Useful in template of steps
  private tags: any[];
  private tenants: any[];
  private groups: any[];

  constructor(
    private $scope,
    private $timeout,
    private $mdDialog,
    private $stateParams,
    private $window,
    private ApiService: ApiService,
    private NotificationService: NotificationService,
    private UserService: UserService,
    private $state: StateService,
    private Constants: any,
    private $rootScope,
  ) {
    'ngInject';
    this.api = {
      gravitee: ['2.0.0', '1.0.0'].includes($stateParams.definitionVersion) ? $stateParams.definitionVersion : '2.0.0',
      proxy: {
        endpoints: [],
      },
      pages: [],
      plans: [],
      tags: [],
      groups: [],
    };

    if (this.Constants.org.settings.jupiterMode.enabled && this.Constants.org.settings.jupiterMode.isDefault !== 'never') {
      this.api.execution_mode = 'jupiter';
    } else {
      this.api.execution_mode = 'v3';
    }

    this.contextPathInvalid = true;
    this.plan = {
      characteristics: [],
    };

    this.pages = {};
    this.securityTypes = [];
    if (this.Constants.env.settings.plan.security.apikey.enabled) {
      this.securityTypes.push({
        id: PlanSecurityType.API_KEY,
        name: 'API Key',
      });
    }
    if (this.Constants.env.settings.plan.security.keyless.enabled) {
      this.securityTypes.push({
        id: PlanSecurityType.KEY_LESS,
        name: 'Keyless (public)',
      });
    }

    this.rateLimitTimeUnits = ['SECONDS', 'MINUTES'];
    this.quotaTimeUnits = ['HOURS', 'DAYS', 'WEEKS', 'MONTHS'];

    this.methods = ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS', 'TRACE', 'CONNECT'];

    this.resourceFiltering = {
      whitelist: [],
    };

    // init steps settings
    this.initStepSettings();

    // init documentation settings
    this.initDocumentationSettings();
  }

  $onInit = () => {
    this.attachableGroups = this.groups.filter((group) => group.apiPrimaryOwner == null);
    const currentUserGroups = this.UserService.getCurrentUserGroups();
    this.poGroups = this.groups.filter(
      (group) => group.apiPrimaryOwner != null && currentUserGroups.some((userGroup) => userGroup === group.name),
    );
  };

  /*
   md-stepper
   */
  initStepSettings() {
    this.skippedStep = false;
    this.apiSteps = this.steps().slice(0, 2);
    this.vm = {
      selectedStep: 0,
      stepProgress: 1,
      maxStep: 5,
      showBusyText: false,
      stepData: [
        { step: 1, completed: false, optional: false, data: {} },
        { step: 2, completed: false, optional: false, data: {} },
        { step: 3, label: 'Plan', completed: false, optional: true, data: {} },
        { step: 4, label: 'Documentation', completed: false, optional: true, data: {} },
        { step: 5, label: 'Confirmation', completed: false, optional: false, data: {} },
      ],
    };
  }

  enableNextStep() {
    // do not exceed into max step
    if (this.vm.selectedStep >= this.vm.maxStep) {
      return;
    }
    // do not increment vm.stepProgress when submitting from previously completed step
    if (this.vm.selectedStep === this.vm.stepProgress - 1) {
      this.vm.stepProgress = this.vm.stepProgress + 1;
    }

    const stepIndex = this.vm.selectedStep + 1;
    // change api step state
    if (this.skippedStep) {
      this.apiSteps[stepIndex].badgeClass = 'disable';
      this.apiSteps[stepIndex].badgeIconClass = 'content:remove_circle';
      this.apiSteps[stepIndex].title = this.steps()[this.vm.selectedStep].title + ' <em>skipped</em>';
      this.skippedStep = false;
    } else {
      this.apiSteps[stepIndex].badgeClass = 'info';
      this.apiSteps[stepIndex].badgeIconClass = 'action:check_circle';
    }
    if (!this.apiSteps[stepIndex + 1]) {
      this.apiSteps.push(this.steps()[stepIndex + 1]);
    }

    this.$timeout(() => {
      this.vm.selectedStep = this.vm.selectedStep + 1;
    });
  }

  moveToPreviousStep() {
    if (this.vm.selectedStep > 0) {
      this.vm.selectedStep = this.vm.selectedStep - 1;
    } else {
      this.$state.go('management.apis.new');
    }
  }

  selectStep(step) {
    this.vm.selectedStep = step;
  }

  submitCurrentStep(stepData) {
    this.vm.showBusyText = true;
    if (!stepData.completed) {
      if (this.vm.selectedStep !== 4) {
        this.vm.showBusyText = false;
        // move to next step when success
        stepData.completed = true;
        this.enableNextStep();
      }
    } else {
      this.vm.showBusyText = false;
      this.enableNextStep();
    }
  }

  /*
   API creation
   */
  createAPI(deployAndStart, readyForReview?: boolean) {
    // clear API pages json format
    _.forEach(this.api.pages, (page) => {
      if (!page.name) {
        page.name = page.fileName;
      }
      delete page.fileName;
      // handle publish state
      page.published = deployAndStart;
    });

    // handle plan publish state
    _.forEach(this.api.plans, (plan) => {
      plan.status = deployAndStart ? 'PUBLISHED' : 'STAGING';
    });

    if (this.api.groups != null) {
      this.api.groups = this.api.groups.map((group) => group.name);
    }

    // create API
    if (deployAndStart) {
      this.api.lifecycle_state = 'PUBLISHED';
    }
    this.ApiService.import(null, this.api)
      .then((api) => {
        this.vm.showBusyText = false;
        return api;
      })
      .then((api) => {
        if (readyForReview) {
          this.ApiService.askForReview(api.data).then((response) => {
            api.data.workflow_state = 'IN_REVIEW';
            api.data.etag = response.headers('etag');
            this.api = api.data;
            this.$rootScope.$broadcast('apiChangeSuccess', { api: api.data });
          });
        }
        return api;
      })
      .then((api) => {
        if (deployAndStart) {
          this.ApiService.deploy(api.data.id).then(() => {
            this.ApiService.start(api.data).then(() => {
              this.NotificationService.show('API created, deployed and started');
              this.$state.go('management.apis.detail.portal.general', { apiId: api.data.id });
            });
          });
        } else {
          this.NotificationService.show('API created');
          this.$state.go('management.apis.detail.portal.general', { apiId: api.data.id });
        }
        return api;
      })
      .catch(() => {
        this.vm.showBusyText = false;
      });
  }

  /*
   API context-path
   */
  validFirstStep(stepData) {
    const stepMessage = `${this.api.name} (${this.api.version}) <code>${this.api.proxy.context_path}</code>`;
    if (this.contextPathInvalid) {
      const criteria = { context_path: this.api.proxy.context_path };
      this.ApiService.verify(criteria).then(
        () => {
          this.contextPathInvalid = false;
          this.submitCurrentStep(stepData);
          this.apiSteps[this.vm.selectedStep + 1].title = stepMessage;
        },
        () => {
          this.contextPathInvalid = true;
        },
      );
    } else {
      this.submitCurrentStep(stepData);
      this.apiSteps[this.vm.selectedStep + 1].title = stepMessage;
    }
  }

  onChangeContextPath() {
    this.contextPathInvalid = true;
  }

  /*
   API endpoint
   */
  selectEndpoint() {
    this.api.proxy.endpoints = [];
    const endpoint = {
      name: 'default',
      target: this.endpoint,
      tenants: this.selectedTenants,
      inherit: true,
    };

    this.api.proxy.endpoints.push(endpoint);

    this.apiSteps[this.vm.selectedStep].title = endpoint.target;
  }

  /*
   API plan
   */
  selectPlan() {
    // set validation mode
    if (this.plan.security === PlanSecurityType.KEY_LESS) {
      this.plan.validation = 'AUTO';
    }
    if (!this.plan.validation) {
      this.plan.validation = 'MANUAL';
    }

    // set resource filtering whitelist
    _.remove(this.resourceFiltering.whitelist, (whitelistItem: any) => {
      return !whitelistItem.pattern;
    });
    if (this.api.gravitee === '1.0.0') {
      this.plan.paths = {
        '/': [],
      };

      if (this.resourceFiltering.whitelist.length) {
        this.plan.paths['/'].push({
          methods: this.methods,
          'resource-filtering': {
            whitelist: this.resourceFiltering.whitelist,
          },
        });
      }
      // set rate limit policy
      if (this.rateLimit && this.rateLimit.limit) {
        this.plan.paths['/'].push({
          methods: this.methods,
          'rate-limit': {
            rate: this.rateLimit,
          },
        });
      }
      // set quota policy
      if (this.quota && this.quota.limit) {
        this.plan.paths['/'].push({
          methods: this.methods,
          quota: {
            quota: this.quota,
            addHeaders: true,
          },
        });
      }
    } else {
      const flow = {
        'path-operator': {
          path: '/',
          operator: 'STARTS_WITH',
        },
        condition: '',
        pre: [],
        post: [],
      };
      if (this.resourceFiltering.whitelist.length) {
        flow.pre.push({
          name: 'Resource Filtering',
          policy: 'resource-filtering',
          configuration: {
            whitelist: this.resourceFiltering.whitelist,
          },
        });
      }
      // set rate limit policy
      if (this.rateLimit && this.rateLimit.limit) {
        flow.pre.push({
          name: 'Rate limit',
          policy: 'rate-limit',
          configuration: {
            rate: this.rateLimit,
          },
        });
      }
      // set quota policy
      if (this.quota && this.quota.limit) {
        flow.pre.push({
          name: 'Quota',
          policy: 'quota',
          configuration: {
            quota: this.quota,
            addHeaders: true,
          },
        });
      }
      this.plan.flows = [flow];
    }
    this.api.plans = [this.plan];
    // set api step message
    this.apiSteps[this.vm.selectedStep].title = `${this.plan.name} <code>${this.plan.security}</code><code>${this.plan.validation}</code>`;
  }

  skipAddPlan() {
    this.api.plans = [];
    this.plan = {};
    this.skippedStep = true;
  }

  resetRateLimit() {
    delete this.rateLimit;
  }

  resetQuota() {
    delete this.quota;
  }

  /*
   API documentation
   */
  initDocumentationSettings() {
    this.$scope.$watch('newApiPageFile.content', (data) => {
      if (data) {
        const file = {
          name: this.$scope.newApiPageFile.name,
          content: data,
          type: '',
        };

        const fileExtension = file.name.split('.').pop().toUpperCase();
        switch (fileExtension) {
          case 'MD':
            file.type = 'MARKDOWN';
            break;
          case 'YAML':
          case 'YML':
          case 'JSON':
            if (file.content.match(/.*"?(swagger|openapi)"?: *['"]?\d/)) {
              file.type = 'SWAGGER';
            } else if (file.content.match(/.*"?asyncapi"?: *['"]?\d/)) {
              file.type = 'ASYNCAPI';
            }
            break;
          case 'ADOC':
            file.type = 'ASCIIDOC';
            break;
        }
        if (file.type) {
          this.selectFile(file);
        } else {
          this.NotificationService.showError('Only Markdown, OpenAPI, AsyncAPI, and AsciiDoc files are supported');
        }
      }
    });
  }

  selectDocumentation() {
    this.apiSteps[this.vm.selectedStep].title = this.api.pages.map((page) => page.name).join(' ');
  }

  selectFile(file) {
    if (file && !this.pageAlreadyExist(file.name)) {
      const page = {
        fileName: file.name,
        name: file.name,
        content: file.content,
        type: file.type,
        published: false,
      };

      this.api.pages.push(page);
    }
  }

  pageAlreadyExist(pageFileName) {
    return _.some(this.api.pages, (page: any) => {
      return page.fileName === pageFileName;
    });
  }

  hasPage() {
    return this.api.pages && this.api.pages.length > 0;
  }

  removePage(pageToRemove: { fileName: string }): IPromise<void> {
    return this.$mdDialog
      .show({
        controller: 'DialogConfirmController',
        controllerAs: 'ctrl',
        template: require('../../../../components/dialog/confirmWarning.dialog.html'),
        clickOutsideToClose: true,
        locals: {
          title: 'Warning',
          msg: 'Are you sure you want to remove this page?',
        },
      })
      .then(() => {
        this.api.pages = this.api.pages.filter((page) => page.fileName !== pageToRemove.fileName);
      });
  }

  skipDocumentation() {
    this.api.pages = [];
    this.skippedStep = true;
  }

  steps() {
    return [
      {
        badgeClass: 'info',
        badgeIconClass: 'action:check_circle',
        title: getDefinitionVersionTitle(this.api.gravitee),
        content: getDefinitionVersionDescription(this.api.gravitee),
      },
      {
        badgeClass: 'disable',
        badgeIconClass: 'notification:sync',
        title: 'General',
        content: 'Name, version and context-path',
      },
      {
        badgeClass: 'disable',
        badgeIconClass: 'notification:sync',
        title: 'Gateway',
        content: 'Endpoint',
      },
      {
        badgeClass: 'disable',
        badgeIconClass: 'notification:sync',
        title: 'Plan',
        content: 'Name, security type and validation mode',
      },
      {
        badgeClass: 'disable',
        badgeIconClass: 'notification:sync',
        title: 'Documentation',
        content: 'Pages name',
      },
    ];
  }
}

export default ApiCreationController;
