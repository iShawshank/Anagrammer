package Anagrammer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import org.apache.log4j.Logger;

public class Dictionary {

    private final ArrayList<String> dictionaryList = new ArrayList<String>();
    final static Logger logger = Logger.getLogger(Dictionary.class);

    public Dictionary() {
        logger.debug("Loading Dictionary...");
        load();
        logger.debug("Sorting Dictionary...");
        sort();
        logger.debug("Dictionary loaded and ready.");
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
            logger.error("Error in loading dictionary: " + ex.getMessage());
        }
    }

    // Sort the dictionary to allow binary searches of the ArrayList
    public void sort() {

        Collections.sort(this.dictionaryList);
    }
}
