import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.security._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.crypto._
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

object inmemory {

  private val TRANSFORMATION_AES = "AES/CBC/PKCS5Padding"
  private val keyGen = KeyGenerator.getInstance("AES")
  keyGen.init(256) // required modified US lib
  private val genSecretKey = keyGen.generateKey()
  private val origSecretKey = Base64.getEncoder.encodeToString(genSecretKey.getEncoded)
  private val salt = new Array[Byte](16)
  SecureRandom.getInstance("SHA1PRNG").nextBytes(salt)

  private def keygen(e: String): (SecretKeySpec, IvParameterSpec) = {
    val keyAndIv = try {
      val modSecretKey = curdate() + origSecretKey.drop(8)
      val newSecretKey = Base64.getDecoder.decode(modSecretKey)
      val secretKey = new SecretKeySpec(newSecretKey, 0, newSecretKey.length, "AES")
      val iv = new IvParameterSpec(salt)
      (secretKey, iv)
    } catch {
      case e: NoSuchAlgorithmException => throw new RuntimeException(e)
    }
    keyAndIv
  }

  def main(args: Array[String]): Unit = {

    case class Config(argMethod: String = null, argPath: String = null, argFile: String = null, argKey: String = null,
                      argInv: String = null)

    val parser = new scopt.OptionParser[Config]("scopt") {
      head("\nAES256-ScalaLib (Jan Rock)", "1.0")

      opt[String]('m', "m") required() action { (x, c) =>
        c.copy(argMethod = x)
      } text "flag for method - x for encrypt / d for decrypt"
      opt[String]('p', "p") required() action { (x, c) =>
        c.copy(argPath = x)
      } text "flag for path - </path>"
      opt[String]('f', "f") required() action { (x, c) =>
        c.copy(argFile = x)
      } text "flag for file - <file> / * for all"
      opt[String]('k', "k") required() action { (x, c) =>
        c.copy(argKey = x)
      } text "flag for key - \"g\" (generate) for encrypt and <key> for decrypt"
      opt[String]('i', "i") required() action { (x, c) =>
        c.copy(argInv = x)
      } text "flag for iv - \"g\" (generate) for encrypt and <initialisation vector> for decrypt"
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        val selMethod = config.argMethod
        val selPath = config.argPath
        val selFile = config.argFile
        val selKey = config.argKey
        val selInv = config.argInv

        if (selMethod == "encrypt") {
          var flist = new File(selPath).listFiles.map(_.getName).toList
          if (selFile != "*") {
            var flist = new File(selPath+selFile)
          }
          for (e <- flist)
            if (e.substring(0, 1) != "@" && e.substring(0, 1) != ".") {
              val file_path = selPath + e
              val value = Files.readAllBytes(Paths.get(file_path))
              if (selKey != "g" || selInv != "g") {
                println("Error: add parameter for generate key --k g and --i g for generate init vector")
                System.exit(1)
              }
              val key = keygen(e)._1
              val iv = keygen(e)._2
              val encryptedBytes = encrypt(value, key, iv)
              Files.write(Paths.get(selPath + "@" + e), encryptedBytes)
              println("Encryption of " + e + " is done!")
              println("Key(Mod): " + Base64.getEncoder.encodeToString(key.getEncoded))
              println("IV(Salt): " + Base64.getEncoder.encodeToString(iv.getIV))
            }
        }
        else {
          if (selMethod == "decrypt") {
            val flist = new File(selPath).listFiles.map(_.getName).toList
            for (e <- flist)
              if (e.substring(0, 1) == "@") {
                if (selKey == "" || selInv == "") {
                  println("Error: add parameter for generate key --k <key> and --i <int> for generate init vector")
                  System.exit(1)
                }
                val hushKey = Base64.getDecoder.decode(selInv)
                val iv1 = new IvParameterSpec(hushKey)
                val decodedKey = Base64.getDecoder.decode(selKey)
                val key1 = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES")
                val value1 = Files.readAllBytes(Paths.get(selPath + e))
                val decryptedBytes = new String(decrypt(value1, key1, iv1))
                Files.write(Paths.get(selPath + "new_" + e.drop(1)), decryptedBytes.getBytes(StandardCharsets.UTF_8))
                println("Decryption of new_" + e.drop(1) + " is done!")
              }
          }
        }
      case None =>
        // arguments are bad, error message will have been displayed
        println("Error! Folder of file doesn't exist!")
    }
  }

  private def curdate() = {
    val datenow = LocalDate.now
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    datenow.format(formatter)
  }

  private def encrypt(bytes: Array[Byte], key: Key, iv: IvParameterSpec) = {
    try {
      val cipher = Cipher.getInstance(TRANSFORMATION_AES)
      cipher.init(Cipher.ENCRYPT_MODE, key, iv)
      cipher.doFinal(bytes);
    } catch {
      case e@(
        _: InvalidAlgorithmParameterException |
        _: NoSuchPaddingException |
        _: NoSuchAlgorithmException |
        _: InvalidKeyException |
        _: IllegalBlockSizeException |
        _: BadPaddingException) => throw new RuntimeException(e)
    }
  }

  private def decrypt(bytes: Array[Byte], key: Key, iv: IvParameterSpec) = {
    try {
      val cipher = Cipher.getInstance(TRANSFORMATION_AES)
      cipher.init(Cipher.DECRYPT_MODE, key, iv)
      cipher.doFinal(bytes);
    } catch {
      case e@(
        _: InvalidAlgorithmParameterException |
        _: NoSuchPaddingException |
        _: NoSuchAlgorithmException |
        _: InvalidKeyException |
        _: IllegalBlockSizeException |
        _: BadPaddingException) => throw new RuntimeException(e)
    }
  }

}