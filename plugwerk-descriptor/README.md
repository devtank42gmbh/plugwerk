# plugwerk-descriptor

Parser and validator for plugin descriptors embedded in artifact JARs and ZIPs.

## Purpose

When a plugin artifact is uploaded, its metadata must be extracted and validated. This module handles:

1. **Parsing** `MANIFEST.MF` — reads both standard PF4J attributes (`Plugin-Id`, `Plugin-Version`, ...) and custom `Plugin-*` attributes (categories, tags, icon, etc.)
2. **Fallback parsing** of `plugin.properties` (`plugin.id`, `plugin.version`, ...)
3. **Resolving** which descriptor source to use (`MANIFEST.MF` preferred, then `plugin.properties`; for ZIP bundles, root-level JARs are checked before `lib/` JARs)
4. **Validating** all fields (SemVer format, ID format, length limits, HTML/script injection detection)

## Contents

| Class | Responsibility |
|-------|---------------|
| `PlugwerkDescriptor` | Immutable data class representing parsed plugin metadata |
| `Pf4jManifestParser` | Parses standard PF4J attributes and custom `Plugin-*` attributes from `MANIFEST.MF` |
| `DescriptorResolver` | Tries `MANIFEST.MF` first, then `plugin.properties`, for JARs and ZIP bundles |
| `DescriptorValidator` | Validates field format, length, cardinality, and rejects dangerous content |
| `DescriptorExceptions` | `DescriptorParseException`, `DescriptorValidationException`, `DescriptorNotFoundException` |

## Descriptor Priority

When resolving from a JAR:

1. `MANIFEST.MF` with `Plugin-Id` attribute
2. `plugin.properties` with `plugin.id` key

When resolving from a ZIP bundle, root-level JARs are checked before `lib/` JARs.

## MANIFEST.MF Attributes

| MANIFEST.MF Attribute | Purpose | Required | PF4J Standard |
|---|---|---|---|
| `Plugin-Id` | Unique plugin identifier | **Yes** | Yes |
| `Plugin-Version` | SemVer version | **Yes** | Yes |
| `Plugin-Class` | Plugin class name | No | Yes |
| `Plugin-Provider` | Provider/organisation | No | Yes |
| `Plugin-Description` | Short description | No | Yes |
| `Plugin-Dependencies` | Comma-separated deps | No | Yes |
| `Plugin-Requires` | SemVer range for host | No | Yes |
| `Plugin-License` | SPDX license | No | Yes |
| `Plugin-Name` | Display name | No | No (custom) |
| `Plugin-Categories` | Comma-separated categories | No | No (custom) |
| `Plugin-Tags` | Comma-separated tags | No | No (custom) |
| `Plugin-Icon` | Icon URL/path | No | No (custom) |
| `Plugin-Screenshots` | Comma-separated URLs | No | No (custom) |
| `Plugin-Homepage` | Project URL | No | No (custom) |
| `Plugin-Repository` | Source code URL | No | No (custom) |

## Validation Rules

- Plugin ID: `[a-zA-Z0-9][a-zA-Z0-9._-]*`, max 128 characters, no path traversal
- Version: valid SemVer (via semver4j)
- Text fields (name, description, provider, license): max 255 characters
- Lists (categories, tags, screenshots): max 20 entries, each max 64 characters
- Dependencies: valid IDs and SemVer version ranges
- HTML/script content: rejected (defense-in-depth XSS prevention)

## Compatibility

- **JVM target:** 11
- **Dependencies:** `plugwerk-spi`, Jackson (Kotlin module), SLF4J
