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
import {
    CategorizedLinks,
    CategorizedLinksFromJSON,
    CategorizedLinksFromJSONTyped,
    CategorizedLinksToJSON,
} from './';

/**
 * 
 * @export
 * @interface LinksResponse
 */
export interface LinksResponse {
    /**
     * Map of CategorizedLinks. Keys of the map can be:
     * * aside
     * * header
     * * topfooter
     * * footer
     * @type {{ [key: string]: Array<CategorizedLinks>; }}
     * @memberof LinksResponse
     */
    slots?: { [key: string]: Array<CategorizedLinks>; };
}

export function LinksResponseFromJSON(json: any): LinksResponse {
    return LinksResponseFromJSONTyped(json, false);
}

export function LinksResponseFromJSONTyped(json: any, ignoreDiscriminator: boolean): LinksResponse {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'slots': !exists(json, 'slots') ? undefined : json['slots'],
    };
}

export function LinksResponseToJSON(value?: LinksResponse | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'slots': value.slots,
    };
}


