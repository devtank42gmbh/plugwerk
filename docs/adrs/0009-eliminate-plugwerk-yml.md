<!--
  Plugwerk — Plugin Marketplace for the PF4J Ecosystem
  Copyright (C) 2026 devtank42 GmbH

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program. If not, see <https://www.gnu.org/licenses/>.
-->

# ADR-0009: Eliminate plugwerk.yml — Use MANIFEST.MF as Single Source of Truth

## Status

Accepted

## Context

The original design introduced `plugwerk.yml` as a custom descriptor format embedded in plugin artifacts alongside the standard PF4J manifest (`MANIFEST.MF` / `plugin.properties`). This created several problems:

- **Metadata duplication**: Plugin ID, version, provider, dependencies, and other fields had to be maintained in both `plugwerk.yml` and `MANIFEST.MF`, leading to inconsistencies and maintenance overhead.
- **Extra dependency**: Parsing the YAML descriptor required a YAML parser (Jackson YAML module), adding unnecessary weight to the `plugwerk-descriptor` module.
- **Complexity**: The `DescriptorResolver` had to manage a three-level priority chain (`plugwerk.yml` > `MANIFEST.MF` > `plugin.properties`) with merging logic between the YAML descriptor and PF4J manifest.
- **Friction for plugin developers**: Authors had to learn a custom format and maintain a separate file instead of using the standard PF4J manifest they already knew.

## Decision

1. **Remove `plugwerk.yml`** as a descriptor format entirely. The `PlugwerkDescriptorParser` and `PlugwerkYamlModel` classes are deleted.
2. **Extend `MANIFEST.MF`** with custom `Plugin-*` attributes for marketplace-specific metadata (categories, tags, icon, screenshots, homepage, repository, name, license). These attributes follow the PF4J naming convention and are ignored by PF4J itself.
3. **Rename `author` to `provider`** in the data model and API to align with the PF4J-standard `Plugin-Provider` attribute.
4. **Remove `requiresApiLevel`** from the data model. Host version compatibility is expressed exclusively through `Plugin-Requires` SemVer ranges.
5. **Simplify the resolver chain** to: `MANIFEST.MF` (preferred) > `plugin.properties` (fallback). For ZIP bundles, root-level JARs are checked before `lib/` JARs.

The full set of supported MANIFEST.MF attributes:

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

## Consequences

**What becomes easier:**

- Plugin developers use a single, familiar format (`MANIFEST.MF`) for all metadata.
- The parser is simpler with no YAML dependency and no merging logic.
- The data model is aligned with PF4J conventions (`Plugin-Provider` instead of custom `author`).
- The resolver has only two levels instead of three, reducing edge cases.

**What becomes more difficult:**

- Existing plugins that used `plugwerk.yml` must migrate their metadata to `MANIFEST.MF` attributes. This is a one-time effort per plugin.
- MANIFEST.MF is less expressive for complex nested structures (e.g., plugin-to-plugin dependencies with version ranges). Dependencies use the PF4J format: `dep-id@>=1.0.0`.
