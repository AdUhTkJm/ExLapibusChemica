package io.aduhtkjm.mekanismheated.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import io.aduhtkjm.mekanismheated.content.unstablelava.UnstableLavaVariant;
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
 * Client rendering for one unstable lava variant: vanilla's lava textures (still and flowing) tinted with the
 * variant's tint, plus lava-ish fog. Modelled on the extension Mekanism registers for its own fluids; one is
 * registered per variant by hand in {@link ModClient} because unstable lava is registered outside of Mekanism's
 * {@code FluidDeferredRegister}.
 */
public class UnstableLavaClientExtensions implements IClientFluidTypeExtensions {

    private final UnstableLavaVariant variant;

    public UnstableLavaClientExtensions(UnstableLavaVariant variant) {
        this.variant = variant;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return UnstableLavaVariant.STILL_TEXTURE;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return UnstableLavaVariant.FLOWING_TEXTURE;
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
        return variant.tint();
    }

    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        int tint = variant.tint();
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
