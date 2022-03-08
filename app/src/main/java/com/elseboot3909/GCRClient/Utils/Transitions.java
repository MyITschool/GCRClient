package com.elseboot3909.GCRClient.Utils;

import com.google.android.material.transition.MaterialSharedAxis;

public class Transitions {

    public static MaterialSharedAxis getForwardTransition() {
        return new MaterialSharedAxis(MaterialSharedAxis.X, false);
    }

    public static MaterialSharedAxis getBackwardTransition() {
        return new MaterialSharedAxis(MaterialSharedAxis.X, true);
    }

}
