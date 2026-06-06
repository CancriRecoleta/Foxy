package com.github.foxy.client.core.util;

// Foxy's stand-in for Sodium 0.8's net.caffeinemc.mods.sodium.client.util.FogParameters, which
// does not exist in Embeddium 0.3.31. It carries the vanilla fog state Foxy needs to feed its LOD
// shader; it is populated from the active render fog state inside the render hooks (see the
// minecraft/iris mixins).
public record FogParameters(float environmentalStart, float environmentalEnd,
                            float red, float green, float blue, float alpha) {
}
