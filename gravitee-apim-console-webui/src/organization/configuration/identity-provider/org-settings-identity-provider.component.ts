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
import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { StateService } from '@uirouter/angularjs';
import { cloneDeep, isEmpty } from 'lodash';
import { combineLatest, EMPTY, Subject } from 'rxjs';
import { catchError, distinctUntilChanged, shareReplay, takeUntil, tap } from 'rxjs/operators';

import { UIRouterState, UIRouterStateParams } from '../../../ajs-upgraded-providers';
import { Environment } from '../../../entities/environment/environment';
import { GroupMapping, IdentityProvider, RoleMapping } from '../../../entities/identity-provider';
import { EnvironmentService } from '../../../services-ngx/environment.service';
import { GroupService } from '../../../services-ngx/group.service';
import { IdentityProviderService } from '../../../services-ngx/identity-provider.service';
import { RoleService } from '../../../services-ngx/role.service';
import { SnackBarService } from '../../../services-ngx/snack-bar.service';

export interface ProviderConfiguration {
  name: string;
  getFormGroups(): Record<string, FormGroup>;
}
@Component({
  selector: 'org-settings-identity-provider',
  styles: [require('./org-settings-identity-provider.component.scss')],
  template: require('./org-settings-identity-provider.component.html'),
})
export class OrgSettingsIdentityProviderComponent implements OnInit, OnDestroy {
  isLoading = true;

  identityProviderFormGroup: FormGroup;

  mode: 'new' | 'edit' = 'new';

  // Used for the edit mode
  initialIdentityProviderValue: IdentityProvider | null = null;

  @ViewChild('providerConfiguration', { static: false })
  set providerConfiguration(providerPart: ProviderConfiguration | undefined) {
    // only if providerPart changed
    if (providerPart && this._providerPartName !== providerPart.name) {
      this._providerPartName = providerPart.name;
      this.addProviderFormGroups(providerPart.getFormGroups());
    }
  }
  private _providerPartName: string;

  identityProviderType: IdentityProvider['type'] | null = null;

  groups$ = this.groupService.list().pipe(shareReplay());

  organizationRoles$ = this.roleService.list('ORGANIZATION').pipe(shareReplay());

  environments$ = this.environmentService.list().pipe(shareReplay());
  allEnvironments: Environment[];

  environmentRoles$ = this.roleService.list('ENVIRONMENT').pipe(shareReplay());

  environmentTableDisplayedColumns = ['name', 'description', 'actions'];

  private unsubscribe$ = new Subject<boolean>();

  private identityProviderFormControlKeys: string[] = [];

  constructor(
    private readonly identityProviderService: IdentityProviderService,
    private readonly groupService: GroupService,
    private readonly roleService: RoleService,
    private readonly environmentService: EnvironmentService,
    private readonly snackBarService: SnackBarService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    @Inject(UIRouterState) private readonly ajsState: StateService,
    @Inject(UIRouterStateParams) private readonly ajsStateParams,
  ) {}

  ngOnInit() {
    this.identityProviderFormGroup = new FormGroup({
      type: new FormControl(),
      enabled: new FormControl(true),
      name: new FormControl(null, [Validators.required, Validators.maxLength(50), Validators.minLength(2)]),
      description: new FormControl(),
      emailRequired: new FormControl(true),
      syncMappings: new FormControl(false),
    });

    this.identityProviderFormGroup
      .get('type')
      .valueChanges.pipe(takeUntil(this.unsubscribe$), distinctUntilChanged())
      .subscribe((type) => {
        this.identityProviderType = type;
        this.identityProviderFormGroup.markAsUntouched();
      });

    if (this.ajsStateParams.id) {
      this.mode = 'edit';

      combineLatest([this.identityProviderService.get(this.ajsStateParams.id), this.environments$])
        .pipe(
          takeUntil(this.unsubscribe$),
          tap(([identityProvider, environments]) => {
            this.identityProviderType = identityProvider.type;
            this.initialIdentityProviderValue = cloneDeep(identityProvider);

            this.identityProviderFormGroup.addControl('groupMappings', new FormArray([]), { emitEvent: false });
            identityProvider.groupMappings.forEach((groupMapping) => this.addGroupMappingToIdentityProviderFormGroup(groupMapping, false));

            this.allEnvironments = environments;
            this.identityProviderFormGroup.addControl('roleMappings', new FormArray([]), { emitEvent: false });
            identityProvider.roleMappings.forEach((roleMapping) => this.addRoleMappingToIdentityProviderFormGroup(roleMapping, false));

            this.isLoading = false;
          }),
        )
        .subscribe();
    } else {
      this.mode = 'new';
      this.identityProviderFormGroup.get('type').setValue('GRAVITEEIO_AM');
      this.isLoading = false;
    }
  }

  ngOnDestroy() {
    this.unsubscribe$.next(true);
    this.unsubscribe$.unsubscribe();
  }

  addProviderFormGroups(formGroups: Record<string, FormGroup>) {
    if (this.isLoading) {
      return;
    }

    // clean previous form group
    if (!isEmpty(this.identityProviderFormControlKeys)) {
      this.identityProviderFormControlKeys.forEach((key) => {
        this.identityProviderFormGroup.removeControl(key);
      });

      this.identityProviderFormControlKeys = [];
    }

    // add provider form group
    if (this.identityProviderFormGroup && !isEmpty(formGroups)) {
      Object.entries(formGroups).forEach(([key, formGroup]) => {
        this.identityProviderFormControlKeys.push(key);
        this.identityProviderFormGroup.addControl(key, formGroup, { emitEvent: false });
      });
    }

    // For the edit mode
    // Initializes the form value when the sub-form linked to the idP type is added
    if (this.mode === 'edit') {
      this.identityProviderFormGroup.patchValue(this.initialIdentityProviderValue, { emitEvent: false });
      this.identityProviderFormGroup.markAsPristine();
      this.identityProviderFormGroup.markAsUntouched();
      this.changeDetectorRef.detectChanges();
    }
  }

  onSubmit() {
    if (this.identityProviderFormGroup.invalid) {
      return;
    }

    const formSettingsValue = this.identityProviderFormGroup.getRawValue();

    const upsertIdentityProvider$ =
      this.mode === 'new'
        ? this.identityProviderService.create(formSettingsValue)
        : this.identityProviderService.update({ ...this.initialIdentityProviderValue, ...formSettingsValue });

    upsertIdentityProvider$
      .pipe(
        takeUntil(this.unsubscribe$),
        tap(() => {
          this.snackBarService.success('Identity provider successfully saved!');
        }),
        catchError(({ error }) => {
          this.snackBarService.error(error.message);
          return EMPTY;
        }),
      )
      .subscribe((identityProvider) => {
        if (this.mode === 'new') {
          this.ajsState.go('organization.settings.ng-identityprovider-edit', { id: identityProvider.id });
        } else {
          this.resetComponent();
        }
      });
  }

  addGroupMappingToIdentityProviderFormGroup(groupMapping?: GroupMapping, emitEvent = true) {
    const groupMappings = this.identityProviderFormGroup.get('groupMappings') as FormArray;
    groupMappings.push(
      new FormGroup({
        condition: new FormControl(groupMapping?.condition ?? null, [Validators.required]),
        groups: new FormControl(groupMapping?.groups ?? [], [Validators.required]),
      }),
      { emitEvent },
    );
    if (emitEvent) {
      this.identityProviderFormGroup.markAsDirty();
    }
  }

  removeGroupMappingFromIdentityProviderFormGroup(index: number) {
    const groupMappings = this.identityProviderFormGroup.get('groupMappings') as FormArray;
    groupMappings.removeAt(index);
    this.identityProviderFormGroup.markAsDirty();
  }

  addRoleMappingToIdentityProviderFormGroup(roleMapping?: RoleMapping, emitEvent = true) {
    const roleMappings = this.identityProviderFormGroup.get('roleMappings') as FormArray;
    roleMappings.push(
      new FormGroup({
        condition: new FormControl(roleMapping?.condition ?? null, [Validators.required]),
        organizations: new FormControl(roleMapping?.organizations ?? [], [Validators.required]),
        // new form group with environment.id as key and Environment[] as FormControl
        environments: new FormGroup({
          ...this.allEnvironments.reduce(
            (prev, environment) => ({
              ...prev,
              [environment.id]: new FormControl(roleMapping?.environments[environment.id] ?? [], [Validators.required]),
            }),
            {},
          ),
        }),
      }),
      { emitEvent },
    );
    if (emitEvent) {
      this.identityProviderFormGroup.markAsDirty();
    }
  }

  removeRoleMappingFromIdentityProviderFormGroup(index: number) {
    const groupMappings = this.identityProviderFormGroup.get('roleMappings') as FormArray;
    groupMappings.removeAt(index);
    this.identityProviderFormGroup.markAsDirty();
  }

  onFormReset() {
    const groupMappings = this.identityProviderFormGroup.get('groupMappings') as FormArray;
    groupMappings.clear();
    this.initialIdentityProviderValue.groupMappings.forEach((groupMapping) =>
      this.addGroupMappingToIdentityProviderFormGroup(groupMapping, false),
    );
  }

  // reset component to initial state
  private resetComponent(): void {
    this.isLoading = true;
    this.initialIdentityProviderValue = null;

    // reset sub form property to force new call of addProviderFormGroups in order to patchValue the form with new idP get
    this._providerPartName = null;
    this.identityProviderType = null;

    this.ngOnInit();
  }
}
