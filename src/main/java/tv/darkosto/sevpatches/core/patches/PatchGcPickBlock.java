package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

import java.util.ListIterator;

/**
 * DarkPacks/SevTech-Ages#3522
 */
public class PatchGcPickBlock extends Patch {
    public PatchGcPickBlock(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("getPickBlock")) continue;
            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();
                if (insnNode instanceof MethodInsnNode && ((MethodInsnNode) insnNode).name.equals(SevPatchesLoadingPlugin.GET_BLOCK_STATE)) {
                    methodNode.instructions.remove(insnNode.getPrevious());
                    ((VarInsnNode) insnNode.getPrevious()).var = 1;
                    methodNode.instructions.remove(insnNode);
                }
            }
        }
        return true;
    }
}
