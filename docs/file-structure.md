# File Structure

## Main Source

- `src/main/java/com/vibeText/Main.java`: entry point and app bootstrap wiring
- `src/main/java/com/vibeText/config/`: static app configuration
- `src/main/java/com/vibeText/core/`: piece table and editor state model
- `src/main/java/com/vibeText/commands/`: command abstractions and undo/redo engine
- `src/main/java/com/vibeText/ui/`: Swing views and controller
- `src/main/java/com/vibeText/io/`: thread-safe file operations
- `src/main/java/com/vibeText/clipboard/`: clipboard abstraction and implementation

## Tests

- `src/test/java/com/vibeText/core/`: text model tests
- `src/test/java/com/vibeText/commands/`: command/undo tests
- `src/test/java/com/vibeText/io/`: unit tests for file service
- `src/test/java/com/vibeText/integration/`: file+model integration tests
- `src/test/java/com/vibeText/performance/`: large-text memory behavior checks
