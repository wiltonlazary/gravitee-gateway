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
import { ApplicationGrantType } from './applicationGrantType';


export interface ApplicationType { 
    /**
     * Unique identifier of an application type.
     */
    id?: string;
    /**
     * Name of an application type.
     */
    name?: string;
    /**
     * description of an application type.
     */
    description?: string;
    /**
     * if true, application type require redirect uri
     */
    requires_redirect_uris?: boolean;
    /**
     * List of allowed grant types
     */
    allowed_grant_types?: Array<ApplicationGrantType>;
    /**
     * List of mandatory grant types
     */
    mandatory_grant_types?: Array<ApplicationGrantType>;
    /**
     * List of default grant types
     */
    default_grant_types?: Array<ApplicationGrantType>;
}

