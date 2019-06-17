package Anagrammer;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import static Anagrammer.AnagramConstants.LIMIT_NOT_USED;

public class AnagramUtils {

    Logger logger = Logger.getLogger(AnagramUtils.class);

    /*******************************************************************************
     * Method that calculates a list of all anagrams of a original word.
     * @param originalWord - AnagramWprd object of the original word.
     * @param limit - maximum amount of anagrams to return.
     * @return String ArrayList of all anagrams.
     ********************************************************************************/
    protected ArrayList<String> getAllAnagrams (AnagramWord originalWord, Integer limit, ArrayList<AnagramWord> anagramWordList) {

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
     * @param wordOne - first AnagramWord used for comparison.
     * @param wordOne - second AnagramWord used for comparison.
     * @return boolean value as to if both words are anagrams of each other.
     ********************************************************************************/
    protected boolean isAnagram(AnagramWord wordOne, AnagramWord wordTwo) {

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
     * @param list - ArrayList of Strings to convert
     * @return JSON Array String
     ********************************************************************************/
    protected String formatJsonArray (ArrayList<String> list) {

        Gson gsonBuilder = new GsonBuilder().setLenient().setPrettyPrinting().create();
        String jsonMapString = "";
        Map jsonMap = new HashMap();

        jsonMap.put("anagrams", list);
        jsonMapString = gsonBuilder.toJson(jsonMap);

        logger.debug("JSON array string: " + jsonMapString);

        return jsonMapString;
    }

    /*******************************************************************************
     * Method that a JSON Array String into an Array List of Strings.
     * @param jsonString - JSON Array String to convert.
     * @return ArrayList of Strings
     ********************************************************************************/
    protected ArrayList<String> jsonToArrayList (String jsonString) {

        JsonElement jElement = new JsonParser().parse(jsonString);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray jsonArray = jObject.getAsJsonArray("words");
        Gson gson = new Gson();

        ArrayList<String> newArrayList = gson.fromJson(jsonArray, ArrayList.class);

        return newArrayList;
    }

    /*******************************************************************************
     * Method determines if a String word is located in the data store currently.
     * @param wordString - String word to search data store for.
     * @return boolean value of if the word is currently in the data store.
     ********************************************************************************/
    protected boolean wordIsInDataStore (String wordString, ArrayList<AnagramWord> anagramWordList ) {

        for (AnagramWord word : anagramWordList) {
            if (word.getWord().equals(wordString)) {
                return true;
            }
        }

        return false;
    }

    /*******************************************************************************
     * Method that removes a stored word from the data store.
     * @param wordToDelete - String value of the Anagram word to delete from the data
     *                     store.
     ********************************************************************************/
    protected ArrayList<AnagramWord> removeWordFromDataStore (String wordToDelete, ArrayList<AnagramWord> anagramWordList) {

        for (int i = 0; i < anagramWordList.size(); i++){
            if (anagramWordList.get(i).getWord().equals(wordToDelete)) {
                anagramWordList.remove(i);
                break;
            }
        }

        return anagramWordList;
    }


}
