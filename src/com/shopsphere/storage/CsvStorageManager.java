package com.shopsphere.storage;

import com.shopsphere.model.DigitalProduct;
import com.shopsphere.model.PhysicalProduct;
import com.shopsphere.model.Product;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV export and import operations for Products using BufferedReader and BufferedWriter.
 */
public class CsvStorageManager {

    private static final String CSV_FILE = "data" + File.separator + "products.csv";

    public static void exportProductsToCsv(List<Product> products) {
        try {
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
                writer.write("product_id,type,category_id,name,brand,price,discount,stock,rating,extra1,extra2\n");
                for (Product p : products) {
                    if (p instanceof PhysicalProduct) {
                        PhysicalProduct pp = (PhysicalProduct) p;
                        writer.write(String.format("%d,%s,%d,\"%s\",\"%s\",%.2f,%.2f,%d,%.2f,%.2f,%.2f\n",
                                pp.getProductId(), pp.getProductType(), pp.getCategoryId(),
                                pp.getName().replace("\"", "\"\""), pp.getBrand(), pp.getPrice(),
                                pp.getDiscountPercent(), pp.getStock(), pp.getRating(),
                                pp.getWeightKg(), pp.getShippingFee()));
                    } else if (p instanceof DigitalProduct) {
                        DigitalProduct dp = (DigitalProduct) p;
                        writer.write(String.format("%d,%s,%d,\"%s\",\"%s\",%.2f,%.2f,%d,%.2f,\"%s\",%.2f\n",
                                dp.getProductId(), dp.getProductType(), dp.getCategoryId(),
                                dp.getName().replace("\"", "\"\""), dp.getBrand(), dp.getPrice(),
                                dp.getDiscountPercent(), dp.getStock(), dp.getRating(),
                                dp.getDownloadUrl(), dp.getFileSizeMb()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to export products to CSV: " + e.getMessage());
        }
    }

    public static List<Product> importProductsFromCsv() {
        List<Product> products = new ArrayList<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 9) {
                    int id = Integer.parseInt(parts[0].trim());
                    String type = parts[1].trim();
                    int catId = Integer.parseInt(parts[2].trim());
                    String name = parts[3].replace("\"", "").trim();
                    String brand = parts[4].replace("\"", "").trim();
                    double price = Double.parseDouble(parts[5].trim());
                    double discount = Double.parseDouble(parts[6].trim());
                    int stock = Integer.parseInt(parts[7].trim());
                    double rating = Double.parseDouble(parts[8].trim());

                    if ("DIGITAL".equalsIgnoreCase(type)) {
                        String downloadUrl = parts.length > 9 ? parts[9].replace("\"", "").trim() : "https://downloads.shopsphere.com";
                        double fileSize = parts.length > 10 ? Double.parseDouble(parts[10].trim()) : 50.0;
                        products.add(new DigitalProduct(id, catId, name, brand, name, price, discount, stock, "digital.png", rating, downloadUrl, fileSize));
                    } else {
                        double weight = parts.length > 9 ? Double.parseDouble(parts[9].trim()) : 0.5;
                        double shipping = parts.length > 10 ? Double.parseDouble(parts[10].trim()) : 50.0;
                        products.add(new PhysicalProduct(id, catId, name, brand, name, price, discount, stock, "physical.png", rating, weight, shipping));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        return products;
    }
}
