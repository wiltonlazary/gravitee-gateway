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
import * as moment from 'moment';

function DialogApiKeyExpirationController($scope, $mdDialog, maxEndDate) {
  'ngInject';

  const now = new Date();
  $scope.minDate = now;
  $scope.maxDate = maxEndDate ? new Date(maxEndDate) : new Date(2099, 11, 31);

  $scope.expiration = moment(now);

  this.hide = function () {
    $mdDialog.cancel();
  };

  this.save = function () {
    $mdDialog.hide($scope.expiration ? $scope.expiration.toDate() : null);
  };
}

export default DialogApiKeyExpirationController;
