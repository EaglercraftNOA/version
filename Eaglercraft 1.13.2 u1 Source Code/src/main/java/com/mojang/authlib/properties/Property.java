package com.mojang.authlib.properties;

public class Property {
    private final String name;
    private final String value;
    private final String signature;

    public Property(final String value, final String name) {
        this(value, name, null);
    }

    public Property(final String name, final String value, final String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public boolean hasSignature() {
        return signature != null;
    }

    public boolean isSignatureValid(final Object publicKey) {
        return this.signature != null;
    }
}
