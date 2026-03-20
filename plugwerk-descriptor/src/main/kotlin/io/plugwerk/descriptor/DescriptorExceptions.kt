package io.plugwerk.descriptor

class DescriptorParseException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class DescriptorNotFoundException(message: String) : RuntimeException(message)
