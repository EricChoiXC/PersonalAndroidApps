package com.ericxc.android.accounting

object Config {
    const val DB_NAME = "accounting.db"
    const val DB_VERSION = 2

    const val EXPORT_DIR = "账单"
    const val EXPORT_FILE_PREFIX = "账单-"
    const val EXPORT_FILE_SUFFIX = ".xlsx"

    const val MAX_DATE_RANGE_DAYS = 365L

    const val DATE_FORMAT_DISPLAY = "yyyy-MM-dd"
    const val DATE_TIME_FORMAT_DISPLAY = "yyyy-MM-dd HH:mm"
    const val DATE_FORMAT_EXPORT = "yyyyMMdd-HHmmss"
}
