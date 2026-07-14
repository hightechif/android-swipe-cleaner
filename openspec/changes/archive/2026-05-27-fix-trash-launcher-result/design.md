## Context

Jetpack Compose screen launchers for activity results (`rememberLauncherForActivityResult`) handle the callback when the system dialog for trashing photos completes. Currently, the launcher callbacks in both the `SwipeScreen` and `CompletionScreen` ignore the `ActivityResult` return parameters and unconditionally invoke `viewModel.onTrashRequestCompleted()`. This causes the database's delete queue to be cleared even if the user denied or cancelled the permission prompt.

## Goals / Non-Goals

**Goals:**
- Properly check the `result.resultCode` of the `ActivityResult` within the `rememberLauncherForActivityResult` launchers.
- Only clear the local database queue (`viewModel.onTrashRequestCompleted()`) if the result code is `Activity.RESULT_OK`.
- Ensure the delete queue remains intact on cancellation (`Activity.RESULT_CANCELED`) or system denial.

**Non-Goals:**
- Altering the Android `MediaStore` repository implementation or legacy fallback deletion methods.
- Modifying SQLite schemas or altering how local photo metadata is stored.

## Decisions

### Decision 1: Activity Result Code Verification
Verify the `result.resultCode` from the `StartIntentSenderForResult` callback using `android.app.Activity.RESULT_OK`.
- **Rationale**: Since the trash permission prompt is a system activity dialog, standard Android result codes apply. If the user allows the deletion, `RESULT_OK` (`-1`) is returned. If they deny or cancel, `RESULT_CANCELED` (`0`) is returned. By checking for `RESULT_OK`, we prevent clearing the database for unsuccessful deletions.
- **Alternatives Considered**: 
  - *Checking the gallery contents manually after the callback*: This would require query operations against the MediaStore after every deletion process, which is slow and performance-intensive.
  - *Checking if `result.data` is not null*: Some Android versions or custom ROMs may not populate additional intent data on completion, making `resultCode` the only reliable field.

## Risks / Trade-offs

- **[Risk]** The user denies the prompt, leaves the completion screen, and assumes the photos were deleted because the screen layout is still in the completion state.
  - **[Mitigation]** The action button text is reactive and updates dynamically based on the delete queue size. If the queue is preserved, the button continues to display "Move X Photos to Trash" instead of transitioning to "No Photos to Delete", providing immediate visual feedback that the deletion did not occur.
