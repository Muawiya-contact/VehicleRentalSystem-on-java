import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class VehicleRentalSystem {
    private static String pinCode; 
    private static final String PIN_FILE = "pinCode.txt"; 
    private static final String VEHICLES_FILE = "vehicles.txt";
    private static final String RENTED_FILE = "rentedVehicles.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt"; 

    public static void main(String[] args) {
        loadPinCode(); 

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to Vehicle Rental System");
            System.out.println("1. List of available vehicles");
            System.out.println("2. Rent a Vehicle");
            System.out.println("3. Return a Vehicle");
            System.out.println("4. View Total Earnings");
            System.out.println("5. Change Pin Code");
            System.out.println("0. Exit");

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listAvailableVehicles();
                    break;
                case "2":
                    rentVehicle(scanner);
                    break;
                case "3":
                    returnVehicle(scanner);
                    break;
                case "4":
                    System.out.print("Enter the pin code: ");
                    String pin = scanner.nextLine().trim();
                    if (pin.equals(pinCode)) {
                        countEarnings();
                    } else {
                        System.out.println("Incorrect pin code.");
                    }
                    break;
                case "5":
                    changePin(scanner);
                    break;
                case "0":
                    System.out.println("Goodbye! See you next time.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please select a valid number.");
            }
        }
    }

    private static void loadPinCode() {
        try (BufferedReader br = new BufferedReader(new FileReader(PIN_FILE))) {
            pinCode = br.readLine().trim();
        } catch (IOException e) {
            pinCode = "1234";
            savePinCode();  
        }
    }

    private static void savePinCode() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PIN_FILE))) {
            bw.write(pinCode);
        } catch (IOException e) {
            System.out.println("Error saving the pin code.");
        }
    }

    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }
        return lines;
    }

    public static void writeFile(String filename, List<String> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : data) {
                bw.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

    public static void appendToFile(String filename, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(line + "\n");
        } catch (IOException e) {
            System.out.println("Error appending to file: " + filename);
        }
    }

    public static void listAvailableVehicles() {
        List<String> vehicles = readFile(VEHICLES_FILE);
        List<String> rented = readFile(RENTED_FILE);

        System.out.println("\nAvailable Vehicles for Rent:");
        for (String vehicle : vehicles) {
            String[] parts = vehicle.split(",");
            if (!rented.contains(parts[0])) {
                System.out.println("  - " + vehicle);
            }
        }
    }

    public static void rentVehicle(Scanner scanner) {
        List<String> vehicles = readFile(VEHICLES_FILE);
        List<String> rentedCars = readFile(RENTED_FILE);
    
        String finalRegistrationNumber = null;
    
        while (true) {
            System.out.print("Enter the registration number of the vehicle: ");
            String tempRegistrationNumber = scanner.nextLine().trim().toUpperCase(); 
    
            boolean exists = vehicles.stream().anyMatch(vehicle -> vehicle.startsWith(tempRegistrationNumber));
    
            if (exists) {
                if (rentedCars.contains(tempRegistrationNumber)) {
                    System.out.println("Sorry, this vehicle is already rented.");
                } else {
                    finalRegistrationNumber = tempRegistrationNumber;
                    break;
                }
            } else {
                System.out.println("Vehicle not found. Please enter a valid registration number.");
            }
        }
        if (finalRegistrationNumber == null) return; 
    
        String birthDate = getValidDateInput(scanner, "Please enter your birth date (DD/MM/YYYY): ");
        int age = calculateAge(birthDate);
        if (age < 18) {
            System.out.println("Sorry, you must be at least 18 years old to rent a vehicle.");
            return;
        }
    
        String firstName = getValidNameInput(scanner, "Please enter your first name: ");
        String lastName = getValidNameInput(scanner, "Please enter your last name: ");
    
        System.out.print("Please enter your email: ");
        String email = scanner.nextLine().trim();
    
        appendToFile(CUSTOMERS_FILE, birthDate + "," + firstName + "," + lastName + "," + email);
        String rentStart = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        appendToFile(RENTED_FILE, finalRegistrationNumber + "," + birthDate + "," + rentStart);
        appendToFile(TRANSACTIONS_FILE, finalRegistrationNumber + "," + rentStart + ",1000");
    
        System.out.println("Congratulations, " + firstName + " " + lastName + "! You've successfully rented the vehicle.");
    }
    

    private static int calculateAge(String birthDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDateObj = sdf.parse(birthDate);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDateObj);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return -1;
        }
    }

    private static String getValidNameInput(Scanner scanner, String prompt) {
        String name;
        while (true) {
            System.out.print(prompt);
            name = scanner.nextLine().trim();
            if (name.matches("[a-zA-Z]+")) {
                break;
            } else {
                System.out.println("Invalid name. Please enter a valid name.");
            }
        }
        return name;
    }

    private static String getValidDateInput(Scanner scanner, String prompt) {
        String date;
        while (true) {
            System.out.print(prompt);
            date = scanner.nextLine().trim();
            if (date.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                break;
            } else {
                System.out.println("Invalid date format. Please use DD/MM/YYYY.");
            }
        }
        return date;
    }

    public static void returnVehicle(Scanner scanner) {
        List<String> rented = readFile(RENTED_FILE);

        System.out.print("Enter the registration number of the vehicle you're returning: ");
        String registrationNumber = scanner.nextLine().trim().toUpperCase();

        if (!rented.removeIf(line -> line.startsWith(registrationNumber))) {
            System.out.println("This vehicle is not rented.");
        } else {
            writeFile(RENTED_FILE, rented);
            System.out.println("Return Successful! Thank you!");
        }
    }

    public static void countEarnings() {
        double totalEarnings = readFile(TRANSACTIONS_FILE).stream()
                .mapToDouble(line -> Double.parseDouble(line.split(",")[2]))
                .sum();
        System.out.println("Total Earnings: " + totalEarnings + " PKR");
    }

    public static void changePin(Scanner scanner) {
        System.out.print("Enter new pin code: ");
        pinCode = scanner.nextLine().trim();
        savePinCode();
        System.out.println("Pin code changed successfully!");
    }
}
