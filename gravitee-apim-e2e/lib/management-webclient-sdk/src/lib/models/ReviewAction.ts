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
export enum ReviewAction {
    ASK = 'ASK',
    ACCEPT = 'ACCEPT',
    REJECT = 'REJECT'
}

export function ReviewActionFromJSON(json: any): ReviewAction {
    return ReviewActionFromJSONTyped(json, false);
}

export function ReviewActionFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReviewAction {
    return json as ReviewAction;
}

export function ReviewActionToJSON(value?: ReviewAction | null): any {
    return value as any;
}

