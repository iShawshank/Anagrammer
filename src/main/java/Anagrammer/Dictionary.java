package Anagrammer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Dictionary {

    private final ArrayList<String> dictionaryList = new ArrayList<String>();

    public Dictionary() {
        System.out.println("Loading Dictionary...");
        load();
        System.out.println("Sorting Dictionary...");
        sort();
        System.out.println("Dictionary loaded and ready.");
    }

    public ArrayList<String> getDictionaryList() {
        return dictionaryList;
    }

    public void load() {
        try {
            Scanner scanner = new Scanner(new File("dictionary.txt"));

            while (scanner.hasNext()) {
                // Add new anagramWord to dictionary
                this.dictionaryList.add(scanner.next());
            }
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    public void sort() {

        // Sorting array by letter count and alphabetical (Ascending)
        Collections.sort(this.dictionaryList);
    }
}
