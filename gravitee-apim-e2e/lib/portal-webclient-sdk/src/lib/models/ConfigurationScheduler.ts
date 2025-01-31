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
 * @interface ConfigurationScheduler
 */
export interface ConfigurationScheduler {
    /**
     * Number of seconds for notification scheduler.
     * @type {number}
     * @memberof ConfigurationScheduler
     */
    notificationsInSeconds?: number;
}

export function ConfigurationSchedulerFromJSON(json: any): ConfigurationScheduler {
    return ConfigurationSchedulerFromJSONTyped(json, false);
}

export function ConfigurationSchedulerFromJSONTyped(json: any, ignoreDiscriminator: boolean): ConfigurationScheduler {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'notificationsInSeconds': !exists(json, 'notificationsInSeconds') ? undefined : json['notificationsInSeconds'],
    };
}

export function ConfigurationSchedulerToJSON(value?: ConfigurationScheduler | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'notificationsInSeconds': value.notificationsInSeconds,
    };
}


