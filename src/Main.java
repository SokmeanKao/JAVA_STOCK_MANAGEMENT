import controller.ProductController;
import model.ProductImplement;
import view.ProductView;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Instantiate the Model, View, and Controller
        Scanner scanner= new Scanner(System.in);
        ProductImplement model = new ProductImplement();
        ProductView view = new ProductView();
        ProductController controller = new ProductController(model, view);
        controller.initializeTables();
        do {
            controller.displayProductList();
            System.out.println("*Display");
            System.out.println("W) Write    R) Read    U) Update    D) Delete     S) Search    Se) Set Row \n" +
                    "Sa) Save   Un) Unsaved    Ba) Backup    Re) Restore    H) Help   E) Exit  ");
            System.out.println("-------------------------------");
            System.out.print("Choose an option(): ");
            String option = scanner.next();
            switch (option) {
                case "*" -> {
                    controller.displayProductList();
                }
                case "w", "W" -> {
                    controller.insertUnsavedProduct();
                }
                case "r", "R" -> {
                    controller.viewProductDetails();
                }
                case "u", "U" -> {
                    controller.updateProduct();
                }
                case "un", "Un" -> {
                    controller.displayUnsavedProducts(); scanner.nextLine();
                    System.out.println("'I' for Unsaved Insertion and 'U' for Unsaved Update and 'B' for back to menu: " );
                    String choose = scanner.nextLine();
                    switch(choose){
                        case "i", "I":
                            controller.displayUnsavedProducts();
                            break;
                        case "u", "U":
                            controller.displayUpdatedUnsavedProducts();
                            break;
                        case "b", "B":
                            return;
                    }

                }
                case "sa", "Sa" -> {
                    controller.addAndSaveUnsavedProducts();
                }
                case "d", "D" -> {
                   controller.deleteProduct();
                }
                case "f", "F" -> {
                    controller.firstPage();
                }
                case "p", "P" -> {
                    controller.previousPage();
                }
                case "n", "N" -> {
                    controller.nextPage();
                }
                case "l", "L" -> {
                    controller.lastPage();
                }
                case "g", "G" -> {
                    controller.goToSpecificPage();
                }
                case "s", "S" -> {
                    controller.searchProductByNameInput();
                }
                case "se", "Se" -> {
                    controller.setNumberOfDisplayRows();
                }
                case "h", "H" -> {
                    controller.help();
                }
                case "ba", "Ba", "BA", "bA" -> {
                    controller.backup();
                }
                case "re", "Re", "RE", "rE" -> {
                    controller.restore();
                }
                case "E","e" ->{
                    System.out.println("Exiting Stock Management System. Goodbye!!!!");
                    return;
                }
            }
        }while (true);


    }
}
