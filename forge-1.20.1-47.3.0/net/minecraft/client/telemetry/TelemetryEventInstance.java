//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
    public static final Codec<TelemetryEventInstance> CODEC;

    public TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
        properties.propertySet().forEach((p_261699_) -> {
            if (!type.contains(p_261699_)) {
                String var10002 = p_261699_.id();
                throw new IllegalArgumentException("Property '" + var10002 + "' not expected for event: '" + type.id() + "'");
            }
        });
        this.type = type;
        this.properties = properties;
    }

    public TelemetryEvent export(TelemetrySession p_261645_) {
        return this.type.export(p_261645_, this.properties);
    }

    public TelemetryEventType type() {
        return this.type;
    }

    public TelemetryPropertyMap properties() {
        return this.properties;
    }

    static {
        CODEC = TelemetryEventType.CODEC.dispatchStable(TelemetryEventInstance::type, TelemetryEventType::codec);
    }
}
