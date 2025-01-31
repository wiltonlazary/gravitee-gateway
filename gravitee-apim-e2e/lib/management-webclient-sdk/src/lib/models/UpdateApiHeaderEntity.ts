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
 * @interface UpdateApiHeaderEntity
 */
export interface UpdateApiHeaderEntity {
    /**
     * 
     * @type {string}
     * @memberof UpdateApiHeaderEntity
     */
    id?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdateApiHeaderEntity
     */
    name?: string;
    /**
     * 
     * @type {number}
     * @memberof UpdateApiHeaderEntity
     */
    order?: number;
    /**
     * 
     * @type {string}
     * @memberof UpdateApiHeaderEntity
     */
    value?: string;
}

export function UpdateApiHeaderEntityFromJSON(json: any): UpdateApiHeaderEntity {
    return UpdateApiHeaderEntityFromJSONTyped(json, false);
}

export function UpdateApiHeaderEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): UpdateApiHeaderEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': !exists(json, 'name') ? undefined : json['name'],
        'order': !exists(json, 'order') ? undefined : json['order'],
        'value': !exists(json, 'value') ? undefined : json['value'],
    };
}

export function UpdateApiHeaderEntityToJSON(value?: UpdateApiHeaderEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'order': value.order,
        'value': value.value,
    };
}


