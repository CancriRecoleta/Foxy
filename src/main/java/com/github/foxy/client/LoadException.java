package com.github.foxy.client;

// Retained from upstream Voxy for parity. Upstream throws this on a fatal world-join error
// (MixinClientCommonPacketListenerImpl#onPacketError) and force-surfaces it as a crash via
// MixinBlockableEventLoop's redirect of BlockableEventLoop#isNonRecoverable. Both anchor surfaces
// were introduced in 1.20.2+/1.21 and do not exist in 1.20.1 (ClientCommonPacketListenerImpl is
// absent, and 1.20.1 BlockableEventLoop#doRunTask has no isNonRecoverable call), so that mechanism
// is not portable as written and this type is currently unused on this platform. Kept so the force-
// crash diagnostic can be reattached to a 1.20.1-equivalent hook later without re-adding the class.
public class LoadException extends RuntimeException {
    public LoadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
