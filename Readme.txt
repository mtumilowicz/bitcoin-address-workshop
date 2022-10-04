# bitcoin-notes
* references
    * https://www.oreilly.com/library/view/mastering-bitcoin-2nd/9781491954379/
    * https://www.manning.com/books/grokking-bitcoin
    * https://bitcointalk.org/index.php?topic=141848.0
    * https://deeprnd.medium.com/length-extension-attack-bff5b1ad2f70
    * https://github.com/marcelo140/length-extension
    * https://cstheory.stackexchange.com/questions/585/what-is-the-difference-between-a-second-preimage-attack-and-a-collision-attack
    * https://medium.com/@parthshah.ce/generate-bitcoin-addresses-using-java-in-six-steps-b1c418796a9e
    * https://awebanalysis.com/en/bitcoin-address-validate/
    * https://www.baeldung.com/java-secure-random
    * https://www.baeldung.com/scala/enumeratum

## keys, addresses
* string of digits and characters
* derived from public keys
    * algorithms used to make a bitcoin address from a public key: SHA256 and RIPEMD160
        * PKH = RIPEMD160(SHA256(PUBLIC_KEY))
        * PKH - public key hash
        * SHA256 - cryptographic hash function that outputs a 256-bit (32-byte) number
        * RIPEMD160 - cryptographic hash function that outputs a 160-bit (20-byte) number
            * deliberate choice to make the PKHs shorter
        * motivation of using two different hash functions
            * well-balanced trade-off between security and size
            * different roots
                * SHA256 - US National Security Agency (NSA)
                * RIPEMD160 - European university in open collaboration with a broad community
            * composite hash does not have a Merkle–Damgård structure
                * concern about possible weaknesses in the Merkle–Damgård structure itself
                    * example: length extension attack
                        * key idea
                            * the output of the hash function corresponds to its internal state
                            when it finishes processing the input
                            * we can resume the hashing and provide it extra input
                        * tool: https://github.com/bwall/HashPump
            * pre-image-resistant = is the property of a hash function that it is hard to invert
                * if either hash function turns out to not be pre-image-resistant, the other still is
                    * example
                        * if you can calculate an input to RIPEMD160 that gives a certain
                        PKH output, you still need to pre-image attack SHA256 to find the public key
                            * 2^255 guesses
                * first preimage attack
                * second preimage attack
            * however if the output set of either cryptographic hash function is smaller than
            anticipated => output set of combined hash-function is affected
                * pretend SHA256 has only 100 possible output values => Only 100 different PKHs
                * just try different random privateKey => use corresponding publicKey => calculate corresponding PKH
* represent the owner of a private/public key pair
    * or a payment script
* almost always encoded as "Base58Check" to help human readability
    * Base58Check = 58 characters and checksum
        * Base58 = Base64 without the 0, O, l, I, +, /
        * checksum = additional four bytes
            * appended to the end
            * hashOfHash = SHA256(SHA256(prefix+data))
                * prefix example
                    * bitcoin address: zero (0x00 in hex)
                        * Base58 result prefix: 1
                    * private key: 128 (0x80 in hex)
                        * Base58 result prefix: 5, K, or L
            * checksum = first four bytes of hashOfHash
