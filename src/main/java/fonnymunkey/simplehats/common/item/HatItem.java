package fonnymunkey.simplehats.common.item;

import fonnymunkey.simplehats.common.init.ModConfig;
import fonnymunkey.simplehats.common.init.ModRegistry;
import fonnymunkey.simplehats.util.HatEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class HatItem extends Item implements ICurioItem {

    private final HatEntry hatEntry;

    public HatItem(HatEntry entry) {
        super(new Item.Properties()
                .stacksTo(1)
                .tab(ModRegistry.HAT_TAB)
                .rarity(entry.getHatRarity())
                .fireResistant());
        this.hatEntry = entry;
    }

    public HatEntry getHatEntry() {
        return this.hatEntry;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if(((HatItem)itemStack.getItem()).getHatEntry().getHatVariantRange()>0) tooltip.add(Component.translatable("tooltip.simplehats.variant"));
        if(((HatItem)itemStack.getItem()).getHatEntry().getHatName().equalsIgnoreCase("special")) {
            if(itemStack.getTag()!=null && itemStack.getTag().getInt("CustomModelData") > 0) {
                tooltip.add(Component.translatable("tooltip.simplehats.special_true"));
            }
            else {
                tooltip.add(Component.translatable("tooltip.simplehats.special_false"));
            }
        }
    }

    @Override
    @Nullable
    public EquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return ModConfig.COMMON.allowHatInHelmetSlot.get() ? EquipmentSlot.HEAD : null;
    }

    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        if(slotContext.entity() instanceof Player && ModConfig.COMMON.keepHatOnDeath.get()) return ICurio.DropRule.ALWAYS_KEEP;
        else return defaultInstance.getDropRule(slotContext, source, lootingLevel, recentlyHit);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
