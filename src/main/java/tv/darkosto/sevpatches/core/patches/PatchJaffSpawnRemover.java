package tv.darkosto.sevpatches.core.patches;

import tv.darkosto.sevpatches.core.utils.EventHandlerUtils;

/**
 * Remove laggy and unnecessary fish spawning loop
 */
public class PatchJaffSpawnRemover extends Patch {
    public PatchJaffSpawnRemover(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        return EventHandlerUtils.deregisterEventHandler(classNode, "onWorldTick");
    }
}
