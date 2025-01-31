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
import {
    Enabled,
    EnabledFromJSON,
    EnabledFromJSONTyped,
    EnabledToJSON,
} from './';

/**
 * 
 * @export
 * @interface PortalApis
 */
export interface PortalApis {
    /**
     * 
     * @type {Enabled}
     * @memberof PortalApis
     */
    apiHeaderShowCategories?: Enabled;
    /**
     * 
     * @type {Enabled}
     * @memberof PortalApis
     */
    apiHeaderShowTags?: Enabled;
    /**
     * 
     * @type {Enabled}
     * @memberof PortalApis
     */
    categoryMode?: Enabled;
    /**
     * 
     * @type {Enabled}
     * @memberof PortalApis
     */
    promotedApiMode?: Enabled;
    /**
     * 
     * @type {Enabled}
     * @memberof PortalApis
     */
    tilesMode?: Enabled;
}

export function PortalApisFromJSON(json: any): PortalApis {
    return PortalApisFromJSONTyped(json, false);
}

export function PortalApisFromJSONTyped(json: any, ignoreDiscriminator: boolean): PortalApis {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'apiHeaderShowCategories': !exists(json, 'apiHeaderShowCategories') ? undefined : EnabledFromJSON(json['apiHeaderShowCategories']),
        'apiHeaderShowTags': !exists(json, 'apiHeaderShowTags') ? undefined : EnabledFromJSON(json['apiHeaderShowTags']),
        'categoryMode': !exists(json, 'categoryMode') ? undefined : EnabledFromJSON(json['categoryMode']),
        'promotedApiMode': !exists(json, 'promotedApiMode') ? undefined : EnabledFromJSON(json['promotedApiMode']),
        'tilesMode': !exists(json, 'tilesMode') ? undefined : EnabledFromJSON(json['tilesMode']),
    };
}

export function PortalApisToJSON(value?: PortalApis | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'apiHeaderShowCategories': EnabledToJSON(value.apiHeaderShowCategories),
        'apiHeaderShowTags': EnabledToJSON(value.apiHeaderShowTags),
        'categoryMode': EnabledToJSON(value.categoryMode),
        'promotedApiMode': EnabledToJSON(value.promotedApiMode),
        'tilesMode': EnabledToJSON(value.tilesMode),
    };
}


