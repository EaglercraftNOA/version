package com.arlenh7.eaglercraft.v1_17.java.security;

import java.security.PrivateKey;
import java.security.PublicKey;

public final class KeyPair {
   private final PublicKey publicKey;
   private final PrivateKey privateKey;

   public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
      this.publicKey = publicKey;
      this.privateKey = privateKey;
   }

   public PublicKey getPublic() {
      return this.publicKey;
   }

   public PrivateKey getPrivate() {
      return this.privateKey;
   }
}
