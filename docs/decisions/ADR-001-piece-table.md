# ADR-001: Piece Table for Text Storage

## Status

Accepted

## Context

The editor needs memory-efficient editing for larger files while keeping implementation complexity low.

## Decision

Use a piece table with:

- immutable original buffer
- append-only add buffer
- piece list that references slices in those buffers

## Rationale

- Avoids copying full text on each insert/delete.
- Keeps edit operations localized to piece metadata changes.
- Simpler than rope balancing while still effective for medium-large text sizes.

## Consequences

- Converting to full string (`getText`) is linear and used at UI sync/save boundaries.
- Additional optimizations (piece merging, indexed lookups) can be added later if profiling requires.

