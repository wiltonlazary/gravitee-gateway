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
import { Workflow } from '../workflow/workflow';

export type TaskType = 'SUBSCRIPTION_APPROVAL' | 'IN_REVIEW' | 'REQUEST_FOR_CHANGES' | 'USER_REGISTRATION_APPROVAL' | 'PROMOTION_APPROVAL';

export interface GenericTask<Type = TaskType, Data = any> {
  type: Type;
  data: Data;
  created_at: number;
}

export interface PromotionApprovalTaskData {
  promotionId: string;
  apiName: string;
  sourceEnvironmentName: string;
  targetEnvironmentName: string;
  targetApiId?: string;
  isApiUpdate: boolean;
  authorDisplayName: string;
  authorEmail?: string;
  authorPicture?: string;
}

export type PromotionTask = GenericTask<'PROMOTION_APPROVAL', PromotionApprovalTaskData>;

export type Task =
  // TODO: Improve types
  | GenericTask<'SUBSCRIPTION_APPROVAL', any>
  | GenericTask<'IN_REVIEW', Workflow>
  | GenericTask<'REQUEST_FOR_CHANGES', Workflow>
  // TODO: Improve types
  | GenericTask<'USER_REGISTRATION_APPROVAL', any>
  | PromotionTask;
