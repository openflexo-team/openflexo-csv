# openflexo-csv — Backlog

Features and evolutions to develop. Each entry says what problem it solves and when it is done. Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `CSV-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML runtime errors

### CSV-F-1 — The csv adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-csv#1](https://github.com/openflexo-team/openflexo-csv/issues/1)

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`), done with the other remaining adapters in one batch (asked by the user, 2026-11-16), with the rules settled on the first adapters (xlsx, xml, jdbc, rest, formod, docx): the edition actions caught the failures of their evaluations (four catches printing the stack, or a log), and went on with null after a warning.

**Rules applied (decided with the user on the first adapters).** Evaluations through `FMLEvaluation`; every `execute` / `performExecute` declares `throws FMLExecutionException`; a null receiver, model, document, name or required input is an `FMLNullReferenceError`; a select from a null source is an `FMLNullReferenceError` naming the select; a foreseen failure of the technology an `FMLTechnologyError` with a message of its own; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an `FMLCancellation`; a typed failure of `AbstractCreateResource` rethrown unchanged; a log just before a throw removed (the failure is reported once, by the editor); the `System.out` traces of the actions removed. Scope: the paths that FML execution goes through; loading and conversion code left.

**Done (2026-11-16).**
- `AddCSVCell`, `AddCSVRow`: a null document, row, index or column index is an `FMLNullReferenceError`, a negative index or a missing row at an index an `FMLTechnologyError` (they returned null after a warning); a null value leaves the cell as it is. Every failure was caught, `Exception` included, logged and turned into null: it now propagates.
- `SelectCSVRow`, `SelectCSVCell` and their unique forms: a row or document given but null, or a null receiver, is an `FMLNullReferenceError` (a warning and an empty result before).
- `CreateCSVResource`: a null name, URI or resource center is an `FMLNullReferenceError`, a failing creation an `FMLTechnologyError` (untyped before, each logged at SEVERE).

**Chosen without an explicit decision (batch review).** The optional bindings (row, document, cell to copy, cells) are evaluated when set,
even invalid (an `FMLResolutionError`; they were skipped); a negative index and a missing row are `FMLTechnologyError`s; the value and the
cell to copy of `AddCSVCell` are evaluated before the cell is created.

**Verified (2026-11-16).** csv-ta-test 117 green; ECJ compiles csv-ta. No new test.
