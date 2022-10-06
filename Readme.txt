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
    * http://www.herongyang.com/Cryptography/Certificate-Format-DER-Distinguished-Encoding-Rules.html
    * https://crypto.stackexchange.com/questions/22919/explanation-of-each-of-the-parameters-used-in-ecc
    * https://www.ietf.org/rfc/rfc5639.txt
    * https://cryptobook.nakov.com/asymmetric-key-ciphers/elliptic-curve-cryptography-ecc
    * https://en.wikipedia.org/wiki/Elliptic_curve_point_multiplication

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
    * exercise (BitcoinAddressTest could be helpful)
        * using Secp256k1 object, derive public key (DER) from private key
            ```
            a76448f06981aeb02df458f657be1f2994729f8df0de672ecc0095421089f5bc
            ```
        * encode public key in base64
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
    * values: (0–F)
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

## elliptic curves
* elliptic curve visualisation: https://www.desmos.com/calculator/ialhd71we3?lang=pl
* elliptic curve cryptography (ECC) uses elliptic curves over the finite field 𝔽p (where p is prime and p > 3) or 𝔽2m (where the fields size p = 2_m_)
    * field is a square matrix of size p x p and the points on the curve are limited to integer coordinates within the field only
    * All algebraic operations within the field (like point addition and multiplication) result in another point within the field
    * in practice the "elliptic curves" used in cryptography are "sets of points in square matrix", not classical "curves"
* Intuitively, this is not dissimilar to the fact that if you had a point P on a circle, adding 42.57 degrees to its angle may still be a point "not too far" from P, but adding 1000 or 1001 times 42.57 degrees will yield a point that requires a bit more complex calculation to be found
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
            ECPoint     G,
            BigInteger  n,
            BigInteger  h)
    ```
* Cryptography uses elliptic curves in a simplified form (Weierstras form)
    * y2 = x3 + _a_x + b
    * For example, the NIST curve secp256k1 (used in Bitcoin)
        * y2 = x3 + 7 (the above elliptic curve equation, where a = 0 and b = 7)
* domain parameters
    * ECC elliptic curves are described by a set of elliptic curve domain parameters, such as the curve equation
    parameters, the field parameters and the generator point coordinates
        * These parameters are specified in cryptography standards (ex. SEC 2: Recommended Elliptic
        Curve Domain Parameters)
    * a,b,p define a curve (y^2 == x^3+a^x +b) mod p
        * "Bitcoin curve" secp256k1 takes the form: y2 ≡ x3 + 7 (mod p)
    * G - generation point
        * for the elliptic curves over finite fields, the ECC cryptosystems define a special pre-defined
        (constant) EC point called generator point G (base point), which can generate any other point
        in its subgroup over the elliptic curve by multiplying G by some integer in the range [0...r]
            * The number r is called "order" of the cyclic subgroup (the total number of all points in the subgroup).
        * For curves with cofactor = 1 there is only one subgroup and the order n of the curve (the total number of different points over the curve, including the infinity) is equal to the number r
        * When G and n are carefully selected, and the cofactor = 1, all possible EC points on the curve (including the special point infinity) can be generated from the generator G by multiplying it by integer in the range [1...n]
            * This integer n is known as "order of the curve".
        * It is important to know that the order r of the subgroup, obtained from certain EC generator point G (which may be different from the order of the curve) defines the total number of all possible private keys for this curve: r = n / h (curve order, divided by the curve cofactor)
        * To summarize, in the ECC cryptography the EC points, together with the generator point G form cyclic groups (or cyclic subgroups), which means that a number r exists (r > 1), such that r * G = 0 * G = infinity and all points in the subgroup can be obtained by multiplying G by integer in the range [1...r]. The number r is called order of the group (or subgroup).
    * q is the prime order of the group generated by G
    * h is the cofactor of G in E, i.e., #E(GF(p))/q
        * h is often 1
        * Аn elliptic curve over a finite field can form a finite cyclic algebraic group, which consists of all the points on the curve
            * In a cyclic group, if two EC points are added or an EC point is multiplied to an integer, the result is another EC point from the same cyclic group (and on the same curve)
        * The order of the curve is the total number of all EC points on the curve
        * This total number of points includes also the special point called "point at infinity", which is obtained when a point is multiplied by 0
        * Some curves form a single cyclic group (holding all their EC points), while others form several non-overlapping cyclic subgroups (each holding a subset of the curve's EC points)
            * In the second scenario the points on the curve are split into h cyclic subgroups (partitions), each of order r (each subgroup holds equal number of points)
            *  The order of entire group is n = h * r (the number of subgroups, multiplied by the number of points in each subgroup)
        * The cofactor is typically expressed by the following formula: h = n / r
            * n is the order of the curve (the number of all its points)
            * h is the curve cofactor (the number of non-overlapping subgroups of points, which together hold all curve points)
            * r is the order of the subgroups (the number of points in each subgroup, including the infinity point for each subgroup)
        * In other words, the points over an elliptic curve stay in one or several non-overlapping subsets, called cyclic subgroups
            * The number of subgroups is called "cofactor"
            * The total number of points in all subgroups is called "order" of the curve and is usually denoted by n
            * If the curve consists of only one cyclic subgroup, its cofactor h = 1. If the curve consists of several subgroups, its cofactor > 1
        * Example of elliptic curve having cofactor = 1 is secp256k1.
        * Example of elliptic curve having cofactor = 8 is Curve25519.
    * A private key is a big integer d, and a public key is the result of the ECC point multiplication [d]G
        * private keys in the ECC are integers (in the range of the curve's field size, typically 256-bit integers)
        *  public keys in the ECC are EC points - pairs of integer coordinates {x, y}, laying on the curve
            * Due to their special properties, EC points can be compressed to just one coordinate + 1 bit (odd or even)
            * Thus the compressed public key, corresponding to a 256-bit ECC private key, is a 257-bit integer