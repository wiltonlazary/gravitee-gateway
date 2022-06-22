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
 * @interface PageSourceEntity
 */
export interface PageSourceEntity {
    /**
     * 
     * @type {string}
     * @memberof PageSourceEntity
     */
    configuration?: string;
    /**
     * 
     * @type {string}
     * @memberof PageSourceEntity
     */
    type?: string;
}

export function PageSourceEntityFromJSON(json: any): PageSourceEntity {
    return PageSourceEntityFromJSONTyped(json, false);
}

export function PageSourceEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): PageSourceEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'configuration': !exists(json, 'configuration') ? undefined : json['configuration'],
        'type': !exists(json, 'type') ? undefined : json['type'],
    };
}

export function PageSourceEntityToJSON(value?: PageSourceEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'configuration': value.configuration,
        'type': value.type,
    };
}


