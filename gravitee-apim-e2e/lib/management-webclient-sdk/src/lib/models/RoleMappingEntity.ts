/* tslint:disable */
/* eslint-disable */
/**
 * Gravitee.io - Management API
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface RoleMappingEntity
 */
export interface RoleMappingEntity {
    /**
     * 
     * @type {string}
     * @memberof RoleMappingEntity
     */
    condition?: string;
    /**
     * 
     * @type {{ [key: string]: Array<string>; }}
     * @memberof RoleMappingEntity
     */
    environments: { [key: string]: Array<string>; };
    /**
     * 
     * @type {Array<string>}
     * @memberof RoleMappingEntity
     */
    organizations: Array<string>;
}

export function RoleMappingEntityFromJSON(json: any): RoleMappingEntity {
    return RoleMappingEntityFromJSONTyped(json, false);
}

export function RoleMappingEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): RoleMappingEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'condition': !exists(json, 'condition') ? undefined : json['condition'],
        'environments': json['environments'],
        'organizations': json['organizations'],
    };
}

export function RoleMappingEntityToJSON(value?: RoleMappingEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'condition': value.condition,
        'environments': value.environments,
        'organizations': value.organizations,
    };
}


