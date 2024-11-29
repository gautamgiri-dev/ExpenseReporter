# Expense Reporter

A user-friendly mobile application for tracking and managing expenses. Developed during my training program in preparation for **WorldSkills Lyon 2024**, the **Expense Reporter** app simplifies expense management by providing intuitive features like expense logging, invoice attachment, and data export.

## Features

### Expense Management
- Log new expenses with details such as:
  - **Type**
  - **Name**
  - **Amount**
  - **Description**
- Automatically attach invoices from shared files (e.g., **UPI apps**, **PDFs**, or **screenshots**).

### File Support
- Supports **PDF** and **image files** for invoice attachments.

### Export Options
- **Export to CSV**: Generates a CSV file containing all expense data for easy sharing.
- **Export to ZIP**: Compiles all attached invoices into a ZIP file with filenames formatted as `date_name` for seamless organization.

### Homepage
- Displays a list of all expenses in **descending order of date**, providing quick access to the latest entries.

### User-Centric Design
- Built for simplicity and speed, ensuring a distraction-free workflow.
- Optimized for performance across Android devices.

## Technical Details

### Development Tools
- **Android Studio**: Primary development environment.
- **Java**: Programming languages used.
- **SQLite**: Local database for managing expense records and attachments.
- **XML**: For user interface design.

### Application Workflow
1. **User Input**: Enter expense details and attach invoices.
2. **Data Storage**: Save expense data in the local SQLite database.
3. **Display**: View expenses on the homepage in descending order of date.
4. **Export**: Generate CSV or ZIP files for sharing or archiving.

## Installation

Download the latest APK from the [Releases](https://github.com/gautamgiri-dev/ExpenseReporter/releases) section of this repository and install it on your Android device.

## License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! If you’d like to suggest enhancements or report bugs, feel free to open an issue or submit a pull request.

## Acknowledgments

- Developed as part of training for **WorldSkills Lyon 2024**.
- Guidance provided by **K12 Activity Academy** and **Ethnus Consultancy Services**.
- Special thanks to competitors and trainers who provided feedback during development.
