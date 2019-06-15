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



    @RequestMapping(value = "/words.json",
                    method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addWords(HttpServletRequest request, @RequestBody String jsonString) {

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

    @RequestMapping(value = "/anagrams/{word}.json", method = RequestMethod.GET)
    public String getanagrams(@PathVariable String word, @RequestParam(value = "limit", required=false) Integer limit) {

        System.out.println("Fetching anagrams for: " + word);

        if (limit == null){
            limit = LIMIT_NOT_USED;
        }

        String anagramList = formatJsonArray(getAllAnagrams(new AnagramWord(word), limit));

        return anagramList;
    }

    @RequestMapping(value = "/words/{word}.json", method = RequestMethod.DELETE)
    public void deleteSingleword (@PathVariable String word) {

        System.out.println(String.format("Removing '%s' from data store.", word));
        for (int i = 0; i < anagramWordList.size(); i++){
            if (anagramWordList.get(i).getWord().equals(word)) {
                anagramWordList.remove(i);
                break;
            }
        }
    }

    @RequestMapping(value = "/words.json", method = RequestMethod.DELETE)
    public void deleteAllWords () {

        // Delete all words from data store
        anagramWordList.clear();
    }

    /*
    * Description:
    * Input:
    * Output:
    * Calls to: isAnagram
    */
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

    /*
    * Description:
     * Input:
     * Output:
    */
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

    /**/
    private String formatJsonArray (ArrayList<String> list) {

        Gson gsonBuilder = new GsonBuilder().setLenient().setPrettyPrinting().create();
        String jsonMapString = "";
        Map jsonMap = new HashMap();

        jsonMap.put("anagrams", list);
        jsonMapString = gsonBuilder.toJson(jsonMap);

        System.out.println(jsonMapString);

        return jsonMapString;
    }

    /**/
    private ArrayList<String> jsonToArrayList (String jsonString) {

        JsonElement jElement = new JsonParser().parse(jsonString);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray jsonArray = jObject.getAsJsonArray("words");
        Gson gson = new Gson();

        ArrayList<String> newArrayList = gson.fromJson(jsonArray, ArrayList.class);

        return newArrayList;
    }

    private boolean wordIsInDataStore (String wordString ) {

        for (AnagramWord word : anagramWordList) {
            if (word.getWord().equals(wordString)) {
                return true;
            }
        }

        return false;
    }
}
