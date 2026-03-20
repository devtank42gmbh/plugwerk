package io.plugwerk.common.model

data class SearchCriteria(
    val query: String? = null,
    val category: String? = null,
    val tag: String? = null,
    val compatibleWith: String? = null,
)
