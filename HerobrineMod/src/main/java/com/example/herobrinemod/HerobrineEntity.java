package com.example.herobrinemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class HerobrineEntity extends Monster {
    private int eventCooldown = 200;

    public HerobrineEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 666.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.ATTACK_DAMAGE, 10.0D)
            .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide()) return;

        Player player = level().getNearestPlayer(this, 64.0D);
        if (player != null) {
            getLookControl().setLookAt(player, 10.0F, 10.0F);
            if (distanceToSqr(player) < 64.0D) {
                disappear();
                return;
            }
        }

        if (--eventCooldown <= 0) {
            triggerEvent();
            eventCooldown = 400 + random.nextInt(800);
        }
    }

    private void disappear() {
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE,
                getX(), getY() + 1, getZ(),
                30, 0.4, 0.6, 0.4, 0.02);
        }
        discard();
    }

    private void triggerEvent() {
        if (!(level() instanceof ServerLevel server)) return;
        BlockPos pos = blockPosition();

        switch (random.nextInt(3)) {
            case 0 -> buildPyramid(server, pos);
            case 1 -> removeLeaves(server, pos);
            case 2 -> createTunnel(server, pos);
        }
    }

    // Pískovec místo písku, aby se stavba nesesypala gravitací.
    private void buildPyramid(ServerLevel level, BlockPos pos) {
        BlockPos base = pos.offset(
            random.nextInt(21) - 10, -1,
            random.nextInt(21) - 10);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlockAndUpdate(
                    base.offset(x, 0, z), Blocks.SANDSTONE.defaultBlockState());
            }
        }
        level.setBlockAndUpdate(base.above(), Blocks.SANDSTONE.defaultBlockState());
    }

    private void removeLeaves(ServerLevel level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(
            center.offset(-8, -3, -8), center.offset(8, 8, 8))) {
            var state = level.getBlockState(pos);
            if (state.is(Blocks.OAK_LEAVES)
                || state.is(Blocks.DARK_OAK_LEAVES)
                || state.is(Blocks.BIRCH_LEAVES)) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private void createTunnel(ServerLevel level, BlockPos start) {
        var direction = getDirection();
        for (int i = 0; i < 10; i++) {
            BlockPos pos = start.relative(direction, i);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());

            BlockPos side = pos.relative(direction.getClockWise());
            level.setBlockAndUpdate(side, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(side.above(), Blocks.AIR.defaultBlockState());
        }
    }
}
