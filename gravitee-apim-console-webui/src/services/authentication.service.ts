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
import { IScope } from 'angular';
import * as _ from 'lodash';
import { AuthProvider, SatellizerConfig } from 'satellizer';

import RouterService from './router.service';
import UserService from './user.service';

import { IdentityProvider } from '../entities/identity-provider/identityProvider';

class AuthenticationService {
  constructor(
    private $rootScope: IScope,
    private Constants,
    private $window,
    private $state: StateService,
    private $auth: AuthProvider,
    private UserService: UserService,
    private RouterService: RouterService,
    private SatellizerConfig: SatellizerConfig,
  ) {
    'ngInject';
  }

  authenticate(provider: IdentityProvider, state?: string) {
    let satellizerProvider = this.SatellizerConfig.providers[provider.id];
    if (!satellizerProvider) {
      const newSatellizerProviderConfig: any = {
        oauthType: '2.0',
        scope: provider.scopes,
      };
      if (provider.type === 'GOOGLE' || provider.type === 'GITHUB') {
        newSatellizerProviderConfig.requiredUrlParams = provider.requiredUrlParams;
        newSatellizerProviderConfig.optionalUrlParams = provider.optionalUrlParams;
      } else {
        newSatellizerProviderConfig.requiredUrlParams = ['scope', 'state'];
      }

      satellizerProvider = _.merge(provider, newSatellizerProviderConfig);
    } else {
      provider.scope = provider.scopes;
      _.merge(satellizerProvider, provider);
    }

    this.SatellizerConfig.providers[provider.id] = _.merge(satellizerProvider, {
      state: state || this.nonce(32),
      url: this.Constants.org.baseURL + '/auth/oauth2/' + provider.id,
      redirectUri: window.location.origin + (window.location.pathname === '/' ? '' : window.location.pathname),
    });

    this.$auth.authenticate(provider.id).then((response) => {
      this.UserService.current().then((user) => {
        if (provider.userLogoutEndpoint) {
          this.$window.localStorage.setItem('user-logout-url', provider.userLogoutEndpoint);
        }
        this.$rootScope.$broadcast('graviteeUserRefresh', { user: user });

        const state = response.data.state;

        if (state !== undefined) {
          const nonce = JSON.parse(this.$window.localStorage[state]);
          if (nonce.redirectUri) {
            this.$window.location.href = nonce.redirectUri;
            return;
          }
        }

        const route = this.RouterService.getLastRoute();
        if (route.from && route.from.name !== '' && route.from.name !== 'logout' && route.from.name !== 'confirm') {
          this.$state.go(route.from.name, route.fromParams);
        } else {
          this.$state.go('management');
        }
      });
    });
  }

  nonce(length: number) {
    let text = '';
    const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    for (let i = 0; i < length; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
  }
}

export default AuthenticationService;
