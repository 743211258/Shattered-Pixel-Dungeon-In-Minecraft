package com.example.spdim.core.projectile;

import java.util.List;

import com.example.spdim.core.mechanic.Invincible;
import com.example.spdim.core.Macro;
import com.example.spdim.core.network.MyModNetwork;
import com.example.spdim.core.network.BlastWaveSyncPacket;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.ClipContext;

import com.example.spdim.SPDIM;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.PacketDistributor;

public class BlastWave extends ThrowableProjectile implements ItemSupplier{

    private double explodeRadius;
    private static final EntityDataAccessor<Float> EXPLODE_RADIUS = SynchedEntityData.defineId(BlastWave.class, EntityDataSerializers.FLOAT);

    public BlastWave(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected float getGravity() {
        // Not effected by gravity.
        return 0.0f;
    }

    public void sendShockwaveParticles() {
        if (this.level().isClientSide) {
            return;
        }
        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = this.position();
        double r = this.explodeRadius;
        // Number of points scales with radius.
        int points = (int) (72 * r);

        // Fibonacci sphere
        for (int i = 0; i < points; i++) {
            double phi = Math.acos(1 - 2 * (i + 0.5) / points);
            double theta = Math.PI * (1 + Math.sqrt(5)) * i;

            double x = Math.sin(phi) * Math.cos(theta);
            double y = Math.cos(phi);
            double z = Math.sin(phi) * Math.sin(theta);

            serverLevel.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.FIREWORK,
                    center.x + x * r,
                    center.y + y * r,
                    center.z + z * r,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EXPLODE_RADIUS, (float) explodeRadius);
    }

    public double getExplodeRadius() {
        return explodeRadius;
    }


    public void setExplodeRadius(double radius) {
        this.explodeRadius = radius;
    }


/*    @Override
    public void tick() {
        super.tick();

        // Maximum distance is 100 blocks
        if (this.tickCount > 100) {
            explode(this.position());
        }

        // Explode when it touches liquid
        if (this.isInWaterOrBubble() || this.isInLava()) {
            explode(this.position());
        }

        // Projectile collision detection
        AABB area = this.getBoundingBox().inflate(0.5);

        List<Projectile> projectiles = this.level().getEntitiesOfClass(Projectile.class, area,
                p -> p != this);
        if (!projectiles.isEmpty()) {
            for (Projectile proj : projectiles) {
                if (proj instanceof BlastWave wave) {
                    System.out.println("projectile hit an entity");
                    explode(wave.position());
                }

                // Remove all projectile involved.
                proj.discard();
            }
            explode(this.position());
        }
    }*/

    @Override
    public void tick() {
        super.tick();

        // Maximum distance is 100 blocks
        if (this.tickCount > 100) {
            explode(this.position());
            return;
        }

        // Explode when it touches liquid
        if (this.isInWaterOrBubble() || this.isInLava()) {
            explode(this.position());
            return;
        }

        // Projectile collision detection
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        AABB startBox = this.getBoundingBox();
        Vec3 startPos = this.position();
        Vec3 movement = this.getDeltaMovement();

        List<Entity> entities = new java.util.ArrayList<>();

        for (Entity entity : serverLevel.getAllEntities()) {
        if (entity != this && ((entity instanceof LivingEntity && this.canHitEntity(entity)) || entity instanceof Projectile)) {
                entities.add(entity);
            }
        }

        System.out.println(entities.size());

        for (Entity entity : entities) {
    if (entity instanceof Projectile) {
        System.out.println(
            "PROJECTILE FOUND: "
            + entity.getClass().getName()
            + " ID=" + entity.getId()
            + " Alive=" + entity.isAlive()
            + " CanBeHit=" + entity.canBeHitByProjectile()
        );
    }
            AABB targetBox = entity.getBoundingBox();
            Vec3 targetMovement = entity.getDeltaMovement();

            double time = getCollisionTime(
                    startBox,
                    movement,
                    targetBox,
                    targetMovement
            );

            if (time == -1) {
                continue;
            }

            // Calculate both AABBs at collision time
            AABB projectileHitBox = startBox.move(movement.scale(time));
            AABB entityHitBox = targetBox.move(targetMovement.scale(time));

            // Calculate both centers
            Vec3 projectileCenter = projectileHitBox.getCenter();
            Vec3 entityCenter = entityHitBox.getCenter();

            // Move the blast wave to the entity center
            this.setPos(entityCenter.x, entityCenter.y, entityCenter.z);

            if (entity instanceof Projectile) {
                System.out.println("Projectile collided");
                entity.discard();
            } else {
                // Move the entity to its collision center
                entity.setPos(entityCenter.x, entityCenter.y, entityCenter.z);
            }

            // Explode at the projectile's collision center
            explode(projectileCenter);
            return;
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            explode(result.getLocation());
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
    }
    
    public void explode(Vec3 pos) {
System.out.printf(
    "[EXPLODE] waveId=%d tick=%d pos=%s%n",
    this.getId(),
    this.tickCount,
    pos
);
        if (this.level().isClientSide) {
            return;
        }

        AABB box = this.getBoundingBox().inflate(explodeRadius);
        // Get a list of all targetable entities within the exploding radius.
        List<Entity> candidates = this.level().getEntitiesOfClass(
                Entity.class,
                box,
                e -> e instanceof LivingEntity living && !Invincible.isInvincible(living)
        );

        for (Entity entity : candidates) {
            // Ignore the blast wave itself
            if (entity == this) {
                continue;
            }

            double magnitude = 0.0F;
            if (entity instanceof LivingEntity livingEntity) {
                magnitude = livingEntity.getAttributeValue(Attributes.ARMOR);
            }
            DamageSource source = entity.level().damageSources().explosion(this, getOwner());
            entity.hurt(source, (float) (2.0D + magnitude * Macro.BLAST_WAVE_DAMAGE_MULTIPLIER));
            Vec3 direction = entity.position().subtract(pos);
            double radius = direction.length();
            if (radius > explodeRadius) {
                continue;
            }
            if (radius < 0.0001) {
                direction = new Vec3(0, 1, 0);
                radius = 0.0001;
            }
            double factor = Math.max(0, 1.0 - ((radius / explodeRadius) * (radius / explodeRadius)));
            Vec3 pushForce = direction.normalize().scale((float) ((10.0D - Macro.BLAST_WAVE_KNOCKBACK_MULTIPLIER * magnitude) * factor));
            if (isBlockedByBlock(pos, entity)) {
                pushForce = pushForce.scale(0.2);
            }
            if (entity instanceof ServerPlayer sp) {
                if (isBlockedByShield(this, sp)) {
                    pushForce = pushForce.scale(0.33);
                }
                sp.push(pushForce.x, pushForce.y, pushForce.z);
                MyModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new BlastWaveSyncPacket(sp.getDeltaMovement())
                );
            } else {
                entity.setDeltaMovement(entity.getDeltaMovement().add(pushForce));
            }
        System.out.printf(
    "[TARGET] waveId=%d targetId=%d speed=%s%n",
    this.getId(),
    entity.getId(),
    entity.getDeltaMovement()
);
        }
        sendShockwaveParticles();
        this.discard();
    }
    @Override
    public ItemStack getItem() {
        return new ItemStack(SPDIM.BLAST_WAVE_ITEM.get());
    }

    private boolean isBlockedByBlock(Vec3 from, Entity entity) {
        Level level = entity.level();

        Vec3 to = entity.getBoundingBox().getCenter();

        BlockHitResult hit = level.clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));

        return hit.getType() == HitResult.Type.BLOCK;
    }

    private boolean isBlockedByShield(Entity source, Player target) {
        ItemStack mainHand = target.getMainHandItem();
        ItemStack offHand  = target.getOffhandItem();
        if (!((mainHand.getItem() instanceof ShieldItem || offHand.getItem() instanceof ShieldItem) && target.isUsingItem())) {
            return false;
        }

        // Block is effective if it is facing the blast wave.
        Vec3 direction = source.position().subtract(target.position()).normalize();
        Vec3 look = target.getLookAngle().normalize();
        double dot = direction.dot(look);
        return dot > 0.5;
    }
    private double getCollisionTime(
            AABB a,
            Vec3 velocityA,
            AABB b,
            Vec3 velocityB
    ) {
        Vec3 d = velocityA.subtract(velocityB);

        double[] min = {
            b.minX - a.maxX,
            b.minY - a.maxY,
            b.minZ - a.maxZ
        };

        double[] max = {
            b.maxX - a.minX,
            b.maxY - a.minY,
            b.maxZ - a.minZ
        };

        double[] movement = { d.x, d.y, d.z };

        double tEnter = Double.NEGATIVE_INFINITY;
        double tExit = Double.POSITIVE_INFINITY;

        for (int i = 0; i < 3; i++) {
            double v = movement[i];

            if (v == 0.0) {
                if (min[i] > 0 || max[i] < 0) {
                    return -1;
                }
                continue;
            }

            double t1 = min[i] / v;
            double t2 = max[i] / v;

            tEnter = Math.max(tEnter, Math.min(t1, t2));
            tExit = Math.min(tExit, Math.max(t1, t2));

            if (tEnter > tExit) {
                return -1;
            }
        }

        if (tExit < 0 || tEnter > 1) {
            return -1;
        }

        return Math.max(0, tEnter);
    }
}
