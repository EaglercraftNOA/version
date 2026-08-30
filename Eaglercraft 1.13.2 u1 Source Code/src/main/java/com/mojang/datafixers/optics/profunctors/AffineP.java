// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package com.mojang.datafixers.optics.profunctors;

import com.mojang.datafixers.OpticProof;
import com.mojang.datafixers.kinds.K2;

public interface AffineP<P extends K2, Mu extends AffineP.Mu> extends Cartesian<P, Mu>, Cocartesian<P, Mu> {
    interface Mu extends Cartesian.Mu, Cocartesian.Mu {
        OpticProof TYPE_TOKEN = OpticProof.of("AffineP", Cartesian.Mu.TYPE_TOKEN, Cocartesian.Mu.TYPE_TOKEN);
    }
}
