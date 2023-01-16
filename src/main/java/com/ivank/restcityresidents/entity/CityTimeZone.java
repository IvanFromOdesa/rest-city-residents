package com.ivank.restcityresidents.entity;

/**
 * Enums stored in db as smallint (not the best approach).
 * Smallint values represent enum objects in the order they are declared here.
 * E.g. AST - 0, EST - 1 so on.
 */
public enum CityTimeZone {
    AST,
    EST, // Europe
    CST,
    MST,
    PST,
    JST, // Japan's timeZone
    AKST,
    HST,
    UTC_11,
    UTC_10
}
