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
import {
    Alert,
    AlertFromJSON,
    AlertFromJSONTyped,
    AlertToJSON,
    ConsoleAuthentication,
    ConsoleAuthenticationFromJSON,
    ConsoleAuthenticationFromJSONTyped,
    ConsoleAuthenticationToJSON,
    ConsoleCors,
    ConsoleCorsFromJSON,
    ConsoleCorsFromJSONTyped,
    ConsoleCorsToJSON,
    ConsoleReCaptcha,
    ConsoleReCaptchaFromJSON,
    ConsoleReCaptchaFromJSONTyped,
    ConsoleReCaptchaToJSON,
    ConsoleScheduler,
    ConsoleSchedulerFromJSON,
    ConsoleSchedulerFromJSONTyped,
    ConsoleSchedulerToJSON,
    Email,
    EmailFromJSON,
    EmailFromJSONTyped,
    EmailToJSON,
    Logging,
    LoggingFromJSON,
    LoggingFromJSONTyped,
    LoggingToJSON,
    Maintenance,
    MaintenanceFromJSON,
    MaintenanceFromJSONTyped,
    MaintenanceToJSON,
    Management,
    ManagementFromJSON,
    ManagementFromJSONTyped,
    ManagementToJSON,
    Newsletter,
    NewsletterFromJSON,
    NewsletterFromJSONTyped,
    NewsletterToJSON,
    Theme,
    ThemeFromJSON,
    ThemeFromJSONTyped,
    ThemeToJSON,
} from './';

/**
 * 
 * @export
 * @interface ConsoleSettingsEntity
 */
export interface ConsoleSettingsEntity {
    /**
     * 
     * @type {Alert}
     * @memberof ConsoleSettingsEntity
     */
    alert?: Alert;
    /**
     * 
     * @type {ConsoleAuthentication}
     * @memberof ConsoleSettingsEntity
     */
    authentication?: ConsoleAuthentication;
    /**
     * 
     * @type {ConsoleCors}
     * @memberof ConsoleSettingsEntity
     */
    cors?: ConsoleCors;
    /**
     * 
     * @type {Email}
     * @memberof ConsoleSettingsEntity
     */
    email?: Email;
    /**
     * 
     * @type {Logging}
     * @memberof ConsoleSettingsEntity
     */
    logging?: Logging;
    /**
     * 
     * @type {Maintenance}
     * @memberof ConsoleSettingsEntity
     */
    maintenance?: Maintenance;
    /**
     * 
     * @type {Management}
     * @memberof ConsoleSettingsEntity
     */
    management?: Management;
    /**
     * 
     * @type {{ [key: string]: Array<string>; }}
     * @memberof ConsoleSettingsEntity
     */
    readonly metadata?: { [key: string]: Array<string>; };
    /**
     * 
     * @type {Newsletter}
     * @memberof ConsoleSettingsEntity
     */
    newsletter?: Newsletter;
    /**
     * 
     * @type {ConsoleReCaptcha}
     * @memberof ConsoleSettingsEntity
     */
    reCaptcha?: ConsoleReCaptcha;
    /**
     * 
     * @type {ConsoleScheduler}
     * @memberof ConsoleSettingsEntity
     */
    scheduler?: ConsoleScheduler;
    /**
     * 
     * @type {Theme}
     * @memberof ConsoleSettingsEntity
     */
    theme?: Theme;
}

export function ConsoleSettingsEntityFromJSON(json: any): ConsoleSettingsEntity {
    return ConsoleSettingsEntityFromJSONTyped(json, false);
}

export function ConsoleSettingsEntityFromJSONTyped(json: any, ignoreDiscriminator: boolean): ConsoleSettingsEntity {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'alert': !exists(json, 'alert') ? undefined : AlertFromJSON(json['alert']),
        'authentication': !exists(json, 'authentication') ? undefined : ConsoleAuthenticationFromJSON(json['authentication']),
        'cors': !exists(json, 'cors') ? undefined : ConsoleCorsFromJSON(json['cors']),
        'email': !exists(json, 'email') ? undefined : EmailFromJSON(json['email']),
        'logging': !exists(json, 'logging') ? undefined : LoggingFromJSON(json['logging']),
        'maintenance': !exists(json, 'maintenance') ? undefined : MaintenanceFromJSON(json['maintenance']),
        'management': !exists(json, 'management') ? undefined : ManagementFromJSON(json['management']),
        'metadata': !exists(json, 'metadata') ? undefined : json['metadata'],
        'newsletter': !exists(json, 'newsletter') ? undefined : NewsletterFromJSON(json['newsletter']),
        'reCaptcha': !exists(json, 'reCaptcha') ? undefined : ConsoleReCaptchaFromJSON(json['reCaptcha']),
        'scheduler': !exists(json, 'scheduler') ? undefined : ConsoleSchedulerFromJSON(json['scheduler']),
        'theme': !exists(json, 'theme') ? undefined : ThemeFromJSON(json['theme']),
    };
}

export function ConsoleSettingsEntityToJSON(value?: ConsoleSettingsEntity | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'alert': AlertToJSON(value.alert),
        'authentication': ConsoleAuthenticationToJSON(value.authentication),
        'cors': ConsoleCorsToJSON(value.cors),
        'email': EmailToJSON(value.email),
        'logging': LoggingToJSON(value.logging),
        'maintenance': MaintenanceToJSON(value.maintenance),
        'management': ManagementToJSON(value.management),
        'newsletter': NewsletterToJSON(value.newsletter),
        'reCaptcha': ConsoleReCaptchaToJSON(value.reCaptcha),
        'scheduler': ConsoleSchedulerToJSON(value.scheduler),
        'theme': ThemeToJSON(value.theme),
    };
}


