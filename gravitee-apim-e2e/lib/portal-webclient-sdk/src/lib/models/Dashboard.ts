/* tslint:disable */
/* eslint-disable */
/**
 * Gravitee.io Portal Rest API
 * API dedicated to the devportal part of Gravitee
 *
 * Contact: contact@graviteesource.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface Dashboard
 */
export interface Dashboard {
    /**
     * Unique identifier of a dashboard.
     * @type {string}
     * @memberof Dashboard
     */
    id?: string;
    /**
     * 
     * @type {string}
     * @memberof Dashboard
     */
    name?: string;
    /**
     * 
     * @type {string}
     * @memberof Dashboard
     */
    definition?: string;
}

export function DashboardFromJSON(json: any): Dashboard {
    return DashboardFromJSONTyped(json, false);
}

export function DashboardFromJSONTyped(json: any, ignoreDiscriminator: boolean): Dashboard {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': !exists(json, 'name') ? undefined : json['name'],
        'definition': !exists(json, 'definition') ? undefined : json['definition'],
    };
}

export function DashboardToJSON(value?: Dashboard | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'definition': value.definition,
    };
}


