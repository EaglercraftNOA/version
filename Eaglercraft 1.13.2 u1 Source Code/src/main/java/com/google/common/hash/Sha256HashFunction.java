package com.google.common.hash;

import com.arlenh7.eaglercraft.v1_17.crypto.SHA256;
import java.io.Serializable;

final class Sha256HashFunction extends AbstractNonStreamingHashFunction implements Serializable {
  static final HashFunction INSTANCE = new Sha256HashFunction();
  private static final long serialVersionUID = 0L;

  private Sha256HashFunction() {
  }

  @Override
  public int bits() {
    return 256;
  }

  @Override
  public HashCode hashBytes(byte[] input, int off, int len) {
    byte[] bytes = new byte[len];
    System.arraycopy(input, off, bytes, 0, len);
    return HashCode.fromBytesNoCopy(SHA256.digest(bytes));
  }

  @Override
  public String toString() {
    return "Hashing.sha256()";
  }
}
