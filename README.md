# VibeText

VibeText is a totally vibecoded Java text editor: lean, fast to boot, and structured for maintainable growth.

## What it does today

- Swing-based editor window with line numbers
- Text selection using mouse and keyboard (native `JTextArea` behavior)
- Clipboard operations (Copy, Cut, Paste) through system clipboard integration
- Open and Save file actions with error dialogs
- Memory-efficient core text model using a piece table
- Thread-safe local file I/O service

## Architecture Overview

Source layout (Java packages under `src/main/java/com/vibeText`):

- `Main.java`: bootstrap and dependency wiring
- `config`: app constants (`AppConfig`)
- `core`: text engine and model (`PieceTable`, `EditorModel`, `TextSnapshot`)
- `commands`: command abstractions + undo/redo manager
- `ui`: Swing frame, text area, line-number gutter, controller
- `io`: file strategy and local file service
- `clipboard`: system clipboard strategy

The editor separates concerns as follows:

- UI handles rendering and input
- `EditorController` orchestrates user actions
- `EditorModel` owns text/file state and notifies observers
- `PieceTable` stores text edits efficiently for large-file operations

## Build and run

```bash
./gradlew clean check
./gradlew jacocoTestReport
./gradlew run
```

Coverage policy: JaCoCo enforces >= 90% line coverage for non-UI modules (`core`, `commands`, `io`, `clipboard`) while excluding Swing view/bootstrap classes from the coverage gate.

## Project repository

Canonical remote:

`git@github.com:Y-u-k-i-h/VibeText.git`

Using this remote for milestone pushes and tags is recommended so architectural and performance changes are easy to track over time.

## Documentation

- `docs/file-structure.md`
- `docs/architecture.md`
- `docs/decisions/ADR-001-piece-table.md`
- `docs/memory-profiling-notes.md`
