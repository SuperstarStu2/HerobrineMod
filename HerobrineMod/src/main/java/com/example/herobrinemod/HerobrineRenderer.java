package com.example.herobrinemod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HerobrineRenderer extends
        HumanoidMobRenderer<HerobrineEntity, HumanoidModel<HerobrineEntity>> {

    private static final ResourceLocation TEXTURE =
        new ResourceLocation(HerobrineMod.MODID,
            "textures/entity/herobrine.png");

    public HerobrineRenderer(EntityRendererProvider.Context context) {
        super(context,
            new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HerobrineEntity entity) {
        return TEXTURE;
    }
}
