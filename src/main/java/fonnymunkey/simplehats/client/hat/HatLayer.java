package fonnymunkey.simplehats.client.hat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fonnymunkey.simplehats.common.init.ModConfig;
import fonnymunkey.simplehats.common.item.HatItem;
import fonnymunkey.simplehats.util.HatEntry;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class HatLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
	
	public HatLayer(RenderLayerParent<T,M> renderer) {
		super(renderer);
	}
	
	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		//Hacky fix for first person render mods also rendering player layers over the camera
		if(livingEntity == Minecraft.getInstance().cameraEntity && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON && ModConfig.CLIENT.forceFirstPersonNoRender.get()) return;
		
		CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity)
				 .ifPresent(handler -> handler.getCurios()
											  .forEach((id, stacksHandler) -> {
												  if(stacksHandler.isVisible() && "head".equals(stacksHandler.getIdentifier())) {
													  
													  IDynamicStackHandler stackHandler = stacksHandler.getStacks();
													  IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();
													  for(int i = 0; i < stackHandler.getSlots(); i++) {
														  ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);
														  NonNullList<Boolean> renderStates = stacksHandler.getRenders();
														  boolean renderable = renderStates.size() > i && renderStates.get(i);
														  
														  if(stack.isEmpty() && renderable) {
															  stack = stackHandler.getStackInSlot(i);
														  }
														  
														  if(!stack.isEmpty() && stack.getItem() instanceof HatItem) {
															  render(stack, poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch);
														  }
													  }
												  }
											  }));
	}
	
	private void render(ItemStack itemStack, PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
		if(!livingEntity.isInvisible()) {
			poseStack.pushPose();
			
			//Slightly scale up to fix some skin layer difference issues
			poseStack.scale(1.01F, 1.01F, 1.01F);
			poseStack.translate(0.0F, 0.0F - ModConfig.CLIENT.hatYOffset.get(), 0.0F);
			
			boolean flag = livingEntity instanceof Villager || livingEntity instanceof ZombieVillager;
			if(livingEntity.isBaby() && !(livingEntity instanceof Villager)) {
				poseStack.translate(0.0F, 0.03125F, 0.0F);
				poseStack.scale(0.7F, 0.7F, 0.7F);
				poseStack.translate(0.0F, 1.0F, 0.0F);
			}
			
			this.getParentModel().getHead().translateAndRotate(poseStack);
			translateToHead(poseStack, flag);
			Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, itemStack, ItemTransforms.TransformType.HEAD, false, poseStack, buffer, packedLight);
			
			poseStack.popPose();
		}
		if(livingEntity instanceof Player) {
			HatEntry.HatParticleSettings particleSettings = ((HatItem)itemStack.getItem()).getHatEntry().getHatParticleSettings();
			if(particleSettings.getUseParticles() && !Minecraft.getInstance().isPaused() && livingEntity.getRandom().nextFloat() < (livingEntity.isInvisible() ? particleSettings.getParticleFrequency()/2 : particleSettings.getParticleFrequency())) {
				double d0 = livingEntity.getRandom().nextGaussian() * 0.02D,
						d1 = livingEntity.getRandom().nextGaussian() * 0.02D,
						d2 = livingEntity.getRandom().nextGaussian() * 0.02D,
						y = switch(particleSettings.getParticleMovement()) {
							case TRAILING_HEAD -> livingEntity.getY()+1.75;
							case TRAILING_FEET -> livingEntity.getY()+0.25;
							case TRAILING_FULL -> livingEntity.getRandomY();
						};
				livingEntity.level.addParticle(particleSettings.getParticleType(), livingEntity.getRandomX(0.5D), y, livingEntity.getRandomZ(0.5D), d0, d1,d2);
			}
		}
	}
	
	private static void translateToHead(PoseStack pPoseStack, boolean pIsVillager) {
		pPoseStack.translate(0.0F, -0.25F, 0.0F);
		pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		pPoseStack.scale(0.625F, -0.625F, -0.625F);
		if(pIsVillager) {
			pPoseStack.translate(0.0F, 0.1875F, 0.0F);
		}
	}
}