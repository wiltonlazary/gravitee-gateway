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

/**
 * 
 * @export
 * @enum {string}
 */
export enum Visibility {
    PUBLIC = 'PUBLIC',
    PRIVATE = 'PRIVATE'
}

export function VisibilityFromJSON(json: any): Visibility {
    return VisibilityFromJSONTyped(json, false);
}

export function VisibilityFromJSONTyped(json: any, ignoreDiscriminator: boolean): Visibility {
    return json as Visibility;
}

export function VisibilityToJSON(value?: Visibility | null): any {
    return value as any;
}

