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


/**
 * Response logged by the API Gateway
 */
export interface Response { 
    status?: number;
    /**
     * List of String List
     */
    headers?: { [key: string]: Array<string>; };
    body?: string;
}

