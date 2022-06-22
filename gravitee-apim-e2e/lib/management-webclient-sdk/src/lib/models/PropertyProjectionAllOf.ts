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
 * @interface PropertyProjectionAllOf
 */
export interface PropertyProjectionAllOf {
    /**
     * 
     * @type {string}
     * @memberof PropertyProjectionAllOf
     */
    property?: string;
}

export function PropertyProjectionAllOfFromJSON(json: any): PropertyProjectionAllOf {
    return PropertyProjectionAllOfFromJSONTyped(json, false);
}

export function PropertyProjectionAllOfFromJSONTyped(json: any, ignoreDiscriminator: boolean): PropertyProjectionAllOf {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'property': !exists(json, 'property') ? undefined : json['property'],
    };
}

export function PropertyProjectionAllOfToJSON(value?: PropertyProjectionAllOf | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'property': value.property,
    };
}


