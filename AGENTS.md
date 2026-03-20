# AGENTS.md

Universal AI agent instructions for Plugwerk. All AI coding agents (Claude Code, GitHub Copilot, Cursor, etc.) should read this file first.

## What Plugwerk Is

Plugwerk is a **plugin marketplace for the Java/PF4J ecosystem** – the missing link between the PF4J plugin framework and a product's plugin ecosystem. Think Maven Central, but for runtime plugins instead of build dependencies.

Two artifacts:
- **Plugwerk Server** – Spring Boot 4.x + Kotlin web application: REST API, catalog, upload, versioning, download
- **Plugwerk Client SDK** – Kotlin library (JVM 11+) deployed as a PF4J plugin with isolated classloader: discovery, download, install, update lifecycle

Project concept: `docs/concepts/concept-pf4j-marketplace-en.md`

## Project Status

Phase 1 (MVP) is **in active development**. The Gradle multi-module project is scaffolded. Tracking issue: [#7](https://github.com/devtank42gmbh/plugwerk/issues/7).

## Naming

Use **"Plugwerk"** (not "PlugWerk") everywhere. Base package: `io.plugwerk`.

## Language

All project communication is in **English**: code, documentation, issues, PR descriptions, reviews, ADRs.

## Git Workflow

- **Never commit directly to `main`** – always use a feature branch
- Branch naming: `feature/<issue-id>_<short-description>` (e.g. `feature/42_user-auth`) – every branch ties back to a GitHub Issue
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `chore:`, etc.)
- AI-generated commits include a `Co-Authored-By` trailer
- PRs use the template at `.github/PULL_REQUEST_TEMPLATE.md` (includes AI agent disclosure section)
- One logical change per PR; reference fixed issues with `Closes #N` in the PR body
- Issues use the templates in `.github/ISSUE_TEMPLATE/`:
  - Bugs: `bug_report.md`
  - Features: `feature_request.md`

## Issue Management

Every GitHub Issue must have (see [ADR-0002](docs/adrs/0002-issue-management-guidelines.md)):
- **Type** set (Feature / Bug / Task)
- **Milestone** assigned
- **Labels** applied
- **Relationships** (parent/child) if applicable

## Documentation

- Architecture Decision Records: `docs/adrs/` — use `docs/adrs/TEMPLATE.md` for new ADRs
- Feature specifications: `docs/features/` — GitHub Issues link to their corresponding spec file
- Project concept: `docs/concepts/`

## Architecture

### Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| Server Backend | Spring Boot 4.x / JVM 21+ |
| Server API | Spring Web (REST) + OpenAPI 3.1 (API-First) |
| Database | PostgreSQL + Liquibase |
| Storage | Filesystem (MVP) / S3-compatible (Phase 2) |
| Web UI | React / TypeScript / Material UI / Zustand |
| Auth | API key (MVP) / Spring Security + OIDC (Phase 2) |
| Client SDK | PF4J plugin / OkHttp / Jackson / JVM 11+ |
| Build | Gradle 9.x multi-module (Kotlin DSL) |
| Tests | JUnit 6 + Mockito + Testcontainers |

### Module Structure

```
plugwerk/
├── plugwerk-api/                  # OpenAPI 3.1 spec (API-First) + generated DTOs/interfaces
├── plugwerk-common/               # Shared ExtensionPoint interfaces, DTOs, constants (JVM 11)
├── plugwerk-descriptor/           # plugwerk.yml parser/validator + PF4J manifest fallback (JVM 11)
├── plugwerk-server/
│   ├── plugwerk-server-backend/   # Spring Boot 4.x + Kotlin REST API (JVM 21)
│   └── plugwerk-server-frontend/  # React + TypeScript + MUI + Zustand (embedded in server JAR)
└── plugwerk-client-sdk/           # PF4J plugin, OkHttp, Jackson (JVM 11)
```

### Key Design Constraints

- **Client SDK is a PF4J plugin** with isolated classloader – no dependency conflicts with host application
- **Hybrid Extension Point pattern** – `PlugwerkMarketplace` facade + granular `PlugwerkCatalog`, `PlugwerkInstaller`, `PlugwerkUpdateChecker` as separate ExtensionPoints (interfaces in `plugwerk-common`)
- **pf4j-update backward compatibility** – `PlugwerkUpdateRepository` is a drop-in for `DefaultUpdateRepository`; `GET /plugins.json` maintains pf4j-update format
- **API-First** – OpenAPI 3.1 spec in `plugwerk-api` is the single source of truth
- **Transactional installation** – no partial state on failure; rollback requires retaining previous version
- **Namespace isolation** – all resources are scoped to a namespace; one server serves multiple products/organizations
