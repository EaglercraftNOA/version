// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package com.mojang.datafixers;

import java.util.Objects;

public final class OpticProof {
    private final String name;
    private final OpticProof[] parents;

    private OpticProof(final String name, final OpticProof... parents) {
        this.name = name;
        this.parents = parents == null ? new OpticProof[0] : parents;
    }

    public static OpticProof of(final String name, final OpticProof... parents) {
        return new OpticProof(name, parents);
    }

    public boolean isSupertypeOf(final OpticProof proof) {
        if (proof == null) {
            return false;
        }
        if (this == proof || Objects.equals(name, proof.name)) {
            return true;
        }
        for (final OpticProof parent : proof.parents) {
            if (isSupertypeOf(parent)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OpticProof && Objects.equals(name, ((OpticProof) obj).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
