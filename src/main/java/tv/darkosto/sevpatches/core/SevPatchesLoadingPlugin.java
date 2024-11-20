package tv.darkosto.sevpatches.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("SevPatches")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class SevPatchesLoadingPlugin implements IFMLLoadingPlugin {
    public static Logger LOGGER = LogManager.getLogger("sevpatches_core");

    public static String GET_SHORT;
    public static String SET_SHORT;

    public static String GET_INT;
    public static String SET_INT;

    public static String GET_BLOCK_STATE;
    public static String NEIGHBOUR_CHANGED;
    public static String UPDATE_TICK;

    public static String ENTITY_WORLD;
    public static String ENTITY_IS_NOT_COLLIDING;
    public static String ENTITY_IS_PUSHED_BY_WATER;
    public static String ENTITY_GET_CAN_SPAWN_HERE;
    public static String GET_ENTITY_BOUNDING_BOX;
    public static String CHECK_NO_ENTITY_COLLISION;

    public static String FIND_CHUNKS_FOR_SPAWNING;
    public static String FIND_CHUNKS_FOR_SPAWNING_DESC;
    public static String VEC_3I_DISTANCE_SQ;
    public static String VEC_3I_DISTANCE_SQ_DESC;

    public static String INIT_ENTITY_AI;
    public static String ENTITY_TASKS;
    public static String ENTITY_TASKS_ADD_TASK;

    public static String GET_DESTROY_SPEED;
    public static String GET_MATERIAL;
    public static String EFFICIENCY;
    public static String MATERIAL_ANVIL;
    public static String MATERIAL_IRON;
    public static String MATERIAL_PLANTS;
    public static String MATERIAL_ROCK;
    public static String MATERIAL_VINE;
    public static String MATERIAL_WOOD;

    public static String IS_EMPTY;

    public static String WOODEN_HOE;
    public static String PRISMARINE_SHARD;

    public static String ENTITY_ROTATION_YAW;
    public static String POSITIVE_MODULO;
    public static String FLOOR;

    public static String GRAB_MOUSE_CURSOR;
    public static String UNGRAB_MOUSE_CURSOR;

    public static String ENTITY_ON_ENTITY_COLLISION;
    public static String ITEMSTACK_IS_ITEM_EQUAL;

    public SevPatchesLoadingPlugin() {
        LOGGER.info("setting up mixin environment");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.sevpatches.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"tv.darkosto.sevpatches.core.SevPatchesTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        boolean dev = !(boolean) data.get("runtimeDeobfuscationEnabled");
        SevPatchesLoadingPlugin.GET_SHORT = dev ? "getShort" : "func_74765_d";
        SevPatchesLoadingPlugin.SET_SHORT = dev ? "setShort" : "func_74777_a";

        SevPatchesLoadingPlugin.GET_INT = dev ? "getInteger" : "func_74762_e";
        SevPatchesLoadingPlugin.SET_INT = dev ? "setInteger" : "func_74768_a";

        SevPatchesLoadingPlugin.GET_BLOCK_STATE = dev ? "getBlockState" : "func_180495_p";
        SevPatchesLoadingPlugin.NEIGHBOUR_CHANGED = dev ? "neighborChanged" : "func_189540_a";
        SevPatchesLoadingPlugin.UPDATE_TICK = dev ? "updateTick" : "func_180650_b";

        SevPatchesLoadingPlugin.ENTITY_WORLD = dev ? "world" : "field_70170_p";
        SevPatchesLoadingPlugin.ENTITY_IS_NOT_COLLIDING = dev ? "isNotColliding" : "func_70058_J";
        SevPatchesLoadingPlugin.ENTITY_IS_PUSHED_BY_WATER = dev ? "isPushedByWater" : "func_96092_aw";
        SevPatchesLoadingPlugin.ENTITY_GET_CAN_SPAWN_HERE = dev ? "getCanSpawnHere" : "func_70601_bi";
        SevPatchesLoadingPlugin.GET_ENTITY_BOUNDING_BOX = dev ? "getEntityBoundingBox" : "func_174813_aQ";
        SevPatchesLoadingPlugin.CHECK_NO_ENTITY_COLLISION = dev ? "checkNoEntityCollision" : "func_72917_a";

        // Why is this entirely obfuscated in production?
        SevPatchesLoadingPlugin.FIND_CHUNKS_FOR_SPAWNING = dev ? "findChunksForSpawning" : "a";
        SevPatchesLoadingPlugin.FIND_CHUNKS_FOR_SPAWNING_DESC = dev ? "(Lnet/minecraft/world/WorldServer;ZZZ)I" : "(Loo;ZZZ)I";
        SevPatchesLoadingPlugin.VEC_3I_DISTANCE_SQ = dev ? "distanceSq" : "f";
        SevPatchesLoadingPlugin.VEC_3I_DISTANCE_SQ_DESC = "(DDD)D";

        SevPatchesLoadingPlugin.INIT_ENTITY_AI = dev ? "initEntityAI" : "func_184651_r";
        SevPatchesLoadingPlugin.ENTITY_TASKS = dev ? "tasks" : "field_70714_bg";
        SevPatchesLoadingPlugin.ENTITY_TASKS_ADD_TASK = dev ? "addTask" : "func_75776_a";

        SevPatchesLoadingPlugin.GET_DESTROY_SPEED = dev ? "getDestroySpeed" : "func_150893_a";
        SevPatchesLoadingPlugin.GET_MATERIAL = dev ? "getMaterial" : "func_185904_a";
        SevPatchesLoadingPlugin.EFFICIENCY = dev ? "efficiency" : "field_77864_a";
        SevPatchesLoadingPlugin.MATERIAL_ANVIL = dev ? "ANVIL" : "field_151574_g";
        SevPatchesLoadingPlugin.MATERIAL_IRON = dev ? "IRON" : "field_151573_f";
        SevPatchesLoadingPlugin.MATERIAL_PLANTS = dev ? "PLANTS" : "field_151585_k";
        SevPatchesLoadingPlugin.MATERIAL_ROCK = dev ? "ROCK" : "field_151576_e";
        SevPatchesLoadingPlugin.MATERIAL_VINE = dev ? "VINE" : "field_151582_l";
        SevPatchesLoadingPlugin.MATERIAL_WOOD = dev ? "WOOD" : "field_151575_d";

        SevPatchesLoadingPlugin.IS_EMPTY = dev ? "isEmpty" : "func_191420_l";

        SevPatchesLoadingPlugin.WOODEN_HOE = dev ? "WOODEN_HOE" : "field_151017_I";
        SevPatchesLoadingPlugin.PRISMARINE_SHARD = dev ? "PRISMARINE_SHARD" : "field_179562_cC";

        SevPatchesLoadingPlugin.ENTITY_ROTATION_YAW = dev ? "rotationYaw" : "field_70177_z";
        SevPatchesLoadingPlugin.POSITIVE_MODULO = dev ? "positiveModulo" : "func_188207_b";
        SevPatchesLoadingPlugin.FLOOR = dev ? "floor" : "func_76141_d";

        SevPatchesLoadingPlugin.GRAB_MOUSE_CURSOR = dev ? "grabMouseCursor" : "a";
        SevPatchesLoadingPlugin.UNGRAB_MOUSE_CURSOR = dev ? "ungrabMouseCursor" : "b";

        SevPatchesLoadingPlugin.ENTITY_ON_ENTITY_COLLISION = dev ? "onEntityCollision" : "func_180634_a";
        SevPatchesLoadingPlugin.ITEMSTACK_IS_ITEM_EQUAL = dev ? "isItemEqual" : "func_77969_a";
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
