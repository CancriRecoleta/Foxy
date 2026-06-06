//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AdvancementList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
    private final Set<Advancement> roots = Sets.newLinkedHashSet();
    private final Set<Advancement> tasks = Sets.newLinkedHashSet();
    @Nullable
    private Listener listener;

    public AdvancementList() {
    }

    private void remove(Advancement p_139340_) {
        Iterator var2 = p_139340_.getChildren().iterator();

        while(var2.hasNext()) {
            Advancement $$1 = (Advancement)var2.next();
            this.remove($$1);
        }

        LOGGER.info("Forgot about advancement {}", p_139340_.getId());
        this.advancements.remove(p_139340_.getId());
        if (p_139340_.getParent() == null) {
            this.roots.remove(p_139340_);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementRoot(p_139340_);
            }
        } else {
            this.tasks.remove(p_139340_);
            if (this.listener != null) {
                this.listener.onRemoveAdvancementTask(p_139340_);
            }
        }

    }

    public void remove(Set<ResourceLocation> p_139336_) {
        Iterator var2 = p_139336_.iterator();

        while(var2.hasNext()) {
            ResourceLocation $$1 = (ResourceLocation)var2.next();
            Advancement $$2 = (Advancement)this.advancements.get($$1);
            if ($$2 == null) {
                LOGGER.warn("Told to remove advancement {} but I don't know what that is", $$1);
            } else {
                this.remove($$2);
            }
        }

    }

    public void add(Map<ResourceLocation, Advancement.Builder> p_139334_) {
        Map<ResourceLocation, Advancement.Builder> $$1 = Maps.newHashMap(p_139334_);

        label42:
        while(!$$1.isEmpty()) {
            boolean $$2 = false;
            Iterator<Map.Entry<ResourceLocation, Advancement.Builder>> $$3 = $$1.entrySet().iterator();

            Map.Entry $$8;
            while($$3.hasNext()) {
                $$8 = (Map.Entry)$$3.next();
                ResourceLocation $$5 = (ResourceLocation)$$8.getKey();
                Advancement.Builder $$6 = (Advancement.Builder)$$8.getValue();
                Map var10001 = this.advancements;
                Objects.requireNonNull(var10001);
                if ($$6.canBuild(var10001::get)) {
                    Advancement $$7 = $$6.build($$5);
                    this.advancements.put($$5, $$7);
                    $$2 = true;
                    $$3.remove();
                    if ($$7.getParent() == null) {
                        this.roots.add($$7);
                        if (this.listener != null) {
                            this.listener.onAddAdvancementRoot($$7);
                        }
                    } else {
                        this.tasks.add($$7);
                        if (this.listener != null) {
                            this.listener.onAddAdvancementTask($$7);
                        }
                    }
                }
            }

            if (!$$2) {
                $$3 = $$1.entrySet().iterator();

                while(true) {
                    if (!$$3.hasNext()) {
                        break label42;
                    }

                    $$8 = (Map.Entry)$$3.next();
                    LOGGER.error("Couldn't load advancement {}: {}", $$8.getKey(), $$8.getValue());
                }
            }
        }

        LOGGER.info("Loaded {} advancements", this.advancements.size());
    }

    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.tasks.clear();
        if (this.listener != null) {
            this.listener.onAdvancementsCleared();
        }

    }

    public Iterable<Advancement> getRoots() {
        return this.roots;
    }

    public Collection<Advancement> getAllAdvancements() {
        return this.advancements.values();
    }

    @Nullable
    public Advancement get(ResourceLocation p_139338_) {
        return (Advancement)this.advancements.get(p_139338_);
    }

    public void setListener(@Nullable Listener p_139342_) {
        this.listener = p_139342_;
        if (p_139342_ != null) {
            Iterator var2 = this.roots.iterator();

            Advancement $$2;
            while(var2.hasNext()) {
                $$2 = (Advancement)var2.next();
                p_139342_.onAddAdvancementRoot($$2);
            }

            var2 = this.tasks.iterator();

            while(var2.hasNext()) {
                $$2 = (Advancement)var2.next();
                p_139342_.onAddAdvancementTask($$2);
            }
        }

    }

    public interface Listener {
        void onAddAdvancementRoot(Advancement var1);

        void onRemoveAdvancementRoot(Advancement var1);

        void onAddAdvancementTask(Advancement var1);

        void onRemoveAdvancementTask(Advancement var1);

        void onAdvancementsCleared();
    }
}
