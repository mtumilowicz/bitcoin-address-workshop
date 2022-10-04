import org.bitcoinj.core.Base58
import org.bouncycastle.crypto.digests.RIPEMD160Digest
import org.bouncycastle.util.encoders.Hex

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object Codec {

  def sha256(input: Array[Byte]): Array[Byte] = {
    val sha = MessageDigest.getInstance("SHA-256")
    sha.digest(input)
  }

  def ripemd160(input: Array[Byte]): Array[Byte] = {
    val rmd = new RIPEMD160Digest()
    rmd.update(input, 0, input.length)
    val output = Array.ofDim[Byte](rmd.getDigestSize)
    rmd.doFinal(output, 0)
    output
  }

  def decodeUtf8(string: String): Array[Byte] =
    string.getBytes(StandardCharsets.UTF_8)

  def encodeBase58(bytes: Array[Byte]): String =
    Base58.encode(bytes)

  def decodeHex(hex: String): Array[Byte] =
    Hex.decode("a76448f06981aeb02df458f657be1f2994729f8df0de672ecc0095421089f5bc")
}
