# Memory Profiling Notes

## Storage choice

`PieceTable` was selected to reduce copy amplification during edits:

- Original file content is stored once in `originalBuffer`.
- Inserted text is appended once to `addBuffer`.
- Active document is represented by a list of references (`Piece`) into both buffers.

## Why this helps

Compared to naive full-string replacement, edit operations avoid full-document reallocations for each keystroke. Memory growth is mostly proportional to inserted text plus piece metadata.

## Current benchmark coverage

`PieceTablePerformanceTest` validates handling of ~10MB input with a bounded memory delta assertion to catch runaway allocations.

## Next profiling steps

- Capture heap snapshots under repeated edit workloads.
- Add piece coalescing to reduce metadata churn on burst typing.
- Track latency for `getText()` on large files and add incremental rendering when needed.

