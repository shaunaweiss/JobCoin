package com.gemini.jobcoin.exceptions

class ConfigLoadingException(item: String) :
    Exception("Failed Loading $item in config")
