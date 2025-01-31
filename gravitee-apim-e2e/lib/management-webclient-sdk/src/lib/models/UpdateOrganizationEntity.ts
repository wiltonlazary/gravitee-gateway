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
    Flow,
    FlowFromJSON,
    FlowFromJSONTyped,
    FlowToJSON,
} from './';

/**
 * 
 * @export
 * @interface UpdateOrganizationEntity
 */
export interface UpdateOrganizationEntity {
    /**
     * 
     * @type {string}
     * @memberof UpdateOrganizationEntity
     */
    cockpitId?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdateOrganizationEntity
     */
    description?: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof UpdateOrganizationEntity
     */
    domainRestrictions?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof UpdateOrganizationEntity
     */
    flowMode?: UpdateOrganizationEntityFlowModeEnum;
    /**
     * 
     * @type {Array<Flow>}
     * @memberof UpdateOrganizationEntity
     */
    flows?: Array<Flow>;
    /**
     * 
     * @type {Array<string>}
     * @memberof UpdateOrganizationEntity
     */
    hrids?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof UpdateOrganizationEntity
     */
    name: string;
}

export function UpdateOrganizationEntityFromJSON(json: any): UpdateOrganizationEntity {
    return UpdateOrganizationEntityFromJSONTyped(json, false);
}

export function UpdateOrganizationEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): UpdateOrganizationEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'cockpitId': !exists(json, 'cockpitId') ? undefined : json['cockpitId'],
        'description': !exists(json, 'description') ? undefined : json['description'],
        'domainRestrictions': !exists(json, 'domainRestrictions') ? undefined : json['domainRestrictions'],
        'flowMode': !exists(json, 'flowMode') ? undefined : json['flowMode'],
        'flows': !exists(json, 'flows') ? undefined : ((json['flows'] as Array<any>).map(FlowFromJSON)),
        'hrids': !exists(json, 'hrids') ? undefined : json['hrids'],
        'name': json['name'],
    };
}

export function UpdateOrganizationEntityToJSON(value?: UpdateOrganizationEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'cockpitId': value.cockpitId,
        'description': value.description,
        'domainRestrictions': value.domainRestrictions,
        'flowMode': value.flowMode,
        'flows': value.flows === undefined ? undefined : ((value.flows as Array<any>).map(FlowToJSON)),
        'hrids': value.hrids,
        'name': value.name,
    };
}

/**
* @export
* @enum {string}
*/
export enum UpdateOrganizationEntityFlowModeEnum {
    DEFAULT = 'DEFAULT',
    BESTMATCH = 'BEST_MATCH'
}


