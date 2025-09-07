# Phase 8 — Characters + Symbol Set Editor

## Session 1 — CharactersSettingsScreen via Specs

### System prompt

```
Same rules.
```

### Task prompt

```
Render CHARACTERS_SPECS; add navigation to Symbol Set Editor.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/characters/**
- app/src/main/java/com/example/matrixscreen/ui/settings/model/**

### Acceptance

- Characters basic settings functional; editor link works

## Session 2 — Symbol Set Editor Refactor + JSON Import/Export

### System prompt

```
Same rules.
```

### Task prompt

```
Repo/VM-backed editor; SAF-based import/export of .ms-symbols.json.
```

### Touch-only paths

- app/src/main/java/com/example/matrixscreen/ui/settings/characters/**
- app/src/main/java/com/example/matrixscreen/data/**

### Acceptance

- Create/edit/delete; import/export round-trip OK

## Session 3 — Tests (Symbols)

### System prompt

```
Same rules.
```

### Task prompt

```
Unit tests for persistence and JSON round-trip.
```

### Touch-only paths

- app/src/test/java/com/example/matrixscreen/**

### Acceptance

- Tests pass
