package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.ProductService;
import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.model.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

// @RestController kombinerer @Controller + @ResponseBody
// Betyder at alle metoder returnerer data (JSON) i stedet for HTML-sider
@RestController
// @RequestMapping definerer base URL for alle endpoints i denne controller
// Alle metoder starter med /api/products
@RequestMapping("/api/products")
public class ProductController {

    // Spring injicerer automatisk ProductService
    @Autowired
    private ProductService productService;

    // GET /api/products - Henter alle produkter
    // @GetMapping uden parameter = samme URL som @RequestMapping
    @GetMapping
    public List<Product> getAllProducts() {
        // Kalder service-metoden
        List<Product> products = productService.getAllProducts();
        // Spring konverterer automatisk List<Product> til JSON
        return products;
    }

    // GET /api/products/5 - Henter produkt med ID 5
    // @GetMapping("/{id}") betyder URL'en slutter med et tal
    @GetMapping("/{id}")
    // @PathVariable Long id henter tallet fra URL'en
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // Henter Optional<Product> fra service
        Optional<Product> optionalProduct = productService.getProductById(id);

        // Tjekker om produktet findes
        if (optionalProduct.isEmpty()) {
            // Returnerer HTTP 404 Not Found hvis produktet ikke findes
            return ResponseEntity.notFound().build();
        }

        // .get() henter Product fra Optional
        Product product = optionalProduct.get();
        // ResponseEntity.ok() returnerer HTTP 200 OK med produktet som JSON
        return ResponseEntity.ok(product);
    }

    // POST /api/products - Opretter nyt produkt
    // @PostMapping håndterer HTTP POST requests
    @PostMapping
    // @RequestBody konverterer JSON fra request til Product objekt
    public Product createProduct(@RequestBody Product product) {
        // Gemmer produktet i databasen
        // Database auto-genererer ID'et
        Product saved = productService.saveProduct(product);
        // Returnerer det gemte produkt (nu med ID) som JSON
        return saved;
    }

    // PUT /api/products/5 - Opdaterer produkt med ID 5
    @PutMapping("/{id}")
    // Modtager både ID fra URL og produkt-data fra request body
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,              // ID fra URL
            @RequestBody Product productDetails // Ny data fra JSON
    ) {
        // Finder det eksisterende produkt
        Optional<Product> optionalProduct = productService.getProductById(id);

        // Tjekker om produktet findes
        if (optionalProduct.isEmpty()) {
            // Returnerer 404 hvis ikke fundet
            return ResponseEntity.notFound().build();
        }

        // Henter produktet fra Optional
        Product product = optionalProduct.get();

        // Opdaterer alle felter med de nye værdier
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setStockQuantity(productDetails.getStockQuantity());
        product.setSize(productDetails.getSize());

        // Gemmer det opdaterede produkt
        Product saved = productService.saveProduct(product);

        // Returnerer HTTP 200 OK med det opdaterede produkt
        return ResponseEntity.ok(saved);
    }

    // PATCH /api/products/5/price - Opdaterer KUN prisen
    // PATCH bruges til partial updates (kun nogle felter)
    @PatchMapping("/{id}/price")
    // Modtager ny pris som JSON: bare tallet, fx 199.00
    public ResponseEntity<Product> updatePrice(
            @PathVariable Long id,
            @RequestBody Double newPrice
    ) {
        try {
            // Kalder service-metoden der opdaterer pris
            Product updated = productService.updatePrice(id, newPrice);
            // Returnerer opdateret produkt
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Hvis produktet ikke findes, returnerer 404
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/products/5 - Sletter produkt med ID 5
    @DeleteMapping("/{id}")
    // ResponseEntity<?> betyder returtypen er fleksibel
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        // Tjekker om produktet findes før sletning
        Optional<Product> optionalProduct = productService.getProductById(id);

        if (optionalProduct.isEmpty()) {
            // Returnerer 404 hvis produktet ikke findes
            return ResponseEntity.notFound().build();
        }

        // Sletter produktet fra databasen
        productService.deleteProduct(id);

        // Returnerer HTTP 200 OK (tom response)
        // .build() betyder ingen data i response body
        return ResponseEntity.ok().build();
    }

    // GET /api/products/category/T_SHIRT - Filtrer efter kategori
    @GetMapping("/category/{category}")
    // Spring konverterer automatisk "T_SHIRT" string til ProductCategory enum
    public List<Product> getProductsByCategory(@PathVariable ProductCategory category) {
        // Henter produkter i den valgte kategori
        return productService.getProductsByCategory(category);
    }

    // POST /api/products/init-data - Initialiserer testdata
    // Denne endpoint kan kaldes manuelt hvis man vil genindlæse data
    @PostMapping("/init-data")
    public ResponseEntity<String> initializeTestData() {
        // Kalder service-metoden der opretter testdata
        productService.initializeTestData();
        // Returnerer en tekstbesked i stedet for JSON
        return ResponseEntity.ok("Product test data initialized");
    }
}