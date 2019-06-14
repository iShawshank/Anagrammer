package Anagrammer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

public class WordController {


    @RequestMapping(value = "/words.json", method = RequestMethod.POST)
    public void AddWords(@RequestParam(value = "words") ArrayList<Word> words) {

        // TODO Add in logic for Adding words.
    }

    @RequestMapping(value = "/anagrams/:word.json", method = RequestMethod.GET)
    public void FetchWords() {

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
