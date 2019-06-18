#!/usr/bin/env ruby

require 'json'
require_relative 'anagram_client'
require 'test/unit'

# capture ARGV before TestUnit Autorunner clobbers it

class TestCases < Test::Unit::TestCase

  # runs before each test
  def setup
    @client = AnagramClient.new(ARGV)

    # add words to the dictionary
    @client.post('/words.json', nil, {"words" => ["read", "dear", "dare"] }) rescue nil
  end

  # runs after each test
  def teardown
    # delete everything
    @client.delete('/words.json') rescue nil
  end

  def test_adding_words
    res = @client.post('/words.json', nil, {"words" => ["ready", "deary", "yeard"] })

    assert_equal('201', res.code, "Unexpected response code")
  end

  def test_adding_words_but_not_sending_any
    # Clear out the word array.
    @client.delete('/words.json')

    #post but don't include any words
    res = @client.post('/words.json')

    assert_equal('304', res.code, "Unexpected response code")
  end

  def test_adding_duplicate_words

    res = @client.post('/words.json', nil, {"words" => ["read", "dear", "dare"] })

    assert_equal('304', res.code, "Unexpected response code")
  end

  def test_fetching_anagrams
    # fetch anagrams
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")
    assert_not_nil(res.body)

    body = JSON.parse(res.body)

    assert_not_nil(body['anagrams'])

    expected_anagrams = %w(dare dear)
    assert_equal(expected_anagrams, body['anagrams'].sort)
  end

  def test_fetching_anagrams_with_limit
    # fetch anagrams with limit
    res = @client.get('/anagrams/read.json', 'limit=1')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(1, body['anagrams'].size)
  end

  def test_fetch_for_word_with_no_anagrams
    # fetch anagrams with limit
    res = @client.get('/anagrams/zyxwv.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_all_words
    res = @client.delete('/words.json')

    assert_equal('204', res.code, "Unexpected response code")

    # should fetch an empty body
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_all_words_multiple_times
    3.times do
      res = @client.delete('/words.json')

      assert_equal('204', res.code, "Unexpected response code")
    end

    # should fetch an empty body
    res = @client.get('/anagrams/read.json', 'limit=1')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_single_word_that_does_not_exist
    # delete the word
    res = @client.delete('/words/kevin.json')

    assert_equal('204', res.code, "Unexpected response code")

    # expect it not to show up in results
    res = @client.get('/anagrams/kevin.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_single_word
    # delete the word
    res = @client.delete('/words/dear.json')

    assert_equal('204', res.code, "Unexpected response code")

    # expect it not to show up in results
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(['dare'], body['anagrams'])
  end

  def test_data_store_stats

    res = @client.post('/words.json', nil, {"words" => ["yeanling", "yearbird", "yearbook"] })
    assert_equal('201', res.code, "Unexpected response code")

    res = @client.get('/stats/stats.json')

    assert_equal('200', res.code, "Unexpected response code")
    assert_not_nil(res.body)

    body = JSON.parse(res.body)
    assert_not_nil(body['wordCount'])

    expected_word_count = 6
    expected_median = 6.0
    expected_min = 4
    expected_max = 8

    assert_equal(expected_word_count, body['wordCount'])
    assert_equal(expected_median, body['median'])
    assert_equal(expected_min, body['min'])
    assert_equal(expected_max, body['max'])

  end

  def test_delete_word_and_all_anagrams
    res = @client.post('/words.json', nil, {"words" => ["ready", "deary", "yeard"] })
    assert_equal('201', res.code, "Unexpected response code")

    res = @client.delete('/words/delete/read.json')

    assert_equal('204', res.code, "Unexpected response code")

    #should fetch an empty body
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)

    #fetch anagrams for new word
    res = @client.get('/anagrams/ready.json')

    assert_equal('200', res.code, "Unexpected response code")
    assert_not_nil(res.body)

    body = JSON.parse(res.body)

    assert_not_nil(body['anagrams'])

    expected_anagrams = %w(deary yeard)
    assert_equal(expected_anagrams, body['anagrams'].sort)
  end

  def test_delete_word_and_all_anagrams_where_word_and_anagrams_dont_exist
    # Prove that word and anagrams don't exist.
    res = @client.get('/anagrams/nshd.json')
    body = JSON.parse(res.body)
    assert_equal(0, body['anagrams'].size)

    # Attempt to delete the word and it's anagrams
    res = @client.delete('/words/delete/nshd.json')
    assert_equal('204', res.code, "Unexpected response code")

    # Fetch "Empty" body
    res = @client.get('/anagrams/nshd.json')
    assert_equal('200', res.code, "Unexpected response code")

    # Test that's it's a empty
    body = JSON.parse(res.body)
    assert_equal(0, body['anagrams'].size)
  end

  def test_delete_word_and_all_anagrams_where_word_doesnt_exist_but_anagrams_do
    res = @client.post('/words.json', nil, {"words" => ["and"] })
    assert_equal('201', res.code, "Unexpected response code")


    # Prove that anagrams do exist even though word doesn't
    res = @client.get('/anagrams/dan.json')
    body = JSON.parse(res.body)
    assert_not_nil(body['anagrams'])
    expected_anagrams = %w(and)
    assert_equal(expected_anagrams, body['anagrams'].sort)

    # Attempt to delete the word and it's anagrams
    res = @client.delete('/words/delete/dan.json')
    assert_equal('204', res.code, "Unexpected response code")

    # Fetch "Empty" body
    res = @client.get('/anagrams/nshd.json')
    assert_equal('200', res.code, "Unexpected response code")

    # Test that's it's a empty
    body = JSON.parse(res.body)
    assert_equal(0, body['anagrams'].size)
  end
end
