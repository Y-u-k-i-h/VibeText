# ADR-002: Swing as Primary UI Toolkit

## Status

Accepted

## Context

Project goal is a minimal editor with sub-second startup and broad JDK compatibility.

## Decision

Use Swing for first implementation milestone.

## Rationale

- Included with standard JDK, no extra runtime dependencies.
- Fast initialization for simple desktop windows.
- Sufficient built-in selection/editing behavior for an MVP editor.

## Consequences

- Visual theming and advanced UI effects are limited compared to JavaFX.
- If richer UI is required later, architecture allows swapping/bridging view layer.

