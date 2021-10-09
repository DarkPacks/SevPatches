package tv.darkosto.sevpatches.core.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import tv.darkosto.sevpatches.core.SevPatchesLoadingPlugin;
import tv.darkosto.sevpatches.core.utils.AsmUtils;

public class PatchAstralTools extends Patch {
    public PatchAstralTools(byte[] inputClass) {
        super(inputClass);
    }

    @Override
    protected boolean patch() {
        MethodNode destroySpeed = AsmUtils.findMethod(this.classNode, SevPatchesLoadingPlugin.GET_DESTROY_SPEED);
        if (destroySpeed != null) this.classNode.methods.remove(destroySpeed);

        /*
            @Override
            public float getDestroySpeed(ItemStack stack, IBlockState state) {
                Material material = state.getMaterial();
                if (material != Material.WOOD && material != Material.PLANTS && material != Material.VINE) {
                    return super.getDestroySpeed(stack, state);
                } else {
                    ToolCrystalProperties properties = getToolProperties(stack);
                    return this.efficiency * properties.getEfficiencyMultiplier() * 2F;
                }
            }
         */
        MethodNode getDestroySpeed = new MethodNode(
                Opcodes.ACC_PUBLIC,
                SevPatchesLoadingPlugin.GET_DESTROY_SPEED,
                "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/state/IBlockState;)F",
                null,
                null
        );

        InsnList hookInsns = getDestroySpeed.instructions;
        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 2));
        hookInsns.add(new MethodInsnNode(
                Opcodes.INVOKEINTERFACE,
                "net/minecraft/block/state/IBlockState",
                SevPatchesLoadingPlugin.GET_MATERIAL,
                "()Lnet/minecraft/block/material/Material;",
                true
        ));
        hookInsns.add(new VarInsnNode(Opcodes.ASTORE, 3));

        String[] axe_materials = new String[]{SevPatchesLoadingPlugin.MATERIAL_WOOD, SevPatchesLoadingPlugin.MATERIAL_PLANTS, SevPatchesLoadingPlugin.MATERIAL_VINE};
        String[] pickaxe_materials = new String[]{SevPatchesLoadingPlugin.MATERIAL_ROCK, SevPatchesLoadingPlugin.MATERIAL_IRON, SevPatchesLoadingPlugin.MATERIAL_ANVIL};

        LabelNode jump_label = new LabelNode();

        if (classNode.name.contains("Pickaxe")) {
            for (String material : pickaxe_materials) {
                hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 3));
                hookInsns.add(new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "net/minecraft/block/material/Material",
                        material,
                        "Lnet/minecraft/block/material/Material;"
                ));
                hookInsns.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, jump_label));
            }
        } else if (classNode.name.contains("Axe")) {
            for (String material : axe_materials) {
                hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 3));
                hookInsns.add(new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "net/minecraft/block/material/Material",
                        material,
                        "Lnet/minecraft/block/material/Material;"
                ));
                hookInsns.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, jump_label));
            }
        } else {
            SevPatchesLoadingPlugin.LOGGER.warn("Unexpected tool class: {}", classNode.name);
            return false;
        }

        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 2));
        hookInsns.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                "hellfirepvp/astralsorcery/common/item/tool/ItemCrystalToolBase",
                SevPatchesLoadingPlugin.GET_DESTROY_SPEED,
                "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/state/IBlockState;)F",
                false
        ));
        hookInsns.add(new InsnNode(Opcodes.FRETURN));

        hookInsns.add(jump_label);
        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 1));
        hookInsns.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "hellfirepvp/astralsorcery/common/item/tool/ItemCrystalToolBase",
                "getToolProperties",
                "(Lnet/minecraft/item/ItemStack;)Lhellfirepvp/astralsorcery/common/item/crystal/ToolCrystalProperties;",
                false
        ));
        hookInsns.add(new VarInsnNode(Opcodes.ASTORE, 4));
        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
        hookInsns.add(new FieldInsnNode(
                Opcodes.GETFIELD,
                "net/minecraft/item/ItemTool",
                SevPatchesLoadingPlugin.EFFICIENCY,
                "F"
        ));
        hookInsns.add(new VarInsnNode(Opcodes.ALOAD, 4));
        hookInsns.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "hellfirepvp/astralsorcery/common/item/crystal/ToolCrystalProperties",
                "getEfficiencyMultiplier",
                "()F",
                false
        ));
        hookInsns.add(new InsnNode(Opcodes.FMUL));
        hookInsns.add(new InsnNode(Opcodes.FCONST_2));
        hookInsns.add(new InsnNode(Opcodes.FMUL));
        hookInsns.add(new InsnNode(Opcodes.FRETURN));

        classNode.methods.add(getDestroySpeed);

        return true;
    }
}
