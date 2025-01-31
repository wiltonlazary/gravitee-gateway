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
    ReferenceMetadataFormatType,
    ReferenceMetadataFormatTypeFromJSON,
    ReferenceMetadataFormatTypeFromJSONTyped,
    ReferenceMetadataFormatTypeToJSON,
} from './';

/**
 * 
 * @export
 * @interface ReferenceMetadata
 */
export interface ReferenceMetadata {
    /**
     * Unique identifier of a metadata.
     * @type {string}
     * @memberof ReferenceMetadata
     */
    key: string;
    /**
     * Name of the metadata.
     * @type {string}
     * @memberof ReferenceMetadata
     */
    name: string;
    /**
     * Id of the application to which the metadata refers.
     * @type {string}
     * @memberof ReferenceMetadata
     */
    application?: string;
    /**
     * 
     * @type {ReferenceMetadataFormatType}
     * @memberof ReferenceMetadata
     */
    format?: ReferenceMetadataFormatType;
    /**
     * value of the metadata. Supports freemarker syntax.
     * @type {string}
     * @memberof ReferenceMetadata
     */
    value?: string;
    /**
     * default value of the metadata.
     * @type {string}
     * @memberof ReferenceMetadata
     */
    defaultValue?: string;
}

export function ReferenceMetadataFromJSON(json: any): ReferenceMetadata {
    return ReferenceMetadataFromJSONTyped(json, false);
}

export function ReferenceMetadataFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReferenceMetadata {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'key': json['key'],
        'name': json['name'],
        'application': !exists(json, 'application') ? undefined : json['application'],
        'format': !exists(json, 'format') ? undefined : ReferenceMetadataFormatTypeFromJSON(json['format']),
        'value': !exists(json, 'value') ? undefined : json['value'],
        'defaultValue': !exists(json, 'defaultValue') ? undefined : json['defaultValue'],
    };
}

export function ReferenceMetadataToJSON(value?: ReferenceMetadata | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'key': value.key,
        'name': value.name,
        'application': value.application,
        'format': ReferenceMetadataFormatTypeToJSON(value.format),
        'value': value.value,
        'defaultValue': value.defaultValue,
    };
}


