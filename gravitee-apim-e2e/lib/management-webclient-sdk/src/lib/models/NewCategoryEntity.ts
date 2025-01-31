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
 * @interface NewCategoryEntity
 */
export interface NewCategoryEntity {
    /**
     * 
     * @type {string}
     * @memberof NewCategoryEntity
     */
    background?: string;
    /**
     * 
     * @type {string}
     * @memberof NewCategoryEntity
     */
    description?: string;
    /**
     * 
     * @type {boolean}
     * @memberof NewCategoryEntity
     */
    hidden?: boolean;
    /**
     * 
     * @type {string}
     * @memberof NewCategoryEntity
     */
    highlightApi?: string;
    /**
     * 
     * @type {string}
     * @memberof NewCategoryEntity
     */
    name: string;
    /**
     * 
     * @type {number}
     * @memberof NewCategoryEntity
     */
    order?: number;
    /**
     * 
     * @type {string}
     * @memberof NewCategoryEntity
     */
    page?: string;
    /**
     * 
     * @type {string}
     * @memberof NewCategoryEntity
     */
    picture?: string;
}

export function NewCategoryEntityFromJSON(json: any): NewCategoryEntity {
    return NewCategoryEntityFromJSONTyped(json, false);
}

export function NewCategoryEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): NewCategoryEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'background': !exists(json, 'background') ? undefined : json['background'],
        'description': !exists(json, 'description') ? undefined : json['description'],
        'hidden': !exists(json, 'hidden') ? undefined : json['hidden'],
        'highlightApi': !exists(json, 'highlightApi') ? undefined : json['highlightApi'],
        'name': json['name'],
        'order': !exists(json, 'order') ? undefined : json['order'],
        'page': !exists(json, 'page') ? undefined : json['page'],
        'picture': !exists(json, 'picture') ? undefined : json['picture'],
    };
}

export function NewCategoryEntityToJSON(value?: NewCategoryEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'background': value.background,
        'description': value.description,
        'hidden': value.hidden,
        'highlightApi': value.highlightApi,
        'name': value.name,
        'order': value.order,
        'page': value.page,
        'picture': value.picture,
    };
}


