package io.plugwerk.common.version

import org.semver4j.Semver

fun String.toSemVer(): Semver {
    require(isNotBlank()) { "Version string must not be blank" }
    val parsed = Semver.parse(this)
    requireNotNull(parsed) { "Invalid SemVer version: '$this'" }
    return parsed
}

fun String.toSemVerOrNull(): Semver? {
    if (isBlank()) return null
    return Semver.parse(this)
}

fun String.isValidSemVer(): Boolean = toSemVerOrNull() != null

fun String.satisfiesSemVerRange(range: String): Boolean {
    val version = toSemVer()
    val normalizedRange = normalizeRange(range)
    return version.satisfies(normalizedRange)
}

fun compareSemVer(v1: String, v2: String): Int {
    val semver1 = v1.toSemVer()
    val semver2 = v2.toSemVer()
    return semver1.compareTo(semver2)
}

internal fun normalizeRange(range: String): String = range.replace(Regex("\\s*&\\s*"), " ")
