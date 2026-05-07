package com.github.foxy.client.compat;

public record FogParameters(
        float environmentalStart,
        float environmentalEnd,
        float red,
        float green,
        float blue,
        float alpha
) {
    public static final FogParameters NONE = new FogParameters(Float.MAX_VALUE, Float.MAX_VALUE, 0, 0, 0, 0);
}
