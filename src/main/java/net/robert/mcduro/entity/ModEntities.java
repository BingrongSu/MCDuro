package net.robert.mcduro.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;

public class ModEntities {

//    public static final EntityType<HDBombProjectileEntity> HD_BOMB_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
//            new Identifier(TemplateMod.MOD_ID, "hd_bomb_projectile"),
//            FabricEntityTypeBuilder.<HDBombProjectileEntity>create(SpawnGroup.MISC, HDBombProjectileEntity::new)
//                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build());

    public static void registerModEntities() {
        MCDuro.LOGGER.info("Registering entities for "+MCDuro.MOD_ID);
    }
}
