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
export enum AnalyticsAverageType {
    AVAILABILITY = 'AVAILABILITY',
    RESPONSETIME = 'RESPONSE_TIME'
}

export function AnalyticsAverageTypeFromJSON(json: any): AnalyticsAverageType {
    return AnalyticsAverageTypeFromJSONTyped(json, false);
}

export function AnalyticsAverageTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): AnalyticsAverageType {
    return json as AnalyticsAverageType;
}

export function AnalyticsAverageTypeToJSON(value?: AnalyticsAverageType | null): any {
    return value as any;
}

