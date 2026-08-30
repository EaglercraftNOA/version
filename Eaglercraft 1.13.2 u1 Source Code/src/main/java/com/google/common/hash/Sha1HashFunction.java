package com.google.common.hash;

import com.arlenh7.eaglercraft.v1_17.crypto.SHA1;
import java.io.Serializable;

final class Sha1HashFunction extends AbstractNonStreamingHashFunction implements Serializable {
  static final HashFunction INSTANCE = new Sha1HashFunction();
  private static final long serialVersionUID = 0L;

  private Sha1HashFunction() {
  }

  @Override
  public int bits() {
    return 160;
  }

  @Override
  public HashCode hashBytes(byte[] input, int off, int len) {
    byte[] bytes = new byte[len];
    System.arraycopy(input, off, bytes, 0, len);
    return HashCode.fromBytesNoCopy(SHA1.digest(bytes));
  }

  @Override
  public String toString() {
    return "Hashing.sha1()";
  }
}
