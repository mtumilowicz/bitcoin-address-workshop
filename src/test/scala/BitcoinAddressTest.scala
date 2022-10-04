import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class BitcoinAddressTest extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  val byteGen = Arbitrary.arbitrary[Byte]
  val _32BytesArrayGen = Gen.listOfN(32, byteGen).map(_.toArray)

  "generating bitcoin address" should "be valid for all 32 random bytes" in {
    forAll(_32BytesArrayGen) { bytes =>
      val bitcoinAddress = BitcoinAddress.generate(bytes)
      BitcoinAddress.isValid(bitcoinAddress) shouldBe true
    }
  }

  "for given private key, bitcoin address" should "be valid" in {
    val privateKey = Codec.decodeHex("a76448f06981aeb02df458f657be1f2994729f8df0de672ecc0095421089f5bc")
    val publicKey = Secp256k1.derivePublicKey(privateKey)
    val bitcoinAddress = BitcoinAddress.generate(publicKey)
    BitcoinAddress.isValid(bitcoinAddress) shouldBe true
  }

}
