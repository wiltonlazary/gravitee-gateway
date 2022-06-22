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
import ReCaptchaService from '../../services/reCaptcha.service';

class ResetPasswordController {
  constructor(jwtHelper, $state, $scope, UserService, NotificationService, ReCaptchaService: ReCaptchaService) {
    'ngInject';

    ReCaptchaService.displayBadge();

    if (!$state.params.token) {
      $state.go('management');
    }
    try {
      if (jwtHelper.isTokenExpired($state.params.token)) {
        $scope.error = 'Your token is expired!';
      } else {
        $scope.user = jwtHelper.decodeToken($state.params.token);
      }
    } catch (e) {
      $scope.error = e.toString();
    }

    $scope.changePassword = function () {
      ReCaptchaService.execute('finalizeResetPassword').then(() => {
        UserService.finalizeResetPassword($scope.user.sub, {
          token: $state.params.token,
          password: $scope.confirmPassword,
          firstname: $scope.user.firstname,
          lastname: $scope.user.lastname,
        }).then(() => {
          $scope.formConfirm.$setPristine();
          NotificationService.show('Your password has been initialized successfully, you can now login...');
          $state.go('login');
        });
      });
    };

    $scope.isInvalidPassword = function () {
      return $scope.confirmPassword && $scope.user.password && $scope.confirmPassword !== $scope.user.password;
    };
  }
}

export default ResetPasswordController;
