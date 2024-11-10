package limechain.ethereum_fetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableTransactionManagement
public class App {

    public static void main(String... args) {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(App.class, args);
    }
}