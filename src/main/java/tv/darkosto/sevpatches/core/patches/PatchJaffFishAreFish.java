package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

/**
 * Implement methods on the abstract Fish class such that fish behave like fish
 */
public class PatchJaffFishAreFish extends Patch {
    public PatchJaffFishAreFish(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        /*
        public boolean isNotColliding() {
            this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
        }
         */
        MethodNode isFishNotColliding = new MethodNode(
                Opcodes.ACC_PUBLIC,
                SevPatchesLoadingPlugin.ENTITY_IS_NOT_COLLIDING,
                "()Z",
                null,
                null
        );
        InsnList fishyInsns = isFishNotColliding.instructions;
        fishyInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        fishyInsns.add(new FieldInsnNode(
                Opcodes.GETFIELD,
                "com/tmtravlr/jaff/entities/EntityFish",
                SevPatchesLoadingPlugin.ENTITY_WORLD,
                "Lnet/minecraft/world/World;"
        ));
        fishyInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        fishyInsns.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "com/tmtravlr/jaff/entities/EntityFish",
                SevPatchesLoadingPlugin.GET_ENTITY_BOUNDING_BOX,
                "()Lnet/minecraft/util/math/AxisAlignedBB;",
                false
        ));
        fishyInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        fishyInsns.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "net/minecraft/world/World",
                SevPatchesLoadingPlugin.CHECK_NO_ENTITY_COLLISION,
                "(Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/entity/Entity;)Z",
                false
        ));
        fishyInsns.add(new InsnNode(Opcodes.IRETURN));
        classNode.methods.add(isFishNotColliding);
        SevPatchesLoadingPlugin.LOGGER.info("JAFFA patch: implemented isNotColliding");

        /*
        public boolean isPuhsedByWater() {
            return false;
        }
         */
        MethodNode isPushedByWater = new MethodNode(Opcodes.ACC_PUBLIC,
                SevPatchesLoadingPlugin.ENTITY_IS_PUSHED_BY_WATER,
                "()Z",
                null,
                null
        );
        InsnList wateryInsns = isPushedByWater.instructions;
        wateryInsns.add(new InsnNode(Opcodes.ICONST_0));
        wateryInsns.add(new InsnNode(Opcodes.IRETURN));
        classNode.methods.add(isPushedByWater);
        SevPatchesLoadingPlugin.LOGGER.info("JAFFA patch: implemented isPushedByWater");

        MethodNode methodNode = AsmUtils.findMethod(classNode, SevPatchesLoadingPlugin.ENTITY_GET_CAN_SPAWN_HERE);
        if (methodNode == null) {
            SevPatchesLoadingPlugin.LOGGER.error("Failed to find EntityFish#getCanSpawnHere");
            return false;
        }
        InsnList replacement = new InsnList();
        replacement.add(new InsnNode(Opcodes.ICONST_1));
        replacement.add(new InsnNode(Opcodes.IRETURN));  // return true
        methodNode.instructions = replacement;
        SevPatchesLoadingPlugin.LOGGER.info("JAFFA patch: getCanSpawnHere now always true");

        return true;
    }
}
