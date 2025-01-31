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
 * @interface RegisterUserEntity
 */
export interface RegisterUserEntity {
    /**
     * 
     * @type {string}
     * @memberof RegisterUserEntity
     */
    firstname: string;
    /**
     * 
     * @type {string}
     * @memberof RegisterUserEntity
     */
    lastname: string;
    /**
     * 
     * @type {string}
     * @memberof RegisterUserEntity
     */
    password: string;
    /**
     * 
     * @type {string}
     * @memberof RegisterUserEntity
     */
    token: string;
}

export function RegisterUserEntityFromJSON(json: any): RegisterUserEntity {
    return RegisterUserEntityFromJSONTyped(json, false);
}

export function RegisterUserEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): RegisterUserEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'firstname': json['firstname'],
        'lastname': json['lastname'],
        'password': json['password'],
        'token': json['token'],
    };
}

export function RegisterUserEntityToJSON(value?: RegisterUserEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'firstname': value.firstname,
        'lastname': value.lastname,
        'password': value.password,
        'token': value.token,
    };
}


