[![Build Status](https://app.travis-ci.com/mtumilowicz/bitcoin-address-workshop.svg?branch=master)](https://app.travis-ci.com/mtumilowicz/bitcoin-address-workshop)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# bitcoin-address-workshop
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
    * http://www.herongyang.com/Cryptography/Certificate-Format-DER-Distinguished-Encoding-Rules.html
    * https://crypto.stackexchange.com/questions/22919/explanation-of-each-of-the-parameters-used-in-ecc
    * https://www.ietf.org/rfc/rfc5639.txt
    * https://cryptobook.nakov.com/asymmetric-key-ciphers/elliptic-curve-cryptography-ecc
    * https://en.wikipedia.org/wiki/Elliptic_curve_point_multiplication
    * https://bitcoin.stackexchange.com/questions/85387/what-is-the-reasoning-behind-the-choice-of-2256-232-977-for-the-prime-on-the-s
    * https://crypto.stackexchange.com/questions/70507/in-elliptic-curve-what-does-the-point-at-infinity-look-like

## preface
* goals of this workshop
    * introduction to encoding formats: DER, hex, sha256, ripemd160, base64, base58
    * understanding of bouncy castle lib
        * generating elliptic curve key pair
        * deriving public key from private key
        * entities: SECObjectIdentifiers, ECDomainParameters, ECNamedDomainParameters
    * understanding how bitcoin address is created from public key
    * can explain elliptic curve domain parameters

## formats
* DER
    * is a binary format for data structures described by ASN.1
    * used as the most popular encoding format to store X.509 certificates in files
    * basic rule: all data types shall be encoded as four components in the following order:
        * Identifier octets
        * Length octets
        * Contents octets
        * End-of-contents octets
    * exercise
        * using Secp256k1.derivePublicKey, derive public key (DER) from private key
            ```
            a76448f06981aeb02df458f657be1f2994729f8df0de672ecc0095421089f5bc // HEX private key
            ```
            ```
            3056301006072a8648ce3d020106052b8104000a03420004f51b58c89eebcdcdedfb6733bfe45fb884186e8277910ea7dea83fd44380a96de8671ddb36f0bf38d502d9ec7b1973ffe6d696431edc163d73f95cf9acd2180a
            ```
        * encode public key in base64
            ```
            echo "3056301006072a8648ce3d020106052b8104000a03420004f51b58c89eebcdcdedfb6733bfe45fb884186e8277910ea7dea83fd44380a96de8671ddb36f0bf38d502d9ec7b1973ffe6d696431edc163d73f95cf9acd2180a" | xxd -r -p | base64
            ```
            ```
            MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE9RtYyJ7rzc3t+2czv+RfuIQYboJ3kQ6n3qg/1EOAqW3oZx3bNvC/ONUC2ex7GXP/5taWQx7cFj1z+Vz5rNIYCg==
            ```
        * use openssl asn1parse to see details and key
            ```
             echo MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE9RtYyJ7rzc3t+2czv+RfuIQYboJ3kQ6n3qg/1EOAqW3oZx3bNvC/ONUC2ex7GXP/5taWQx7cFj1z+Vz5rNIYCg== | base64 -d | openssl asn1parse -inform der -dump
            ```
        * output
            ```
             0:d=0  hl=2 l=  86 cons: SEQUENCE
             2:d=1  hl=2 l=  16 cons: SEQUENCE
             4:d=2  hl=2 l=   7 prim: OBJECT            :id-ecPublicKey
            13:d=2  hl=2 l=   5 prim: OBJECT            :secp256k1
            20:d=1  hl=2 l=  66 prim: BIT STRING
               0000 - 00 04 f5 1b 58 c8 9e eb-cd cd ed fb 67 33 bf e4   ....X.......g3..
               0010 - 5f b8 84 18 6e 82 77 91-0e a7 de a8 3f d4 43 80   _...n.w.....?.C.
               0020 - a9 6d e8 67 1d db 36 f0-bf 38 d5 02 d9 ec 7b 19   .m.g..6..8....{.
               0030 - 73 ff e6 d6 96 43 1e dc-16 3d 73 f9 5c f9 ac d2   s....C...=s.\...
               0040 - 18 0a                                             ..
            ```
        * compare it with deriving key in HEX format
            ```
            SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKeyParams).getEncoded("HEX")
            ```
            ```
            hex encoded:            3056301006072a8648ce3d020106052b8104000a03420004f51b58c89eebcdcdedfb6733bfe45fb884186e8277910ea7dea83fd44380a96de8671ddb36f0bf38d502d9ec7b1973ffe6d696431edc163d73f95cf9acd2180a
            from openssl asn1parse: --------------------------------------------0004f51b58c89eebcdcdedfb6733bfe45fb884186e8277910ea7dea83fd44380a96de8671ddb36f0bf38d502d9ec7b1973ffe6d696431edc163d73f95cf9acd2180a
            ```
        * and then convert it back to DER:
            * prefix: 3056301006072a8648ce3d020106052b8104000a0342
            * raw key: 0004f51b58c89eebcdcdedfb6733bfe45fb884186e8277910ea7dea83fd44380a96de8671ddb36f0bf38d502d9ec7b1973ffe6d696431edc163d73f95cf9acd2180a
            * command
                ```
                (echo -n 3056301006072a8648ce3d020106052b8104000a0342; echo 0004f51b58c89eebcdcdedfb6733bfe45fb884186e8277910ea7dea83fd44380a96de8671ddb36f0bf38d502d9ec7b1973ffe6d696431edc163d73f95cf9acd2180a) | xxd -r -p | base64
                ```
            * result
                ```
                MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE9RtYyJ7rzc3t+2czv+RfuIQYboJ3kQ6n3qg/1EOAqW3o
                Zx3bNvC/ONUC2ex7GXP/5taWQx7cFj1z+Vz5rNIYCg==
                ```
* hex - base16
    * values: (0???F)
    * hex digit is called nibble
* base64
    * values: A-Z, a-z, 0-9 (62 values) and +, /
* base58 - base64 without the 0, O, l, I, +, /
* sha256
    * cryptographic hash function that outputs a 256-bit (32-byte) number
* ripemd160
    * cryptographic hash function that outputs a 160-bit (20-byte) number

## keys, addresses
* string of digits and characters
* derived from public keys
    * algorithms used to make a bitcoin address from a public key: SHA256 and RIPEMD160
        * PKH = RIPEMD160(SHA256(PUBLIC_KEY))
        * PKH - public key hash
        * RIPEMD160 - deliberate choice to make the PKHs shorter
        * motivation of using two different hash functions
            * well-balanced trade-off between security and size
            * different roots
                * SHA256 - US National Security Agency (NSA)
                * RIPEMD160 - European university in open collaboration with a broad community
            * composite hash does not have a Merkle???Damg??rd structure
                * concern about possible weaknesses in the Merkle???Damg??rd structure itself
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
    * Base58Check = base58 plus checksum
        * checksum = additional four bytes
            * appended to the end
            * hashOfHash = SHA256(SHA256(prefix+data))
                * prefix example
                    * bitcoin address: zero (0x00 in hex)
                        * Base58 result prefix: 1
                    * private key: 128 (0x80 in hex)
                        * Base58 result prefix: 5, K, or L
            * checksum = first four bytes of hashOfHash

## bouncy castle
* it would be valuable to take a look here: https://github.com/mtumilowicz/elliptic-curve-workshop
* bouncy castle
    * to create public key in DER format we need
        ```
        SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(publicKeyParams).getEncoded("DER")
        ```
    * publicKeyParams are created from a constructor
        ```
        public ECPublicKeyParameters(
            ECPoint             q, // point representing the public key
            ECDomainParameters  parameters) // domain parameters representing the curve
        ```
    * ECNamedDomainParameters = ASN1ObjectIdentifier (curve name) and ECDomainParameters (domainParameters)
        ```
        ECDomainParameters(
                ECCurve     curve,
                ECPoint     G, // generation point
                BigInteger  n, // order of point G
                BigInteger  h) // cofactor of G
        ```

## compressed public keys
* introduced to bitcoin to reduce the size of transactions
* reduce size of bitcoin blockchain database
* each public key requires 520 bits (prefix + x + y)
* public key is a point (x,y) on an elliptic curve
    * (x, y^2 mod p = (x^3 + 7) mod p)
    * we can store only x and sign
        * reduce the size by 256 bits
* uncompressed public keys have a prefix of 04
    * compressed - either a 02 or a 03 prefix
    * in binary arithmetic y coordinate is either even or odd (corresponds to the positive/negative sign)
        * 02 if the y is even, and 03 if it is odd
* corresponds to the same private key
* remark
    * a single private key can produce a public key expressed in two different formats that produce two
    different bitcoin addresses
