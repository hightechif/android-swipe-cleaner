## MODIFIED Requirements

### Requirement: Completion Screen Statistics and Celebration
When the photo pool is exhausted, the application SHALL transition to a Completion Screen. The Completion Screen SHALL display the session stats (total reviewed, total marked for deletion) and SHALL play a celebratory Lottie confetti animation regardless of the outcome of the deletion dialog.

#### Scenario: Session Completed
- **WHEN** the user swipes the last photo in the pool
- **THEN** the application navigates to the Completion Screen and plays the confetti Lottie animation.

#### Scenario: Review Remaining Photos Preserves Trash
- **WHEN** the user clicks "Review Remaining Photos" on the completion screen/view
- **THEN** the application reloads the photo pool, filters out all photos currently in the delete queue, and preserves the delete queue list.
