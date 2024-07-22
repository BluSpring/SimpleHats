package fonnymunkey.simplehats.client.hatdisplay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fonnymunkey.simplehats.common.entity.HatDisplay;
import fonnymunkey.simplehats.common.item.HatItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HatDisplayLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final RenderLayerParent<T, M> renderLayerParent;

    public HatDisplayLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.renderLayerParent = renderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(livingEntity instanceof HatDisplay display) {
            ItemStack stack = display.getItemBySlot(null);
            if(!stack.isEmpty() && stack.getItem() instanceof HatItem) {
                if(!livingEntity.isInvisible()) {
                    poseStack.pushPose();
                    
                    poseStack.scale(1.01F, 1.01F, 1.01F);
                    poseStack.translate(0.0F, 0.97F, 0.0F);
                    
                    poseStack.scale(0.66F, 0.66F, 0.66F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    
                    Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(livingEntity, stack, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);
                    
                    poseStack.popPose();
                }
            }
        }
    }
}
