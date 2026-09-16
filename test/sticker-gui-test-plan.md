# Sticker GUI test plan

Run with Java 25. Use temporary task storage for the test session.

| Aim | Input / setup | Expected result |
| --- | --- | --- |
| Standalone size | `random_sticker` | One sticker in a 160 × 160 fit area; no response text; profile remains 40 × 40. |
| Confusion before error | `unknown` | Nankore in a 120 × 120 fit area above the unchanged error text. |
| Successful additions | `todo read`, valid `deadline`, valid `event` | Yatta above each existing confirmation. |
| Completion and reopening | `mark 1`, `unmark 1` | Naisu, then Otsu, above the existing confirmations. |
| Successful deletion/restoration | `delete 1`, `undo` | Naisu above each existing confirmation. |
| Queries | `list`, `find read`, matching `list_by_date` | Otsu when results exist; Gomen when empty. |
| Invalid input | `mark 99`, `random_sticker extra` | Nankore above the existing error, overriding success mapping. |
| Unavailable action | Duplicate task, empty undo history, failed storage write | Gomen above the existing response. |
| Text-only responses | Start and `bye` | Greeting and goodbye have no sticker; exit behavior remains unchanged. |
| Layout | Long task description and repeated replies; resize window | Text wraps below the sticker, with no overlap; conversation scrolls to the latest reply. |
| Image integrity | Render all five stickers in both display modes | Every PNG loads, preserves its aspect ratio, and uses the correct fit dimensions. |

All contextual stickers use a 120 × 120 fit area; their text remains visible underneath.
Console behavior is covered by `ui-test-plan.md`.

## Verification record

Verified with Java 25 using the actual MainWindow FXML and temporary task storage.
All five sticker assets decoded in both display modes. Assertions verified 160/120 px
fit dimensions, preserved aspect ratio, 40 px profile dimensions, sticker-before-text
ordering, unchanged response text, and no excess vertical space.

Visual inspection confirmed the random-sticker and unknown-command replies, then
a long task reply after narrowing the window to 330 px. Text wrapped below the
sticker without overlap, and the latest reply remained visible through automatic scrolling.

The command mapping and failure cases are covered by StickerResponseTest and
CommandProcessorTest. All 164 unit tests and all 17 exact-output console cases passed;
Checkstyle passed for main and test sources.

Preview images: `build/reports/stickers/chat.png` and
`build/reports/stickers/narrow-chat.png` (generated local artifacts).
