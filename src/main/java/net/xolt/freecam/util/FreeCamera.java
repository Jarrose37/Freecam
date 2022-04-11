package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.xolt.freecam.config.ModConfig;

import java.util.UUID;

import static net.xolt.freecam.Freecam.MC;

public class FreeCamera extends ClientPlayerEntity {

    private static final ClientPlayNetworkHandler NETWORK_HANDLER = new ClientPlayNetworkHandler(MC, MC.currentScreen, MC.getNetworkHandler().getConnection(), new GameProfile(UUID.randomUUID(), "FreeCamera")) {
        @Override
        public void sendPacket(Packet<?> packet) {
        }
    };

    public FreeCamera() {
        super(MC, MC.world, NETWORK_HANDLER, MC.player.getStatHandler(), MC.player.getRecipeBook(), false, false);

        copyPositionAndRotation(MC.player);
        renderPitch = getPitch();
        renderYaw = getYaw();
        lastRenderPitch = renderPitch;
        lastRenderYaw = renderYaw;
        getAbilities().flying = true;
        input = new KeyboardInput(MC.options);
    }

    public void spawn() {
        if (clientWorld != null) {
            clientWorld.addEntity(getId(), this);
        }
    }

    public void despawn() {
        if (clientWorld != null && clientWorld.getEntityById(getId()) != null) {
            clientWorld.removeEntity(getId(), RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public float getHandSwingProgress(float tickDelta) {
        return MC.player.getHandSwingProgress(tickDelta);
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public void setPose(EntityPose pose) {
    }

    @Override
    public void tickMovement() {
        noClip = ModConfig.INSTANCE.noclip;
        if (ModConfig.INSTANCE.flightMode.equals(ModConfig.FlightMode.DEFAULT)) {
            getAbilities().setFlySpeed(0);
            Motion.doMotion(this, ModConfig.INSTANCE.horizontalSpeed, ModConfig.INSTANCE.verticalSpeed);
        } else {
            getAbilities().setFlySpeed((float) ModConfig.INSTANCE.verticalSpeed / 10);
        }
        super.tickMovement();
        getAbilities().flying = true;
        onGround = false;
    }
}
