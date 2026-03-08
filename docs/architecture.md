# Architecture Notes

## Why this shape

- Fast startup: Swing + minimal dependency graph keeps launch latency low.
- Separation of concerns: UI, domain model, and I/O are isolated behind small interfaces.
- Evolvability: command and observer hooks allow incremental additions (undo stacks, plugins, alternate storages).

## Runtime flow

1. `Main` starts on JVM entry and schedules UI initialization on the Swing EDT.
2. `Main` creates services (`LocalFileService`, `SystemClipboardService`) and `EditorModel`.
3. `EditorFrame` renders UI; `EditorController` binds events.
4. Edits propagate from Swing document events into `EditorModel`/`PieceTable`.
5. Model observer updates ensure UI and domain stay consistent for non-typing operations (open/load).

## Threading model

- UI updates run on Swing EDT.
- File open/save run on background threads in `EditorController`.
- `LocalFileService` uses read/write locks for thread-safe file operations.
