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
export enum Type {
    INLINE = 'INLINE',
    URL = 'URL'
}

export function TypeFromJSON(json: any): Type {
    return TypeFromJSONTyped(json, false);
}

export function TypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): Type {
    return json as Type;
}

export function TypeToJSON(value?: Type | null): any {
    return value as any;
}

