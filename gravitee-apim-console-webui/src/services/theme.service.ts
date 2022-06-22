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

import { Theme } from '../entities/theme';

class ThemeService {
  constructor(private $http, private Constants) {
    'ngInject';
  }

  get() {
    return this.$http.get(`${this.Constants.env.baseURL}/configuration/themes/default`);
  }

  restoreDefaultTheme(theme: Theme) {
    return this.$http.post(`${this.Constants.env.baseURL}/configuration/themes/${theme.id}/reset`);
  }

  update(theme: Theme) {
    if (theme) {
      return this.$http.put(`${this.Constants.env.baseURL}/configuration/themes/${theme.id}`, {
        id: theme.id,
        name: theme.name,
        reference_type: theme.reference_type,
        reference_id: theme.reference_id,
        enabled: theme.enabled,
        definition: theme.definition,
        logo: theme.logo,
        optionalLogo: theme.optionalLogo,
        backgroundImage: theme.backgroundImage,
        favicon: theme.favicon,
      });
    }
  }

  getLogoUrl(theme) {
    if (theme) {
      return this.getImageUrl(theme, 'logo');
    }
    return '';
  }

  getOptionalLogoUrl(theme) {
    if (theme) {
      return this.getImageUrl(theme, 'optionalLogo');
    }
    return '';
  }

  getFaviconUrl(theme) {
    if (theme) {
      return this.getImageUrl(theme, 'favicon');
    }
    return '';
  }

  getBackgroundImageUrl(theme) {
    if (theme) {
      return this.getImageUrl(theme, 'backgroundImage');
    }
    return '';
  }

  private getImageUrl(theme, image) {
    return `${this.Constants.env.baseURL}/configuration/themes/${theme.id}/${image}?${theme.updated_at}`;
  }
}

export default ThemeService;
