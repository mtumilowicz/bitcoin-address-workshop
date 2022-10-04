import org.bitcoinj.core.Address
import org.bitcoinj.params.MainNetParams

import scala.util.Try


object BitcoinAddress {

  def isValid(address: String) =
    Try(Address.fromString(MainNetParams.get(), address))
      .fold(_ => false, _ => true)

  def addPrefix(prefix: BitcoinVersionPrefix, bytes: Array[Byte]): Array[Byte] =
    Array[Byte](prefix.value) ++ bytes

  def generate(publicKey: Array[Byte]): String = {
    val sha256 = Codec.sha256(publicKey)
    val ripemd160 = Codec.ripemd160(sha256)
    val versionPrefixed = addPrefix(BitcoinVersionPrefix.MainNetwork, ripemd160)
    val checksumAppended = Bytes.appendChecksum(versionPrefixed)
    Codec.encodeBase58(checksumAppended)
  }

}
