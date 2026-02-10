import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.nio.file.*;
import java.io.*;

public class LibraryManagementSystem {
    // ArrayList to store all patron objects
    public static ArrayList<Patron> patrons = new ArrayList<>();

    // Scanner to read input from the user
    static Scanner scanner = new Scanner(System.in);

    // Method to display the main menu to the user
    private static void displayMenu() {
        System.out.println("---------------------------------------------------");
        System.out.println("\t***PLEASE SELECT A MENU OPTION BY NUMBER***");
        System.out.println("---------------------------------------------------");
        System.out.println("1. ADD PATRON MANUALLY");
        System.out.println("2. ADD PATRONS FROM .TXT FILE");
        System.out.println("3. REMOVE PATRON FROM SYSTEM");
        System.out.println("4. LIST ALL PATRONS");
        System.out.println("5. EXIT MENU");
    }

    // Main menu loop that handles user input and executes menu options
    public static void Menu() {
        int choice = 0; // variable to store the user's choice

        do {
            displayMenu(); // show menu options
            System.out.print("MAKE YOUR SELECTION: ");
            String input = scanner.nextLine(); // read user input as string

            try {
                // converting user input to integer
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // If input is invalid, prompt user again
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                continue;
            }

            // Switch cases to handle each menu option
            switch (choice) {
                case 1:
                    addPatron(); // add a patron manually to system
                    break;
                case 2:
                    addPatronFile(); // add patrons from a text file
                    break;
                case 3:
                    removePatron(); // remove a patron by ID#
                    break;
                case 4:
                    listPatrons(); // list all current patrons in system
                    break;
                case 5:
                    System.out.println("Exiting menu."); // exit the menu
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-5.");
            }
        } while (choice != 5); // loop until user chooses to exit
    }

    // Method to add a patron manually
    private static void addPatron() {
        String patronID;
        String name;
        String address;
        double fineAmount;

        // while loop to validate Patron ID
        while (true) {
            System.out.print("Enter 7-Digit Patron ID#: ");
            patronID = scanner.nextLine();

            if (patronID.length() != 7) {
                System.out.println("Patron ID must be exactly 7 digits.");
                continue;
            }

            boolean allDigits = true;
            for (char c : patronID.toCharArray()) {
                if (!Character.isDigit(c)) {
                    allDigits = false;
                    break;
                }
            }

            if (!allDigits) {
                System.out.println("Patron ID must contain only digits.");
                continue;
            }

            if (CheckID(patronID)) {
                System.out.println("Patron ID already exists.");
                continue;
            }

            break; // exit loop if ID is valid
        }

        // Loop to validate name input from user
        while (true) {
            System.out.print("Enter Patron Name: ");
            name = scanner.nextLine();
            if (!name.isBlank()) break; // exit loop if name is not blank
            System.out.println("Patron Name must not be blank.");
        }

        // Loop to validate address input from user
        while (true) {
            System.out.print("Enter Patron Address: ");
            address = scanner.nextLine();
            if (!address.isBlank()) break; // exit loop if address is not blank
            System.out.println("Patron Address must not be blank.");
        }

        // Loop to validate fine amount input from user
        while (true) {
            System.out.print("Enter Patron fine amount (0–250): ");
            String fineInput = scanner.nextLine();

            try {
                fineAmount = Double.parseDouble(fineInput); // convert to double
                if (fineAmount >= 0 && fineAmount <= 250) break; // valid amount
                System.out.println("Invalid fine amount. Must be between 0 and 250.");
            } catch (NumberFormatException e) {
                System.out.println("Fine amount must be a number.");
            }
        }

        // Try to create and add the Patron object to the list
        try {
            patrons.add(new Patron(patronID, name, address, fineAmount));
            System.out.println("Patron added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // Show all patrons after addition
        listPatrons();
    }

    // Method to read patrons from a text file and add them to the list
    private static void addPatronFile() {
        while (true) {
            System.out.print("Please enter filepath of file to import (.txt) or 'Q' to quit: ");
            String filePath = scanner.nextLine().trim();

            if (filePath.equalsIgnoreCase("Q")) {
                System.out.println("File import canceled.");
                break; // exit file import loop
            }

            List<String> fileLines = new ArrayList<>(); // store all lines from file

            try {
                Scanner fileReader = new Scanner(new File(filePath)); // open file
                while (fileReader.hasNextLine()) {
                    String currentLine = fileReader.nextLine();
                    if (currentLine.startsWith("\uFEFF")) {
                        currentLine = currentLine.substring(1); // remove BOM if present
                    }
                    fileLines.add(currentLine); // add line to list
                }
                fileReader.close(); // close file reader
            } catch (FileNotFoundException e) {
                System.out.println("File not found. Please try again.");
                continue; // ask for file path again
            }

            // Loop through all lines from the file
            for (String line : fileLines) {
                String[] parts = line.split("-"); // split line into ID, name, address, fine

                if (parts.length != 4) {
                    System.out.println("Skipping invalid line (wrong format): " + line);
                    continue; // skip lines with incorrect format
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                String address = parts[2].trim();
                String fineStr = parts[3].trim();

                // Skip if ID already exists
                if (CheckID(id)) {
                    System.out.println("Skipping line (duplicate ID): " + line);
                    continue;
                }

                // Parse fine amount and validate
                double fine;
                try {
                    fine = Double.parseDouble(fineStr);
                    if (fine < 0 || fine > 250) {
                        System.out.println("Skipping line (invalid fine amount): " + line);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line (non-numeric fine): " + line);
                    continue;
                }

                // Add Patron to list
                try {
                    patrons.add(new Patron(id, name, address, fine));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping line (error adding patron): " + line);
                }
            }

            System.out.println("File import completed.");
            break; // exit after successful file processing
        }

        // Show all patrons after importing from file
        listPatrons();
    }

    // Method to remove a patron by ID
    private static void removePatron() {
        while(true) {
            System.out.print("To delete a patron, please enter valid 7-Digit patron ID #, OR if you would like to exit enter 'Q': ");
            String patronID = scanner.nextLine();

            if (patronID.equalsIgnoreCase("Q")) {
                System.out.println("Quitting removal process");
                break; // exit removal loop
            }

            Patron toRemove = null; // variable to store the patron to remove

            // Search for patron with matching ID
            for (Patron p : patrons) {
                if (p.getPatronID().equals(patronID)) {
                    toRemove = p;
                    break;
                }
            }

            // Remove patron if found
            if (toRemove != null) {
                patrons.remove(toRemove);
                System.out.println("Patron has been removed successfully.");
                break; // exit loop after removal
            } else {
                System.out.println("Patron ID does not exist. Please try again.");
            }
        }

        // Show updated list after removal
        listPatrons();
    }

    // Method to list all current patrons
    private static void listPatrons() {
        System.out.println("----All Patrons Currently in System----");
            //if list is empty say so
        if (patrons.isEmpty()) {
            System.out.println("No patrons in System"); // show message if no patrons
        } else {
            // Print each patron in the list
            for (Patron p : patrons) {
                System.out.println(p);
            }
        }
    }

    // Method to check if a Patron ID already exists in the system
    public static boolean CheckID(String id) {
        for (Patron p : patrons) {
            if (p.getPatronID().equals(id)) {
                return true; // true = ID already exists
            }
        }
        return false; // false = ID does not exist
    }
}
