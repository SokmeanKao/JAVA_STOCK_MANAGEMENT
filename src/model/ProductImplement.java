
package model;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import lib.DatabaseConnection;
public class ProductImplement {
    private List<Product> productList;
    private List<Product> unsavedProducts;
    private List<Product> unsavedUpdatesList;

    public ProductImplement() {
        this.productList = new ArrayList<>();
        this.unsavedProducts = new ArrayList<>();
        this.unsavedUpdatesList = new ArrayList<>();
    }
    public List<Product> getUnsavedUpdatesList() {
        return new ArrayList<>(unsavedUpdatesList);
    }

 
    public void insertUnsavedProduct(Product product) {
        unsavedProducts.add(product);
    }
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM products");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Product product = mapResultSetToProductDTO(resultSet);
                productList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }
    public void saveUnsavedProductsToDatabase() {
        for (Product unsavedProduct : unsavedProducts) {
            insertProduct(unsavedProduct);
        }
        unsavedProducts.clear();
    }

    public void insertProduct(Product product) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getUnitPrice());
            preparedStatement.setInt(3, product.getStockQuantity());
            preparedStatement.setDate(4, new java.sql.Date(product.getImportedDate().getTime()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Product> getProducts(int currentPage, int rowsPerPage) {
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?")) {

            int offset = (currentPage - 1) * rowsPerPage;
            preparedStatement.setInt(1, rowsPerPage);
            preparedStatement.setInt(2, offset);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapResultSetToProductDTO(resultSet);
                    productList.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    public Product getProductById(int productId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM products WHERE id = ?")) {

            preparedStatement.setInt(1, productId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToProductDTO(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if the product with the specified ID is not found
    }
    public void deleteProduct(int productId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM products WHERE id = ?")) {

            preparedStatement.setInt(1, productId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception based on your application's requirements
        }
    }
    public void updateProduct(Product updatedProduct) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE products SET name = ?, unit_price = ?, stock_quantity = ?, imported_date = ? WHERE id = ?")) {

            preparedStatement.setString(1, updatedProduct.getName());
            preparedStatement.setDouble(2, updatedProduct.getUnitPrice());
            preparedStatement.setInt(3, updatedProduct.getStockQuantity());
            preparedStatement.setDate(4, new java.sql.Date(updatedProduct.getImportedDate().getTime()));
            preparedStatement.setInt(5, updatedProduct.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception based on your application's requirements
        }
    }
    public int getTotalPages(int rowsPerPage) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM products")) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int totalProducts = resultSet.getInt(1);
                    return (int) Math.ceil((double) totalProducts / rowsPerPage);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception based on your application's requirements
        }

        return 0; // Return 0 if there's an issue calculating the total pages
    }
    public int countProducts() {
        String query = "SELECT COUNT(*) FROM products";
        try (Connection connection =  DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new RuntimeException("Error: count query returned no results.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error executing the count query.", e);
        }
    }

    public List<Product> searchProductsByName(String productName) {
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM products WHERE name LIKE ?")) {

            preparedStatement.setString(1, "%" + productName + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapResultSetToProductDTO(resultSet);
                    productList.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    public List<Product> getUnsavedProducts() {
        return new ArrayList<>(unsavedProducts);
    }

    private Product mapResultSetToProductDTO(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setUnitPrice(resultSet.getDouble("unit_price"));
        product.setStockQuantity(resultSet.getInt("stock_quantity"));
        product.setImportedDate(resultSet.getDate("imported_date"));
        // Set other attributes based on your ProductDTO class
        return product;
    }
    //Backup & Restore Data
    public void backup() {
        String script = getBackupScript();
        // after done prepare script, need to prepare the filename
        List<String> allBackupFiles = getAllSqlFileName();
        String writeFileName;
        if (allBackupFiles.isEmpty()) {
            writeFileName = "1-product-backup-" + df.format(new Date()) + ".sql";
        }
        else {
            String lastFileName = allBackupFiles.get(allBackupFiles.size()-1);
            String prefix = lastFileName.split("-")[0]; // the 1-, 2-, 3-...(only number, no -)
            prefix = String.format("%d",Integer.parseInt(prefix) + 1);
            writeFileName = prefix + "-product-backup-" + df.format(new Date()) + ".sql";
            System.out.println(writeFileName);
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(BACKUP_FOLDER + "/" + writeFileName));
            writer.write(script);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getBackupScript() {
        List<Product> allProducts = getAllProductsWithoutPageData();

        // preparing script: drop the table, and then create the table again to insert value
        String script = DROP_TABLE_PRODUCTS + ";\n\n" + CREATE_TABLE_IF_NOT_EXISTS_PRODUCTS;
        for (Product product : allProducts) {
            script += String.format("INSERT INTO products (name, unit_price, stock_quantity, imported_date) VALUES ('%s', %f, %d, '%s');\n", product.getName(), product.getUnitPrice(), product.getStockQuantity(), product.getImportedDate());
        }
        return script;
    }
    public List<Product> getAllProductsWithoutPageData() {
        List<Product> productList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM products ORDER BY id")) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapResultSetToProductDTO(resultSet);
                    productList.add(product);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
    public List<String> getAllSqlFileName() {
        List<String> sqlFileName = new ArrayList<>();
        File folder = new File(BACKUP_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            return sqlFileName;
        }

        // when the folder exists, read the files
        File[] allSqlFiles = folder.listFiles();

        if (allSqlFiles != null) {
            for (File sqlfile : allSqlFiles) {
                if (sqlfile.isFile() && sqlfile.getName().endsWith(".sql")) {
                    sqlFileName.add(sqlfile.getName());
                }
            }
        }

        return sqlFileName;
    }
    public void restoreTableFromScript() {
        Scanner scanner= new Scanner(System.in);
        List<String> allBackupFiles = getAllSqlFileName();

        int i = 1;
        for (String filename : allBackupFiles) {
            System.out.println(i + " : " + filename);
            i++;
        }

        System.out.print("select version to restore: ");
        int index = scanner.nextInt();
        // need validation
        runSqlScript(allBackupFiles.get(index-1));
    }
    public void runSqlScript(String backupFileName) {
        String line, script = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(BACKUP_FOLDER + "/" + backupFileName));
            while((line = reader.readLine()) != null) {
                script += line + "\n";
            }


            // connect db, execute the backup script
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         script)) {

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                } catch (SQLException e) {

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void initializeTables() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE_IF_NOT_EXISTS_PRODUCTS)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

            } catch (SQLException e) {
                // guard error when there is no execution on this script (in order word, the table alr exists)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
       public void updateProductInDatabase(Product updatedProduct) {
        String sql = "UPDATE your_table_name SET name=?, unit_price=?, stock_quantity=?, imported_date=? WHERE id=?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, updatedProduct.getName());
            statement.setDouble(2, updatedProduct.getUnitPrice());
            statement.setInt(3, updatedProduct.getStockQuantity());
            statement.setDate(4, new java.sql.Date(updatedProduct.getImportedDate().getTime()));
            statement.setInt(5, updatedProduct.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Product updated in the database.");
            } else {
                System.out.println("Failed to update product in the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
      public void updateProductAndMoveToUnsavedUpdates(Product updatedProduct) {
        // Check if the product is in the productList
        if (productList.contains(updatedProduct)) {
            // Move the product to unsaved updates list
            unsavedUpdatesList.add(updatedProduct);
        } else {
            System.out.println("Product not found in the productList.");
        }
    }

    public void saveUnsavedUpdatesToDatabase() {
        // Save the unsaved updates to the database (implement your database logic here)
        for (Product updatedProduct : unsavedUpdatesList) {
            // Example: Assume you have a method to save a product to the database
            productList.add(updatedProduct);
        }

        // Clear the unsaved updates list after saving to the database
        unsavedUpdatesList.clear();
    }

    private void saveProductToDatabase(Product product) {
        // Implement your database logic to save the product
        // This could involve using JDBC or any other database access method
        // For simplicity, we assume productList is the database in this example
        productList.add(product);
    }
    private final String CREATE_TABLE_IF_NOT_EXISTS_PRODUCTS = "CREATE TABLE if not EXISTS products (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    name VARCHAR(255) NOT NULL,\n" +
            "    unit_price DECIMAL(10, 2) NOT NULL,\n" +
            "    stock_quantity INTEGER NOT NULL,\n" +
            "    imported_date DATE NOT NULL\n" +
            ");\n";
    private final DateFormat df = new SimpleDateFormat("yyy-MM-dd");
    private final String BACKUP_FOLDER = "Backupfile";
    private final String DROP_TABLE_PRODUCTS = "DROP TABLE products";
}


