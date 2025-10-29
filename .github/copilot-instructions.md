# BoxLang RSS Module - AI Agent Instructions

## Architecture Overview

This is a **BoxLang module** that wraps the Java `rssreader` library (v3.11.0) to provide RSS/Atom feed reading capabilities with iTunes podcast extensions. BoxLang is a JVM language runtime, and modules follow a specific structure:

- **Language**: Mixed BoxLang (`.bx` files) and Java
- **Build System**: Gradle with custom tasks for module packaging
- **Module System**: BoxLang's module service auto-registers BIFs, components, and interceptors
- **Java Interop**: BoxLang code directly imports and uses Java classes with `@bxrss` alias

## MCP Servers

Here you can find MCP servers that utilize this module:

- BoxLang:  https://boxlang.ortusbooks.com/~gitbook/mcp

## Example Components

Here you can find sample components built for BoxLang using BoxLang:

- https://github.com/ortus-boxlang/bx-charts/tree/development/components

### Key Components

```
src/main/
├── bx/                              # BoxLang source
│   ├── ModuleConfig.bx             # Module metadata & lifecycle
│   ├── bifs/RSS.bx                 # BIF entry point (delegates to FeedUtil)
│   ├── components/                  # Components (future: would delegate to FeedUtil)
│   └── models/FeedUtil.bx          # ⚡ CENTRAL ABSTRACTION LAYER (static methods)
├── java/ortus/boxlang/feed/
│   └── util/KeyDictionary.java     # BoxLang Key constants
└── resources/                       # Bundled in JAR
```

### Architectural Pattern: Controller → Service Layer

**Critical Pattern**: `FeedUtil.bx` is the **central abstraction layer** containing all feed reading logic:
- **Static methods** with reusable business logic
- **Static reader instances** (RssReader, ItunesRssReader) initialized once
- **All Java library integration** happens here (import statements, Optional handling, stream processing)

**BIFs and Components act as thin controllers**:
- `bifs/RSS.bx`: Simply delegates to `FeedUtil.readFeed()` with `argumentCollection`
- Future components would follow the same pattern - validation, then delegate to `FeedUtil`
- **Never duplicate logic** - if it processes feeds, it belongs in `FeedUtil`

**Why this matters**:
1. **Single source of truth** - Feed reading logic exists in one place
2. **Testability** - Test `FeedUtil` methods directly, not through BIF layer
3. **Reusability** - Components, BIFs, or future features all use the same underlying methods
4. **Java interop isolation** - Only `FeedUtil` deals with Java Optional<T>, stream processing quirks

## Development Workflow

### Initial Setup (Required)
```bash
./gradlew downloadBoxLang  # Downloads BoxLang binary to src/test/resources/libs/
```

### Build & Test Cycle
```bash
./gradlew build           # Compile, test, create JAR
./gradlew test            # Run JUnit tests only
./gradlew test --tests "ortus.boxlang.feed.bifs.RSSTest"  # Single test class
./gradlew shadowJar       # Create fat JAR with dependencies
```

### Module Packaging
The `createModuleStructure` task (runs during build) creates `build/module/`:
- Copies JAR to `build/module/libs/`
- Copies `.bx` files with token replacement (`@build.version@`, `@build.number@`)
- Creates `box.json`, `readme.md`, `changelog.md` with version tokens

**Important**: The build replaces `@build.version@+@build.number@` in source files. Version in `gradle.properties`, branch logic in `build.gradle` appends `-snapshot` for development builds.

## BoxLang-Specific Conventions

### Module Structure Rules
1. **Module Name**: Defined in `ModuleConfig.bx` as `this.mapping = "bxrss"` → creates mapping `bxModules.bxrss.*`
2. **BIF Registration**: Classes in `src/main/bx/bifs/` with `@BoxBIF` annotation auto-register
3. **Import Syntax**: `import java:com.apptasticsoftware.rssreader.RssReader@bxrss;`
   - `java:` prefix for Java classes
   - `@bxrss` suffix references the module's JAR dependency
4. **Imports within module**: `import bxModules.bxrss.models.FeedUtil;` (BoxLang code)

### BoxLang vs Java Patterns
- **Arrays**: 1-indexed in BoxLang (e.g., `items[1]` not `items[0]`)
- **Optional handling**: Use `.orElse()`, `.ifPresent()` on Java Optionals
- **Null checking**: Use `isNull()` BoxLang function, not `== null`
- **Closures/Lambdas**: BoxLang syntax `( i ) -> true` works in Java Stream API
- **Date/Time Objects**: All date/time values in BoxLang are `ortus.boxlang.runtime.types.DateTime` objects
  - Wraps `java.time.ZonedDateTime` internally
  - Use `.toDate()` to convert to `java.util.Date` for Java libraries (like Rome)
  - Use `.toEpoch()` for seconds, `.toEpochMillis()` for milliseconds
  - Use `parseDateTime(string)` to parse strings → returns DateTime object
  - Example: `parseDateTime("2024-01-15").toDate()` for Rome's `setPublishedDate()`

## Testing Patterns

Tests are **Java JUnit 5** tests that execute BoxLang code:

```java
runtime.executeSource(
    """
    feedData = rss( 'https://example.com/feed.xml' );
    count = feedData.items.size()
    """,
    context
);
assertThat( variables.getAsInteger( Key.of( "count" ) ) ).isAtLeast( 5 );
```

- Extend `BaseIntegrationTest` for runtime setup
- Use Google Truth assertions (`com.google.truth.Truth.assertThat`)
- Access BoxLang variables via `variables.getAsStruct()`, `variables.getAsInteger()`
- BoxLang code runs in isolated context with shared variables scope

## Feed Reading Architecture

### Reader Selection Pattern
The module maintains static reader instances and selects based on parameters:
```boxlang
static {
    rssOb = new RssReader();           // Standard RSS/Atom
    itunesRssOb = new ItunesRssReader(); // Podcast feeds
}
var reader = arguments.itunes ? static.itunesRssOb : static.rssOb
```

### Data Flow
1. **Input**: URLs (string or array) → normalized to array
2. **Java Stream Processing**:
   ```boxlang
   reader.read(urls).filter(filter).sorted().limit(maxItems).toList()
   ```
3. **Output**: Struct with `items[]` array and `channel{}` metadata
4. **Conditional Fields**: iTunes fields only included when `itunes=true`

### Java Library Integration
- Library: `com.apptasticsoftware:rssreader:3.11.0` (declared in `build.gradle`)
- Multiple URL support: Library handles merging via `reader.read(List<String>)`
- Feed types: RSS 2.0, RSS 1.0 (RDF), Atom
- Extensions: iTunes podcast fields (23 additional fields when enabled)

## Code Style & Formatting

- **Java**: Eclipse formatter with `.ortus-java-style.xml` (run `./gradlew spotlessApply`)
- **BoxLang**: No auto-formatter configured (manual consistency)
- **Line Endings**: LF (Unix) enforced via `.editorconfig`
- **Indentation**: Tabs for BoxLang, tabs for Java (per Ortus style)

## Module Distribution

Final artifact: `build/distributions/bx-rss-{version}.zip`
- Contains: `libs/*.jar`, `bifs/*.bx`, `models/*.bx`, `ModuleConfig.bx`, `box.json`
- Installation: Unzip to BoxLang `modules/` directory
- ForgeBox: Published via `box publish` (see `box.json` scripts)

## Common Pitfalls

1. **Don't forget `downloadBoxLang`** - tests fail without BoxLang binary
2. **BIF method signature**: Must match `invoke(required urls, ...)` - BoxLang convention
3. **ServiceLoader generation**: Runs automatically via `serviceLoaderBuild` task (needed for BIF discovery)
4. **Module mapping**: Use `bxModules.{mapping}.*` for internal imports, not relative paths
5. **Version tokens**: Don't edit `@build.version@` in source - build replaces these
6. **Java Optional handling**: Always check with `.orElse()` or `.ifPresent()` before accessing

## Extending with New Feed Types

To add support (e.g., Media RSS):
1. Add Java reader instance: `mediaRssOb = new MediaRssReader()`
2. Add boolean parameter: `boolean mediaRss=false`
3. Add reader selection logic: `if(mediaRss) var reader = static.mediaRssOb`
4. Add conditional field mapping in output struct
5. Create test method in `RSSTest.java` with real feed URL
6. Update documentation in `readme.md`

Reference: iTunes implementation in `FeedUtil.bx` lines 48-117 shows the complete pattern.
