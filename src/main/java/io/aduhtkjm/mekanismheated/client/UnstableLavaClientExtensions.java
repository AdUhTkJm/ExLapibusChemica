package io.aduhtkjm.mekanismheated.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import io.aduhtkjm.mekanismheated.registries.ModFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Client rendering for unstable lava: vanilla's lava textures (still and flowing) with an orange tint, plus
 * lava-ish fog. Modelled on the extension Mekanism registers for its own fluids; it is registered by hand in
 * {@link ModClient} because unstable lava is registered outside of Mekanism's {@code FluidDeferredRegister}.
 */
public class UnstableLavaClientExtensions implements IClientFluidTypeExtensions {

    @Override
    public ResourceLocation getStillTexture() {
        return ModFluids.UNSTABLE_LAVA_STILL_TEXTURE;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return ModFluids.UNSTABLE_LAVA_FLOWING_TEXTURE;
    }

    @Override
    public ResourceLocation getOverlayTexture() {
        return ResourceLocation.withDefaultNamespace("block/water_overlay");
    }

    @Nullable
    @Override
    public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
    }

    @Override
    public int getTintColor() {
        return ModFluids.UNSTABLE_LAVA_TINT;
    }

    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        int tint = ModFluids.UNSTABLE_LAVA_TINT;
        return new Vector3f(FastColor.ARGB32.red(tint) / 255F, FastColor.ARGB32.green(tint) / 255F, FastColor.ARGB32.blue(tint) / 255F);
    }

    @Override
    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
        //Same as Mekanism's own fluids: always treat it as if the player has no water vision.
        farDistance = 24F;
        if (farDistance > renderDistance) {
            farDistance = renderDistance;
            shape = FogShape.CYLINDER;
        }
        RenderSystem.setShaderFogStart(-8);
        RenderSystem.setShaderFogEnd(farDistance);
        RenderSystem.setShaderFogShape(shape);
    }
}
