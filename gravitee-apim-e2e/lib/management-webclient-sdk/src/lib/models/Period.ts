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
 * @interface Period
 */
export interface Period {
    /**
     * 
     * @type {number}
     * @memberof Period
     */
    beginHour: number;
    /**
     * 
     * @type {Array<number>}
     * @memberof Period
     */
    days?: Array<number>;
    /**
     * 
     * @type {number}
     * @memberof Period
     */
    endHour: number;
    /**
     * 
     * @type {string}
     * @memberof Period
     */
    zoneId: string;
}

export function PeriodFromJSON(json: any): Period {
    return PeriodFromJSONTyped(json, false);
}

export function PeriodFromJSONTyped(json: any, ignoreDiscriminator: boolean): Period {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'beginHour': json['beginHour'],
        'days': !exists(json, 'days') ? undefined : json['days'],
        'endHour': json['endHour'],
        'zoneId': json['zoneId'],
    };
}

export function PeriodToJSON(value?: Period | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'beginHour': value.beginHour,
        'days': value.days,
        'endHour': value.endHour,
        'zoneId': value.zoneId,
    };
}


