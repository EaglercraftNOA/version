// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package com.mojang.datafixers.optics.profunctors;

import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.OpticProof;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.App2;
import com.mojang.datafixers.kinds.K2;

public interface Closed<P extends K2, Mu extends Closed.Mu> extends Profunctor<P, Mu> {
    static <P extends K2, Proof extends Closed.Mu> Closed<P, Proof> unbox(final App<Proof, P> proofBox) {
        return (Closed<P, Proof>) proofBox;
    }

    interface Mu extends Profunctor.Mu {
        OpticProof TYPE_TOKEN = OpticProof.of("Closed", Profunctor.Mu.TYPE_TOKEN);
    }

    <A, B, X> App2<P, FunctionType<X, A>, FunctionType<X, B>> closed(final App2<P, A, B> input);
}
