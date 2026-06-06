//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.client.renderer;

import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum FaceInfo {
    DOWN(new VertexInfo[]{new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z)}),
    UP(new VertexInfo[]{new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z)}),
    NORTH(new VertexInfo[]{new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z)}),
    SOUTH(new VertexInfo[]{new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z)}),
    WEST(new VertexInfo[]{new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MIN_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z)}),
    EAST(new VertexInfo[]{new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z), new VertexInfo(net.minecraft.client.renderer.FaceInfo.Constants.MAX_X, net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y, net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z)});

    private static final FaceInfo[] BY_FACING = (FaceInfo[])Util.make(new FaceInfo[6], (p_108987_) -> {
        p_108987_[net.minecraft.client.renderer.FaceInfo.Constants.MIN_Y] = DOWN;
        p_108987_[net.minecraft.client.renderer.FaceInfo.Constants.MAX_Y] = UP;
        p_108987_[net.minecraft.client.renderer.FaceInfo.Constants.MIN_Z] = NORTH;
        p_108987_[net.minecraft.client.renderer.FaceInfo.Constants.MAX_Z] = SOUTH;
        p_108987_[net.minecraft.client.renderer.FaceInfo.Constants.MIN_X] = WEST;
        p_108987_[net.minecraft.client.renderer.FaceInfo.Constants.MAX_X] = EAST;
    });
    private final VertexInfo[] infos;

    public static FaceInfo fromFacing(Direction p_108985_) {
        return BY_FACING[p_108985_.get3DDataValue()];
    }

    private FaceInfo(VertexInfo... p_108981_) {
        this.infos = p_108981_;
    }

    public VertexInfo getVertexInfo(int p_108983_) {
        return this.infos[p_108983_];
    }

    @OnlyIn(Dist.CLIENT)
    public static class VertexInfo {
        public final int xFace;
        public final int yFace;
        public final int zFace;

        VertexInfo(int p_109002_, int p_109003_, int p_109004_) {
            this.xFace = p_109002_;
            this.yFace = p_109003_;
            this.zFace = p_109004_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Constants {
        public static final int MAX_Z;
        public static final int MAX_Y;
        public static final int MAX_X;
        public static final int MIN_Z;
        public static final int MIN_Y;
        public static final int MIN_X;

        public Constants() {
        }

        static {
            MAX_Z = Direction.SOUTH.get3DDataValue();
            MAX_Y = Direction.UP.get3DDataValue();
            MAX_X = Direction.EAST.get3DDataValue();
            MIN_Z = Direction.NORTH.get3DDataValue();
            MIN_Y = Direction.DOWN.get3DDataValue();
            MIN_X = Direction.WEST.get3DDataValue();
        }
    }
}
