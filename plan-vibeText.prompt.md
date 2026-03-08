## Plan: Minimal Java Text Editor Architecture

Build a lean Swing-based editor with a memory-efficient core (piece table), command-driven editing actions, and thread-safe file services, then layer UI features (line numbers, selection, clipboard, open/save) on top. Structure modules by responsibility inside Java packages, wire fast startup with deferred service initialization, and add JUnit 5 unit/integration/performance tests plus documentation. Draft plan for your review before implementation.

### Steps
1. Define package layout and bootstrap flow in [`src/main/java/com/vibeText/Main.java`](src/main/java/com/vibeText/Main.java) with `EditorApplication` and `AppConfig`.
2. Implement core text engine in [`src/main/java/com/vibeText/core/`](src/main/java/com/vibeText/core/) using `PieceTable`, `TextBuffer`, and `TextSnapshot`.
3. Add command layer in [`src/main/java/com/vibeText/commands/`](src/main/java/com/vibeText/commands/) with `Command`, `InsertTextCommand`, `DeleteSelectionCommand`, `UndoRedoManager`.
4. Build UI layer in [`src/main/java/com/vibeText/ui/`](src/main/java/com/vibeText/ui/) with `EditorFrame`, `EditorTextArea`, and `LineNumberGutter`.
5. Implement services in [`src/main/java/com/vibeText/io/`](src/main/java/com/vibeText/io/) and [`src/main/java/com/vibeText/clipboard/`](src/main/java/com/vibeText/clipboard/) using `FileService` and `SystemClipboardService`.
6. Add tests/docs in [`src/test/java/com/vibeText/`](src/test/java/com/vibeText/), update [`build.gradle`](build.gradle), and write [`README.md`](README.md) with architecture and memory notes.

### Further Considerations
1. UI toolkit decision: Option A Swing (fast startup) / Option B JavaFX (richer UI) / Option C hybrid prototype first.
2. Test layout preference: keep Gradle-standard `src/test/java` or configure custom `tests/unit|integration|performance` source sets.
3. Large-file behavior target: strict full-load piece table now, or phased lazy line-index loading for 10MB+ responsiveness.

