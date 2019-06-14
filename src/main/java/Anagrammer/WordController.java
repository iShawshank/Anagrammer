package Anagrammer;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class WordController {
    private ArrayList<Word> wordList = new ArrayList<Word>();


    @RequestMapping(value = "/words.json", method = RequestMethod.POST)
    public String AddWords() {

        // TODO Add in logic for Adding words.
    }

    @RequestMapping(value = "/anagrams/:word.json", method = RequestMethod.GET)
    public void FetchWords(@RequestParam(value = "limit", required=false) Integer limit) {

        // TODO Add in logic for fetching and returning words.
    }

    @RequestMapping(value = "/words/:word.json", method = RequestMethod.DELETE)
    public void DeleteSingleword () {

        // TODO add in logic for deleting a single word
    }

    @RequestMapping(value = "/words.json", method = RequestMethod.DELETE)
    public void DeleteAllWords () {

        // TODO add in logic for deleting all words
    }
}
