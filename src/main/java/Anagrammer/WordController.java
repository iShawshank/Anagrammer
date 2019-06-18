package Anagrammer;


import com.google.gson.*;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.*;

import static Anagrammer.AnagramConstants.LIMIT_NOT_USED;


@RestController
public class WordController {

    private ArrayList<AnagramWord> anagramWordList = new ArrayList<AnagramWord>();
    private Dictionary dictionary = new Dictionary();
    private AnagramUtils utils = new AnagramUtils();
    final static Logger logger = Logger.getLogger(WordController.class);
    

    /*******************************************************************************
     * Endpoint that accepts a JSON array string and attempts to add all containing
     * words to the data store.
     * @param jsonString - JSON String containing all words to add to the data store.
     * @return HTTP Status 201 if any words were added to the data store,
     * otherwise returns a HTTP Status 304.
     ********************************************************************************/
    @RequestMapping(value = "/words.json",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addWords(@RequestBody String jsonString) {

        ArrayList<String> newWords = new ArrayList<String>();
        int addWordCount = 0;

        if (jsonString == null || jsonString.equals("null") || jsonString.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }

        /*
        * When making a normal curl request through console the jsonString is encoded and has
        * an extra '=' at the very end. In this weird case, I decode the JSON string and remove
        * the extra character from the string so it can be parsed correctly.
        */
        if (jsonString.substring(jsonString.length()-1).equals("=")) {
            try {
                jsonString = URLDecoder.decode(jsonString, "UTF-8");
            } catch (Exception ex) {
                logger.error("Error: " + ex.getMessage());
            }

            jsonString = jsonString.replace(jsonString.substring(jsonString.length()-1), "");
        }

        newWords = utils.jsonToArrayList(jsonString);

        // Loop through newWords to determine which to add to the data store
        for (String word : newWords) {

            // Only continue if the word is not contained in the data store already
            if (!utils.wordIsInDataStore(word, anagramWordList)){

                // If the word is in the english language dictionary add it to the data store
                if (Collections.binarySearch(dictionary.getDictionaryList(), word) >= 0) {
                    anagramWordList.add(new AnagramWord(word));
                    addWordCount++;
                } else {
                    logger.debug(
                            String.format("Unable to add %s to data store as it's not an english language word.",
                                    word));
                }
            } else {
                logger.debug(word + " already exists in data store... ignoring...");
            }
        }

        Collections.sort(anagramWordList);

        if (addWordCount > 0) {
            logger.debug(String.format("Added %s words to data store.", addWordCount));
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            logger.debug("no words added to data store.");
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }
    }

    /*******************************************************************************
     * Endpoint that receives a word and returns all anagrams of the original word
     * that reside in the data store.
     * @param word - String value that specifies which word to find anagrams of.
     * @param limit - Integer value of maximum number of anagrams to search for.
     * @return JSON String containing anagrams of original word.
     ********************************************************************************/
    @RequestMapping(value = "/anagrams/{word}.json")
    public String getAnagrams(@PathVariable String word, @RequestParam(value = "limit", required=false) Integer limit) {

        logger.debug("Fetching anagrams for: " + word);

        if (limit == null){
            limit = LIMIT_NOT_USED;
        }

        String anagramList = utils.formatJsonArray(utils.getAllAnagrams(new AnagramWord(word), limit, anagramWordList));

        return anagramList;
    }

    /*******************************************************************************
     * Endpoint that delete's a single word from the data store.
     * @param word - String value of the word to delete.
     * @return HTTP Status 204
     ********************************************************************************/
    @RequestMapping(value = "/words/{word}.json", method = RequestMethod.DELETE)
    public ResponseEntity deleteSingleWord (@PathVariable String word) {

        logger.debug(String.format("Removing '%s' from data store.", word));

        anagramWordList = utils.removeWordFromDataStore(word, anagramWordList);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /*******************************************************************************
     * Endpoint that delete's all words from the data store.
     * @return HTTP Status 204
     ********************************************************************************/
    @RequestMapping(value = "/words.json", method = RequestMethod.DELETE)
    public ResponseEntity deleteAllWords () {

        // Delete all words from data store
        anagramWordList.clear();

        logger.debug("Deleted all words from the data store.");

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /*******************************************************************************
    * Endpoint that delete's a word and all of it's anagrams from the data store.
    * @param originalWord - String value of the original word.
    * @return HTTP Status 204
    ********************************************************************************/
    @RequestMapping(value = "/words/delete/{originalWord}.json", method = RequestMethod.DELETE)
    public ResponseEntity deleteWordAndAnagrams (@PathVariable String originalWord) {

        ArrayList<String> anagrams = utils.getAllAnagrams(new AnagramWord(originalWord), LIMIT_NOT_USED, anagramWordList);

        // If originalWord exists in the data store delete it.
        if (anagramWordList.contains(originalWord)) {
            anagramWordList = utils.removeWordFromDataStore(originalWord, anagramWordList);
        }

        // If there are any anagrams, delete them.
        if (anagrams.size() > 0) {
            for (String word : anagrams) {
                anagramWordList =  utils.removeWordFromDataStore(word, anagramWordList);
            }
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /*******************************************************************************
     * Endpoint that calculates the stats of the data store. Calculates min word size,
     * max word size, median word size, and total word count of data store.
     * @return JSON Object string containing all stats of the data store.
     ********************************************************************************/
    @RequestMapping(value = "/stats/stats.json", method = RequestMethod.GET)
    public String getDataStoreStats() {

        String statsJsonString = "";
        DataStoreStats stats = new DataStoreStats();

        // Set total word count in data store.
        stats.setWordCount(anagramWordList.size());

        // Calculate Max letter count
        AnagramWord max = Collections
                            .max(anagramWordList, Comparator.comparing(s -> s.getLetterCount()));
        stats.setMax(max.getLetterCount());

        // Calculate Min letter count
        AnagramWord min = Collections
                            .min(anagramWordList, Comparator.comparing(s -> s.getLetterCount()));
        stats.setMin(min.getLetterCount());

        // Calculate Median letter count
        Double median = anagramWordList.stream()
                                        .mapToInt(s -> s.getLetterCount())
                                        .average()
                                        .orElse(0);
        stats.setMedian(median);

        Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
        statsJsonString = gsonBuilder.toJson(stats);

        logger.debug("Data store stats json: " + statsJsonString);
        return statsJsonString;
    }
}
