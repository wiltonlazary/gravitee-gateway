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
 * @interface JsonPatch
 */
export interface JsonPatch {
    /**
     * 
     * @type {string}
     * @memberof JsonPatch
     */
    jsonPath: string;
    /**
     * 
     * @type {string}
     * @memberof JsonPatch
     */
    operation?: JsonPatchOperationEnum;
    /**
     * 
     * @type {any}
     * @memberof JsonPatch
     */
    value?: any;
}

export function JsonPatchFromJSON(json: any): JsonPatch {
    return JsonPatchFromJSONTyped(json, false);
}

export function JsonPatchFromJSONTyped(json: any, ignoreDiscriminator: boolean): JsonPatch {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'jsonPath': json['jsonPath'],
        'operation': !exists(json, 'operation') ? undefined : json['operation'],
        'value': !exists(json, 'value') ? undefined : json['value'],
    };
}

export function JsonPatchToJSON(value?: JsonPatch | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'jsonPath': value.jsonPath,
        'operation': value.operation,
        'value': value.value,
    };
}

/**
* @export
* @enum {string}
*/
export enum JsonPatchOperationEnum {
    ADD = 'ADD',
    REMOVE = 'REMOVE',
    REPLACE = 'REPLACE',
    TEST = 'TEST'
}


