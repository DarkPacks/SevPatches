package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

import java.util.ListIterator;

public class PatchAstralBootesCheat extends Patch {
    public PatchAstralBootesCheat(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode playEffectMN = AsmUtils.findMethod(classNode, "playEffect");
        if (playEffectMN == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method node: CEffectBootes#playEffect");
            return false;
        }

        MethodInsnNode insertionPoint = null;
        boolean foundEnd = false;

        for (ListIterator<AbstractInsnNode> it = playEffectMN.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();
            if (!(insnNode instanceof MethodInsnNode)) continue;
            if (!((MethodInsnNode) insnNode).name.equals("getHerdingDropsTick")) continue;
            insertionPoint = (MethodInsnNode) insnNode;
            while (it.hasNext()) {
                if (insnNode.getNext() instanceof LabelNode && insnNode.getNext().getNext() instanceof LineNumberNode && ((LineNumberNode) insnNode.getNext().getNext()).line == 78) {
                    foundEnd = true;
                    break;
                }
                playEffectMN.instructions.remove(insnNode.getNext());
            }
            break;
        }

        if (insertionPoint == null || !foundEnd) {
            SevPatchesLoadingPlugin.LOGGER.warn("Could not find target instruction: INVOKE getHerdingDropsTick, or failed to find end. Skipping patch.");
            return false;
        }

        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 9));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 6));
        insnList.add(new FieldInsnNode(
                Opcodes.GETSTATIC,
                "hellfirepvp/astralsorcery/common/constellation/effect/aoe/CEffectBootes",
                "rand",
                "Ljava/util/Random;"
        ));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "tv/darkosto/sevpatches/core/hooks/BootesHook",
                "handleBootesDrops",
                "(Ljava/util/List;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;ZLjava/util/Random;)Z",
                false
        ));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 6));

        playEffectMN.instructions.insert(insertionPoint, insnList);

        return true;
    }
}
