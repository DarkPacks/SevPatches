package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

/**
 * Stop Primal blocks which use the default implementation of ISpreadBlock from spreading
 */
public class PatchPrimalSpreading extends Patch {
    public PatchPrimalSpreading(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode methodNode = AsmUtils.findMethod(classNode, "spreadBlock");
        if (methodNode == null) {
            SevPatchesLoadingPlugin.LOGGER.error("No spreadBlock method found on ISpreadBlock");
            return false;
        }

        InsnList insnList = new InsnList();
        insnList.add(new InsnNode(Opcodes.RETURN));

        methodNode.instructions = insnList;

        SevPatchesLoadingPlugin.LOGGER.info("Primal block spreading disabled");

        return true;
    }
}
