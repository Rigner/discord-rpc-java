package com.github.psnrigner.discordrpcjava;

/**
 * Discord error codes
 */
public enum ErrorCode
{
    SUCCESS,
    PIPE_CLOSED,
    READ_CORRUPT,
    UNKNOWN

    // TODO Implement more error codes, and use an id field instead of ordinal
}