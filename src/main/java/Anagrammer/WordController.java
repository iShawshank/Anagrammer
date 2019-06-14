package Anagrammer;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

@RestController
public class WordController {
    private ArrayList<AnnagramWord> annagramWordList = new ArrayList<AnnagramWord>();
    private Dictionary dictionary = new Dictionary();


    @RequestMapping(value = "/words.json", method = RequestMethod.POST)
    public void addWords() {

        // TODO Add in logic for Adding words.

    }

    @RequestMapping(value = "/anagrams/:word.json", method = RequestMethod.GET)
    public void fetchWords(@RequestParam(value = "limit", required=false) Integer limit) {

        // TODO Add in logic for fetching and returning words.
    }

    @RequestMapping(value = "/words/:word.json", method = RequestMethod.DELETE)
    public void deleteSingleword () {

        // TODO add in logic for deleting a single word
    }

    @RequestMapping(value = "/words.json", method = RequestMethod.DELETE)
    public void deleteAllWords () {

        // TODO add in logic for deleting all words
    }


}
