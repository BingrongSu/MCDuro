package net.robert.mcduro.entity;

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
