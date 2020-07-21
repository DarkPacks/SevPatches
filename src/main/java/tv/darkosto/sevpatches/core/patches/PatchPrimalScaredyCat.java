package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;

import java.util.ListIterator;

/**
 * Makes primal's wolves scared of fish
 * Best we can do as fish are apparently land animals
 */
public class PatchPrimalScaredyCat extends Patch {
    public PatchPrimalScaredyCat(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode initEntityAI = null;

        for (MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals(SevPatchesLoadingPlugin.INIT_ENTITY_AI)) continue;
            initEntityAI = methodNode;
            break;
        }

        if (initEntityAI == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("Did not find target method (scaredyCat)");
            return false;
        }

        InsnNode returnInsn = null;

        for (ListIterator<AbstractInsnNode> it = initEntityAI.instructions.iterator(); it.hasNext(); ) {
            AbstractInsnNode insnNode = it.next();

            if (insnNode.getOpcode() == Opcodes.RETURN) {
                returnInsn = (InsnNode) insnNode;
                break;
            }
        }

        if (returnInsn == null) {
            SevPatchesLoadingPlugin.LOGGER.warn("This method does not return?");
            return false;
        }

        InsnList scaredWolf = new InsnList();

        scaredWolf.add(new VarInsnNode(Opcodes.ALOAD, 0));
        scaredWolf.add(new FieldInsnNode(
                Opcodes.GETFIELD,
                "nmd/primal/core/common/entities/living/EntityCanisCampestris",
                SevPatchesLoadingPlugin.ENTITY_TASKS,
                "Lnet/minecraft/entity/ai/EntityAITasks;"
        ));
        scaredWolf.add(new InsnNode(Opcodes.ICONST_1));
        scaredWolf.add(new TypeInsnNode(
                Opcodes.NEW,
                "net/minecraft/entity/ai/EntityAIAvoidEntity"
        ));
        scaredWolf.add(new InsnNode(Opcodes.DUP));
        scaredWolf.add(new VarInsnNode(Opcodes.ALOAD, 0));
        scaredWolf.add(new LdcInsnNode(Type.getObjectType("com/tmtravlr/jaff/entities/EntityFish")));
        scaredWolf.add(new LdcInsnNode(6.0F));
        scaredWolf.add(new InsnNode(Opcodes.DCONST_1));
        scaredWolf.add(new LdcInsnNode(1.2D));
        scaredWolf.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                "net/minecraft/entity/ai/EntityAIAvoidEntity",
                "<init>",
                "(Lnet/minecraft/entity/EntityCreature;Ljava/lang/Class;FDD)V",
                false
        ));
        scaredWolf.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "net/minecraft/entity/ai/EntityAITasks",
                SevPatchesLoadingPlugin.ENTITY_TASKS_ADD_TASK,
                "(ILnet/minecraft/entity/ai/EntityAIBase;)V",
                false
        ));

        initEntityAI.instructions.insertBefore(returnInsn, scaredWolf);
        return true;
    }
}
