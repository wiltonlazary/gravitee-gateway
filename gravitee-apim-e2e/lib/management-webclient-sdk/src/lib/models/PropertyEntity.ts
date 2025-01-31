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
 * A dictionary (could be dynamic) of properties available in the API context.
 * @export
 * @interface PropertyEntity
 */
export interface PropertyEntity {
    /**
     * 
     * @type {boolean}
     * @memberof PropertyEntity
     */
    dynamic?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof PropertyEntity
     */
    encryptable?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof PropertyEntity
     */
    encrypted?: boolean;
    /**
     * 
     * @type {string}
     * @memberof PropertyEntity
     */
    key: string;
    /**
     * 
     * @type {string}
     * @memberof PropertyEntity
     */
    value: string;
}

export function PropertyEntityFromJSON(json: any): PropertyEntity {
    return PropertyEntityFromJSONTyped(json, false);
}

export function PropertyEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): PropertyEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'dynamic': !exists(json, 'dynamic') ? undefined : json['dynamic'],
        'encryptable': !exists(json, 'encryptable') ? undefined : json['encryptable'],
        'encrypted': !exists(json, 'encrypted') ? undefined : json['encrypted'],
        'key': json['key'],
        'value': json['value'],
    };
}

export function PropertyEntityToJSON(value?: PropertyEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'dynamic': value.dynamic,
        'encryptable': value.encryptable,
        'encrypted': value.encrypted,
        'key': value.key,
        'value': value.value,
    };
}


