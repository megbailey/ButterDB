package com.github.megbailey.gsheets;

public enum GError {

    // Google related errors
    ERROR_GOOGLE_AUTHENTICATION,
    ERROR_GOOGLE_REQUEST,
    ERROR_GOOGLE_BATCH_REQUEST,
    ERROR_GOOGLE_VIZ_REQUEST,

    // SELECT related errors
    ERROR_GENERAL_SELECT,   // General Error
    ERROR_COLUMN,           // Column is not recognized
    WARNING_EMPTY_RETURN,  // No values were returned from the filter, produced, or deleted

    WARNING_STATE_UNCHANGED, /// No items produced or deleted

    // INSERT (create) related errors
    ERROR_GENERAL_INSERT,   // General Error

    // Delete related errors
    ERROR_GENERAL_DELETE   // General Error
}
