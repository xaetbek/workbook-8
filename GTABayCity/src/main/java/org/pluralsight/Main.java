package org.pluralsight;


import java.sql.*;
import java.util.Scanner;
public class Main {

    private static final String DB_URL = "jdbc:sqlserver://skills4it.database.windows.net:1433;" +
            "database=Courses;" +
            "user=gtareader@skills4it;" +
            "password=StrongPass!2025;" +
            "encrypt=true;" +
            "trustServerCertificate=false;" +
            "loginTimeout=30;";

    public static void main (String[]args){
        Scanner inputScanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Bay City SQL CLI ===");
            System.out.println("1. Suspect Scanner (WHERE)");
            System.out.println("2. Vehicle Watchlist (JOIN + WHERE)");
            System.out.println("3. Reward Tracker (GROUP BY + SUM + ORDER BY)");
            System.out.println("4. Elite Agent Filter (GROUP BY + HAVING)");
            System.out.println("5. Search Person");
            System.out.println("6. Search Vehicle");
            System.out.println("7. Search Vehicles A Person Owns");
            System.out.println("8. Find AVG Mission Payout");
            System.out.println("9. Find Inactive Agents");
            System.out.println("0. Exit");
            System.out.print("Select mission: ");

            int choice = inputScanner.nextInt();
            switch (choice) {
                case 1 -> runSuspectScanner();
                case 2 -> runVehicleWatchlist();
                case 3 -> runRewardTracker();
                case 4 -> runEliteAgentFilter();
                case 5 -> searchPerson();
                case 6 -> searchVehicle();
                case 7 -> searchAPersonVehicles();
                case 8 -> runAvgPayout();
                case 9 -> runInactiveAgentReport();
                case 0 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void runSuspectScanner () {
        // Define the SQL query. The '?' is a placeholder for a value we'll provide later.
        String query = "SELECT Name, Alias, WantedLevel FROM GTA.Citizens WHERE WantedLevel >= ?";

        // try-with-resources: this will automatically close the database connection and statement
        try (Connection conn = DriverManager.getConnection(DB_URL); // Connect to the database using DB_URL
             PreparedStatement stmt = conn.prepareStatement(query)) { // Prepare the SQL query to be executed

            // Set the value of the first '?' in the SQL query to 2
            // This means we want all citizens with WantedLevel >= 2
            stmt.setInt(1, 2);

            // Execute the query and store the results in a ResultSet
            ResultSet rs = stmt.executeQuery();

            // Print a heading for the output
            System.out.println("\n--- Suspects with WantedLevel >= 2 ---");

            // Loop through the result set (each row returned by the query)
            while (rs.next()) {
                // Get the values of the current row and print them in a formatted way
                // %-20s = left-aligned text in a 20-character wide column
                System.out.printf("%-20s %-20s Wanted Level: %d\n",
                        rs.getString("Name"),       // Get the 'Name' column
                        rs.getString("Alias"),      // Get the 'Alias' column
                        rs.getInt("WantedLevel"));  // Get the 'WantedLevel' column
            }

        } catch (SQLException e) {
            // If something goes wrong (like a connection issue or bad query), print the error message
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void runVehicleWatchlist () {
        String query = "SELECT c.Name, v.Type, v.Brand " +
                "FROM GTA.Citizens c JOIN GTA.Vehicles v ON c.CitizenID = v.OwnerID " +
                "WHERE v.IsStolen = 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Stolen Vehicles and Their Owners ---");
            while (rs.next()) {
                System.out.printf("%-20s %-15s %-15s\n",
                        rs.getString("Name"), rs.getString("Type"), rs.getString("Brand"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void runRewardTracker () {
        String query = "SELECT C.Name, SUM(M.Reward) AS TotalEarnings\n" +
                "FROM GTA.Citizens C\n" +
                "JOIN GTA.Assignments A ON C.CitizenID = A.CitizenID\n" +
                "JOIN GTA.Missions M ON M.MissionID = A.MissionID\n" +
                "GROUP BY C.Name\n" +
                "ORDER BY TotalEarnings DESC;" ;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Reward Tracker ---");
            while (rs.next()) {
                System.out.printf("%-20s $%.2f\n",
                        rs.getString("Name"), rs.getDouble("TotalEarnings"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void runEliteAgentFilter () {
        String query = "SELECT c.Name, COUNT(*) AS MissionCount, SUM(m.Reward) AS TotalEarnings " +
                "FROM GTA.Citizens C " +
                "JOIN GTA.Assignments A ON C.CitizenID = A.CitizenID " +
                "JOIN GTA.Missions M ON M.MissionID = A.MissionID " +
                "GROUP BY c.Name HAVING COUNT(*) >= 2 AND SUM(m.Reward) >= 4000";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Elite Agents ---");
            while (rs.next()) {
                System.out.printf("%-20s Missions: %d, Earnings: $%.2f\n",
                        rs.getString("Name"), rs.getInt("MissionCount"), rs.getDouble("TotalEarnings"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }




    // challenge 1

    public static void searchPerson() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name to search: ");
        final String nameSearch = scanner.nextLine();
        String query = "SELECT * " +
                "FROM GTA.Citizens C " +
                "WHERE C.Name " +
                "LIKE '%"+ nameSearch +"%'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.printf("%-10s %-20s %-20s %-5s %-20s %-10s\n",
                    "ID", "Name", "Alias", "Age", "Profession", "Wanted");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s %-5d %-20s %-10d\n",
                        rs.getInt("CitizenID"),
                        rs.getString("Name"),
                        rs.getString("Alias"),
                        rs.getInt("Age"),
                        rs.getString("Profession"),
                        rs.getInt("WantedLevel"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }



    // challenge 2

    public static void searchVehicle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter brand to search: ");
        final String brandSearch = scanner.nextLine();
        String query = "SELECT * " +
                "FROM GTA.Vehicles V " +
                "WHERE V.Brand " +
                "LIKE '%"+ brandSearch +"%'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.printf("%-10s %-20s %-20s %-15s %-20s %-10s\n",
                    "VehicleID", "OwnerID", "Type", "Brand", "Speed", "Stolen(1 = stolen 0 = not stolen)");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s %-15s %-20s %-10d\n",
                        rs.getInt("VehicleID"),
                        rs.getString("OwnerID"),
                        rs.getString("Type"),
                        rs.getString("Brand"),
                        rs.getString("Speed"),
                        rs.getInt("IsStolen"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    // challenge 3

    public static void searchAPersonVehicles() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name to search: ");
        final String nameSearch = scanner.nextLine();
        String query = "SELECT C.Name, V.Type, V.Brand, V.IsStolen " +
                "FROM GTA.Vehicles V " +
                "JOIN GTA.Citizens C ON V.OwnerID = C.CitizenID " +
                "WHERE C.Name " +
                "LIKE '%"+ nameSearch +"%'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.printf("%-25s  %-20s %-15s  %-10s\n",
                    "Current Owners Name", "Type", "Brand", "Stolen(1 = stolen 0 = not stolen)");
            while (rs.next()) {
                System.out.printf("%-26s %-20s %-16s %-10d\n",
                        rs.getString("Name"),
                        rs.getString("Type"),
                        rs.getString("Brand"),
                        rs.getInt("IsStolen"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }





    // challenge 4


    public static void runAvgPayout() {

        String query = "SELECT AVG(M.Reward) AS AveragePayout " +
                "FROM GTA.Missions M ";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("----Average payout for a mission----");

            if (rs.next()) {
                System.out.printf("The average mission payout is: $%.2f\n",
                        rs.getDouble("AveragePayout"));
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // challenge 5

    public static void runInactiveAgentReport() {
        String query = "SELECT C.Name FROM GTA.Citizens C " +
                "LEFT JOIN GTA.Assignments A ON C.CitizenID = A.CitizenID " +
                "LEFT JOIN GTA.Missions M ON M.MissionID = A.MissionID " +
                "WHERE A.CitizenID IS NULL";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("\n--- Agents With No Recorded Missions ---");
            while (rs.next()) {
                System.out.printf("Name: %s\n", rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


}
