# Kraken cryptoexchange api with clojure
_NOTE_: this article is an appendix text for 
[this repository](https://github.com/dogz1lla/clj-kraken).

Cryptocurrencies (CC) have one advantage over traditional financial assets 
(think shares, futures, options, etc.) in that it is much easier to "algotrade" 
CC from the programmatic standpoint because biggest cryptoexchanges offer 
modern APIs for us to feast upon. 
It is usually a REST api that can serve web requests from the client 
application. 
[Kraken](https://www.kraken.com) is one of the largest cryptoexchanges and 
it also has an [api](https://docs.kraken.com/rest/) that allows clients to 
write algotrading software in any language. 
I am a fan of clojure so i decided to write a little library that can connect 
to Kraken, send requests to their api and parse the responses.

## Authenticating yourself on Kraken
I quickly realized that the part that will take the most effort will be writing
code that allows the client to authenticate itself on Kraken servers. 
[Official docs](https://docs.kraken.com/rest/#section/Authentication) provide 
example code for several programming languages (no clojure though!) and one can
see that there is quite a lot of encoding/byte manipulations of a string that
involves private api key a "nonce" (unique increasing integer, can be set to 
the current time in milliseconds) and the encoded payload that includes the 
endpoint and all of the parameters of the request.
The algorithm is not terribly complicated however i quickly realized that i 
will have to use java security and cryptography related libraries which i wasnt
really excited about because i have never worked with java before. 

But hey! 

It just means i have something new to learn (and clojure interop is pretty 
straightforward to use)!

## Sending the request and parsing the results
Kraken api has a bunch of endpoints varying from querying current server time 
to downloading price data and submitting orders. 
Each of the endpoints fall into one of the two categories: public and private.
Public endpoints do not really require for the client to be authenticated 
because they only return information that can be publicly available across all 
accounts.
Private endpoints allow one to work with information that is... well, private. 
Think checking balance and order management.

I decided to set up the library such that only the "Balance" endpoint is 
supported initially from the list of private endpoints but one could easily 
enable other endpoints by adding them to the 
[set](https://github.com/dogz1lla/clj-kraken/blob/main/src/kraken_api/requests.clj#L16) 
manually.
On the other hand all public endpoints are enabled by default.

## Using the api client
First of all for the library to work two environmental variables _KRAKEN\_API\_KEY_ 
and _KRAKEN\_API\_SEC_ (for the api key and the api secret key respectively).

Then if one wishes to use the library with the custom algotrading code one must 
hack around with the source code itself. 
However to try it out i included a cli interface which can be run as follows:

```bash
clj -M -m kraken-api.clj-kraken endpoint-name param1=value1 param2=value2 ...
```
