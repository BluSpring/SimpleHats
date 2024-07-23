package fonnymunkey.simplehats.client.hatdisplay;

import fonnymunkey.simplehats.common.entity.HatDisplay;
import fonnymunkey.simplehats.common.item.HatItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class HatDisplayLayer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    private final FeatureRendererContext<T, M> renderLayerParent;

    public HatDisplayLayer(FeatureRendererContext<T, M> renderer) {
        super(renderer);
        this.renderLayerParent = renderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider renderTypeBuffer, int light, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(livingEntity instanceof HatDisplay display) {
            ItemStack stack = display.getEquippedStack(null);
            if(!stack.isEmpty() && stack.getItem() instanceof HatItem && !livingEntity.isInvisible()) {
                matrixStack.push();
                
                matrixStack.scale(1.01F, 1.01F, 1.01F);
                matrixStack.translate(0D, 0.97D, 0.0D);
                
                matrixStack.scale(0.66F, 0.66F, 0.66F);
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                
                MinecraftClient.getInstance().getHeldItemRenderer().renderItem(livingEntity, stack, ModelTransformation.Mode.HEAD, false, matrixStack, renderTypeBuffer, light);
                
                matrixStack.pop();
            }
        }
    }
}
