//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;

public class EndGatewayFeature extends Feature<EndGatewayConfiguration> {
    public EndGatewayFeature(Codec<EndGatewayConfiguration> p_65682_) {
        super(p_65682_);
    }

    public boolean place(FeaturePlaceContext<EndGatewayConfiguration> p_159715_) {
        BlockPos $$1 = p_159715_.origin();
        WorldGenLevel $$2 = p_159715_.level();
        EndGatewayConfiguration $$3 = (EndGatewayConfiguration)p_159715_.config();
        Iterator var5 = BlockPos.betweenClosed($$1.offset(-1, -2, -1), $$1.offset(1, 2, 1)).iterator();

        while(true) {
            while(var5.hasNext()) {
                BlockPos $$4 = (BlockPos)var5.next();
                boolean $$5 = $$4.getX() == $$1.getX();
                boolean $$6 = $$4.getY() == $$1.getY();
                boolean $$7 = $$4.getZ() == $$1.getZ();
                boolean $$8 = Math.abs($$4.getY() - $$1.getY()) == 2;
                if ($$5 && $$6 && $$7) {
                    BlockPos $$9 = $$4.immutable();
                    this.setBlock($$2, $$9, Blocks.END_GATEWAY.defaultBlockState());
                    $$3.getExit().ifPresent((p_65699_) -> {
                        BlockEntity $$4 = $$2.getBlockEntity($$9);
                        if ($$4 instanceof TheEndGatewayBlockEntity $$5) {
                            $$5.setExitPosition(p_65699_, $$3.isExitExact());
                            $$4.setChanged();
                        }

                    });
                } else if ($$6) {
                    this.setBlock($$2, $$4, Blocks.AIR.defaultBlockState());
                } else if ($$8 && $$5 && $$7) {
                    this.setBlock($$2, $$4, Blocks.BEDROCK.defaultBlockState());
                } else if (($$5 || $$7) && !$$8) {
                    this.setBlock($$2, $$4, Blocks.BEDROCK.defaultBlockState());
                } else {
                    this.setBlock($$2, $$4, Blocks.AIR.defaultBlockState());
                }
            }

            return true;
        }
    }
}
