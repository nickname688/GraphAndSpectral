# Research Workbench MVP

## Product Positioning

The app is a local-first research workbench for graph theory and spectral graph theory graduate students and teachers. It should stay simple enough for personal use while helping users keep research directions, papers, notes, and graph computation records in one place.

The app is not trying to be a powerful solver for NP-hard research problems. Computation tools are useful for examples, checks, and small experiments, but the core value is research material management.

## MVP Scope

The first version should focus on a complete local workflow:

1. Automatically create a default local library.
2. Create, view, edit, rename, and delete research directions.
3. Generate a main `direction.md` file for each direction.
4. Store lightweight papers, notes, and computation records under each direction.
5. Keep the existing graph and hypergraph calculators as independent tools.
6. Allow useful graph or hypergraph computations to be saved into a research direction.

Global search is useful, but it can be planned after the first workflow is usable.

## Navigation

The bottom navigation should evolve from calculator-first navigation into research-workbench navigation:

```text
Directions
Tools
Papers
Settings
```

`Tools` should contain the existing graph spectral radius calculator and hypergraph spectral radius calculator. The calculators should remain independent tool pages, with an added "save computation record" path into a direction.

## Local Library Structure

The first version should automatically create and use a default app-local library. Custom folder selection is intentionally deferred because cross-platform file access is complicated.

Recommended structure:

```text
GraphSpectralLibrary/
  library.json
  directions/
    20260429-154233-spectral-radius/
      direction.json
      direction.md
      papers/
      notes/
      computations/
```

The folder name should be generated automatically from a timestamp plus a simple slug. Users should only need to see and edit the title and summary.

## Direction Metadata

Keep direction metadata minimal:

```json
{
  "id": "20260429-154233-spectral-radius",
  "title": "Spectral radius problems",
  "summary": "Notes on bounds, extremal graphs, and related conjectures.",
  "createdAt": "2026-04-29T15:42:33",
  "updatedAt": "2026-04-29T15:42:33"
}
```

Do not add tags, colors, owners, sort weights, or status fields in the MVP unless implementation needs prove otherwise.

## Direction Content

Each direction should behave like a simple enhanced folder:

```text
direction.md
papers/
notes/
computations/
```

`direction.md` is the main Markdown page for the direction. It should support LaTeX formula rendering in the first version because formulas are central to the target users.

Suggested default `direction.md` template:

```markdown
# Direction Title

## Known Boundaries

### Theorems

### Classic Papers

### Common Techniques

### Known Counterexamples

## Current Frontier

### Open Problems

### My Conjectures

### Possible Computations
```

## Papers, Notes, and Computations

Keep three content types separate but thin:

- Papers: a local PDF plus a simple Markdown note.
- Notes: standalone Markdown files.
- Computations: Markdown records exported from graph or hypergraph tools.

Avoid building a Zotero-like literature manager in the MVP. BibTeX import, DOI lookup, online metadata fetching, citation formatting, and PDF full-text search are out of scope.

## Existing Tool Integration

The current app already has graph and hypergraph calculator logic. For graph tools, the current implementation includes:

- `GraphCore`: undirected simple graph core with isolated-node preservation.
- `GraphGenerator`: preset graphs such as `P4`, `C3/K3`, `C4`, `C5`, `K4`, `K5`, `S5`, and `W5`.
- Command-based edge input, manual add/delete edge, delete node, auto-compute, preset graph selection, and graph visualization.
- Current graph computation focuses on adjacency spectral radius, PF vector, and adjacency matrix output.

The MVP should reuse these tools instead of redesigning graph input. Add export/save behavior after the local library and direction model exist.

## Deferred Work

Defer these features until the first local workflow works well:

- Custom library folder selection.
- Cloud sync, accounts, shared libraries, or Git-based workflows.
- AI extraction from papers or notes.
- Advanced full-text search and PDF content search.
- Complex tags, statuses, dashboards, or statistics.
- Zotero-style reference management.
- Large-scale graph search or NP-hard optimization tools.

## First Implementation Step

Start with the local library skeleton and direction CRUD:

1. Define common models for library and direction metadata.
2. Add a platform abstraction for the default app-local library root.
3. Implement library initialization.
4. Implement create/read/update/delete for directions.
5. Add a Directions screen with list and detail entry points.
6. Generate `direction.md`, `papers/`, `notes/`, and `computations/` when a direction is created.
