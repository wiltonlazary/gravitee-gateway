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
function DialogSubscriptionTransferController($scope, $mdDialog, plans) {
  'ngInject';
  this.plans = plans;

  this.hide = function () {
    $mdDialog.cancel();
  };

  this.save = function () {
    $mdDialog.hide(this.plan);
  };

  this.hasGeneralConditions = function (plan) {
    return plan.general_conditions !== undefined && plan.general_conditions !== null;
  };

  this.atLeastOnePlanWithGeneralConditions = function () {
    return this.plans.find((p) => p.general_conditions !== undefined && p.general_conditions !== '') != null;
  };
}

export default DialogSubscriptionTransferController;
