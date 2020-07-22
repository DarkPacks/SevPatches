package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

public class PatchPrimalDrying extends Patch {
    public PatchPrimalDrying(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        boolean patch1 = false;

        MethodNode methodNode = AsmUtils.findMethod(classNode, SevPatchesLoadingPlugin.UPDATE_TICK);
        if (methodNode == null) {
            SevPatchesLoadingPlugin.LOGGER.error("Failed to find updateTick on MudDrying");
        } else {
            InsnList insnList = new InsnList();
            insnList.add(new InsnNode(Opcodes.RETURN));
            methodNode.instructions = insnList;
            patch1 = true;
        }

        boolean patch2 = false;

        MethodNode methodNode1 = AsmUtils.findMethod(classNode, "shouldScheduleOnPlacement");
        if (methodNode1 == null) {
            SevPatchesLoadingPlugin.LOGGER.error("Failed to find shouldSchedule on MudDrying");
        } else {
            InsnList insnList = new InsnList();
            insnList.add(new InsnNode(Opcodes.ICONST_0));
            insnList.add(new InsnNode(Opcodes.IRETURN));
            methodNode1.instructions = insnList;
            patch2 = true;
        }

        return patch1 & patch2;
    }
}
