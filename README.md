# Branch Guard

Branch Guard is a JetBrains IDE plugin that prevents accidental development on protected Git branches.

When you start editing code on branches such as `main`, `master`, or `develop`, Branch Guard offers to create and check out a ticket-based feature branch before you continue.

## Features

- Protected branch detection for Git projects
- First-edit warning on configured protected branches
- Ticket-based feature branch creation
- Custom branch prefixes and templates
- Lightweight Settings UI under `Settings > Tools > Branch Guard`
- Notifications for branch creation, existing branches, and Git failures

## Default Configuration

Protected branches:

- `main`
- `master`
- `develop`

Branch naming:

```text
{prefix}/{ticket}-{description}
```

Default prefix:

```text
feature
```

Default ticket regex:

```text
[A-Z]+-\d+
```

Example:

```text
ABC-123 + Fix Login Error
=> feature/ABC-123-fix-login-error
```

## Development

Run tests:

```bash
./gradlew test
```

Run the plugin in a sandbox IDE:

```bash
./gradlew runIde
```

Build the plugin:

```bash
./gradlew buildPlugin
```

## Marketplace Description

Prevent accidental development on protected branches.

Branch Guard detects when you start editing code on branches like `main`, `master`, or `develop` and offers to create a ticket-based feature branch before you continue.
