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
import { ApiService } from '../../../services/api.service';
import ApplicationService from '../../../services/application.service';
import MetadataService from '../../../services/metadata.service';

function DeleteMetadataDialogController(
  MetadataService: MetadataService,
  ApiService: ApiService,
  ApplicationService: ApplicationService,
  $mdDialog: angular.material.IDialogService,
  metadata,
  $stateParams,
) {
  'ngInject';

  if ($stateParams.apiId) {
    this.referenceType = 'API';
    this.referenceId = $stateParams.apiId;
  } else if ($stateParams.applicationId) {
    this.referenceType = 'APPLICATION';
    this.referenceId = $stateParams.applicationId;
  }

  this.metadataName = metadata.name;

  this.cancel = () => {
    $mdDialog.cancel();
  };

  this.delete = () => {
    if ($stateParams.apiId) {
      ApiService.deleteMetadata($stateParams.apiId, metadata.key).then(() => {
        $mdDialog.hide(true);
      });
    } else if ($stateParams.applicationId) {
      ApplicationService.deleteMetadata($stateParams.applicationId, metadata.key).then(() => {
        $mdDialog.hide(true);
      });
    } else {
      MetadataService.delete(metadata).then(() => {
        $mdDialog.hide(true);
      });
    }
  };
}

export default DeleteMetadataDialogController;
