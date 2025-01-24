import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;

public class LibrarySystem {
    public static ArrayList<Patron> patrons = new ArrayList<Patron>();
    public static Scanner scanner = new Scanner(System.in);

    public static void displayMenu() {
        System.out.println("Enter your file/file path:");
        String fileName = scanner.nextLine();
        File userFile = new File(fileName);
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(userFile);
        } catch (Exception e) {
            fileScanner = null;
            System.out.println("That file does not exist.");
        }
        String Option;
        while (true) {

            System.out.println("Press 1 to add a patron manually.");
            System.out.println("Press 2 to add patron from file.");
            System.out.println("Press 3 to remove patron by ID.");
            System.out.println("Press 4 to display list of patrons.");
            System.out.println("Press 5 to display menu.");
            System.out.println("Press 6 to exit.");
            Option = scanner.nextLine();

            switch (Option) {
                case "1":
                    addPatronManually();
                    break;
                case "2":
                    addPatronFromFile(fileScanner);
                    break;
                case "3":
                    removePatronById();
                    break;
                case "4":
                    displayList();
                    break;
                case "5":

                    break;
                case "6":
                    scanner.close();
                    return;
                default:
                    break;
            }

        }
    }

    private static void displayList() {
        System.out.println("Id\t\tName\t\tAddress\t\t\t\t\t\t\t\tFine");
        for (Patron p : patrons) {
            System.out.println(p.toString());
        }
    }

    private static void addPatronManually() {
        // Add checks to make sure values are expected
        String name;
        String address;
        double fine;

        System.out.println("Enter your name:");
        name = scanner.nextLine();
        System.out.println("Enter your address:");
        address = scanner.nextLine();
        // Add check for MissMatchInput
        System.out.println("Enter your fine:");
        fine = scanner.nextDouble();
        scanner.nextLine();


        int id_int = 9999999 - patrons.size();
        String id = String.valueOf(id_int);
        patrons.add(new Patron(id, name, address, fine));
    }

    public static void removePatronById() {
        System.out.println("Please enter the ID you would like to remove:");
        String idToDelete = scanner.nextLine();
        for (int i = 0; i < patrons.size(); i++) {
            if (patrons.get(i).id.equals(idToDelete)) {
                patrons.remove(i);
                break;
            }
        }
    }

    public static void addPatronFromFile(Scanner scanner) {
        String iD;
        String name;
        String address;
        double fine;
        String lineHolder;
        String[] splitString;
        if (scanner == null) {
            System.out.println("Cannot add Patron. File does not exist.");
            return;
        }
        if (scanner.hasNext()) {
            lineHolder = scanner.nextLine();
            splitString = lineHolder.split("-");
            patrons.add(new Patron(splitString[0], splitString[1],
                    splitString[2], Double.parseDouble(splitString[3])));

        }
    }
}
