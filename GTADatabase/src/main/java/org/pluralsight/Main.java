package org.pluralsight;

import java.sql.*;

public class Main {
    public static void main(String[] args) {

        // 1. Define the JDBC connection string (URL) with necessary configuration options.
        // This includes:
        // - The database server address and port
        // - The database name
        // - Login credentials (username and password)
        // - Security settings (encryption, certificate validation)
        // - Connection timeout settings
        String databaseConnectionUrl = "jdbc:sqlserver://skills4it.database.windows.net:1433;" +
                "database=Courses;" +
                "user=gtareader@skills4it;" +
                "password=StrongPass!2025;" +
                "encrypt=true;" +
                "trustServerCertificate=false;" +
                "loginTimeout=30;";

        // 2. This is the name pattern we're going to search for.
        // The query will return all citizens whose names contain this substring.
        String nameSearchPattern = "Ja";

        // 3. Define the SQL query using a parameter placeholder (?) for the name.
        // The ? will be replaced by a real value using setString().
        String sqlQuery = "SELECT * FROM GTA.Citizens WHERE Name LIKE ?";

        // 4. Declare JDBC resources outside the try block so we can close them in finally.
        Connection databaseConnection = null;
        PreparedStatement selectCitizensStatement = null;
        ResultSet resultSet = null;

        try {
            // 5. Open a connection to the SQL Server database.
            databaseConnection = DriverManager.getConnection(databaseConnectionUrl);

            // 6. Create a PreparedStatement using the SQL query with a parameter.
            // PreparedStatements are secure and protect against SQL injection attacks.
            selectCitizensStatement = databaseConnection.prepareStatement(sqlQuery);

            // 7. Replace the placeholder (?) in the SQL query with the actual search term.
            // The '%' symbols act as wildcards in SQL LIKE expressions.
            selectCitizensStatement.setString(1, "%" + nameSearchPattern + "%");

            // 8. Execute the query and get the result set containing matched records.
            resultSet = selectCitizensStatement.executeQuery();

            // 9. Iterate over the result set and print out the names of matched citizens.
            while (resultSet.next()) {
                String citizenName = resultSet.getString("Name"); // Retrieve the value from the 'Name' column
                System.out.println("Found citizen with name: " + citizenName);
            }

        } catch (SQLException sqlException) {
            // Handle any SQL exceptions that occur during the connection or query execution
            System.err.println("A database error occurred: " + sqlException.getMessage());

        } finally {
            // Always close JDBC resources to prevent memory leaks
            // Each close is wrapped in a try-catch to avoid masking other errors

            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException closeError) {
                System.err.println("Failed to close ResultSet: " + closeError.getMessage());
            }

            try {
                if (selectCitizensStatement != null) selectCitizensStatement.close();
            } catch (SQLException closeError) {
                System.err.println("Failed to close PreparedStatement: " + closeError.getMessage());
            }

            try {
                if (databaseConnection != null) databaseConnection.close();
            } catch (SQLException closeError) {
                System.err.println("Failed to close Connection: " + closeError.getMessage());
            }
        }
    }
}
