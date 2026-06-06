//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class BlockMath {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL = (Map)Util.make(Maps.newEnumMap(Direction.class), (p_121851_) -> {
        p_121851_.put(Direction.SOUTH, Transformation.identity());
        p_121851_.put(Direction.EAST, new Transformation((Vector3f)null, (new Quaternionf()).rotateY(1.5707964F), (Vector3f)null, (Quaternionf)null));
        p_121851_.put(Direction.WEST, new Transformation((Vector3f)null, (new Quaternionf()).rotateY(-1.5707964F), (Vector3f)null, (Quaternionf)null));
        p_121851_.put(Direction.NORTH, new Transformation((Vector3f)null, (new Quaternionf()).rotateY(3.1415927F), (Vector3f)null, (Quaternionf)null));
        p_121851_.put(Direction.UP, new Transformation((Vector3f)null, (new Quaternionf()).rotateX(-1.5707964F), (Vector3f)null, (Quaternionf)null));
        p_121851_.put(Direction.DOWN, new Transformation((Vector3f)null, (new Quaternionf()).rotateX(1.5707964F), (Vector3f)null, (Quaternionf)null));
    });
    public static final Map<Direction, Transformation> VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL = (Map)Util.make(Maps.newEnumMap(Direction.class), (p_121849_) -> {
        Direction[] var1 = Direction.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Direction $$1 = var1[var3];
            p_121849_.put($$1, ((Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get($$1)).inverse());
        }

    });

    public BlockMath() {
    }

    public static Transformation blockCenterToCorner(Transformation p_121843_) {
        Matrix4f $$1 = (new Matrix4f()).translation(0.5F, 0.5F, 0.5F);
        $$1.mul(p_121843_.getMatrix());
        $$1.translate(-0.5F, -0.5F, -0.5F);
        return new Transformation($$1);
    }

    public static Transformation blockCornerToCenter(Transformation p_175260_) {
        Matrix4f $$1 = (new Matrix4f()).translation(-0.5F, -0.5F, -0.5F);
        $$1.mul(p_175260_.getMatrix());
        $$1.translate(0.5F, 0.5F, 0.5F);
        return new Transformation($$1);
    }

    public static Transformation getUVLockTransform(Transformation p_121845_, Direction p_121846_, Supplier<String> p_121847_) {
        Direction $$3 = Direction.rotate(p_121845_.getMatrix(), p_121846_);
        Transformation $$4 = p_121845_.inverse();
        if ($$4 == null) {
            LOGGER.warn((String)p_121847_.get());
            return new Transformation((Vector3f)null, (Quaternionf)null, new Vector3f(0.0F, 0.0F, 0.0F), (Quaternionf)null);
        } else {
            Transformation $$5 = ((Transformation)VANILLA_UV_TRANSFORM_GLOBAL_TO_LOCAL.get(p_121846_)).compose($$4).compose((Transformation)VANILLA_UV_TRANSFORM_LOCAL_TO_GLOBAL.get($$3));
            return blockCenterToCorner($$5);
        }
    }
}
