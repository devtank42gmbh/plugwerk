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

# ADR-0010: Drop Categories — Tags as Single Classification System

## Status

Accepted

## Context

The data model supported two classification dimensions for plugins:

- **Categories**: intended as a controlled taxonomy for coarse navigation (e.g. `Security`, `Reporting`, `Integrations`).
- **Tags**: free-form keywords for fine-grained search and filtering (e.g. `pdf`, `oauth2`, `cli`).

In practice, categories provided no additional value over tags:

- The frontend used a **hardcoded** list of categories — there was no admin UI or API to manage a controlled taxonomy.
- Plugin authors had to maintain both categories and tags in `MANIFEST.MF`, leading to confusion about which to use.
- The backend filtering logic treated categories identically to tags (exact string match on an array column).
- No existing plugin consumer distinguished between categories and tags programmatically.

## Decision

1. **Remove categories entirely** from the data model, API, descriptor, and UI.
2. **Tags become the single classification system** — they serve both coarse grouping (`security`, `reporting`) and fine-grained search (`oauth2`, `pdf-export`).
3. **Add a `GET /namespaces/{ns}/tags` endpoint** that returns a sorted, deduplicated list of all tags in use. The frontend uses this to populate the filter dropdown dynamically instead of relying on hardcoded values.
4. **Drop the `categories` column** from the `plugin` table (modified in the existing `0001_initial_schema` migration — acceptable because the application is pre-production).
5. **Remove `Plugin-Categories`** from the MANIFEST.MF attribute table and descriptor parsing.

## Consequences

**What becomes easier:**

- Plugin authors maintain a single list of keywords in `Plugin-Tags`.
- The filter UI is driven by actual data (dynamic tag list) instead of a hardcoded category list.
- One less field to validate, store, map, and test across the entire stack.

**What becomes more difficult:**

- If a curated taxonomy is needed in the future, it must be built on top of tags (e.g. "promoted tags" or tag groups). This is a deliberate trade-off: the current catalog size does not justify the complexity.
