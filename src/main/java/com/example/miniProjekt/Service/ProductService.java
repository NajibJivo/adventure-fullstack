package com.example.miniProjekt.Service;

import com.example.miniProjekt.Repository.ProductRepository;
import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.model.ProductCategory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

// @Service markerer dette som business logic layer
// Ligger mellem Controller (HTTP) og Repository (Database)
@Service
public class ProductService {

    // @Autowired betyder at Spring automatisk finder og injicerer ProductRepository
    // Vi behøver ikke skrive: productRepository = new ProductRepository()
    @Autowired
    private ProductRepository productRepository;

    // @PostConstruct betyder at denne metode kører AUTOMATISK når applikationen starter
    // Det sker efter at Spring har oprettet ProductService og injiceret Repository
    @PostConstruct
    public void init() {
        // Kalder metoden der laver testdata
        initializeTestData();
    }

    // Henter alle produkter fra databasen
    public List<Product> getAllProducts() {
        // findAll() kommer fra JpaRepository
        // Returnerer en List<Product> med alle produkter
        return productRepository.findAll();
    }

    // Finder ét produkt baseret på ID
    // Returnerer Optional fordi produktet måske ikke findes
    public Optional<Product> getProductById(Long id) {
        // findById() returnerer Optional<Product>
        // Optional kan være tom (empty) hvis ID ikke findes
        return productRepository.findById(id);
    }

    // Gemmer et produkt (både ny og opdatering)
    // Hvis product.id er null: opretter nyt produkt
    // Hvis product.id findes: opdaterer eksisterende produkt
    public Product saveProduct(Product product) {
        // save() returnerer det gemte produkt med auto-genereret ID
        return productRepository.save(product);
    }

    // Sletter et produkt baseret på ID
    // void betyder metoden ikke returnerer noget
    public void deleteProduct(Long id) {
        // deleteById() sletter produktet fra databasen
        productRepository.deleteById(id);
    }

    // Opdaterer kun prisen på et produkt
    public Product updatePrice(Long id, Double newPrice) {
        // Finder produktet - returnerer Optional
        Optional<Product> optionalProduct = productRepository.findById(id);

        // Tjekker om Optional er tom (produktet findes ikke)
        if (optionalProduct.isEmpty()) {
            // Kaster en fejl hvis produktet ikke findes
            throw new RuntimeException("Produkt ikke fundet");
        }

        // .get() henter Product objektet fra Optional
        Product product = optionalProduct.get();

        // Opdaterer prisen
        product.setPrice(newPrice);

        // Gemmer det opdaterede produkt i databasen
        return productRepository.save(product);
    }

    // Finder produkter i en bestemt kategori
    public List<Product> getProductsByCategory(ProductCategory category) {
        // Bruger vores custom repository-metode
        return productRepository.findByCategory(category);
    }

    // Opretter testdata når applikationen starter
    public void initializeTestData() {
        // count() tæller antal produkter i databasen
        // Kører kun hvis databasen er tom (0 produkter)
        if (productRepository.count() == 0) {

            // Opretter første T-shirt
            Product tshirt1 = new Product(
                    "AdventureXP T-shirt",    // navn
                    "Hvid T-shirt med logo",  // beskrivelse
                    149.00,                    // pris
                    ProductCategory.T_SHIRT,   // kategori
                    50                         // lagerbeholdning
            );
            // Tilføjer størrelse separat (var ikke i constructor)
            tshirt1.setSize("M");

            // Opretter anden T-shirt
            Product tshirt2 = new Product(
                    "AdventureXP T-shirt",
                    "Sort T-shirt med logo",
                    149.00,
                    ProductCategory.T_SHIRT,
                    30
            );
            tshirt2.setSize("L");

            // Opretter slik-produkter
            Product candy1 = new Product(
                    "Gummibamser",
                    "200g pose",
                    25.00,
                    ProductCategory.SNACK,
                    100
            );

            Product candy2 = new Product(
                    "Chokoladebar",
                    "Mælkechokolade",
                    15.00,
                    ProductCategory.SNACK,
                    80
            );

            // Opretter sodavand-produkter
            Product soda1 = new Product(
                    "Cola",
                    "0,5L flaske",
                    20.00,
                    ProductCategory.SODA,
                    150
            );

            Product soda2 = new Product(
                    "Fanta",
                    "0,5L flaske",
                    20.00,
                    ProductCategory.SODA,
                    120
            );

            // saveAll() gemmer alle produkter i én database-operation
            // List.of() opretter en liste med de 6 produkter
            productRepository.saveAll(List.of(tshirt1, tshirt2, candy1, candy2, soda1, soda2));
        }
    }
}