object Main extends App {

    val publicKey = Bytes.generate(Codec.decodeUtf8("initial-seed"))
    val address = BitcoinAddress.generate(publicKey)
    println(address)
    println(BitcoinAddress.isValid(address))

}