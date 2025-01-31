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
    IdentityProviderType,
    IdentityProviderTypeFromJSON,
    IdentityProviderTypeFromJSONTyped,
    IdentityProviderTypeToJSON,
} from './';

/**
 * 
 * @export
 * @interface IdentityProvider
 */
export interface IdentityProvider {
    /**
     * Unique identifier of an identity provider.
     * @type {string}
     * @memberof IdentityProvider
     */
    id?: string;
    /**
     * Name of the identity provider.
     * @type {string}
     * @memberof IdentityProvider
     */
    name?: string;
    /**
     * Description of the identity provider.
     * @type {string}
     * @memberof IdentityProvider
     */
    description?: string;
    /**
     * ClientId of the identity provider.
     * @type {string}
     * @memberof IdentityProvider
     */
    client_id?: string;
    /**
     * true, if an email is required for this identity provider.
     * @type {boolean}
     * @memberof IdentityProvider
     */
    email_required?: boolean;
    /**
     * 
     * @type {IdentityProviderType}
     * @memberof IdentityProvider
     */
    type?: IdentityProviderType;
    /**
     * Authorization endpoint of the provider.
     * @type {string}
     * @memberof IdentityProvider
     */
    authorizationEndpoint?: string;
    /**
     * Token introspection endpoint of the provider. (Gravitee.io AM and OpenId Connect only)
     * @type {string}
     * @memberof IdentityProvider
     */
    tokenIntrospectionEndpoint?: string;
    /**
     * User logout endpoint of the provider. (Gravitee.io AM and OpenId Connect only)
     * @type {string}
     * @memberof IdentityProvider
     */
    userLogoutEndpoint?: string;
    /**
     * color to display for this provider. (Gravitee.io AM and OpenId Connect only)
     * @type {string}
     * @memberof IdentityProvider
     */
    color?: string;
    /**
     * Display style of the provider. (Google only)
     * @type {string}
     * @memberof IdentityProvider
     */
    display?: string;
    /**
     * Required URL params of the provider. (Google only)
     * @type {Array<string>}
     * @memberof IdentityProvider
     */
    requiredUrlParams?: Array<string>;
    /**
     * Optionnal URL params of the provider. (Github and Google only)
     * @type {Array<string>}
     * @memberof IdentityProvider
     */
    optionalUrlParams?: Array<string>;
    /**
     * Scope list of the provider.
     * @type {Array<string>}
     * @memberof IdentityProvider
     */
    scopes?: Array<string>;
}

export function IdentityProviderFromJSON(json: any): IdentityProvider {
    return IdentityProviderFromJSONTyped(json, false);
}

export function IdentityProviderFromJSONTyped(json: any, ignoreDiscriminator: boolean): IdentityProvider {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': !exists(json, 'name') ? undefined : json['name'],
        'description': !exists(json, 'description') ? undefined : json['description'],
        'client_id': !exists(json, 'client_id') ? undefined : json['client_id'],
        'email_required': !exists(json, 'email_required') ? undefined : json['email_required'],
        'type': !exists(json, 'type') ? undefined : IdentityProviderTypeFromJSON(json['type']),
        'authorizationEndpoint': !exists(json, 'authorizationEndpoint') ? undefined : json['authorizationEndpoint'],
        'tokenIntrospectionEndpoint': !exists(json, 'tokenIntrospectionEndpoint') ? undefined : json['tokenIntrospectionEndpoint'],
        'userLogoutEndpoint': !exists(json, 'userLogoutEndpoint') ? undefined : json['userLogoutEndpoint'],
        'color': !exists(json, 'color') ? undefined : json['color'],
        'display': !exists(json, 'display') ? undefined : json['display'],
        'requiredUrlParams': !exists(json, 'requiredUrlParams') ? undefined : json['requiredUrlParams'],
        'optionalUrlParams': !exists(json, 'optionalUrlParams') ? undefined : json['optionalUrlParams'],
        'scopes': !exists(json, 'scopes') ? undefined : json['scopes'],
    };
}

export function IdentityProviderToJSON(value?: IdentityProvider | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'id': value.id,
        'name': value.name,
        'description': value.description,
        'client_id': value.client_id,
        'email_required': value.email_required,
        'type': IdentityProviderTypeToJSON(value.type),
        'authorizationEndpoint': value.authorizationEndpoint,
        'tokenIntrospectionEndpoint': value.tokenIntrospectionEndpoint,
        'userLogoutEndpoint': value.userLogoutEndpoint,
        'color': value.color,
        'display': value.display,
        'requiredUrlParams': value.requiredUrlParams,
        'optionalUrlParams': value.optionalUrlParams,
        'scopes': value.scopes,
    };
}


