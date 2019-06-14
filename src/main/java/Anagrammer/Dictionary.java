package Anagrammer;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Dictionary {

    private final ArrayList<AnnagramWord> dictionary = new ArrayList<AnnagramWord>();

    public Dictionary() {
        load();
        sort();
    }

    public ArrayList<AnnagramWord> getDictionary() {
        return dictionary;
    }

    public void load() {
        try {
            Scanner scanner = new Scanner(new File("dictionary.txt"));

            while (scanner.hasNext()) {
                // Add new AnnagramWord to dictionary
                this.dictionary.add( new AnnagramWord(scanner.next()));
            }
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

        System.out.println("Done loading dictionary");
        System.out.println("dictionary size: " + this.dictionary.size());
    }

    public void sort() {
        System.out.println("Sorting dictionary...");

    }
}
