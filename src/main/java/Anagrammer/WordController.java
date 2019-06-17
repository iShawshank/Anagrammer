package Anagrammer;


import com.google.gson.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;


@RestController
public class WordController {
    private ArrayList<AnagramWord> anagramWordList = new ArrayList<AnagramWord>();
    private Dictionary dictionary = new Dictionary();
    private static final Integer LIMIT_NOT_USED = -1;


    /*******************************************************************************
     * Endpoint that accepts a JSON array string and attempts to add all containing
     * words to the data store.
     * Input:
     *      jsonString - JSON String containing all words to add to the data store.
     * Output: Returns HTTP Status 201 if any words were added to the data store,
     * otherwise returns a HTTP Status 304.
     ********************************************************************************/
    @RequestMapping(value = "/words.json",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addWords(@RequestBody String jsonString) {

        ArrayList<String> newWords = new ArrayList<String>();
        int addWordCount = 0;

        /*
        * When making a normal curl request through console the jsonString is encoded and has
        * an extra '=' at the very end. In this weird case, I decode the JSON string and remove
        * the extra character from the string so it can be parsed correctly.
        */
        if (jsonString.substring(jsonString.length()-1).equals("=")) {
            try {
                jsonString = URLDecoder.decode(jsonString, "UTF-8");
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }

            jsonString = jsonString.replace(jsonString.substring(jsonString.length()-1), "");
        }

        newWords = jsonToArrayList(jsonString);

        // Loop through newWords to determine which to add to the data store
        for (String word : newWords) {

            // Only continue if the word is not contained in the data store already
            if (!wordIsInDataStore(word)){

                // If the word is in the english language dictionary add it to the data store
                if (dictionary.getDictionaryList().contains(word)){
                    anagramWordList.add(new AnagramWord(word));
                    addWordCount++;
                } else {
                    System.out.println(
                            String.format("Unable to add %s to data store as it's not an english language word.",
                                    word));
                }
            } else {
                System.out.println(word + " already exists in data store... ignoring...");
            }
        }

        Collections.sort(anagramWordList);

        if (addWordCount > 0) {
            System.out.println("added words.");
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            System.out.println("no words added.");
            return new ResponseEntity(HttpStatus.NOT_MODIFIED);
        }
    }

    /*******************************************************************************
     * Endpoint that receives a word and returns all anagrams of the original word
     * that reside in the data store.
     * Input:
     *      word - String value that specifies which word to find anagrams of.
     *      limit - Integer value of maximum number of anagrams to search for.
     * Output: anagramList - JSON String containing anagrams of original word.
     ********************************************************************************/
    @RequestMapping(value = "/anagrams/{word}.json")
    public String getAnagrams(@PathVariable String word, @RequestParam(value = "limit", required=false) Integer limit) {

        System.out.println("Fetching anagrams for: " + word);

        if (limit == null){
            limit = LIMIT_NOT_USED;
        }

        String anagramList = formatJsonArray(getAllAnagrams(new AnagramWord(word), limit));

        return anagramList;
    }

    /*******************************************************************************
     * Endpoint that delete's a single word from the data store.
     * Input:
     *      word - String value of the word to delete.
     * Output: Returns HTTP Status 204
     ********************************************************************************/
    @RequestMapping(value = "/words/{word}.json", method = RequestMethod.DELETE)
    public ResponseEntity deleteSingleWord (@PathVariable String word) {

        System.out.println(String.format("Removing '%s' from data store.", word));

        removeWordFromDataStore(word);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /*******************************************************************************
     * Endpoint that delete's all words from the data store.
     * Output: Returns HTTP Status 204
     ********************************************************************************/
    @RequestMapping(value = "/words.json", method = RequestMethod.DELETE)
    public ResponseEntity deleteAllWords () {

        // Delete all words from data store
        anagramWordList.clear();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /*******************************************************************************
    * Endpoint that delete's a word and all of it's anagrams from the data store.
    * Input:
     *      originalWord - String value of the original word.
    * Output: Returns HTTP Status 204
    ********************************************************************************/
    @RequestMapping(value = "/words/delete/{originalWord}.json", method = RequestMethod.DELETE)
    public ResponseEntity deleteWordAndAnagrams (@PathVariable String originalWord) {

        ArrayList<String> anagrams = getAllAnagrams(new AnagramWord(originalWord), LIMIT_NOT_USED);

        // If originalWord exists in the data store delete it.
        if (anagramWordList.contains(originalWord)) {
            removeWordFromDataStore(originalWord);
        }

        // If there are any anagrams, delete them.
        if (anagrams.size() > 0) {
            for (String word : anagrams) {
                removeWordFromDataStore(word);
            }
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /*******************************************************************************
     * Endpoint that calculates the stats of the data store. Calculates min word size,
     * max word size, median word size, and total word count of data store.
     * Output: Returns JSON Object string containing all stats of the data store.
     ********************************************************************************/
    @RequestMapping(value = "/stats/stats.json")
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

        System.out.println("statsJsonString: " + statsJsonString);
        return statsJsonString;
    }

    /*******************************************************************************
     * Method that calculates a list of all anagrams of a original word.
     * Input:
     *      originalWord - AnagramWprd object of the original word.
     *      limit - maximum amount of anagrams to return.
     * Output: anagramList - String ArrayList of all anagrams.
     ********************************************************************************/
    private ArrayList<String> getAllAnagrams (AnagramWord originalWord, Integer limit) {

        ArrayList<String> anagramList = new ArrayList<String>();
        Integer limitCount = 0;

        for (AnagramWord currentWord : anagramWordList) {

            // If currentWord is an anagram of the originalWord add it to the anagramList
            if(isAnagram(currentWord, originalWord)) {
                anagramList.add(currentWord.getWord());

                if (!limit.equals(LIMIT_NOT_USED)){
                    limitCount++;

                    if (limitCount >= limit ){
                        break;
                    }
                }
            }
        }

        return anagramList;
    }

    /*******************************************************************************
     * Method that determines if two AnagramWords are anagrams of each other.
     * Input:
     *      wordOne - first AnagramWord used for comparison.
     *      wordOne - second AnagramWord used for comparison.
     * Output: Returns boolean value as to if both words are anagrams of each other.
     ********************************************************************************/
    private boolean isAnagram(AnagramWord wordOne, AnagramWord wordTwo) {

        /*
        * If both words are the same or have different letter counts
        * they are NOT anagrams of each other. Otherwise continue
        */
        if (wordOne.equals(wordTwo) ||
                wordOne.getLetterCount() != wordTwo.getLetterCount()) {
            return false;
        } else {
            char[] wordOneArray = wordOne.getWord().toCharArray();
            char[] wordTwoArray = wordTwo.getWord().toCharArray();

            // Sort both arrays
            Arrays.sort(wordOneArray);
            Arrays.sort(wordTwoArray);

            // return if both arrays are equal. If they are then the word is an anagram.
            return Arrays.equals(wordOneArray, wordTwoArray);
        }
    }

    /*******************************************************************************
     * Method that formats an ArrayList of Strings into a JSON Array String.
     * Input:
     *      list - ArrayList of Strings to convert
     * Output: jsonMapString - JSON Array String
     ********************************************************************************/
    private String formatJsonArray (ArrayList<String> list) {

        Gson gsonBuilder = new GsonBuilder().setLenient().setPrettyPrinting().create();
        String jsonMapString = "";
        Map jsonMap = new HashMap();

        jsonMap.put("anagrams", list);
        jsonMapString = gsonBuilder.toJson(jsonMap);

        System.out.println(jsonMapString);

        return jsonMapString;
    }

    /*******************************************************************************
     * Method that a JSON Array String into an Array List of Strings.
     * Input:
     *      jsonString - JSON Array String to convert.
     * Output: newArrayList - ArrayList of Strings
     ********************************************************************************/
    private ArrayList<String> jsonToArrayList (String jsonString) {

        JsonElement jElement = new JsonParser().parse(jsonString);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray jsonArray = jObject.getAsJsonArray("words");
        Gson gson = new Gson();

        ArrayList<String> newArrayList = gson.fromJson(jsonArray, ArrayList.class);

        return newArrayList;
    }

    /*******************************************************************************
     * Method determines if a String word is located in the data store currently.
     * Input:
     *      wordString - String word to search data store for.
     * Output: Returns boolean value of if the word is currently in the data store.
     ********************************************************************************/
    private boolean wordIsInDataStore (String wordString ) {

        for (AnagramWord word : anagramWordList) {
            if (word.getWord().equals(wordString)) {
                return true;
            }
        }

        return false;
    }

    /*******************************************************************************
     * Method that removes a stored word from the data store.
     * Input:
     *      wordToDelete - String value of the Anagram word to delete from the data
     *      store.
     ********************************************************************************/
    private void removeWordFromDataStore (String wordToDelete) {

        for (int i = 0; i < anagramWordList.size(); i++){
            if (anagramWordList.get(i).getWord().equals(wordToDelete)) {
                anagramWordList.remove(i);
                break;
            }
        }
    }
}
