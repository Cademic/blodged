# Blodged Functional Requirements

## US-1: Display Featured Posts

### Purpose
To provide users with a landing page that displays featured posts based on their interests and engagement metrics.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 1.1 | The system shall display a landing page that presents 5-8 featured posts. |
|FR 1.2 | Featured posts shall be selected based on user interests and post engagement metrics. |
|FR 1.3 | Each featured post shall display title, author, category, and a post preview. |
|FR 1.4 | The landing page shall refresh featured content at least once every 24 hours. |

## US-2: Interest Categories Selection

### Purpose
To allow users to select and manage their interest categories for personalized content.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 2.1 | New users shall be prompted to select at least 3 interest categories during onboarding. |
|FR 2.2 | The system shall provide at least 10 predefined categories. |
|FR 2.3 | Users shall be able to modify their selected categories via settings at any time. |
|FR 2.4 | The system shall use selected categories to personalize featured content. |

## US-3: Coder-themed Style with Light/Dark Mode

### Purpose
To provide a code editor inspired UI with light and dark mode options for better user experience.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 3.1 | The application shall implement a code editor inspired UI with syntax highlighting colors. |
|FR 3.2 | Users shall be able to toggle between light and dark modes. |
|FR 3.3 | The selected theme preference shall persist across sessions. |
|FR 3.4 | The system shall respect OS-level dark/light mode settings by default. |

## US-4: Login and Logout Functionality

### Purpose
To enable secure user authentication with reliable login and logout capabilities.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 4.1 | Users shall be able to log in using username/email and password. |
|FR 4.2 | The system shall provide a "forgot password" recovery option. |
|FR 4.3 | Users shall be able to log out from any page in the application. |
|FR 4.4 | The system shall invalidate user sessions upon logout. |

## US-5: View Settings

### Purpose
To provide users with a centralized location to manage their account preferences and settings.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 5.1 | Users shall have access to a dedicated settings page. |
|FR 5.2 | Settings shall include profile information, notifications, privacy, and theme preferences. |
|FR 5.3 | Changes to settings shall be saved automatically or with explicit save action. |
|FR 5.4 | Settings changes shall take effect immediately after saving. |

## US-6: Change Profile Picture

### Purpose
To allow users to personalize their profile with preset profile pictures.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 6.1 | Users shall be able to select from at least 5 preset profile pictures. |
|FR 6.2 | The system shall display the user's current profile picture in settings. |
|FR 6.3 | Profile picture changes shall be reflected throughout the application. |
|FR 6.4 | Profile pictures shall be displayed next to username in posts and comments. |

## US-7: Notifications

### Purpose
To keep users informed about interactions with their content and activity from accounts they follow.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR 7.1 | The system shall notify users when their posts receive comments or reactions. |
|FR 7.2 | The system shall notify users when accounts they follow publish new content. |
|FR 7.3 | Users shall be able to view all notifications in a dedicated notifications center. |
|FR 7.4 | Users shall be able to configure which types of notifications they receive. |

## Admin-1: Moderation Page

### Purpose
To provide administrators with tools to monitor and moderate content and user behavior.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR A1.1 | Admins shall have access to a dedicated moderation dashboard. |
|FR A1.2 | The system shall display flagged posts and users for review. |
|FR A1.3 | Admins shall be able to filter moderation items by flag reason, date, and user. |
|FR A1.4 | The system shall track moderation actions with timestamps and admin identifiers. |

## Admin-2: Profanity Filters

### Purpose
To maintain community standards by preventing inappropriate content.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR A2.1 | The system shall prevent submission of content containing predefined banned words. |
|FR A2.2 | Admins shall be able to view and update the list of banned words. |
|FR A2.3 | The system shall apply filters to new posts, comments, usernames, and profile information. |
|FR A2.4 | Users shall be notified when content is rejected due to profanity. |

## Admin-3: Delete User Posts

### Purpose
To enable content moderation by allowing administrators to remove inappropriate content.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR A3.1 | Admins shall be able to delete any user post or comment. |
|FR A3.2 | The system shall require confirmation before deletion. |
|FR A3.3 | The system shall log all post deletions with reason, timestamp, and admin identifier. |
|FR A3.4 | The system shall notify users when their content has been removed. |

## PO-1: Captcha for Account Creation

### Purpose
To prevent automated account creation and reduce spam.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR P1.1 | The system shall require users to complete a reCaptcha verification during registration. |
|FR P1.2 | Account creation shall fail if captcha verification is unsuccessful. |
|FR P1.3 | The system shall implement the latest version of reCaptcha. |
|FR P1.4 | Captcha shall be accessible and provide alternatives for users with disabilities. |

## PO-2: Password Confirmation

### Purpose
To reduce user errors during account creation by confirming password entry.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR P2.1 | User registration shall require password entry in two separate fields. |
|FR P2.2 | The system shall validate that both password entries match before proceeding. |
|FR P2.3 | Users shall receive clear error messages when passwords don't match. |
|FR P2.4 | The system shall provide a "show password" toggle to verify entry. |

## PO-3: Cloud Database Migration

### Purpose
To improve reliability, scalability, and security by migrating from local to cloud database.

### Requirements
| FR ID | Description |
|-------|-------------|
|FR P3.1 | The system shall migrate from local SQL database to a cloud database service. |
|FR P3.2 | Database migration shall maintain all existing data and relationships. |
|FR P3.3 | The cloud database shall support automatic backups and point-in-time recovery. |
|FR P3.4 | The system shall implement encrypted connections to the database. |
|FR P3.5 | Database credentials shall be stored securely outside of application code. |
