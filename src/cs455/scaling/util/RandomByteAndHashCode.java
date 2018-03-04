package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class RandomByteAndHashCode {

  public static byte[] randomBytes(){
     byte[] data = new byte[ProjectProperties.BYTE_BUFFER_SIZE];

    new Random().nextBytes(data);
    return data;
  }


  public static String SHA1FromBytes(byte[] data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA1");
      byte[] hash = digest.digest(data);
      BigInteger hashInt = new BigInteger(1, hash);
      return hashInt.toString(16);

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error creating SHA1 from bytes\n" + e.getMessage());
    }

  }

  private static void testByteBufferAverage(int loops){

    int sumOfNon40Length = 0;

    for (int i = 0; i < loops; i++) {
      sumOfNon40Length += getNon40Count();
    }
    System.out.println("The average of non 40 sized bytes is "
        + (double) sumOfNon40Length/loops);
  }


  private static int getNon40Count() {
    int non40Count = 0;
    int loopSize = 100;

    for (int i = 0; i < loopSize; i++) {
      byte[] bytes = RandomByteAndHashCode.randomBytes();
      String hash = RandomByteAndHashCode.SHA1FromBytes(bytes);
      if(hash.length() != 40){
        non40Count++;
      }
    }
    return non40Count;
  }

  public static void main(String[] args) {
    int loops = 1000;
    RandomByteAndHashCode.testByteBufferAverage(loops);
  }
}
