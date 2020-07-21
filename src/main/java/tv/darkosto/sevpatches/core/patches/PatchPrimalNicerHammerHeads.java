package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

/**
 * Prevent Primal's Hammerhead from murdering all the fish
 */
public class PatchPrimalNicerHammerHeads extends Patch {
    public PatchPrimalNicerHammerHeads(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode canAttackEntity = null;
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("canAttackEntity")) {
                canAttackEntity = methodNode;
            }
        }
        if (canAttackEntity == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Couldn't find target method node: EntityHammerHead#canAttackEntity");
            return false;
        }

        LabelNode start = (LabelNode) canAttackEntity.instructions.get(0);
        InsnList addedCheck = new InsnList();
        addedCheck.add(new VarInsnNode(Opcodes.ALOAD, 1));
        addedCheck.add(new TypeInsnNode(Opcodes.INSTANCEOF, "com/tmtravlr/jaff/entities/EntityFish"));
        addedCheck.add(new JumpInsnNode(Opcodes.IFEQ, start));
        addedCheck.add(new InsnNode(Opcodes.ICONST_0));
        addedCheck.add(new InsnNode(Opcodes.IRETURN));
        canAttackEntity.instructions.insertBefore(start, addedCheck);

        return true;
    }
}
