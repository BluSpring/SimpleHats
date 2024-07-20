package fonnymunkey.simplehats.mixin.core;

import fonnymunkey.simplehats.util.TagInjector;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Code taken 1:1 from <a href="https://github.com/wisp-forest/owo-lib/blob/f705f91a33f9a31f38f27c0fd9032977f97f15ca/src/main/java/io/wispforest/owo/mixin/TagGroupLoaderMixin.java#L19">TagGroupLoaderMixin</a>
 * <p>
 * Such is under the MIT license and full credits go to <a href="https://github.com/gliscowo">glisco<a/> for such
 */
@Mixin(TagGroupLoader.class)
public class MixinTagGroupLoader {
    @Shadow
    @Final
    private String dataType;

    @Inject(method = "loadTags", at = @At("TAIL"))
    public void injectValues(ResourceManager manager, CallbackInfoReturnable<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> cir) {
        var map = cir.getReturnValue();

        TagInjector.ADDITIONS.forEach((location, entries) -> {
            if (!this.dataType.equals(location.type())) return;

            var list = map.computeIfAbsent(location.tagId(), id -> new ArrayList<>());
            entries.forEach(addition -> list.add(new TagGroupLoader.TrackedEntry(addition, "owo")));
        });
    }
}