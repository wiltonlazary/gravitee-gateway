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
import { ILogService, IScope, ITimeoutService } from 'angular';
import * as _ from 'lodash';

interface ILogsFiltersScope extends IScope {
  logsFiltersForm: any;
  $parent: any;
}
class LogsFiltersController {
  public filters: any = {};
  public methods = {
    0: 'OTHER',
    1: 'CONNECT',
    2: 'DELETE',
    3: 'GET',
    4: 'HEAD',
    5: 'OPTIONS',
    6: 'PATCH',
    7: 'POST',
    8: 'PUT',
    9: 'TRACE',
  };
  public responseTimes = {
    '[0 TO 100]': '0 to 100ms',
    '[100 TO 200]': '100 to 200ms',
    '[200 TO 300]': '200 to 300ms',
    '[300 TO 400]': '300 to 400ms',
    '[400 TO 500]': '400 to 500ms',
    '[500 TO 1000]': '500 to 1000ms',
    '[1000 TO 2000]': '1000 to 2000ms',
    '[2000 TO 5000]': '2000 to 5000ms',
    '[5000 TO *]': '> 5000ms',
  };
  public httpStatus = {
    100: 'CONTINUE',
    101: 'SWITCHING PROTOCOLS',
    102: 'PROCESSING',
    200: 'OK',
    201: 'CREATED',
    202: 'ACCEPTED',
    203: 'NON AUTHORITATIVE INFORMATION',
    204: 'NO CONTENT',
    205: 'RESET CONTENT',
    206: 'PARTIAL CONTENT',
    207: 'MULTI STATUS',
    300: 'MULTIPLE CHOICES',
    301: 'MOVED PERMANENTLY',
    302: 'FOUND',
    303: 'SEE OTHER',
    304: 'NOT MODIFIED',
    305: 'USE PROXY',
    307: 'TEMPORARY REDIRECT',
    400: 'BAD REQUEST',
    401: 'UNAUTHORIZED',
    402: 'PAYMENT REQUIRED',
    403: 'FORBIDDEN',
    404: 'NOT FOUND',
    405: 'METHOD NOT ALLOWED',
    406: 'NOT ACCEPTABLE',
    407: 'PROXY AUTHENTICATION REQUIRED',
    408: 'REQUEST TIMEOUT',
    409: 'CONFLICT',
    410: 'GONE',
    411: 'LENGTH REQUIRED',
    412: 'PRECONDITION FAILED',
    413: 'REQUEST ENTITY TOO LARGE',
    414: 'REQUEST URI TOO LONG',
    415: 'UNSUPPORTED MEDIA TYPE',
    416: 'REQUESTED RANGE NOT SATISFIABLE',
    417: 'EXPECTATION FAILED',
    422: 'UNPROCESSABLE ENTITY',
    423: 'LOCKED',
    424: 'FAILED DEPENDENCY',
    429: 'TOO MANY REQUESTS',
    500: 'INTERNAL SERVER ERROR',
    501: 'NOT IMPLEMENTED',
    502: 'BAD GATEWAY',
    503: 'SERVICE UNAVAILABLE',
    504: 'GATEWAY TIMEOUT',
    505: 'HTTP VERSION NOT SUPPORTED',
    507: 'INSUFFICIENT STORAGE',
  };
  private fields = {
    responseTime: 'response-time',
    id: '_id',
  };
  private onFiltersChange: any;
  private metadata: any;
  private api: any;
  private context: string;
  private displayMode: any;

  private displayModes = [
    {
      field: '',
      key: '',
      label: 'All',
    },
    {
      key: 'endpoint',
      field: '_exists_',
      label: 'Only hits to the backend endpoint',
    },
    {
      key: 'endpoint',
      field: '!_exists_',
      label: 'Without hits to the backend endpoint',
    },
  ];

  private static convert(params) {
    return '(' + params.join(' OR ') + ')';
  }

  constructor(
    private $scope: ILogsFiltersScope,
    private $state: StateService,
    private $timeout: ITimeoutService,
    private $log: ILogService,
  ) {
    'ngInject';
    this.$scope = $scope;
  }

  $onInit() {
    // init filters based on stateParams
    const q = this.$state.params.q;
    if (q) {
      this.decodeQueryFilters(q);
      _.forEach(this.displayModes, (displayMode) => {
        if (this.filters[displayMode.field]) {
          this.displayMode = displayMode;
        }
      });
      if (!this.displayMode) {
        this.displayMode = this.displayModes[0];
      }
    }

    if (this.context === 'platform' || this.context === 'api') {
      this.metadata.applications.push({
        id: '1',
        name: 'Unknown application',
      });
    }

    if (this.context === 'platform' || this.context === 'application') {
      this.metadata.apis.push({
        id: '1',
        name: 'Unknown API',
      });
    }
  }
  search() {
    const query = this.buildQuery(this.filters);
    // Update the query parameter
    this.$state.transitionTo(
      this.$state.current,
      _.merge(this.$state.params, {
        q: query,
      }),
      { notify: false },
    );

    this.$timeout(() => {
      this.onFiltersChange({ filters: query });
    });
  }

  clearFilters() {
    this.$scope.logsFiltersForm.$setPristine();
    this.filters = {};
    this.search();
  }

  hasFilters() {
    return !_.isEmpty(this.filters) && !this.isEmpty(this.filters);
  }

  updateDisplayMode() {
    _.forEach(this.displayModes, (displayMode) => {
      delete this.filters[displayMode.field];
    });
    delete this.filters[this.displayMode.field];
    if (this.displayMode.field) {
      this.filters[this.displayMode.field] = this.displayMode.key;
    }
  }

  private decodeQueryFilters(query) {
    const filters = query.split('AND');
    for (let i = 0; i < filters.length; i++) {
      const filter = filters[i].replace(/[()]/g, '');
      const k = filter.split(':')[0].trim();
      const v = filter
        .substring(filter.indexOf(':') + 1)
        .split('OR')
        .map((x) => x.trim());
      switch (k) {
        case 'api':
          this.filters.api = v;
          break;
        case 'application':
          this.filters.application = v;
          break;
        case 'path': {
          const value = v[0].replace(/\\"/g, '');
          if (this.api) {
            this.filters.uri = this.api.proxy.virtual_hosts[0].path + value;
          } else {
            this.filters.uri = value;
          }
          break;
        }
        case 'uri':
          this.filters.uri = v[0].replace(/\*|\\\\/g, '');
          break;
        case 'plan':
          this.filters.plan = v;
          break;
        case 'response-time':
          this.filters.responseTime = v;
          break;
        case 'method':
          this.filters.method = v;
          break;
        case 'status':
          this.filters.status = v;
          break;
        case '_id':
          this.filters.id = v[0];
          break;
        case 'transaction':
          this.filters.transaction = v[0];
          break;
        case 'tenant':
          this.filters.tenant = v;
          break;
        case '_exists_':
          this.filters._exists_ = v;
          break;
        case '!_exists_':
          this.filters['!_exists_'] = v;
          break;
        case 'body':
          this.filters.body = v[0].replace(/\*/g, '');
          break;
        case 'endpoint':
          this.filters.endpoint = v[0].replace(/\*|\\\\/g, '');
          break;
        case 'remote-address':
          this.filters['remote-address'] = v;
          break;
        case 'host':
          this.filters.host = v[0].replace(/\\"/g, '');
          break;
        default:
          this.$log.error('unknown filter: ', k);
          break;
      }
    }
  }

  private isEmpty(map) {
    // eslint-disable-next-line guard-for-in
    for (const key in map) {
      const val = map[key];
      if (val !== undefined && val.length > 0) {
        return false;
      }
    }
    return true;
  }

  private buildQuery(filters): string {
    let query = '';
    const keys = _.filter(Object.keys(filters), (key) => filters[key] !== undefined && filters[key].length > 0);
    let index = 0;
    _.forEach(keys, (key) => {
      let val = filters[key];

      // 1. add the first / for uri
      if (key === 'uri' && !val.startsWith('/')) {
        val = '/' + val;
      }

      // 2. escape reserved characters
      // + - = && || > < ! ( ) { } [ ] ^ " ~ ? : \ /
      if (typeof val === 'string' || val instanceof String) {
        val = val.replace(/(\+|-|=|&{2}|\|{2}|>|<|!|\(|\)|{|}|\[|]|\^|"|~|\?|:|\\|\/)/g, '\\\\$1');
      }

      // 3. add the last * for uri
      if (key === 'uri' && !val.endsWith('*')) {
        val += '*';
      }

      if (key === 'body') {
        val = '*' + val + '*';
      }
      const params = val.constructor === Array && val.length > 1 ? LogsFiltersController.convert(val) : val;
      query += this.map(key, this.fields, true) + ':' + params;
      if (index + 1 < keys.length) {
        query += ' AND ';
      }
      index++;
    });
    return query;
  }

  private map(_val, list, strict) {
    let val = null;
    if (strict) {
      val = list[_val];
    } else {
      val = list[_.filter(Object.keys(list), (elt) => elt.toLowerCase().includes(_val.toLowerCase())).pop()];
    }
    return val ? val : _val;
  }
}

export default LogsFiltersController;
