
package controller;

// ProductController.java
import model.Product;
import model.ProductImplement;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import view.ProductView;

import java.util.List;

// ProductController.java
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProductController {
    private ProductImplement model;
    private ProductView view;
    private int currentPage = 1;
    private int rowsPerPage = 3;

    public ProductController(ProductImplement model, ProductView view) {
        this.model = model;
        this.view = view;
    }

    public void addAndSaveUnsavedProducts() {

        // Prompt the user for confirmation to save unsaved products
        if (confirmSave()) {
            // Save the unsaved products to the database
            model.saveUnsavedProductsToDatabase();
            System.out.println("Unsaved products saved to the database.");
        } else {
            System.out.println("Unsaved products not saved to the database.");
        }
    }

    private boolean confirmSave() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to save the unsaved products to the database? (y/n)");
        String choice = scanner.nextLine().toLowerCase();
        return choice.equals("y");
    }

    public void insertUnsavedProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter product details to insert into unsaved changes:");

        // Get product details from the user (ID, name, price, quantity, date, etc.)
        // For simplicity, let's assume a method getUnsavedProductFromUser() in your view class

        Product unsavedProduct = view.getUnsavedProductFromUser();
        model.insertUnsavedProduct(unsavedProduct);

        System.out.println("Product added to unsaved changes.");
    }

    public void displayUnsavedProducts() {
        List<Product> unsavedProducts = model.getUnsavedProducts();
        view.displayUnsavedProducts(unsavedProducts);
    }


    public void displayProductList() {
        List<Product> productList = model.getProducts(currentPage, rowsPerPage);
        view.displayProductList(productList);
        int totalRecord = model.countProducts();
        Table pagination = new Table(25, BorderStyle.DESIGN_CURTAIN, ShownBorders.SURROUND);
        pagination.addCell("Page:\t");
        pagination.addCell(String.valueOf(currentPage));
        pagination.addCell("  of ");
        pagination.addCell(String.valueOf(model.getTotalPages(rowsPerPage)));
        pagination.addCell(" ".repeat(70));
        pagination.addCell("Total records : ");
        pagination.addCell(String.valueOf(totalRecord));
        System.out.println(pagination.render());
        System.out.println();
        System.out.println("F).First Page   N). Next Page  P).Previous Page  L).Last Page  G).Goto  ");
        System.out.println();

    }
    public void help(){

        view.helpDisplay();
    }
    public void searchProductByName(String name) {
        List<Product> products = model.searchProductsByName(name);
        if (!products.isEmpty()) {
            view.displayProductList(products);
        } else {
            System.out.println("No products found with the given name.");
        }
    }
    public void searchProductByNameInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the product name to search: ");
        String name = scanner.nextLine();
        searchProductByName(name);

        System.out.println("--------------------------> Press Enter to Continue");
        scanner.nextLine();
    }
//    public void insertProduct() {
//        Product product = view.getProductDetailsFromUser();
//        model.insertProduct(product);
//        System.out.println("Product inserted successfully!");
//    }


    public void viewProductDetails() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the product ID to view details:");
        int productId = scanner.nextInt();

        Product product = model.getProductById(productId);
        view.displayProduct(product);
    }
    //Delete method
    public void deleteProduct() {
        int productId = view.getProductIDFromUser();
        model.deleteProduct(productId);
        Product product = model.getProductById(productId);

        System.out.println("Product deleted successfully!");
    }

    public void updateProduct() {
        int productId = view.getProductIDFromUser();
        Product product = model.getProductById(productId);
        if (product != null) {
            Product updatedProduct = view.getUpdatedProductDetails(product);
            model.updateProduct(updatedProduct);
            System.out.println("Product updated successfully!");
        } else {
            System.out.println("Product not found!");
        }
    }
    public void setNumberOfDisplayRows() {
        int newRowsPerPage = view.getRowsPerPageFromUser();
        if (newRowsPerPage > 0) {
            rowsPerPage = newRowsPerPage;
            currentPage = 1;
            displayProductList();
        } else {
            System.out.println("Invalid number of display rows. Please try again.");
        }
    }
    public void firstPage(){
        currentPage = 1;
    }
    public void nextPage(){
        currentPage++;
    }
    public void previousPage(){
        currentPage = Math.max(currentPage - 1, 1);
    }
    public void lastPage(){
        int totalPages = model.getTotalPages(rowsPerPage);
        currentPage = Math.min(currentPage + 1, totalPages);
    }
    public void goToSpecificPage() {
        int targetPage = view.getPageNumberFromUser();
        int totalPages = model.getTotalPages(rowsPerPage);
        if (targetPage >= 1 && targetPage <= totalPages) {
            currentPage = targetPage;
            displayProductList();
        } else {
            System.out.println("Invalid page number. Please try again.");
        }
    }
    public void initializeTables() {
        model.initializeTables();
    }
    public void backup() {
        model.backup();
    }

    public void restore() {
        model.restoreTableFromScript();
    }
    public Product updateProductAndMoveToUnsavedUpdates() {
        // Prompt the user for product details
        Product updatedProduct = view.getUnsavedProductFromUser();

        // Update the product and move it to the unsaved updates list
        model.updateProductAndMoveToUnsavedUpdates(updatedProduct);

        System.out.println("Product updated and moved to the unsaved updates list.");
        return updatedProduct;
    }

    public void saveUnsavedUpdatesToDatabase() {
        // Prompt the user to confirm saving unsaved updates to the database
        if (confirmSave()) {
            // Save the unsaved updates to the database
            model.saveUnsavedUpdatesToDatabase();

            System.out.println("Unsaved updates saved to the database.");
        } else {
            System.out.println("Unsaved updates not saved to the database.");
        }
    }

    public void displayUpdatedUnsavedProducts() {
        List<Product> unsavedUpdatesList = model.getUnsavedUpdatesList();
        view.displayUnsavedProducts(unsavedUpdatesList);
    }


    //Validation int
    private static int checkIntInput(String prompt) {
        Scanner scanInt = new Scanner(System.in);
        int enter;
        do {
            System.out.print(prompt);
            while (!scanInt.hasNextInt()) {
                System.out.println("Invalid enter. Please enter again!!!");
                System.out.print(prompt);
                scanInt.next();
            }
            enter = scanInt.nextInt();
            if (enter < 0) {
                System.out.println("Invalid enter. Please enter a positive double.");
            }
        } while (enter < 0);
        return enter;
    }

    //Validation double
    private static double checkDoubleInput(String prompt) {
        Scanner scanDouble = new Scanner(System.in);
        double enter;
        do {
            System.out.print(prompt);
            while (!scanDouble.hasNextDouble()) {
                System.out.println("Invalid enter. Please enter again!!!");
                System.out.print(prompt);
                scanDouble.next();
            }
            enter = scanDouble.nextDouble();
            if (enter < 0) {
                System.out.println("Invalid enter. Please enter a positive double.");
            }
        } while (enter < 0);
        return enter;
    }

    //Validation String
    private static String checkStringInput(String prompt) {
        Scanner scanString = new Scanner(System.in);
        String enter;
        Pattern namePattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)?$");
        do {
            System.out.print(prompt);
            enter = scanString.nextLine().trim();
            if (!namePattern.matcher(enter).matches()) {
                System.out.println("Invalid input. Please enter again!!! ");
            }
        } while (!namePattern.matcher(enter).matches());
        return enter;
    }

    //Validation Option-Choice
    private static int checkOption(Scanner scanner, String prompt, int rangeStart, int rangeEnd) {
        int enter;
        do {
            System.out.print(prompt);

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer.");
                System.out.print(prompt);
                scanner.next();
            }
            enter = scanner.nextInt();

            if (enter < rangeStart || enter > rangeEnd) {
                System.out.println("Input out of range. Please enter a value between " + rangeStart + " to " + rangeEnd + ".");
            }
        } while (enter < rangeStart || enter > rangeEnd);
        return enter;
    }

}

