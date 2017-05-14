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

const PagesComponent: ng.IComponentOptions = {
  bindings: {
    pages: '<'
  },
  template: require('./pages.html'),
  controller: function($state, $stateParams) {
    'ngInject';

    this.$onInit = function() {
      if (this.pages.length && !$stateParams.pageId) {
        $state.go('portal.pages.page', {pageId: this.pages[0].id});
      } else {
        _.each(this.pages, function(p) { p.selected = (p.id === $stateParams.pageId); });
      }
    };

    this.selectPage = function (page) {
      _.each(this.pages, function(p) { p.selected = false; });
      page.selected = true;
      $state.go('portal.pages.page', {pageId: page.id});
    }
  }
};

export default PagesComponent;
