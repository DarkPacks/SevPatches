package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

/**
 * Add appropriate spawn registration to JAFF
 */
public class PatchJaffFishLiveInWater extends Patch {
    public PatchJaffFishLiveInWater(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode preInit = AsmUtils.findMethod(classNode, "preInit");
        MethodNode postInit = AsmUtils.findMethod(classNode, "postInit");

        if (preInit == null || postInit == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Failed to find JAFF init methods");
            return false;
        }

        InsnList callRegisterPlacement = new InsnList();
        callRegisterPlacement.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/FishHook",
                "registerPlacement",
                "()V",
                false
        ));
        InsnNode preInitReturn = AsmUtils.findReturn(preInit);
        if (preInitReturn != null)
            preInit.instructions.insertBefore(preInitReturn, callRegisterPlacement);
        else return false;

        InsnList callRegisterSpawns = new InsnList();
        callRegisterSpawns.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/FishHook",
                "registerSpawns",
                "()V",
                false
        ));
        InsnNode postInitReturn = AsmUtils.findReturn(postInit);
        if (postInitReturn != null)
            postInit.instructions.insertBefore(postInitReturn, callRegisterSpawns);
        else return false;

        return true;
    }
}
