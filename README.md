# VibeText ✨

# VibeText ✨

**A completely vibe-coded text editor, built entirely through AI-assisted development.**

Every single line of code in this project — from the piece-table core to the Swing UI to the Gradle build config — was generated through conversational prompts with an AI coding assistant. No boilerplate was hand-written, no Stack Overflow was consulted. Just vibes.

VibeText is a lightweight, fast-booting desktop text editor written in Java. It's lean by design: a clean architecture with a memory-efficient core, multi-tab editing, and native Wayland support on Java 25+.

## Features

- **Multi-tab editing** — open multiple files in tabs (Ctrl+T new, Ctrl+W close)
- **Line number gutter** — always-visible line numbers alongside the editor
- **Text selection** — click-drag and keyboard shortcuts (native JTextArea behavior)
- **Clipboard integration** — Copy, Cut, Paste through the system clipboard
- **File I/O** — Open and Save with error dialogs and thread-safe background I/O
- **Undo / Redo** — command-pattern-driven undo stack (Ctrl+Z / Ctrl+Y)
- **Piece table core** — memory-efficient text storage for handling large files
- **Native Wayland rendering** — auto-detects Wayland on Java 25+ and uses WLToolkit
- **Sub-second startup** — Swing + minimal dependency graph, lazy service initialization

## The Vibe-Coded Philosophy

This project is an experiment in pure AI-pair-programmed software. The entire development process — architecture decisions, implementation, bug fixes, debugging blank screens on Wayland, fixing dropdown menus that wouldn't stay open — was driven by natural language conversation with an AI assistant.

**What "vibe-coded" means here:**
- The developer describes what they want in plain English
- The AI writes the code, runs the builds, debugs the errors
- The developer reviews, tests, and steers direction
- Rinse and repeat until it works

No manual coding. Just intent → conversation → working software.

## Architecture

```
src/main/java/com/vibeText/
├── Main.java            # Bootstrap, toolkit selection, dependency wiring
├── config/              # App constants (AppConfig)
├── core/                # Text engine: PieceTable, EditorModel, TextSnapshot
├── commands/            # Command pattern: InsertText, DeleteSelection, UndoRedoManager
├── ui/                  # Swing: EditorFrame, EditorTextArea, LineNumberGutter, TabBar, TabInfo
├── io/                  # File strategy: FileService interface, LocalFileService
└── clipboard/           # Clipboard strategy: ClipboardService, LazyClipboardService
```

**Separation of concerns:**
- **UI** handles rendering and user input
- **EditorController** orchestrates actions between UI and domain
- **EditorModel** owns text/file state, notifies observers on changes
- **PieceTable** stores text edits as append-only piece descriptors — no copying of the original buffer on each edit

**Threading model:**
- UI updates run on the Swing EDT
- File open/save run on background threads
- `LocalFileService` uses read/write locks for thread safety

## Build & Run

Requires **Java 21+** (Java 25 recommended for native Wayland support).

```bash
# Build and run all tests
./gradlew clean build

# Run the editor
./gradlew run

# Or use the convenience script
./run.sh

# Generate coverage report
./gradlew jacocoTestReport
```

Coverage policy: JaCoCo enforces ≥ 90% line coverage for non-UI modules (`core`, `commands`, `io`, `clipboard`). Swing view and bootstrap classes are excluded from the coverage gate.

## Repository

```
git@github.com:Y-u-k-i-h/VibeText.git
```

## Documentation

Detailed docs live in the `docs/` directory:

- [`docs/architecture.md`](docs/architecture.md) — runtime flow and threading model
- [`docs/file-structure.md`](docs/file-structure.md) — package layout explained
- [`docs/memory-profiling-notes.md`](docs/memory-profiling-notes.md) — why piece table, memory characteristics
- [`docs/decisions/ADR-001-piece-table.md`](docs/decisions/ADR-001-piece-table.md) — piece table ADR
- [`docs/decisions/ADR-002-ui-toolkit-swing.md`](docs/decisions/ADR-002-ui-toolkit-swing.md) — Swing selection ADR

## License

This project is open source. Do whatever you want with it — it was vibe-coded anyway. ✌️
