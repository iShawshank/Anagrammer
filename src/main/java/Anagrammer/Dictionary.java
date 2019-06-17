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
            Scanner scanner = new Scanner(new File("resources/dictionary.txt"));

            while (scanner.hasNext()) {
                // Add new anagramWord to dictionary
                this.dictionaryList.add(scanner.next());
            }
        } catch (Exception ex) {
            System.out.println("Error in loading dictionary: " + ex.getMessage());
        }
    }

    // Sort the dictionary to allow binary searches of the ArrayList
    public void sort() {

        Collections.sort(this.dictionaryList);
    }
}
