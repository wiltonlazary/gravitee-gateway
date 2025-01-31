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
    PlanValidationType,
    PlanValidationTypeFromJSON,
    PlanValidationTypeFromJSONTyped,
    PlanValidationTypeToJSON,
    Rule,
    RuleFromJSON,
    RuleFromJSONTyped,
    RuleToJSON,
} from './';

/**
 * 
 * @export
 * @interface UpdatePlanEntity
 */
export interface UpdatePlanEntity {
    /**
     * 
     * @type {Array<string>}
     * @memberof UpdatePlanEntity
     */
    characteristics?: Array<string>;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    comment_message?: string;
    /**
     * 
     * @type {boolean}
     * @memberof UpdatePlanEntity
     */
    comment_required?: boolean;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    crossId?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    description: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof UpdatePlanEntity
     */
    excluded_groups?: Array<string>;
    /**
     * 
     * @type {Array<Flow>}
     * @memberof UpdatePlanEntity
     */
    flows?: Array<Flow>;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    general_conditions?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    id?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    name: string;
    /**
     * 
     * @type {number}
     * @memberof UpdatePlanEntity
     */
    order: number;
    /**
     * 
     * @type {{ [key: string]: Array<Rule>; }}
     * @memberof UpdatePlanEntity
     */
    paths: { [key: string]: Array<Rule>; };
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    securityDefinition?: string;
    /**
     * 
     * @type {string}
     * @memberof UpdatePlanEntity
     */
    selection_rule?: string;
    /**
     * 
     * @type {Array<string>}
     * @memberof UpdatePlanEntity
     */
    tags?: Array<string>;
    /**
     * 
     * @type {PlanValidationType}
     * @memberof UpdatePlanEntity
     */
    validation: PlanValidationType;
}

export function UpdatePlanEntityFromJSON(json: any): UpdatePlanEntity {
    return UpdatePlanEntityFromJSONTyped(json, false);
}

export function UpdatePlanEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): UpdatePlanEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'characteristics': !exists(json, 'characteristics') ? undefined : json['characteristics'],
        'comment_message': !exists(json, 'comment_message') ? undefined : json['comment_message'],
        'comment_required': !exists(json, 'comment_required') ? undefined : json['comment_required'],
        'crossId': !exists(json, 'crossId') ? undefined : json['crossId'],
        'description': json['description'],
        'excluded_groups': !exists(json, 'excluded_groups') ? undefined : json['excluded_groups'],
        'flows': !exists(json, 'flows') ? undefined : ((json['flows'] as Array<any>).map(FlowFromJSON)),
        'general_conditions': !exists(json, 'general_conditions') ? undefined : json['general_conditions'],
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': json['name'],
        'order': json['order'],
        'paths': json['paths'],
        'securityDefinition': !exists(json, 'securityDefinition') ? undefined : json['securityDefinition'],
        'selection_rule': !exists(json, 'selection_rule') ? undefined : json['selection_rule'],
        'tags': !exists(json, 'tags') ? undefined : json['tags'],
        'validation': PlanValidationTypeFromJSON(json['validation']),
    };
}

export function UpdatePlanEntityToJSON(value?: UpdatePlanEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'characteristics': value.characteristics,
        'comment_message': value.comment_message,
        'comment_required': value.comment_required,
        'crossId': value.crossId,
        'description': value.description,
        'excluded_groups': value.excluded_groups,
        'flows': value.flows === undefined ? undefined : ((value.flows as Array<any>).map(FlowToJSON)),
        'general_conditions': value.general_conditions,
        'id': value.id,
        'name': value.name,
        'order': value.order,
        'paths': value.paths,
        'securityDefinition': value.securityDefinition,
        'selection_rule': value.selection_rule,
        'tags': value.tags,
        'validation': PlanValidationTypeToJSON(value.validation),
    };
}


