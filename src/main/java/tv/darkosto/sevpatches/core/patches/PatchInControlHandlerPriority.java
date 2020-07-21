package tv.darkosto.sevpatches.core.patches;

import tv.darkosto.sevpatches.core.utils.EventHandlerUtils;

/**
 * DarkPacks/SevTech-Ages#3847
 */
public class PatchInControlHandlerPriority extends Patch {
    public PatchInControlHandlerPriority(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        return EventHandlerUtils.setEventSubPriority(classNode, "onLivingDrops", "LOW");
    }
}
