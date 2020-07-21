package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

import java.util.ListIterator;

/**
 * DarkPacks/SevTech-Ages#4091
 */
public class PatchAstralAmulet extends Patch {
    public PatchAstralAmulet(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected byte[] writeClass() {
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    @Override
    protected boolean patch() {
        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("modifyEnchantmentTags")) continue;

            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insnNode = it.next();

                if (!(insnNode instanceof LdcInsnNode) || !((LdcInsnNode) insnNode).cst.equals("id")) continue;

                boolean foundTarget = false;
                while (!foundTarget) {
                    do {
                        insnNode = it.next();
                    } while (!(insnNode instanceof MethodInsnNode));

                    MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                    if (methodInsnNode.name.equals(SevPatchesLoadingPlugin.GET_SHORT)) {
                        methodInsnNode.name = SevPatchesLoadingPlugin.GET_INT;
                        methodInsnNode.desc = "(Ljava/lang/String;)I";

                        foundTarget = true;
                    } else if (methodInsnNode.name.equals(SevPatchesLoadingPlugin.SET_SHORT)) {
                        methodInsnNode.name = SevPatchesLoadingPlugin.SET_INT;
                        methodInsnNode.desc = "(Ljava/lang/String;I)V";

                        methodNode.instructions.remove(methodInsnNode.getPrevious()); // remove I2S
                        foundTarget = true;
                    }
                }
            }
        }
        return true;
    }
}
