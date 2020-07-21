package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

import java.util.ListIterator;

/**
 * DarkPacks/SevTech-Ages#3829
 */
public class PatchHarvestOOO extends Patch {
    public PatchHarvestOOO(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode methodNode = AsmUtils.findMethod(classNode, "lambda$static$0");
        if (methodNode == null) {
            SevPatchesLoadingPlugin.LOGGER.error("HarvestPatch: Target lambda was not found");
            return false;
        }

        InsnList eventFire = new InsnList();
        Label l = new Label();
        LabelNode ln = new LabelNode(l);
        eventFire.add(ln);
        eventFire.add(new LineNumberNode(19, ln));
        eventFire.add(new VarInsnNode(Opcodes.ALOAD, 7));
        eventFire.add(new VarInsnNode(Opcodes.ALOAD, 0));
        eventFire.add(new VarInsnNode(Opcodes.ALOAD, 1));
        eventFire.add(new VarInsnNode(Opcodes.ALOAD, 2));
        eventFire.add(new InsnNode(Opcodes.ICONST_0));
        eventFire.add(new InsnNode(Opcodes.FCONST_1));
        eventFire.add(new InsnNode(Opcodes.ICONST_0));
        eventFire.add(new VarInsnNode(Opcodes.ALOAD, 3));
        eventFire.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "net/minecraftforge/event/ForgeEventFactory",
                "fireBlockHarvesting",
                "(" +
                        "Ljava/util/List;" +
                        "Lnet/minecraft/world/World;" +
                        "Lnet/minecraft/util/math/BlockPos;" +
                        "Lnet/minecraft/block/state/IBlockState;" +
                        "IFZ" +
                        "Lnet/minecraft/entity/player/EntityPlayer;" +
                        ")F",
                false
        ));
        eventFire.add(new InsnNode(Opcodes.POP));

        for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();
            if (insnNode.getNext() instanceof LineNumberNode) {
                LineNumberNode lnn = (LineNumberNode) insnNode.getNext();
                if (lnn.line == 19) {
                    do {
                        methodNode.instructions.remove(insnNode);
                        insnNode = it.next();
                    } while (!(insnNode instanceof LabelNode));
                }
                if (lnn.line == 44) {
                    methodNode.instructions.insertBefore(insnNode, eventFire);
                }
            }
        }

        return true;
    }
}
