package Anagrammer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        //WordController wordController = new WordController();


        SpringApplication.run(Application.class, args);

        //wordController.loadDictionary();
    }
}
