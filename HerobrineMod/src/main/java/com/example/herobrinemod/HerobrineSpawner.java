package com.example.herobrinemod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = HerobrineMod.MODID)
public class HerobrineSpawner {
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.level instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;
        if (level.getGameTime() % 20 != 0 || level.isDay()) return;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || RANDOM.nextInt(300) != 0) continue;

            boolean exists = !level.getEntitiesOfClass(
                HerobrineEntity.class,
                player.getBoundingBox().inflate(128)).isEmpty();

            if (exists) continue;

            int x = player.blockPosition().getX() + RANDOM.nextInt(81) - 40;
            int z = player.blockPosition().getZ() + RANDOM.nextInt(81) - 40;

            if (!level.hasChunkAt(new BlockPos(x, 0, z))) continue;

            BlockPos pos = level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new BlockPos(x, 0, z));

            if (!level.isEmptyBlock(pos)
                || !level.getBlockState(pos.below()).isSolid()) continue;

            HerobrineEntity herobrine =
                HerobrineMod.HEROBRINE.get().create(level);

            if (herobrine == null) continue;

            herobrine.moveTo(pos.getX() + 0.5, pos.getY(),
                pos.getZ() + 0.5, RANDOM.nextFloat() * 360F, 0F);

            level.addFreshEntity(herobrine);
            break;
        }
    }
}
