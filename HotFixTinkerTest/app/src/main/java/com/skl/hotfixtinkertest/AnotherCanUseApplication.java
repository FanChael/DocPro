package com.skl.hotfixtinkertest;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * 另外一种方式的Application - NO use.
 */
public class AnotherCanUseApplication extends TinkerApplication {
    public AnotherCanUseApplication() {
        super(
                //tinkerFlags, which types is supported
                //dex only, library only, all support
                ShareConstants.TINKER_ENABLE_ALL,
                // This is passed as a string so the shell application does not
                // have a binary dependency on your ApplicationLifeCycle class.
                "com.skl.hotfixtinkertest.SampleApplicationLike");
    }
}
