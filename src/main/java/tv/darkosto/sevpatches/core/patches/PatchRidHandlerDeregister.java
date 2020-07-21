package tv.darkosto.sevpatches.core.patches;

import tv.darkosto.sevpatches.core.utils.EventHandlerUtils;

/**
 * DarkPacks/SevTech-Ages#4179 pt1
 */
public class PatchRidHandlerDeregister extends Patch {
    public PatchRidHandlerDeregister(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        return EventHandlerUtils.deregisterEventHandler(classNode, "onEntityJoin")
                & EventHandlerUtils.deregisterEventHandler(classNode, "onServerTick")
                & EventHandlerUtils.deregisterEventHandler(classNode, "onWorldUnload");
    }
}
